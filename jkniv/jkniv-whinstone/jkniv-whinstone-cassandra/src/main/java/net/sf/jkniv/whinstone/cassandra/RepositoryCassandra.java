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

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.cache.Cacheable;
import net.sf.jkniv.exception.HandleableException;
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
import net.sf.jkniv.sqlegance.Updateable;
import net.sf.jkniv.sqlegance.builder.RepositoryConfig;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.ConnectionAdapter;
import net.sf.jkniv.whinstone.JdbcColumn;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.ResultSetParser;
import net.sf.jkniv.whinstone.cassandra.result.FlatObjectResultRow;
import net.sf.jkniv.whinstone.cassandra.result.MapResultRow;
import net.sf.jkniv.whinstone.cassandra.result.NumberResultRow;
import net.sf.jkniv.whinstone.cassandra.result.ObjectResultSetParser;
import net.sf.jkniv.whinstone.cassandra.result.PojoResultRow;
import net.sf.jkniv.whinstone.cassandra.result.ScalarResultRow;
import net.sf.jkniv.whinstone.cassandra.result.StringResultRow;
import net.sf.jkniv.whinstone.classification.Groupable;
import net.sf.jkniv.whinstone.classification.GroupingBy;
import net.sf.jkniv.whinstone.classification.NoGroupingBy;
import net.sf.jkniv.whinstone.classification.Transformable;
import net.sf.jkniv.whinstone.statement.StatementAdapter;
import net.sf.jkniv.whinstone.transaction.Transactional;

/**
 * Encapsulate the data access for Cassandra
 * 
 * @author Alisson Gomes
 *
 */
class RepositoryCassandra implements Repository
{
    private static final Logger LOG       = LoggerFactory.getLogger(RepositoryCassandra.class);
    private static final Assertable                         notNull = AssertsFactory.getNotNull();
    private QueryNameStrategy   strategyQueryName;
    private HandleableException handlerException;
    private RepositoryConfig    repositoryConfig;
    private SqlContext          sqlContext;
    private ConnectionAdapter   adapterConn;
    private boolean             isTraceEnabled;
    private boolean             isDebugEnabled;
    
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

        this.sqlContext = sqlContext;
        this.repositoryConfig = this.sqlContext.getRepositoryConfig();
        this.isDebugEnabled = LOG.isDebugEnabled();
        this.isTraceEnabled = LOG.isTraceEnabled();
        this.adapterConn = new CassandraSessionFactory(sqlContext.getRepositoryConfig().getProperties()).open();
        this.defineQueryNameStrategy();
    }
    
    @Override
    public <T> T get(Queryable queryable)
    {
        return get(queryable, null, null);
    }
    
    @Override
    public <T> T get(Queryable queryable, Class<T> returnType)
    {
        return get(queryable, returnType, null);
    }
    
    @Override
    public <T, R> T get(Queryable queryable, ResultRow<T, R> resultRow)
    {
        return get(queryable, null, resultRow);
    }
    
    @Override
    public <T> T get(T entity)
    {
        notNull.verify(entity);
        String queryName = this.strategyQueryName.toGetName(entity);
        Queryable queryable = QueryFactory.of(queryName, entity);
        return get(queryable, null, null);
    }
    
    @Override
    public <T> T get(Class<T> returnType, Object entity)
    {
        notNull.verify(returnType, entity);
        String queryName = this.strategyQueryName.toGetName(entity);
        Queryable queryable = QueryFactory.of(queryName, entity);
        return get(queryable, returnType, null);
    }
    
    
    private <T, R> T get(Queryable queryable, Class<T> returnType, ResultRow<T, R> resultRow)
    {
        notNull.verify(queryable);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as get command", queryable);
        
        List<T> list = list(queryable, returnType, resultRow);
        
        T ret = null;
        if (list.size() > 1)
            throw new NonUniqueResultException("No unique result for query ["+queryable.getName()+"]");
            
        else if (list.size() == 1)
            ret = list.get(0);
        
        if (isDebugEnabled)
            LOG.debug("Executed [{}] query, {}/{} rows fetched", queryable.getName(), list.size(),
                    queryable.getTotal());
        
        return ret;
    }
    
    @Override
    public <T> T scalar(Queryable queryable)
    {
        notNull.verify(queryable);
        T result = null;
        Map map = get(queryable, Map.class, null);
        if (map != null)
        {
            if(map.size() > 1)
                throw new NonUniqueResultException("Query ["+queryable.getName()+"] no return scalar value, scalar function must return unique row and column");
            result = (T)map.values().iterator().next();
        }
        return result;
    }
    
    @Override
    public boolean enrich(Queryable queryable)
    {
        notNull.verify(queryable, queryable.getParams());
        boolean enriched = false;
        Object o = get(queryable);
        if(o != null)
        {
            ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(queryable.getParams());
            proxy.merge(o);
            enriched = true;
        }
        return enriched;
    }
    
    @Override
    public <T> List<T> list(Queryable queryable)
    {
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as list command", queryable);
        
        List<T> list = list(queryable, null, null);
        if (isDebugEnabled)
            LOG.debug("Executed [{}] query, {} rows fetched", queryable.getName(), list.size());

        return list;
    }
    
    @Override
    public <T> List<T> list(Queryable queryable, Class<T> returnType)
    {
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as list command", queryable);
        
        List<T> list = list(queryable, returnType, null);
        
        if (isDebugEnabled)
            LOG.debug("Executed [{}] query, {} rows fetched", queryable.getName(), list.size());

        return list;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T, R> List<T> list(Queryable queryable, ResultRow<T, R> overloadResultRow)
    {
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as list command", queryable);

        List<T> list = list(queryable, null, overloadResultRow);
        
        if (isDebugEnabled)
            LOG.debug("Executed [{}] query, {} rows fetched", queryable.getName(), list.size());

        return list;
    }
    
    @SuppressWarnings("unchecked")
    private <T, R> List<T> list(Queryable q, Class<T> overloadReturnType, ResultRow<T, R> overloadResultRow)
    {
        Queryable queryable = QueryFactory.clone(q, overloadReturnType);
        
        List<T> list = Collections.emptyList();
        
        Selectable selectable = sqlContext.getQuery(queryable.getName()).asSelectable();
        
        selectable.getValidateType().assertValidate(queryable.getParams());
        
        if (!queryable.isBoundSql())
            queryable.bind(selectable);

        Cacheable.Entry entry = null;
        
        if(!queryable.isCacheIgnore())
            entry = selectable.getCache().getEntry(queryable);
        
        if (entry == null)
        {
            Command command = adapterConn.asSelectCommand(queryable, overloadResultRow);
            list = command.execute();
            if (selectable.hasCache() && !list.isEmpty())
                selectable.getCache().put(queryable, list);
        }
        else
        {
            if (LOG.isDebugEnabled())
                LOG.debug("{} object(s) was returned from cache [{}] using query [{}] since {}", list.size(),
                        selectable.getCache().getName(), selectable.getName(), entry.getTimestamp());
            list = (List<T>) entry.getValue();
            q.cached();
        }
        q.setTotal(queryable.getTotal());
        
        return list;
    }
    
    @Override
    public int add(Queryable queryable)
    {
        notNull.verify(queryable);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as add command with dialect [{}]", queryable);//, this.repositoryConfig.getSqlDialect());
        
        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.INSERT);
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        isql.getValidateType().assertValidate(queryable.getParams());
        
        Command command = adapterConn.asAddCommand(queryable);
        int affected = command.execute();

        if (isDebugEnabled)
            LOG.debug("{} records was affected by add [{}] command", affected, queryable.getName());
        return affected;
    }
    
    @Override
    public <T> T add(T entity)
    {
        notNull.verify(entity);
        String queryName = this.strategyQueryName.toAddName(entity);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as add command", queryName);
        
        Queryable queryable = QueryFactory.of(queryName, entity);
        
        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.INSERT);
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        isql.getValidateType().assertValidate(queryable.getParams());

        Command command = adapterConn.asAddCommand(queryable);
        int affected = command.execute();

        if (isDebugEnabled)
            LOG.debug("{} records was affected by add [{}] command", affected, queryable.getName());
        return entity;
    }
    
    @Override
    public int update(Queryable queryable)
    {
        notNull.verify(queryable);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as add command with dialect [{}]", queryable);
        
        Updateable updateable = sqlContext.getQuery(queryable.getName()).asUpdateable();
        if (!queryable.isBoundSql())
            queryable.bind(updateable);
        
        updateable.getValidateType().assertValidate(queryable.getParams());
        
        Command command = adapterConn.asUpdateCommand(queryable);
        int affected = command.execute();

        if (isDebugEnabled)
            LOG.debug("{} records was affected by add [{}] command", affected, queryable.getName());
        return affected;
    }
    
    @Override
    public <T> T update(T entity)
    {
        notNull.verify(entity);
        String queryName = this.strategyQueryName.toUpdateName(entity);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as update command", queryName);

        Queryable queryable = QueryFactory.of(queryName, entity);
        
        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.UPDATE);
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        isql.getValidateType().assertValidate(queryable.getParams());

        Command command = adapterConn.asUpdateCommand(queryable);
        int affected = command.execute();

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
    
    @Override
    public int remove(Queryable queryable)
    {
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as remove command", queryable);

        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.DELETE);
        if (!queryable.isBoundSql())
            queryable.bind(isql);

        isql.getValidateType().assertValidate(queryable.getParams());
        
        Command command = adapterConn.asDeleteCommand(queryable);
        int affected = command.execute();

        if (isDebugEnabled)
            LOG.debug("{} records was affected by remove [{}] command", affected, queryable.getName());
        return affected;
    }
    
    @Override
    public <T> int remove(T entity)
    {
        notNull.verify(entity);
        String queryName = this.strategyQueryName.toRemoveName(entity);
        Queryable queryable = QueryFactory.of(queryName, entity);
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as remove command", queryable);
        
        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.DELETE);
        if (!queryable.isBoundSql())
            queryable.bind(isql);

        isql.getValidateType().assertValidate(queryable.getParams());
        
        Command command = adapterConn.asDeleteCommand(queryable);
        int affected = command.execute();

        if (isDebugEnabled)
            LOG.debug("{} records was affected by remove [{}] command", affected, queryable.getName());
        return affected;
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
        try
        {
            adapterConn.close();
        }
        catch (SQLException e)
        {
            LOG.warn("Error to try close Cassandra session/cluster [{}]", adapterConn, e);
        }
        sqlContext.close();
    }
    
    private void checkSqlType(Sql isql, SqlType expected)
    {
        if (isql.getSqlType() != expected)
            throw new IllegalArgumentException("Cannot execute sql [" + isql.getName() + "] as ["
                    + isql.getSqlType() + "], exptected is " + expected);
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
    
    private void defineQueryNameStrategy()
    {
        String nameStrategy = repositoryConfig.getQueryNameStrategy();
        ObjectProxy<? extends QueryNameStrategy> proxy = ObjectProxyFactory.newProxy(nameStrategy);
        this.strategyQueryName = proxy.newInstance();
    }
}
