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
package net.sf.jkniv.whinstone.couchdb;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.sqlegance.Command;
import net.sf.jkniv.sqlegance.QueryFactory;
import net.sf.jkniv.sqlegance.QueryNameStrategy;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Repository;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.ResultRow;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.builder.RepositoryConfig;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.sqlegance.transaction.Transactional;
import net.sf.jkniv.whinstone.couchdb.commands.CouchDbSynchViewDesign;

/**
 * Encapsulate the data access for Cassandra
 * 
 * @author Alisson Gomes
 *
 */
public class RepositoryCouchDb implements Repository
{
    private static final Logger     LOG     = LoggerFactory.getLogger(RepositoryCouchDb.class);
    private static final Assertable notNull = AssertsFactory.getNotNull();
    private QueryNameStrategy       strategyQueryName;
    private HandleableException     handlerException;
    private RepositoryConfig        repositoryConfig;
    private CouchDbSqlContext       sqlContext;
    private HttpCookieConnectionAdapter factoryConnection;
    private final static Map<String, Boolean> DOC_SCHEMA_UPDATED = new HashMap<String, Boolean>();
    private boolean                 isTraceEnabled;
    private boolean                 isDebugEnabled;
    
    RepositoryCouchDb()
    {
        this(new Properties(), SqlContextFactory.newInstance("/repository-sql.xml"));
    }
    
    RepositoryCouchDb(Properties props)
    {
        this(props, SqlContextFactory.newInstance("/repository-sql.xml"));
    }

    RepositoryCouchDb(String sqlContext)
    {
        this(new Properties(), SqlContextFactory.newInstance(sqlContext));
    }

    RepositoryCouchDb(SqlContext sqlContext)
    {
        this(new Properties(), sqlContext);
    }

    RepositoryCouchDb(Properties props, SqlContext sqlContext)
    {
        notNull.verify(props, sqlContext);
        if (props.isEmpty() || !props.containsKey(RepositoryProperty.JDBC_URL.key()))
        {
            String jndiName = sqlContext.getRepositoryConfig().getJndiDataSource();
            if (jndiName != null && !"".equals(jndiName))
            {
                Properties propsJndi = lookup(jndiName);
                sqlContext.getRepositoryConfig().add(propsJndi);
            }
        }
        else
            sqlContext.getRepositoryConfig().add(props);
        
        this.sqlContext = new CouchDbSqlContext(sqlContext);
        this.isDebugEnabled = LOG.isDebugEnabled();
        this.isTraceEnabled = LOG.isTraceEnabled();
        this.factoryConnection = (HttpCookieConnectionAdapter) new HttpConnectionFactory(sqlContext.getRepositoryConfig().getProperties()).open();
        this.init();
    }
    
    private void init()
    {
        String hostContext = factoryConnection.getHttpBuilder().getHostContext();
        if (!DOC_SCHEMA_UPDATED.containsKey(hostContext))
        {
            // TODO property to config behavior like auto ddl from hibernate
            CouchDbSynchViewDesign _design = new CouchDbSynchViewDesign(this.factoryConnection.getHttpBuilder(), sqlContext);
            _design.update();
            DOC_SCHEMA_UPDATED.put(hostContext, Boolean.TRUE);
        }
    }
    
    @Override
    public <T> T get(Queryable queryable)
    {
        notNull.verify(queryable);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as get command", queryable);
        
        T ret = get(queryable, null, null);
        
        if (isDebugEnabled)
            LOG.debug("Executed [{}] query  {} rows fetched", queryable.getName(), (ret != null ? "1" : "0"), queryable.getTotal());
        
        return ret;
    }
    
    @Override
    public <T> T get(Queryable queryable, Class<T> returnType)
    {
        notNull.verify(queryable, returnType);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as get command with [{}] as return type", queryable, returnType);

        T ret = get(queryable, returnType, null);
        if (isDebugEnabled)
            LOG.debug("Executed [{}] query  {} rows fetched", queryable.getName(), (ret != null ? "1" : "0"), queryable.getTotal());
        
        return ret;
    }
    
    @Override
    public <T, R> T get(Queryable queryable, ResultRow<T, R> resultRow)
    {
        throw new UnsupportedOperationException("CouchDb Repository doesn't implement this method yet!");
//        notNull.verify(queryable, resultRow);
//        if (isTraceEnabled)
//            LOG.trace("Executing [{}] as get command", queryable);
//        
//        T ret = get(queryable, null, resultRow);
//        
//        if (isDebugEnabled)
//            LOG.debug("Executed [{}] query  {} rows fetched", queryable.getName(), (ret != null ? "1" : "0"), queryable.getTotal());
//        
//        return ret;
//        
    }
    
    @Override
    public <T> T get(T object)
    {
        notNull.verify(object);
        if (isTraceEnabled)
            LOG.trace("Executing as get command with object [{}]", object);
        
        Queryable queryable = QueryFactory.newInstance("get", object);
        T ret = (T) get(queryable, object.getClass(), null);
        
        if (isDebugEnabled)
            LOG.debug("Executed [{}] query  {} rows fetched", queryable.getName(), (ret != null ? "1" : "0"), queryable.getTotal());
        
        return ret;
    }
    
    @Override
    public <T> T get(Class<T> returnType, Object object)
    {
        notNull.verify(object);
        if (isTraceEnabled)
            LOG.trace("Executing as get command with object [{}]", object);
        
        Queryable queryable = QueryFactory.newInstance("get", object);
        T ret = (T) get(queryable, returnType, null);
        
        if (isDebugEnabled)
            LOG.debug("Executed [{}] query  {} rows fetched", queryable.getName(), (ret != null ? "1" : "0"), queryable.getTotal());
        
        return ret;    }

    private <T, R> T get(Queryable queryable, Class<T> returnType, ResultRow<T, R> resultRow)
    {
        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.SELECT);
        isql.getValidateType().assertValidate(queryable.getParams());
        queryable.setReturnType(returnType);        
        
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        Command command = factoryConnection.asSelectCommand(queryable, null);
        T ret = command.execute();
        return ret;    
    }

    @Override
    public <T> T scalar(Queryable queryable)
    {
        throw new UnsupportedOperationException("CouchDb Repository doesn't implement this method yet!");
    }

    @Override
    public boolean enrich(Queryable queryable)
    {
        throw new UnsupportedOperationException("CouchDb Repository doesn't implement this method yet!");
    }
    
    @Override
    public <T> List<T> list(Queryable queryable)
    {
        return list(queryable, null, null);
    }
    
    @Override
    public <T> List<T> list(Queryable queryable, Class<T> returnType)
    {
        return list(queryable, returnType, null);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T, R> List<T> list(Queryable queryable, ResultRow<T, R> overloadResultRow)
    {
        throw new UnsupportedOperationException("CouchDb Repository doesn't implement this method yet!");
        //return list(queryable, null, overloadResultRow);
    }
    
    @SuppressWarnings("unchecked")
    private <T, R> List<T> list(Queryable queryable, Class<T> overloadReturnType, ResultRow<T, R> overloadResultRow)
    {
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as list command", queryable);
        
        List<T> list = Collections.emptyList();
        
        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.SELECT);
        isql.getValidateType().assertValidate(queryable.getParams());
        queryable.setReturnType(overloadReturnType);
        
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        Command command = factoryConnection.asSelectCommand(queryable, overloadResultRow);
        
        list = command.execute();
        
        if (isDebugEnabled)
            LOG.debug("Executed [{}] query, {} rows fetched", queryable.getName(), list.size());
        
        return list;
        
    }

    @Override
    public int add(Queryable queryable)
    {
        notNull.verify(queryable);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as add command with dialect [{}]", queryable, CouchDbDialect.class);
        
        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.INSERT);
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        isql.getValidateType().assertValidate(queryable.getParams());
        
        Command command = factoryConnection.asAddCommand(queryable, null);
        //StatementAdapter<Number, String> adapterStmt = factoryConnection.newStatement(queryable);
        //queryable.bind(adapterStmt).on();
        int affected = command.execute();
        
        if (isDebugEnabled)
            LOG.debug("{} records was affected by add [{}] query", affected, queryable.getName());
        return affected;
    }
    
    @Override
    public <T> T add(T entity)
    {
        notNull.verify(entity);
        Queryable queryable = QueryFactory.newInstance("add", entity);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as add command", queryable);
        
        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.INSERT);
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        isql.getValidateType().assertValidate(queryable.getParams());
        
        Command command = factoryConnection.asAddCommand(queryable, null);
        //StatementAdapter<Number, String> adapterStmt = factoryConnection.newStatement(queryable);
        //queryable.bind(adapterStmt).on();
        int affected = command.execute();
        
        if (isDebugEnabled)
            LOG.debug("{} records was affected by add [{}] query", affected, queryable.getName());
        return entity;
    }
    
    @Override
    public int update(Queryable queryable)
    {
        notNull.verify(queryable);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as update query", queryable);
        
        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.UPDATE);
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        isql.getValidateType().assertValidate(queryable.getParams());
        
        Command command = factoryConnection.asUpdateCommand(queryable, null);
        int affected = command.execute();
        
        if (isDebugEnabled)
            LOG.debug("{} records was affected by update [{}] query", affected, queryable.getName());
        return affected;
    }
    
    @Override
    public <T> T update(T entity)
    {
        notNull.verify(entity);
        Queryable queryable = QueryFactory.newInstance("update", entity);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as update query", queryable);
        
        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.UPDATE);
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        isql.getValidateType().assertValidate(queryable.getParams());
        
        Command command = factoryConnection.asUpdateCommand(queryable, null);
        int affected = command.execute();
        
        if (isDebugEnabled)
            LOG.debug("{} records was affected by update [{}] query", affected, queryable.getName());
        return entity;
    }
    
    @Override
    public int remove(Queryable queryable)
    {
        notNull.verify(queryable);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as remove query", queryable);
        
        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.DELETE);
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        isql.getValidateType().assertValidate(queryable.getParams());
        
        Command command = factoryConnection.asDeleteCommand(queryable, null);
        int affected = command.execute();
        
        if (isDebugEnabled)
            LOG.debug("{} records was affected by remove [{}] query", affected, queryable.getName());
        return affected;
    }
    
    @Override
    public <T> int remove(T entity)
    {
        notNull.verify(entity);
        Queryable queryable = QueryFactory.newInstance("remove", entity);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as remove query", queryable);
        
        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.DELETE);
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        isql.getValidateType().assertValidate(queryable.getParams());
        
        Command command = factoryConnection.asDeleteCommand(queryable, null);
        int affected = command.execute();
        
        if (isDebugEnabled)
            LOG.debug("{} records was affected by remove [{}] query", affected, queryable.getName());
        return affected;    
    }
    
    @Override
    public void flush()
    {
        throw new UnsupportedOperationException("CouchDb Repository doesn't implement this method yet!");
    }
    
    @Override
    public Transactional getTransaction()
    {
        throw new UnsupportedOperationException("CouchDb Repository doesn't implement this method yet!");
    }
    
    @Override
    public void close()
    {
        try
        {
            factoryConnection.close();
        }
        catch (SQLException e)
        {
            LOG.warn("Error to try close Cassandra session/cluster [{}]", factoryConnection, e);
        }
    }
    
    private void checkSqlType(Sql isql, SqlType expected)
    {
        if (isql.getSqlType() != expected)
            throw new IllegalArgumentException("Cannot execute sql [" + isql.getName() + "] as [" + isql.getSqlType()
                    + "], exptected is " + expected);
    }

    private Properties lookup(String remaining)
    {
        Properties prop = null;
        Object resource = JndiResources.lookup(remaining);
        if (resource != null)
        {
            if (resource instanceof Properties)
                prop = (Properties) resource;
            else
                throw new RepositoryException("Resource with name [" + remaining
                        + "] must be an instance of java.util.Properties to connect with CouchDb");
          //TODO exception design, must have ConfigurationException?
        }
        return prop;
    }

    
    //    /**
    //     * Summarize the columns from SQL result in binary data or not.
    //     * @param metadata  object that contains information about the types and properties of the columns in a <code>ResultSet</code> 
    //     * @return Array of columns with name and index
    //     */
    //    @SuppressWarnings("unchecked")
    //    private JdbcColumn<Row>[] getJdbcColumns(ColumnDefinitions metadata)
    //    {
    //        JdbcColumn<Row>[] columns = new JdbcColumn[metadata.size()];
    //        
    //        for (int i = 0; i < columns.length; i++)
    //        {
    //            //int columnNumber = i + 1;
    //            
    //            String columnName = metadata.getName(i);//getColumnName(metadata, columnNumber);
    //            int columnType = metadata.getType(i).getName().ordinal(); //metadata.getColumnType(columnNumber);
    //            //boolean binaryData = false;
    //            //if (columnType == Types.CLOB || columnType == Types.BLOB)
    //            //    binaryData = true;
    //            columns[i] = new CouchDbColumn(i, columnName, columnType);
    //        }
    //        return columns;
    //    }
    
}
