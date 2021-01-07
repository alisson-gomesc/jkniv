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
package net.sf.jkniv.whinstone.cassandra;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.UnsupportedDslOperationException;
import net.sf.jkniv.whinstone.cassandra.dialect.CassandraDialect;
import net.sf.jkniv.whinstone.commands.CommandAdapter;
import net.sf.jkniv.whinstone.commands.CommandHandler;
import net.sf.jkniv.whinstone.commands.CommandHandlerFactory;
import net.sf.jkniv.whinstone.transaction.Transactional;
import net.sf.jkniv.whinstone.types.Convertible;
import net.sf.jkniv.whinstone.types.RegisterType;

/**
 * Encapsulate the data access for Cassandra
 * 
 * @author Alisson Gomes
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
class RepositoryCassandra implements Repository
{
    private static final Logger       LOG      = LoggerFactory.getLogger(RepositoryCassandra.class);
    private static final Assertable   NOT_NULL = AssertsFactory.getNotNull();
    private QueryNameStrategy         strategyQueryName;
    private final HandleableException handlerException;
    private RepositoryConfig          repositoryConfig;
    private SqlContext                sqlContext;
    private CommandAdapter            cmdAdapter;
    private final RegisterType        registerType;
    private boolean                   isTraceEnabled;
    private boolean                   isDebugEnabled;
    
    RepositoryCassandra()
    {
        this(new Properties(), SqlContextFactory.newInstance("/repository-sql.xml"));
    }
    
    RepositoryCassandra(Properties props)
    {
        this(props, SqlContextFactory.newInstance("/repository-sql.xml"));
    }
    
    RepositoryCassandra(String sqlContext)
    {
        this(new Properties(), SqlContextFactory.newInstance(sqlContext));
    }
    
    RepositoryCassandra(SqlContext sqlContext)
    {
        this(new Properties(), sqlContext);
    }
    
    RepositoryCassandra(Properties props, SqlContext sqlContext)
    {
        NOT_NULL.verify(props, sqlContext);
        if (props.isEmpty() || 
                (!props.containsKey(RepositoryProperty.JDBC_URL.key()) &&
                !props.containsKey(RepositoryProperty.KEY_FILE.key()))
                )
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
        
        this.registerType = new RegisterType();
        this.handlerException = new HandlerException(RepositoryException.class, "Cassandra Error cannot execute SQL [%s]");
        this.sqlContext = sqlContext;
        this.repositoryConfig = this.sqlContext.getRepositoryConfig();
        this.repositoryConfig.add(RepositoryProperty.SQL_DIALECT.key(), CassandraDialect.class.getName());
        this.sqlContext.setSqlDialect(this.repositoryConfig.getSqlDialect());
        this.isDebugEnabled = LOG.isDebugEnabled();
        this.isTraceEnabled = LOG.isTraceEnabled();
        this.settingProperties();
        this.cmdAdapter = new CassandraSessionFactory(sqlContext.getRepositoryConfig(),
                sqlContext.getName(), registerType, handlerException).open();
        this.configQueryNameStrategy();
    }
    
    @Override
    public <T> T get(Queryable queryable)
    {
        NOT_NULL.verify(queryable);
        T ret = handleGet(queryable, null, null);
        return ret;
    }
    
    @Override
    public <T> T get(Queryable queryable, Class<T> returnType)
    {
        NOT_NULL.verify(queryable, returnType);
        T ret = handleGet(queryable, null, returnType);
        return ret;
    }
    
    @Override
    public <T, R> T get(Queryable queryable, ResultRow<T, R> customResultRow)
    {
        return handleGet(queryable, customResultRow, null);
    }
    
    @Override
    public <T> T get(T entity)
    {
        NOT_NULL.verify(entity);
        String queryName = this.strategyQueryName.toGetName(entity);
        Queryable queryable = QueryFactory.of(queryName, entity.getClass(), entity);
        T ret = (T) handleGet(queryable, null, null);
        return ret;
    }
    
    @Override
    public <T> T get(Class<T> returnType, Object entity)
    {
        NOT_NULL.verify(entity);
        String queryName = this.strategyQueryName.toGetName(entity);
        Queryable queryable = QueryFactory.of(queryName, returnType, entity);
        T ret = (T) handleGet(queryable, null, null);
        return ret;
    }
    
    @Override
    public <T> T scalar(Queryable queryable)
    {
        NOT_NULL.verify(queryable);
        T result = null;
        Map map = get(queryable, Map.class);
        if (map != null)
        {
            if (map.size() > 1)
                throw new NonUniqueResultException("Query [" + queryable.getName()
                        + "] no return scalar value, scalar function must return unique row and column");
            result = (T) map.values().iterator().next();
        }
        return result;
    }
    
    @Override
    public boolean enrich(Queryable queryable)
    {
        NOT_NULL.verify(queryable, queryable.getParams());
        boolean enriched = false;
        Object o = get(queryable);
        if (o != null)
        {
            ObjectProxy<?> proxy = ObjectProxyFactory.of((Object)queryable.getParams());
            proxy.merge(o);
            enriched = true;
        }
        return enriched;
    }
    
    @Override
    public <T> List<T> list(Queryable queryable)
    {
        return handleList(queryable, null, null);
    }
    
    @Override
    public <T> List<T> list(Queryable queryable, Class<T> overloadReturnType)
    {
        List<T> list = Collections.emptyList();
        list = handleList(queryable, null, overloadReturnType);
        if (isDebugEnabled)
            LOG.debug("Executed [{}] query, {} rows fetched", queryable.getName(), list.size());
        
        return list;
    }
    
    @Override
    public <T, R> List<T> list(Queryable queryable, ResultRow<T, R> customResultRow)
    {
        return handleList(queryable, customResultRow, null);
    }
    
    private <T, R> List<T> handleList(Queryable queryable, ResultRow<T, R> overloadResultRow, Class<T> overloadReturnType)
    {
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as list command", queryable);
        
        Queryable queryableCloned = QueryFactory.clone(queryable, registerType, overloadReturnType); 
        Sql sql = sqlContext.getQuery(queryableCloned.getName());
        CommandHandler handler = CommandHandlerFactory.ofSelect(this.cmdAdapter);
        List<T> list = handler.with(queryableCloned)
                              .with(sql)
                              .checkSqlType(SqlType.SELECT)
                              .with(handlerException)
                              .with(overloadResultRow)
                              .run();
        if (isDebugEnabled)
            LOG.debug("Executed [{}] query, {} rows fetched", queryableCloned.getName(), list.size());
        copy(queryable, queryableCloned);
        return list;
    }
    
    private <T, R> T handleGet(Queryable queryable, ResultRow<T, R> overloadResultRow, Class<T> overloadReturnType)
    {
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as get command", queryable);
        
        T ret = null;
        Queryable queryableCloned = QueryFactory.clone(queryable, registerType, overloadReturnType); 
        Sql sql = sqlContext.getQuery(queryableCloned.getName());
        CommandHandler handler = CommandHandlerFactory.ofSelect(this.cmdAdapter);
        List<T> list = handler.with(queryableCloned)
                              .with(sql)
                              .checkSqlType(SqlType.SELECT)
                              .with(handlerException)
                              .with(overloadResultRow)
                              .run();
        if (list.size() > 1)
            throw new NonUniqueResultException(
                    "No unique result for query [" + queryableCloned.getName() + "] with params [" + queryableCloned.getParams() + "]");
        else if (list.size() == 1)
            ret = list.get(0);
        
        if (isDebugEnabled)
            LOG.debug("Executed [{}] query, fetched object {}", queryableCloned.getName(), ret);
        
        copy(queryable, queryableCloned);
        return ret;
    }
    
    @Override
    public int add(Queryable queryable)
    {
        NOT_NULL.verify(queryable);
        Queryable queryableCloned = QueryFactory.clone(queryable, registerType);
        Sql sql = sqlContext.getQuery(queryableCloned.getName());
        CommandHandler handler = CommandHandlerFactory.ofAdd(this.cmdAdapter);
        int rows = handler.with(queryableCloned).with(sql).checkSqlType(SqlType.INSERT).with(handlerException).run();
        copy(queryable, queryableCloned);
        return rows;
    }
    
    @Override
    public <T> int add(T entity)
    {
        NOT_NULL.verify(entity);
        String queryName = this.strategyQueryName.toAddName(entity);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as add command", queryName);
        
        Queryable queryable = QueryFactory.of(queryName, registerType, entity);
        Sql sql = sqlContext.getQuery(queryable.getName());
        CommandHandler handler = CommandHandlerFactory.ofAdd(this.cmdAdapter);
        int rows = handler
            .with(queryable)
            .with(sql)
            .checkSqlType(SqlType.INSERT)
            .with(handlerException)
            .run();
        return rows;
    }
    
    @Override
    public int update(Queryable queryable)
    {
        NOT_NULL.verify(queryable);
        Queryable queryableCloned = QueryFactory.clone(queryable, registerType);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as update command", queryableCloned);
        Sql sql = sqlContext.getQuery(queryableCloned.getName());
        CommandHandler handler = CommandHandlerFactory.ofUpdate(this.cmdAdapter);
        int rows = handler.with(queryableCloned).with(sql).checkSqlType(SqlType.UPDATE).with(handlerException).run();
        copy(queryable, queryableCloned);
        return rows;
    }
    
    @Override
    public <T> T update(T entity)
    {
        NOT_NULL.verify(entity);
        String queryName = this.strategyQueryName.toUpdateName(entity);
        Queryable queryable = QueryFactory.of(queryName, registerType, entity);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as update command", queryable);
        Sql sql = sqlContext.getQuery(queryable.getName());
        CommandHandler handler = CommandHandlerFactory.ofUpdate(this.cmdAdapter);
        int rows = handler.with(queryable).with(sql).checkSqlType(SqlType.UPDATE).with(handlerException).run();
        return entity;
    }
    
    @Override
    public int remove(Queryable queryable)
    {
        NOT_NULL.verify(queryable);
        Queryable queryableCloned = QueryFactory.clone(queryable, registerType);
        Sql sql = sqlContext.getQuery(queryableCloned.getName());
        CommandHandler handler = CommandHandlerFactory.ofRemove(this.cmdAdapter);
        int rows = handler.with(queryableCloned).with(sql).checkSqlType(SqlType.DELETE).with(handlerException).run();
        copy(queryable, queryableCloned);
        return rows;
    }
    
    @Override
    public <T> int remove(T entity)
    {
        NOT_NULL.verify(entity);
        String queryName = this.strategyQueryName.toRemoveName(entity);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as remove command", queryName);
        Queryable queryable = QueryFactory.of(queryName, registerType, entity);
        Sql sql = sqlContext.getQuery(queryable.getName());
        CommandHandler handler = CommandHandlerFactory.ofRemove(this.cmdAdapter);
        int rows = handler
                    .with(queryable)
                    .with(sql)
                    .checkSqlType(SqlType.DELETE)
                    .with(handlerException)
                    .run();
        return rows;
    }
    
    @Override
    public void flush()
    {
        // TODO implements Repository.flush()
        throw new UnsupportedOperationException("RepositoryCassandra doesn't implement this method yet!");
    }
    
    @Override
    public boolean containsQuery(String name)
    {
        return sqlContext.containsQuery(name);
    }
    
    @Override
    public Transactional getTransaction()
    {
        // TODO implements Repository.getTransaction()
        throw new UnsupportedOperationException("RepositoryCassandra doesn't implement this method yet!");
    }
    
    @Override
    public void close()
    {
        if (this.cmdAdapter instanceof CassandraCommandAdapter)
            ((CassandraCommandAdapter) cmdAdapter).shutdown();

        sqlContext.close();
    }
    
    @Override
    public <T> T dsl()
    {
        throw new UnsupportedDslOperationException("Cassandra Repository does not support DSL operation");
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
                        + "] must be an instance of java.util.Properties to connect with Cassandra");
            //TODO exception design, must have ConfigurationException?
        }
        return prop;
    }
    
    private void settingProperties()
    {
        Properties props = this.repositoryConfig.getProperties();
        Enumeration<Object> keys = props.keys();
        while (keys.hasMoreElements())
        {
            String k = keys.nextElement().toString();
            if (k.startsWith("jkniv.repository.type."))
                configConverters(k, props);                
        }
    }
    private void configConverters(String k, Properties props)
    {
        String className = String.valueOf(props.get(k));// (22) -> "jkniv.repository.type."
        ObjectProxy<Convertible> proxy = ObjectProxyFactory.of(className);
        registerType.register(proxy.newInstance());
    }
    
    private void configQueryNameStrategy()
    {
        String nameStrategy = repositoryConfig.getQueryNameStrategy();
        ObjectProxy<? extends QueryNameStrategy> proxy = ObjectProxyFactory.of(nameStrategy);
        this.strategyQueryName = proxy.newInstance();
    }
    
    
    private void copy(Queryable original, Queryable clone)
    {
        original.setTotal(clone.getTotal());
        original.setBookmark(clone.getBookmark());
        if (clone.isCached())
            original.cached();
    }
}
