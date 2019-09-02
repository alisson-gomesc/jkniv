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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.CommandAdapter;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.jpa2.commands.DefaultCommand;
import net.sf.jkniv.whinstone.jpa2.commands.DefaultQuery;
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
    private static final transient Logger     LOG      = LoggerFactory.getLogger();
    private static final transient Logger     SQLLOG   = net.sf.jkniv.whinstone.jpa2.LoggerFactory.getLogger();
    private static final transient Assertable NOT_NULL = AssertsFactory.getNotNull();
    private static final Pattern PATTERN_ORDER_BY = Pattern.compile("order\\s+by\\s+[\\w|\\W|\\s|\\S]*",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    private final String                      contextName;
    private final HandleableException         handlerException;
    private JpaEmFactory                      emFactory;
    
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
    
    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    @Override
    public <T, R> Command asSelectCommand(Queryable queryable, ResultRow<T, R> overloadResultRow)
    {
        Class returnType = Map.class;
        DefaultQuery command = null;
        String sql = queryable.query();
        if (SQLLOG.isInfoEnabled())
            SQLLOG.info("Bind Native SQL\n{}", sql);
        
        Query queryJpa = build(sql, 
                queryable.getDynamicSql().getLanguageType(), 
                queryable.getDynamicSql().getReturnTypeAsClass(), 
                queryable.getDynamicSql().isReturnTypeManaged());
                
        StatementAdapter<T, R> stmt = new JpaStatementAdapter(
                queryJpa, 
                queryable, 
                this.handlerException);
        queryable.bind(stmt).on();
        
        if (queryable.getReturnType() != null)
            returnType = queryable.getReturnType();
        else if (queryable.getDynamicSql().getReturnTypeAsClass() != null)
            returnType = queryable.getDynamicSql().getReturnTypeAsClass();
        
        stmt.returnType(returnType).resultRow(overloadResultRow)
                .oneToManies(queryable.getDynamicSql().asSelectable().getOneToMany())
                .groupingBy(queryable.getDynamicSql().asSelectable().getGroupByAsList());
        
        if (queryable.isScalar())
            stmt.scalar();
        
        command = new DefaultQuery(stmt, queryable);
        return command;
    }
    
    @Override
    public <T, R> Command asUpdateCommand(Queryable queryable)
    {
        return buildCommand(queryable);
    }
    
    @Override
    public <T, R> Command asDeleteCommand(Queryable queryable)//, ResultRow<T, R> overloadResultRow)
    {
        return buildCommand(queryable);
    }
    
    @Override
    public <T, R> Command asAddCommand(Queryable queryable)//, ResultRow<T, R> overloadResultRow)
    {
        return buildCommand(queryable);
    }
    
    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    private <T, R> Command buildCommand(Queryable queryable)
    {
        Command command = null;
        String sql = queryable.query();
        AutoKey auto = null;
        if (SQLLOG.isInfoEnabled())
            SQLLOG.info("Bind Native SQL\n{}", sql);
        
        Query queryJpa = build(sql, 
                queryable.getDynamicSql().getLanguageType(), 
                queryable.getDynamicSql().getReturnTypeAsClass(), 
                queryable.getDynamicSql().isReturnTypeManaged());
        
        JpaStatementAdapter<Number, ResultSet> stmt = new JpaStatementAdapter<Number, ResultSet>(queryJpa,
                queryable, this.handlerException);
        
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
        
        command = new DefaultCommand(stmt, queryable);
        stmt.with(auto);
        return command;
    }
    
    @Override
    public <T, R> StatementAdapter<T, R> newStatement(String sql, LanguageType languageType)
    {
        
        Query queryJpa = build(sql, languageType,  null, false);
        StatementAdapter<T, R> stmt = new JpaStatementAdapter(queryJpa, null, this.handlerException);
        return stmt;
    }
    /*
    private Query getQueryForPaging(SqlContext sqlContext, Queryable queryable, Sql isql)
    {
        Query queryJpa = null;
        Sql sqlCount = null;
        try
        {
            String queryName = queryable.getName() + "#count";
            sqlCount = sqlContext.getQuery(queryName);
            LOG.trace("creating count query [{}] for {}", queryName, queryable.getName());
            Queryable queryCopy = QueryFactory.of(queryName, queryable.getParams(), 0, Integer.MAX_VALUE);
            queryJpa = QueryJpaFactory.newQuery(sqlCount, emFactory.createEntityManager(), queryCopy);
        }
        catch (QueryNotFoundException e)
        {
            // but its very important remove the order clause, because cannot
            // execute this way wrapping with "select count(*) ... where exists" and performance
            String sqlWithoutOrderBy = removeOrderBy(isql.getSql(queryable.getParams()));
            //String entityName = genericType.getSimpleName();
            String sql = "select count (*) from " + isql.getReturnType() + " where exists (" + sqlWithoutOrderBy + ")";
            LOG.trace("creating counttry to count rows using dynamically query [" + sql + "]");
            Queryable queryCopy = QueryFactory.of(queryable.getName(), queryable.getParams(), 0,
                    Integer.MAX_VALUE);
            queryJpa = newQueryForCount(sql, isql.getLanguageType(), em, queryCopy,
                    isql.getParamParser());
        }
        return queryJpa;
    }
    */
    private Query build(String sql, LanguageType languageType, Class<?> overloadReturnedType, boolean isReturnTypeManaged)
    {
        EntityManager em = getEntityManager();
        Query queryJpa = null;
        
        switch (languageType)
        {
            case JPQL:
                if (isReturnTypeManaged)
                    queryJpa = em.createQuery(sql, overloadReturnedType);
                else
                    queryJpa = em.createQuery(sql);
                
                break;
            case NATIVE:
                if (isReturnTypeManaged)
                    queryJpa = em.createNativeQuery(sql, overloadReturnedType);
                else
                    queryJpa = em.createNativeQuery(sql);
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
    
    /**
     * Remove the order by clause from the query.
     * 
     * @param hql
     *            SQL, JPQL or HQL
     * @return return the query without order by clause.
     */
    private String removeOrderBy(String hql)
    {
        Matcher m = PATTERN_ORDER_BY.matcher(hql);
        StringBuffer sb = new StringBuffer();
        while (m.find())
        {
            if (m.hitEnd())
                m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        return sb.toString();
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
