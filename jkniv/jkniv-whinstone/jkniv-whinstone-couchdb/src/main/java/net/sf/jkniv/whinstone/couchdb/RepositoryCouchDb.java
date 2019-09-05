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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.cache.Cacheable;
import net.sf.jkniv.cache.MemoryCache;
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
import net.sf.jkniv.whinstone.CommandHandler;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.couchdb.commands.CouchDbSynchViewDesign;
import net.sf.jkniv.whinstone.params.ParameterNotFoundException;
import net.sf.jkniv.whinstone.transaction.Transactional;

/**
 * Encapsulate the data access for CouchDB
 * 
 * @author Alisson Gomes
 *
 */
class RepositoryCouchDb implements Repository
{
    private static final Logger               LOG                = LoggerFactory.getLogger(RepositoryCouchDb.class);
    private static final Assertable           notNull            = AssertsFactory.getNotNull();
    private QueryNameStrategy                 strategyQueryName;
    private HandleableException               handlerException;
    private RepositoryConfig                  repositoryConfig;
    private CouchDbSqlContext                 sqlContext;
    private HttpCookieCommandAdapter          cmdAdapter;
    private final static Map<String, Boolean> DOC_SCHEMA_UPDATED = new HashMap<String, Boolean>();
    private boolean                           isTraceEnabled;
    private boolean                           isDebugEnabled;
    private Cacheable<Queryable, Object>      cache;
    
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
        this.cmdAdapter = (HttpCookieCommandAdapter) new HttpConnectionFactory(
                sqlContext.getRepositoryConfig().getProperties(), sqlContext.getName()).open();
        configHanlerException();
        this.cache = new MemoryCache<Queryable, Object>();
        this.init();
    }
    
    private void init()
    {
        String hostContext = cmdAdapter.getHttpBuilder().getHostContext();
        if (!DOC_SCHEMA_UPDATED.containsKey(hostContext))
        {
            // TODO property to config behavior like auto ddl from hibernate
            CouchDbSynchViewDesign _design = new CouchDbSynchViewDesign(this.cmdAdapter.getHttpBuilder(),
                    sqlContext);
            _design.update();
            DOC_SCHEMA_UPDATED.put(hostContext, Boolean.TRUE);
        }
    }
    
    private void configHanlerException()
    {
        this.handlerException = new HandlerException(RepositoryException.class, "CouchDB error at [%s]");
        // ClientProtocolException | JsonParseException | JsonMappingException | IOException
        // FIXME Caused by: java.net.BindException: Address already in use: connect
        this.handlerException.config(ClientProtocolException.class, "Error to HTTP protocol [%s]");
        this.handlerException.config(JsonParseException.class, "Error to parser json non-well-formed content [%s]");
        this.handlerException.config(JsonMappingException.class, "Error to deserialization content [%s]");
        this.handlerException.config(UnsupportedEncodingException.class, "Error at json content encoding unsupported [%s]");
        this.handlerException.config(IOException.class, "Error from I/O json content [%s]");
        this.handlerException.mute(ParameterNotFoundException.class);
    }

    
    @Override
    public <T> T get(Queryable queryable)
    {
        notNull.verify(queryable);
        T ret = handleGet(queryable, null);
        return ret;
    }
    
    @Override
    public <T> T get(Queryable queryable, Class<T> returnType)
    {
        notNull.verify(queryable, returnType);
        Queryable queryableClone = QueryFactory.clone(queryable, returnType);
        T ret = handleGet(queryableClone, null);
        queryable.setTotal(queryableClone.getTotal());
        return ret;
    }
    
    @Override
    public <T> T get(T object)
    {
        notNull.verify(object);
        Queryable queryable = QueryFactory.of("get", object.getClass(), object);
        T ret = (T) handleGet(queryable, null);
        return ret;
    }

    @Override
    public <T> T get(Class<T> returnType, Object object)
    {
        notNull.verify(object);
        Queryable queryable = QueryFactory.of("get", returnType, object);
        T ret = (T) handleGet(queryable, null);
        return ret;
    }

    @Override
    public <T, R> T get(Queryable queryable, ResultRow<T, R> customResultRow)
    {
        return handleGet(queryable, customResultRow);
    }
    
    private <T, R> T handleGet(Queryable q, ResultRow<T, R> overloadResultRow)
    {
        T ret = null;
        Sql sql = sqlContext.getQuery(q.getName());
        CommandHandler handler = new SelectHandler(this.cmdAdapter);
        List<T> list = handler.with(q)
                .with(sql)
                .checkSqlType(SqlType.SELECT)
                .with(handlerException)
                .with(overloadResultRow)
                .run();
        if (list.size() > 1)
            throw new NonUniqueResultException("No unique result for query ["+q.getName()+"] with params ["+q.getParams()+"]");
        else if (list.size() == 1)
            ret = list.get(0);
        
        return ret;
    }
    
    @Override
    public <T> T scalar(Queryable queryable)
    {
        notNull.verify(queryable);
        T result = null;
        Queryable queryableClone = QueryFactory.clone(queryable, Map.class);
        Map<String,T> map = handleGet(queryableClone, null);
        if (map != null)
        {
            if(map.size() > 1)
                throw new NonUniqueResultException("Query ["+queryable.getName()+"] no return scalar value, scalar function must return unique field");
            result = map.values().iterator().next();
            queryable.setTotal(1);
        }
        else
            queryable.setTotal(0);
        return result;
    }
    
    @Override
    public boolean enrich(Queryable queryable)
    {
        notNull.verify(queryable, queryable.getParams());
        boolean enriched = false;
        Object o = handleGet(queryable, null);
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
        return handleList(queryable, null);
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

    @Override
    public <T, R> List<T> list(Queryable queryable, ResultRow<T, R> customResultRow)
    {
        return handleList(queryable, customResultRow);
    }
    
    private <T, R> List<T> handleList(Queryable queryable, ResultRow<T, R> overloadResultRow)
    {
        Sql sql = sqlContext.getQuery(queryable.getName());
        CommandHandler handler = new SelectHandler(this.cmdAdapter);
        List<T> list = handler.with(queryable)
                .with(sql)
                .checkSqlType(SqlType.SELECT)
                .with(handlerException)
                .with(overloadResultRow)
                .run();
        return list;
    }

        
    @Override
    public int add(Queryable queryable)
    {
        notNull.verify(queryable);
        Sql sql = sqlContext.getQuery(queryable.getName());
        CommandHandler handler = new AddHandler(this.cmdAdapter);
        int rows = handler.with(queryable)
                .with(sql)
                .checkSqlType(SqlType.INSERT)
                .with(handlerException)
                .run();
        return rows;
    }
    
    @Override
    public <T> T add(T entity)
    {
        notNull.verify(entity);
        Queryable queryable = QueryFactory.of("add", entity);
        Sql sql = sqlContext.getQuery(queryable.getName());
        CommandHandler handler = new AddHandler(this.cmdAdapter);
        handler.with(queryable)
        .with(sql)
        .checkSqlType(SqlType.INSERT)
        .with(handlerException)
        .run();
        return entity;// FIXME design update must return a number
    }
    
    @Override
    public int update(Queryable queryable)
    {
        notNull.verify(queryable);
        Sql sql = sqlContext.getQuery(queryable.getName()).asUpdateable();
        CommandHandler handler = new UpdateHandler(this.cmdAdapter);
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
        notNull.verify(entity);
        Queryable queryable = QueryFactory.of("update", entity);
        Sql sql = sqlContext.getQuery(queryable.getName()).asUpdateable();
        CommandHandler handler = new UpdateHandler(this.cmdAdapter);
        handler.with(queryable)
        .with(sql)
        .checkSqlType(SqlType.UPDATE)
        .with(handlerException)
        .run();
        return entity;// FIXME design update must return a number 
    }
    
    @Override
    public int remove(Queryable queryable)
    {
        notNull.verify(queryable);
        Sql sql = sqlContext.getQuery(queryable.getName());
        CommandHandler handler = new RemoveHandler(this.cmdAdapter);
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
        notNull.verify(entity);
        Queryable queryable = QueryFactory.of("remove", entity);
        Sql sql = sqlContext.getQuery(queryable.getName());
        CommandHandler handler = new RemoveHandler(this.cmdAdapter);
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
        throw new UnsupportedOperationException("CouchDb Repository doesn't implement this method yet!");
    }
    
    @Override
    public boolean containsQuery(String name)
    {
        return sqlContext.containsQuery(name);
    }
    
    @Override
    public Transactional getTransaction()
    {
        throw new UnsupportedOperationException("CouchDb Repository doesn't supports transaction");
    }
    
    @Override
    public void close()
    {
//        try
//        {
            cmdAdapter.close();
//        }
//        catch (SQLException e)
//        {
//            LOG.warn("Error to try close CouchDB session [{}]", adapterConn, e);
//        }
        sqlContext.close();
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
}
