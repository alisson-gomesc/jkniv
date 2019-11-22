/* 
 * JKNIV, whinstone one contract to access your database.
 * 
 * Copyright (C) 2017, the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.jkniv.whinstone.jpa2;

import java.sql.ResultSet;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.QueryNotFoundException;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.logger.DataMasking;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.commands.Command;
import net.sf.jkniv.whinstone.commands.CommandAdapter;
import net.sf.jkniv.whinstone.jpa2.commands.DefaultJpaCommand;
import net.sf.jkniv.whinstone.jpa2.commands.DefaultJpaQuery;
import net.sf.jkniv.whinstone.jpa2.commands.MergeCommand;
import net.sf.jkniv.whinstone.jpa2.commands.PersistCommand;
import net.sf.jkniv.whinstone.jpa2.commands.RemoveCommand;
import net.sf.jkniv.whinstone.jpa2.statement.JpaStatementAdapter;
import net.sf.jkniv.whinstone.statement.AutoKey;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class JpaCommandAdapter implements CommandAdapter
{
    private static final Logger       LOG               = LoggerFactory.getLogger();
    private static final Logger       SQLLOG            = net.sf.jkniv.whinstone.jpa2.LoggerFactory.getLogger();
    private static final DataMasking  MASKING           = net.sf.jkniv.whinstone.jpa2.LoggerFactory.getDataMasking();
    private static final Assertable   NOT_NULL          = AssertsFactory.getNotNull();
    private static final String       ENTITY_ANNOTATION = "javax.persistence.Entity";
    private final String              contextName;
    private final HandleableException handlerException;
    private JpaEmFactory              emFactory;
    
    public JpaCommandAdapter(String contextName, JpaEmFactory emFactory, HandleableException handlerException)
    {
        NOT_NULL.verify(contextName, emFactory, handlerException);
        this.contextName = contextName;
        this.handlerException = handlerException;
        this.emFactory = emFactory;
    }
    
    @Override
    public String getContextName()
    {
        return this.contextName;
    }
    
    @Override
    public void close() //throws SQLException
    {
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T, R> Command asSelectCommand(Queryable queryable, ResultRow<T, R> overloadResultRow)
    {
        DefaultJpaQuery command = null;
        String sql = queryable.query();
        if (SQLLOG.isInfoEnabled())
            SQLLOG.info("Bind Native SQL\n{}", sql);
        
        Query queryJpa = build(queryable);
        StatementAdapter<T, R> stmt = new JpaStatementAdapter(queryJpa, queryable, this.handlerException);
        queryable.bind(stmt).on();
        stmt.with(overloadResultRow);
        command = new DefaultJpaQuery(stmt, queryable);
        return command;
    }
    
    @Override
    public <T, R> Command asUpdateCommand(Queryable queryable)
    {
        Command command = null;
        if (isEntity(queryable))
            command = new MergeCommand(getEntityManager(), queryable);
        else
            command = buildCommand(queryable);
        
        return command;
    }
    
    @Override
    public <T, R> Command asRemoveCommand(Queryable queryable)//, ResultRow<T, R> overloadResultRow)
    {
        Command command = null;
        if (isEntity(queryable))
            command = new RemoveCommand(getEntityManager(), queryable);
        else
            command = buildCommand(queryable);
        
        return command;
    }
    
    @Override
    public <T, R> Command asAddCommand(Queryable queryable)//, ResultRow<T, R> overloadResultRow)
    {
        Command command = null;
        if (isEntity(queryable))
            command = new PersistCommand(getEntityManager(), queryable);
        else
            command = buildCommand(queryable);
        
        return command;
    }
    
    @SuppressWarnings(
    { "rawtypes" })
    private <T, R> Command buildCommand(Queryable queryable)
    {
        Command command = null;
        String sql = queryable.query();
        AutoKey auto = null;
        if (SQLLOG.isInfoEnabled())
            SQLLOG.info("Bind Native SQL\n{}", sql);
        
        Query queryJpa = build(queryable);
        
        JpaStatementAdapter<Number, ResultSet> stmt = new JpaStatementAdapter<Number, ResultSet>(queryJpa, queryable,
                this.handlerException);
        
        //        if (queryable.getDynamicSql().isBatch() || queryable.isTypeOfBulk())
        //            command = new BulkCommand((CassandraPreparedStatementAdapter) stmt, queryable);
        //        else if (queryable.getDynamicSql().isInsertable()
        //                && queryable.getDynamicSql().asInsertable().isAutoGenerateKey())
        //        {
        //            Insertable isql = queryable.getDynamicSql().asInsertable();
        //            // isSequenceStrategy and isAutoStrategy are the the same in cassandra uuid value is generated
        //            // if(isql.getAutoGeneratedKey().isAutoStrategy())
        //            auto = new CassandraSequenceGeneratedKey(isql, this.session, handlerException);
        //            command = new AddSequenceKeyJdbcCommand(stmt, queryable);
        //        }
        //        else
        //            command = new DefaultCommand((CassandraPreparedStatementAdapter) stmt, queryable);
        
        command = new DefaultJpaCommand(stmt, queryable);
        stmt.with(auto);
        return command;
    }
    
    private boolean isEntity(Queryable queryable)
    {
        boolean isEntity = false;
        if (queryable.getParams() != null)
        {
            isEntity = ObjectProxyFactory.of(queryable.getParams()).mute(ClassNotFoundException.class)
                    .hasAnnotation(ENTITY_ANNOTATION);
        }
        return (isEntity && 
                (queryable.getDynamicSql().getLanguageType() == LanguageType.HQL
                || queryable.getDynamicSql().getLanguageType() == LanguageType.JPQL));
    }
    
//    @Override
//    public <T, R> StatementAdapter<T, R> newStatement(String sql, LanguageType languageType)
//    {
//        Query queryJpa = getEntityManager().createQuery(sql);
//        StatementAdapter<T, R> stmt = new JpaStatementAdapter(queryJpa, null, this.handlerException);
//        return stmt;
//    }

    private Query build(Queryable queryable)
    {
        Sql sql = queryable.getDynamicSql();
        LanguageType languageType = sql.getLanguageType();
        Class<?> overloadReturnedType = sql.getReturnTypeAsClass();
        EntityManager em = getEntityManager();
        Query queryJpa = null;
        String stringSql = queryable.query();
        boolean returnTypeIsEntity = false;
        if (queryable.getDynamicSql().hasReturnType())
        {
            ObjectProxy<?> proxyReturnType = ObjectProxyFactory.of(queryable.getDynamicSql().getReturnType());
            returnTypeIsEntity = proxyReturnType.hasAnnotation(ENTITY_ANNOTATION);
        }
        switch (languageType)
        {
            case JPQL:
                if (sql instanceof NamedQueryForSql)
                {
                    try
                    {
                        if (!Map.class.getName().equals(queryable.getReturnType().getName()))
                            queryJpa = em.createNamedQuery(sql.getName(), sql.getReturnTypeAsClass());
                        else
                            queryJpa = em.createNamedQuery(sql.getName());
                        
                        this.initParams(queryable, queryJpa);
                    }
                    catch (IllegalArgumentException notFound)
                    {
                        throw new QueryNotFoundException("Named Query not found [" + queryable.getName() + "] check if orm.xml have the query named or it's annotated");
                    }                
                }
                else if (returnTypeIsEntity)
                    queryJpa = em.createQuery(stringSql, overloadReturnedType);
                else
                    queryJpa = em.createQuery(stringSql);
                
                break;
            case NATIVE:
                if (returnTypeIsEntity)
                    queryJpa = em.createNativeQuery(stringSql, overloadReturnedType);
                else
                    queryJpa = em.createNativeQuery(stringSql);
                break;
            case STORED:
                throw new UnsupportedOperationException(
                        "RepositoryJpa supports JPQL or NATIVE queries none other, Stored Procedure is pending to implements");
            //              queryJpa = em.createStoredProcedureQuery(isql.asStorable().getSpName());          
            default:
                throw new UnsupportedOperationException(
                        "RepositoryJpa supports JPQL or NATIVE queries none other, Stored Procedure is pending to implements");
        }
        
        return queryJpa;
        /*
        Class<?> mandatoryReturnType = null;
        Sql isql = null;
        boolean containQuery = sqlContext.containsQuery(queryable.getName());
        if (containQuery)
        {
            isql = sqlContext.getQuery(queryable.getName());
            if (overloadReturnedType != null)
                mandatoryReturnType = overloadReturnedType;
            else if (isql.getReturnTypeAsClass() != null)
                mandatoryReturnType = isql.getReturnTypeAsClass();
            
            adapter = new QueryJpaAdapter(em, queryable, isql, mandatoryReturnType);
            if (queryable.isPaging())
                adapter.setQueryJpaForPaging(getQueryForPaging(em, sqlContext, queryable, isql));
        }
        else
        {
            adapter = new NamedQueryJpaAdapter(em, queryable, overloadReturnedType);
        }
        return adapter;
        */
    }
    
    private void initParams(Queryable queryable, Query queryJpa)
    {
        if (!queryable.isTypeOfNull())
        {
            if (queryable.isTypeOfMap())
            {
                //PrepareParams<Query> prepareParams = PrepareParamsFactory.newPrepareParams(queryJpa, new ParamParserColonMark(), queryable);
                setMapParams(queryJpa, (Map) queryable.getParams());
            }
            else if (queryable.getParams().getClass().isArray())
            {
                //PrepareParams<Query> prepareParams = PrepareParamsFactory.newPrepareParams(queryJpa, new ParamParserQuestionMark(), queryable);
                setArrayParams(queryJpa, (Object[]) queryable.getParams());
            }
        }
        if (queryable.isPaging())
            queryJpa.setFirstResult(queryable.getOffset()).setMaxResults(queryable.getMax());
    }
    
    private void setMapParams(Query query, Map<String, Object> params)
    {
        int i = 0;
        for (String s : params.keySet())
        {
            Object o = params.get(s);
            if (SQLLOG.isDebugEnabled())// TODO making data sqlLogger.mask
                SQLLOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", i++,
                        s, o, (o == null ? "NULL" : o.getClass()));
            
            query.setParameter(s, o);
        }
    }
    
    private void setArrayParams(Query query, Object[] params)
    {
        int i = 1;
        for (Object o : params)
        {
            if (SQLLOG.isDebugEnabled())// TODO making data sqlLogger.mask
                SQLLOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", i,
                        "?", o, (o == null ? "NULL" : o.getClass()));
            query.setParameter(i++, o);
        }
    }
    
    private EntityManager getEntityManager()
    {
        EntityManager em = emFactory.createEntityManager();
        LOG.trace("Lookup Entity Manager " + em);
        return em;
    }
    
    @Override
    public String toString()
    {
        return "JpaCommandAdapter [contextName=" + contextName + ", emFactory=" + emFactory + "]";
    }
    
}
