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
package net.sf.jkniv.whinstone.jdbc;

import java.sql.DatabaseMetaData;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.NonUniqueResultException;
import net.sf.jkniv.sqlegance.QueryNameStrategy;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.builder.RepositoryConfig;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.sqlegance.transaction.TransactionType;
import net.sf.jkniv.whinstone.ConnectionAdapter;
import net.sf.jkniv.whinstone.ConnectionFactory;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.UnsupportedDslOperationException;
import net.sf.jkniv.whinstone.commands.CommandHandler;
import net.sf.jkniv.whinstone.commands.CommandHandlerFactory;
import net.sf.jkniv.whinstone.transaction.Transactional;
import net.sf.jkniv.whinstone.types.CalendarTimestampType;
import net.sf.jkniv.whinstone.types.ConvertibleFactory;
import net.sf.jkniv.whinstone.types.DateTimestampType;
import net.sf.jkniv.whinstone.types.DoubleBigDecimalType;
import net.sf.jkniv.whinstone.types.EnumNameType;
import net.sf.jkniv.whinstone.types.EnumOrdinalType;
import net.sf.jkniv.whinstone.types.LongBigDecimalType;
import net.sf.jkniv.whinstone.types.LongNumericType;
import net.sf.jkniv.whinstone.types.ShortIntType;

/**
 * Repository pattern implementation for JDBC API.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
class RepositoryJdbc implements Repository
{
    private static final Logger     LOG     = LoggerFactory.getLogger(RepositoryJdbc.class);
    private static final Assertable NOT_NULL = AssertsFactory.getNotNull();
    private QueryNameStrategy       strategyQueryName;
    private HandleableException     handlerException;
    private RepositoryConfig        repositoryConfig;
    private ConnectionFactory       connectionFactory;
    private SqlContext              sqlContext;
    private boolean                 isTraceEnabled;
    private boolean                 isDebugEnabled;
    
    RepositoryJdbc()// TODO test constructor
    {
        this(null, SqlContextFactory.newInstance("/repository-sql.xml"), new Properties());
    }
    
    /**
     * Create a repository to connect a database via jdbc.
     * 
     * @param props
     *            info a list of arbitrary string tag/value pairs as connection
     *            arguments; normally at least a "user", "password", "url" and
     *            "driver" property should be included
     */
    RepositoryJdbc(Properties props)
    {
        this(null, SqlContextFactory.newInstance("/repository-sql.xml"), props);
    }

    RepositoryJdbc(Properties props, SqlContext sqlContext)
    {
        this(null, sqlContext, props);
    }
    
    /**
     * Create a repository to connect a database via jdbc.
     * @param sqlContext set of queries 
     * @see javax.sql.DataSource
     * @throws RepositoryException TODO javadoc
     */
    RepositoryJdbc(String sqlContext)// TODO test constructor
    {
        this(null, SqlContextFactory.newInstance(sqlContext), new Properties());
    }
    
    /**
     * Create a repository to connect a database via jdbc.
     * @param sqlContext set of queries 
     * @see javax.sql.DataSource
     * @throws RepositoryException TODO javadoc
     */
    RepositoryJdbc(SqlContext sqlContext)
    {
        this(null, sqlContext, new Properties());
    }
    
    RepositoryJdbc(DataSource datasource)
    {
        this(datasource, SqlContextFactory.newInstance("/repository-sql.xml"), new Properties());
    }

    RepositoryJdbc(DataSource datasource, String sqlContext)// TODO test constructor
    {
        this(datasource, SqlContextFactory.newInstance(sqlContext), new Properties());
    }
    
    RepositoryJdbc(DataSource datasource, Properties props)// TODO test constructor
    {
        this(datasource, SqlContextFactory.newInstance("/repository-sql.xml"), props);
    }

    RepositoryJdbc(DataSource datasource, String sqlContext, Properties props)// TODO test constructor
    {
        this(datasource, SqlContextFactory.newInstance(sqlContext, props), props);
    }

    private RepositoryJdbc(DataSource ds, SqlContext sqlContext, Properties props)
    {
        // TODO refactoy constructor
        //if (ds instanceof BasicDataSource)
        //    LOG.trace("datasource {}, properties {}", ((BasicDataSource)ds).getDriverClassName(), props);
        Object[] args = null;
        Class<?>[] types = null;
        Class<? extends ConnectionFactory> adapterClassFactory = null;

        configureHandlerException();
        configureConverters();
        this.sqlContext = sqlContext;
        
        this.repositoryConfig = this.sqlContext.getRepositoryConfig();
        checkTransactionSupports();
        this.repositoryConfig.add(props);
        
        if (ds == null)
            ds = repositoryConfig.lookup();
        
        String classNameJdbcAdapter = repositoryConfig.getProperty(RepositoryProperty.JDBC_ADAPTER_FACTORY.key());
        
        if (ds == null && repositoryConfig.getProperty(RepositoryProperty.JDBC_URL) != null)
        {
            adapterClassFactory = DriverManagerAdapter.class;
            args = new Object[]{repositoryConfig.getProperties(), repositoryConfig.getName()};
            types = new Class<?>[] {Properties.class, String.class};                        
        }
        else if (classNameJdbcAdapter == null || DataSourceAdapter.class.getName().equals(classNameJdbcAdapter))
        {
            adapterClassFactory = DataSourceAdapter.class;
            args = new Object[]{ds, repositoryConfig.getName()};
            types = new Class<?>[] {DataSource.class, String.class};
        }
        else if (SpringDataSourceAdapter.class.getName().equals(classNameJdbcAdapter))
        {
            adapterClassFactory = SpringDataSourceAdapter.class;
            args = new Object[]{ds, repositoryConfig.getName()};
            types = new Class<?>[] {DataSource.class, String.class};
        }
        else if(DriverManagerAdapter.class.getName().equals(classNameJdbcAdapter))
        {
            adapterClassFactory = DriverManagerAdapter.class;
            args = new Object[]{repositoryConfig.getProperties(), repositoryConfig.getName()};
            types = new Class<?>[] {Properties.class, String.class};            
        }
        this.connectionFactory = getJdbcAdapterFactory(classNameJdbcAdapter, adapterClassFactory, args, types);
        this.connectionFactory.with(handlerException);
        this.isDebugEnabled = LOG.isDebugEnabled();
        this.isTraceEnabled = LOG.isTraceEnabled();
        configQueryNameStrategy();
        if (LOG.isInfoEnabled())
            LOG.info("Repository JDBC was started with context [{}] using [{}] as JDBC Adapter", this.sqlContext.getName(), this.connectionFactory.getClass().getName());
        if (isDebugEnabled) //FIXME show metadata must be have property? counter effect to unit test, there are twice invocation into close connection
            showMetadata();
        
    }

    @Override
    public <T> T get(Queryable queryable)
    {
        NOT_NULL.verify(queryable);
        T ret = handleGet(queryable, null);
        return ret;
    }
    
    @Override
    public <T> T get(Queryable queryable, Class<T> returnType)
    {
        NOT_NULL.verify(queryable, returnType);
        Queryable queryableClone = QueryFactory.clone(queryable, returnType);
        T ret = handleGet(queryableClone, null);
        queryable.setTotal(queryableClone.getTotal());
        return ret;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(T entity)
    {
        NOT_NULL.verify(entity);
        String queryName = this.strategyQueryName.toGetName(entity);
        Queryable queryable = QueryFactory.of(queryName, entity.getClass(), entity);
        T ret = (T) handleGet(queryable, null);
        return ret;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> returnType, Object entity)
    {
        NOT_NULL.verify(entity);
        String queryName = this.strategyQueryName.toGetName(entity);
        Queryable queryable = QueryFactory.of(queryName, returnType, entity);
        T ret = (T) handleGet(queryable, null);
        return ret;
    }
    
    @Override
    public <T, R> T get(Queryable queryable, ResultRow<T, R> customResultRow)
    {
        return handleGet(queryable, customResultRow);
    }


    @Override
    public <T> List<T> list(Queryable queryable)
    {
        return handleList(queryable, null);
    }

    @Override
    public <T, R> List<T> list(Queryable queryable, ResultRow<T, R> customResultRow)
    {
        return handleList(queryable, customResultRow);
    }

    @Override
    public <T> List<T> list(Queryable queryable, Class<T> overloadReturnType)
    {
        List<T> list = Collections.emptyList();
        Queryable queryableClone = queryable;
        if (overloadReturnType != null)
            queryableClone = QueryFactory.clone(queryable, overloadReturnType);
        list = handleList(queryableClone, null);
        queryable.setTotal(queryableClone.getTotal());
        return list;
    }
    
    private <T, R> List<T> handleList(Queryable queryable, ResultRow<T, R> overloadResultRow)
    {
        Sql sql = sqlContext.getQuery(queryable.getName());
        CommandHandler handler = CommandHandlerFactory.ofSelect(this.connectionFactory.open(sql.getIsolation()));
        List<T> list = handler.with(queryable)
                .with(sql)
                .checkSqlType(SqlType.SELECT)
                .with(handlerException)
                .with(overloadResultRow)
                .run();
        return list;
    }
    
    private <T, R> T handleGet(Queryable queryable, ResultRow<T, R> overloadResultRow)
    {
        T ret = null;
        Sql sql = sqlContext.getQuery(queryable.getName());
        CommandHandler handler = CommandHandlerFactory.ofSelect(this.connectionFactory.open(sql.getIsolation()));
        List<T> list = handler.with(queryable)
        .with(sql)
        .checkSqlType(SqlType.SELECT)
        .with(handlerException)
        .with(overloadResultRow)
        .run();
        if (list.size() > 1)
            throw new NonUniqueResultException("No unique result for query ["+queryable.getName()+"] with params ["+queryable.getParams()+"]");
        else if (list.size() == 1)
            ret = list.get(0);
        
        return ret;
    }

    @Override
    public <T> T scalar(Queryable queryable)
    {
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as scalar command", queryable);
        NOT_NULL.verify(queryable);
        T result = null;
        queryable.scalar();
        List<T> list = handleList(queryable, null);

        if (list.size() == 1)
            result = list.get(0);
        else if (list.size() > 1)// TODO test NonUniqueResultException for scalar method
            throw new NonUniqueResultException("Query ["+queryable.getName()+"] no return scalar value, scalar function must return unique row and column");
        
        if (isDebugEnabled)
            LOG.debug("Executed scalar query [{}] retrieving [{}] type of [{}]", queryable.getName(), result, (result !=null ? result.getClass().getName() : "NULL"));
        return result;
    }

    @Override
    public int add(Queryable queryable)
    {
        NOT_NULL.verify(queryable);
        Sql sql = sqlContext.getQuery(queryable.getName());
        CommandHandler handler = CommandHandlerFactory.ofAdd(this.connectionFactory.open(sql.getIsolation()));
        int rows = handler.with(queryable)
                .with(sql)
                .checkSqlType(SqlType.INSERT)
                .with(handlerException)
                .run();
        return rows;
    }
    
    @Override
    public <T> int add(T entity)// FIXME design update must return a number
    {
        NOT_NULL.verify(entity);
        String queryName = this.strategyQueryName.toAddName(entity);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as add command", queryName);

        Queryable queryable = QueryFactory.of(queryName, entity);
        Sql sql = sqlContext.getQuery(queryable.getName());
        CommandHandler handler = CommandHandlerFactory.ofAdd(this.connectionFactory.open(sql.getIsolation()));
        int rows = handler.with(queryable)
            .with(sql)
            .checkSqlType(SqlType.INSERT)
            .with(handlerException)
            .run();
        return rows;
    }

    @Override
    public boolean enrich(Queryable queryable) // FIXME test enrich method 
    {
        NOT_NULL.verify(queryable, queryable.getParams());
        boolean enriched = false;
        Object o = get(queryable);
        if(o != null)
        {
            ObjectProxy<?> proxy = ObjectProxyFactory.of(queryable.getParams());
            proxy.merge(o);
            enriched = true;
        }
        return enriched;
    }
    
    @Override
    public int update(Queryable queryable)
    {
        NOT_NULL.verify(queryable);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as update command", queryable);
        Sql sql = sqlContext.getQuery(queryable.getName());
        CommandHandler handler = CommandHandlerFactory.ofUpdate(this.connectionFactory.open(sql.getIsolation()));
        int rows = handler.with(queryable)
                .with(sql)
                .checkSqlType(SqlType.UPDATE)
                .with(handlerException)
                .run();
        return rows;
    }
    
    @Override
    public <T> T update(T entity)
    {
        NOT_NULL.verify(entity);
        String queryName = this.strategyQueryName.toUpdateName(entity);
        Queryable queryable = QueryFactory.of(queryName, entity);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as update command", queryable);
        Sql sql = sqlContext.getQuery(queryable.getName());
        CommandHandler handler = CommandHandlerFactory.ofUpdate(this.connectionFactory.open(sql.getIsolation()));
        handler.with(queryable)
                .with(sql)
                .checkSqlType(SqlType.UPDATE)
                .with(handlerException)
                .run();
        return entity;
    }
 
    @Override
    public int remove(Queryable queryable)
    {
        NOT_NULL.verify(queryable);
        Sql sql = sqlContext.getQuery(queryable.getName());
        CommandHandler handler = CommandHandlerFactory.ofRemove(this.connectionFactory.open(sql.getIsolation()));
        int rows = handler.with(queryable)
                .with(sql)
                .checkSqlType(SqlType.DELETE)
                .with(handlerException)
                .run();
        return rows;
    }
    
    @Override
    public <T> int remove(T entity)
    {
        NOT_NULL.verify(entity);
        String queryName = this.strategyQueryName.toRemoveName(entity);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as remove command", queryName);
        Queryable queryable = QueryFactory.of(queryName, entity);
        Sql sql = sqlContext.getQuery(queryable.getName());
        CommandHandler handler = CommandHandlerFactory.ofRemove(this.connectionFactory.open(sql.getIsolation()));
        int rows = handler.with(queryable)
                .with(sql)
                .checkSqlType(SqlType.DELETE)
                .with(handlerException)
                .run();
        return rows;
    }
    
    @Override
    public void flush()
    {
        // TODO flush implementation
        throw new UnsupportedOperationException("JDBC Repository does not implements flush operation yet");
    }
    
    @Override
    public boolean containsQuery(String name)
    {
        return sqlContext.containsQuery(name);
    }

    @Override
    public Transactional getTransaction()
    {
        return this.connectionFactory.getTransactionManager();
        //return null;
        //return currentWork().getTransaction();
    }
    
    @Override
    public void close()
    {
        //SessionFactory.clear();
        // FIXME release resources transaction/connection factory
        sqlContext.close();
    }
    
    @Override
    public <T> T dsl()
    {
        throw new UnsupportedDslOperationException("JDBC Repository does not support DSL operation");
    }

    
    private void configQueryNameStrategy()
    {
        String nameStrategy = repositoryConfig.getQueryNameStrategy();
        ObjectProxy<? extends QueryNameStrategy> proxy = ObjectProxyFactory.of(nameStrategy);
        this.strategyQueryName = proxy.newInstance();
    }
    
    private void checkTransactionSupports()
    {
        TransactionType type = this.repositoryConfig.getTransactionType();
        if (type == TransactionType.GLOBAL ||
            type == TransactionType.EJB)
            throw new UnsupportedTransactionException("GLOBAL or EJB transaction unsupported");
    }
    
    private void showMetadata()
    {
        ConnectionAdapter conn = null;
        try
        {
            conn = connectionFactory.open();
            DatabaseMetaData metaDb = (DatabaseMetaData) conn.getMetaData();
            //metaDb.getDriverVersion()
            LOG.trace("JDBC driver version: {}, name: {}, dbversion: {}",
                    metaDb.getJDBCMajorVersion() + "." + metaDb.getJDBCMinorVersion(), metaDb.getDriverName(),
                    metaDb.getDriverVersion());
        }
        catch (Exception sqle)
        {
            LOG.warn("Cannot read database metadata: " + sqle.getMessage());
        }
        finally 
        {
            connectionFactory.close(conn);
        }
    }
    
    private ConnectionFactory getJdbcAdapterFactory(
            String adpterClassName,
            Class<? extends ConnectionFactory> defaultClass, 
            Object[] args,
            Class<?>[] types)
    {
        ObjectProxy<? extends ConnectionFactory> factory = null;
        
        if (adpterClassName == null)
            factory = ObjectProxyFactory.of(defaultClass);
        else
            factory = ObjectProxyFactory.of(adpterClassName);
        
        if (args != null)
        {
            factory.setConstructorArgs(args);
            if (types != null)
                factory.setConstructorTypes(types);
        }
        return factory.newInstance();
    }
    
    private void configureConverters()
    {
        ConvertibleFactory.register(new DateTimestampType());
        ConvertibleFactory.register(new CalendarTimestampType());
        ConvertibleFactory.register(new LongNumericType());
        ConvertibleFactory.register(new LongBigDecimalType());
        ConvertibleFactory.register(new ShortIntType());
        ConvertibleFactory.register(new DoubleBigDecimalType());
        //ConvertibleFactory.register(new EnumOrdinalType());
        //ConvertibleFactory.register(new EnumNameType());
    }
    
    private void configureHandlerException()
    {
        this.handlerException = new HandlerException(RepositoryException.class, "JDBC Error cannot execute SQL [%s]");
    }

}
