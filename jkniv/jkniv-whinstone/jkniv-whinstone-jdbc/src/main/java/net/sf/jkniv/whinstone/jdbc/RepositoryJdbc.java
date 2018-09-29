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
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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
import net.sf.jkniv.sqlegance.Selectable;
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
import net.sf.jkniv.whinstone.jdbc.transaction.Work;
import net.sf.jkniv.whinstone.transaction.Transactional;

/**
 * Repository pattern implementation for JDBC API.
 * 
 * @author Alisson Gomes
 *
 */
class RepositoryJdbc implements Repository
{
    private static final Logger     LOG     = LoggerFactory.getLogger(RepositoryJdbc.class);
    private static final Assertable notNull = AssertsFactory.getNotNull();
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

        this.handlerException = new HandlerException(RepositoryException.class, "JDBC Error cannot execute SQL [%s]");
        this.sqlContext = sqlContext;
        
        this.repositoryConfig = this.sqlContext.getRepositoryConfig();
        checkTransactionSupports();
        this.repositoryConfig.add(props);
        
        if (ds == null)
            ds = lookupDataSource(repositoryConfig.getJndiDataSource());
        
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
        this.isDebugEnabled = LOG.isDebugEnabled();
        this.isTraceEnabled = LOG.isTraceEnabled();
        defineQueryNameStrategy();
        if (LOG.isInfoEnabled())
            LOG.info("Repository JDBC was started with context [{}] using [{}] as JDBC Adapter", this.sqlContext.getName(), this.connectionFactory.getClass().getName());
        if (isDebugEnabled)
            showMetadata();
        
    }

    private Work currentWork()
    {
        return SessionFactory.currentWork(connectionFactory, this.repositoryConfig);
    }
    
    /*
     * (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.Repository#get(net.sf.jkniv.sqlegance.Queryable)
     */
    public <T> T get(Queryable queryable)
    {
        notNull.verify(queryable);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as get command", queryable);
        Sql isql = sqlContext.getQuery(queryable.getName());
        
        checkSqlType(isql, SqlType.SELECT);
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        List<T> list = currentWork().select(queryable, null, null);
        T ret = null;
        if (list.size() > 1)
            throw new NonUniqueResultException("No unique result for query ["+queryable.getName()+"]");
        
        else if (list.size() == 1)
            ret = list.get(0);
        
        if (isDebugEnabled)
            LOG.debug("Executed [{}] query, {}/{} rows fetched", queryable.getName(), list.size(), queryable.getTotal());
        return ret;
    }

    @SuppressWarnings("unchecked")
    public <T, R> T get(Queryable queryable, ResultRow<T, R> resultRow)
    {
        return get(queryable, null, (ResultRow<T, ResultSet>) resultRow);
    }

    public <T> T get(Queryable queryable, Class<T> returnType)
    {
        return get(queryable, returnType, null);
    }
    
    private <T> T get(Queryable queryable, Class<T> returnType, ResultRow<T, ResultSet> resultRow)
    {
        notNull.verify(queryable);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as get command", queryable);

        Sql isql = sqlContext.getQuery(queryable.getName());
        
        checkSqlType(isql, SqlType.SELECT);
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        List<T> list = currentWork().select(queryable, returnType, resultRow);
        T ret = null;
        if (list.size() > 1)
            throw new NonUniqueResultException("No unique result for query ["+queryable.getName()+"]");
        
        else if (list.size() == 1)
            ret = list.get(0);
        
        if (isDebugEnabled)
            LOG.debug("Executed [{}] query, {}/{} rows fetched", queryable.getName(), list.size(), queryable.getTotal());

        return ret;
    }
    
    public <T> T get(T entity)
    {
        return get(null, entity);
    }
    
    public <T> T get(Class<T> returnType, Object entity)
    {
        notNull.verify(entity);
        String queryName = this.strategyQueryName.toGetName(entity);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as get command", queryName);

        Queryable queryable = QueryFactory.of(queryName, entity);
        
        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.SELECT);
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        List<T> list = currentWork().select(queryable, returnType);
        T ret = null;
        if (list.size() > 1)
            throw new NonUniqueResultException("No unique result for query ["+queryable.getName()+"]");
        
        else if (list.size() == 1)
            ret = list.get(0);

        if (isDebugEnabled)
            LOG.debug("Executed [{}] query, {}/{} rows fetched", queryable.getName(), list.size(), queryable.getTotal());

        return ret;
    }

    /*
     * (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.Repository#list(net.sf.jkniv.sqlegance.Queryable)
     */
    public <T> List<T> list(Queryable queryable)
    {
        return list(queryable, null, null);
    }

    @SuppressWarnings("unchecked")
    public <T, R> List<T> list(Queryable queryable, ResultRow<T, R> resultRow)
    {
        return list(queryable, null, (ResultRow<T, ResultSet>) resultRow);
    }

    public <T> List<T> list(Queryable queryable, Class<T> returnType)
    {
        return list(queryable, returnType, null);
    }
    
    private <T,R> List<T> list(Queryable queryable, Class<T> returnType, ResultRow<T, ResultSet> resultRow)
    {
        notNull.verify(queryable);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as list command", queryable);
        
        Selectable isql = sqlContext.getQuery(queryable.getName()).asSelectable();
        checkSqlType(isql, SqlType.SELECT);
        isql.getValidateType().assertValidate(queryable.getParams());
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        List<T> list = currentWork().select(queryable, returnType, resultRow);
        
        if (isDebugEnabled)
            LOG.debug("Executed [{}] query, {}/{} rows fetched", queryable.getName(), list.size(), queryable.getTotal());
        return list;
    }
    
    
    /*
     * (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.Repository#count(net.sf.jkniv.sqlegance.Queryable)
     */
    public <T> T scalar(Queryable queryable)
    {
        notNull.verify(queryable);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as scalar command", queryable);
        T result = null;
        queryable.scalar();
        
        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.SELECT);
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        isql.getValidateType().assertValidate(queryable.getParams());
        
        List<T> list = currentWork().select(queryable);
        if (list.size() == 1)
            result = list.get(0);
        else if (list.size() > 1)// TODO test NonUniqueResultException for scalar method
            throw new NonUniqueResultException("Query ["+queryable.getName()+"] no return scalar value, scalar function must return unique row and column");
            //throw new NonUniqueResultException("Not a single result was generated, scalar function must return unique row and column!");
        
        if (isDebugEnabled)
            LOG.debug("Executed scalar query [{}] retrieving [{}] type of [{}]", queryable.getName(), result, (result !=null ? result.getClass().getName() : "NULL"));
        return result;
    }
    

    /*
     * (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.Repository#add(net.sf.jkniv.sqlegance.Queryable)
     */
    public int add(Queryable queryable)
    {
        notNull.verify(queryable);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as add command with dialect [{}]", queryable, this.repositoryConfig.getSqlDialect());
        
        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.INSERT);
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        isql.getValidateType().assertValidate(queryable.getParams());
        
        int affected = currentWork().execute(queryable);
        if (isDebugEnabled)
            LOG.debug("{} records was affected by add [{}] command", affected, queryable.getName());
        return affected;
    }
    
    public <T> T add(T entity)
    {
        notNull.verify(entity);
        String queryName = this.strategyQueryName.toAddName(entity);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as add command", queryName);
        //LOG.trace("trying query " + queryName);
        Queryable queryable = QueryFactory.of(queryName, entity);
        
        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.INSERT);
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        isql.getValidateType().assertValidate(queryable.getParams());

        int affected = currentWork().execute(queryable);
        if (isDebugEnabled)
            LOG.debug("{} records was affected by add [{}] command", affected, queryable.getName());
        return entity;
    }
    
    public boolean enrich(Queryable queryable) // FIXME test enrich method 
    {
        notNull.verify(queryable, queryable.getParams());
        boolean enriched = false;
        List<Object> list = list(queryable);
        for(Object o : list)
        {
            ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(queryable.getParams());
            proxy.merge(o);
            enriched = true;
        }
        return enriched;
    }
    
    /*
     * (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.Repository#update(net.sf.jkniv.sqlegance.Queryable)
     */
    public int update(Queryable queryable)
    {
        notNull.verify(queryable);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as update command", queryable);

        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.UPDATE);
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        isql.getValidateType().assertValidate(queryable.getParams());

        int affected = currentWork().execute(queryable);
        if (isDebugEnabled)
            LOG.debug("{} records was affected by update [{}] command", affected, queryable.getName());
        return affected;
    }
    
    public <T> T update(T entity)
    {
        notNull.verify(entity);
        String queryName = this.strategyQueryName.toUpdateName(entity);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as update command", queryName);
        //LOG.trace("trying query " + queryName);
        Queryable queryable = QueryFactory.of(queryName, entity);
        
        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.UPDATE);
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        isql.getValidateType().assertValidate(queryable.getParams());

        int affected = currentWork().execute(queryable);
        if (affected > 1)
        {
            LOG.error(
                    "{} records was affected by update command, the query [{}] must update the record using primary key or using unique columns",
                    affected, queryable.getName());
            handlerException
                    .throwMessage("update(T) cannot update more one records, to update several objects use update(Query)");
        }
        
        if (isDebugEnabled)
            LOG.debug("{} records was affected by update [{}] command", affected, queryable.getName());
        
        return entity;
    }
    
    /*
     * (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.Repository#remove(net.sf.jkniv.sqlegance.Queryable)
     */
    public int remove(Queryable queryable)
    {
        notNull.verify(queryable);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as remove command", queryable);

        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.DELETE);
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        isql.getValidateType().assertValidate(queryable.getParams());
        
        int affected = currentWork().execute(queryable);
        if (isDebugEnabled)
            LOG.debug("{} records was affected by remove [{}] command", affected, queryable.getName());
        return affected;
    }
    
    public <T> int remove(T entity)
    {
        notNull.verify(entity);
        String queryName = this.strategyQueryName.toRemoveName(entity);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as remove command", queryName);
        //LOG.trace("trying query " + queryName);
        Queryable queryable = QueryFactory.of(queryName, entity);
        
        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.DELETE);
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        isql.getValidateType().assertValidate(queryable.getParams());

        int affected = currentWork().execute(queryable);
        if (affected > 1)
        {
            LOG.error(
                    "{} records was affected by remove command, the query [{}] must delete records using primary key or set of unique columns",
                    affected, queryable.getName());
            handlerException
                    .throwMessage("remove(T) cannot delete more one records, to remove many objects use remove(Query)");
        }
        
        if (isDebugEnabled)
            LOG.debug("{} records was affected by remove [{}] command", affected, queryable.getName());

        return affected;
    }
    
    /*
     * (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.Repository#flush()
     */
    public void flush()
    {
        // TODO flush implementation
        throw new UnsupportedOperationException("Not Implemented yet");
    }
    
    @Override
    public boolean containsQuery(String name)
    {
        return sqlContext.containsQuery(name);
    }

    
    public Transactional getTransaction()
    {
        return currentWork().getTransaction();
    }
    
    public void close()
    {
        SessionFactory.clear();
        sqlContext.close();
    }
    
    private void defineQueryNameStrategy()
    {
        String nameStrategy = repositoryConfig.getQueryNameStrategy();
        ObjectProxy<? extends QueryNameStrategy> proxy = ObjectProxyFactory.newProxy(nameStrategy);
        this.strategyQueryName = proxy.newInstance();
    }
    
    private void checkTransactionSupports()
    {
        TransactionType type = this.repositoryConfig.getTransactionType();
        if (type == TransactionType.GLOBAL ||
            type == TransactionType.EJB)
            throw new UnsupportedTransactionException("GLOBAL or EJB transaction unsupported");
    }
    
    private DataSource lookupDataSource(String jndi)
    {
        DataSource ds = null;
        Context ctx = null;
        try
        {
            //ctx = (Context) new InitialContext().lookup("java:comp/env");
            ctx = new InitialContext();
            if (LOG.isDebugEnabled())
                LOG.debug("Lookuping JNDI resource with: new InitialContext().lookup(\"{}\") ...", jndi);
            ds = (DataSource) ctx.lookup(jndi);
        }
        catch (NamingException ne)
        {
            LOG.error("NamingException, cannot lookup jndi datasource [" + repositoryConfig.getJndiDataSource() + "]: " + ne.getMessage());
        }
        return ds;
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
    
    private void checkSqlType(Sql isql, SqlType expected)
    {
        if (isql.getSqlType() != expected)
            throw new IllegalArgumentException("Cannot execute sql ["+isql.getName()+"] as ["+isql.getSqlType()+"], exptected is "+ expected);
    }
    
    /*
    private Connection getConnection(ISql isql)
    {
        Connection conn = jdbcConnection.open(isql.getIsolation());
        if (LOG.isTraceEnabled())
        {
            try
            {
                DatabaseMetaData metaDb = conn.getMetaData();
                //metaDb.getDriverVersion()
                LOG.trace("JDBC driver version: {}, name: {}, dbversion: {}",
                        metaDb.getJDBCMajorVersion() + "." + metaDb.getJDBCMinorVersion(), metaDb.getDriverName(),
                        metaDb.getDriverVersion());
            }
            catch (SQLException sqle)
            {
                LOG.warn("Cannot read database metadata: " + sqle.getMessage());
            }
        }
        return conn;
    }
    private int executeUpdate(final Queryable query)
    {
        Query q = (Query) query;
        Connection conn = null;
        PreparedStatement stmt = null;
        int affected = 0;
        try
        {
            ISql isql = XmlBuilderSql.getQuery(q.getName());
            if (isql.getSqlCommandType() == SqlCommandType.SELECT)
                handlerException.throwMessage("SELECT SQL cannot be execute as INSERT | UPDATE | DELETE");
            
            conn = getConnection(isql);
            PreparedStatementStrategy stmtStrategy = getPreparedStatementStrategy(isql, query);
            stmt = stmtStrategy.prepareStatement(conn);
            affected = stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            handlerException.handleUnchecked(e, "Cannot execute SQL reason: " + e.getMessage());// TODO Exception message
        }
        finally
        {
            jdbcConnection.close(conn, stmt, null);
        }
        return affected;
    }
    
    private <T> List<T> executeQuery(final Queryable query)
    {
        Query q = (Query) query;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<T> list = null;
        try
        {
            ISql isql = XmlBuilderSql.getQuery(q.getName());
            conn = getConnection(isql);
            PreparedStatementStrategy stmtStrategy = getPreparedStatementStrategy(isql, query);
            stmt = stmtStrategy.prepareStatement(conn);
            rs = stmt.executeQuery();
            Class<T> clazz = ReflectionUtils.forName(isql.getReturnType());
            
            FlatObjectResultSetParser<T> rsParser = new FlatObjectResultSetParser<T>(clazz);
            list = rsParser.parse(rs);
        }
        catch (SQLException e)
        {
            handlerException.handleUnchecked(e, e.getMessage());// TODO Exception message
        }
        finally
        {
            jdbcConnection.close(conn, stmt, rs);
        }
        return list;
    }
    
    private PreparedStatementStrategy getPreparedStatementStrategy(ISql isql, Queryable q)
    {
        PreparedStatementStrategy stmtStrategy = null;
        String stmtNameDefaultStrategy = XmlBuilderSql.getProperty(RepositoryProperty.PREPARED_STATEMENT_STRATEGY);
        if (stmtNameDefaultStrategy == null || "".equals(stmtNameDefaultStrategy))
        {
            stmtStrategy = new DefaultPreparedStatementStrategy(isql, q.getParams());
        }
        else
        {
            ObjectProxy<PreparedStatementStrategy> proxy = new DefaultObjectProxy<PreparedStatementStrategy>(
                    stmtNameDefaultStrategy);
            stmtStrategy = proxy.createInstance();
        }
        return stmtStrategy;
    }
    */
    
    private ConnectionFactory getJdbcAdapterFactory(
            String adpterClassName,
            Class<? extends ConnectionFactory> defaultClass, 
            Object[] args,
            Class<?>[] types)
    {
        ObjectProxy<? extends ConnectionFactory> factory = null;
        
        if (adpterClassName == null)
            factory = ObjectProxyFactory.newProxy(defaultClass);
        else
            factory = ObjectProxyFactory.newProxy(adpterClassName);
        
        if (args != null)
        {
            factory.setConstructorArgs(args);
            if (types != null)
                factory.setConstructorTypes(types);
        }
        return factory.newInstance();
    }
    
}
