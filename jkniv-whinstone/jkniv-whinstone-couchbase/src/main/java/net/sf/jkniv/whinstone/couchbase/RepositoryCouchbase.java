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
package net.sf.jkniv.whinstone.couchbase;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.client.ClientProtocolException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.NonUniqueResultException;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.UnsupportedDslOperationException;
import net.sf.jkniv.whinstone.commands.CommandHandler;
import net.sf.jkniv.whinstone.commands.CommandHandlerFactory;
import net.sf.jkniv.whinstone.couchbase.commands.CouchbaseCommand;
import net.sf.jkniv.whinstone.couchbase.dialect.CouchbaseDialect6o5;
import net.sf.jkniv.whinstone.params.ParameterNotFoundException;
import net.sf.jkniv.whinstone.transaction.Transactional;
import net.sf.jkniv.whinstone.types.Convertible;
import net.sf.jkniv.whinstone.types.RegisterType;

/**
 * Encapsulate the data access for Couchbase
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
class RepositoryCouchbase implements Repository
{
    //private static final Logger               LOG                = LoggerFactory.getLogger(RepositoryCouchbase.class);
    private static final Assertable           NOT_NULL            = AssertsFactory.getNotNull();
    private HandleableException               handlerException;
    private CouchbaseSqlContext               sqlContext;
    private CouchbaseCommandAdapter           cmdAdapter;
    private final RegisterType        registerType;
    //private final static Map<String, Boolean> DOC_SCHEMA_UPDATED = new HashMap<String, Boolean>();
    
    RepositoryCouchbase()
    {
        this(new Properties(), SqlContextFactory.newInstance("/repository-sql.xml"));
    }
    
    RepositoryCouchbase(Properties props)
    {
        this(props, SqlContextFactory.newInstance("/repository-sql.xml"));
    }
    
    RepositoryCouchbase(String sqlContext)
    {
        this(new Properties(), SqlContextFactory.newInstance(sqlContext));
    }
    
    RepositoryCouchbase(SqlContext sqlContext)
    {
        this(new Properties(), sqlContext);
    }
    
    RepositoryCouchbase(Properties props, SqlContext sqlContext)
    {
        NOT_NULL.verify(props, sqlContext);
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
        
        this.registerType = new RegisterType();
        this.sqlContext = new CouchbaseSqlContext(sqlContext);
        this.sqlContext.getRepositoryConfig().add(RepositoryProperty.SQL_DIALECT.key(),
                CouchbaseDialect6o5.class.getName());
        this.sqlContext.setSqlDialect(this.sqlContext.getRepositoryConfig().getSqlDialect());
        this.sqlContext.buildInQueries();
        configHanlerException();
        this.settingProperties();
        this.cmdAdapter = (CouchbaseCommandAdapter) new CouchbaseSessionFactory(
                sqlContext.getRepositoryConfig(), sqlContext.getName(), this.handlerException).open();
    }
    
    private void configHanlerException()
    {
/*
# GET method throws under the following conditions:
 TimeoutException: The operation takes longer than the specified timeout: TimeoutException wrapped in a RuntimeException
 BackpressureException: The producer outpaces the SDK: BackpressureException
 RequestCancelledException: The operation had to be cancelled while on the wire or the retry strategy cancelled it instead of retrying: RequestCancelledException
 TemporaryFailureException: The server is currently not able to process the request, retrying may help: TemporaryFailureException
 CouchbaseOutOfMemoryException: The server is out of memory: CouchbaseOutOfMemoryException
 CouchbaseException: Unexpected errors are caught and contained in a generic CouchbaseException.
 RepositoryMappingException: com.couchbase.client.java.repository.mapping.RepositoryMappingException: Could not instantiate entity.

# INSERT method throws under the following conditions:
 TimeoutException: The operation takes longer than the specified timeout: TimeoutException wrapped in a RuntimeException
 BackpressureException: The producer outpaces the SDK: BackpressureException
 RequestCancelledException: The operation had to be cancelled while on the wire or the retry strategy cancelled it instead of retrying: RequestCancelledException
 DocumentAlreadyExistsException: The original insert failed because the document is already stored: DocumentAlreadyExistsException
 RequestTooBigException: The request content is too big: RequestTooBigException
 DurabilityException: The durability constraint could not be fulfilled because of a temporary or persistent problem: DurabilityException.
 TemporaryFailureException: The server is currently not able to process the request, retrying may help: TemporaryFailureException
 CouchbaseOutOfMemoryException: The server is out of memory: CouchbaseOutOfMemoryException
 CouchbaseException: Unexpected errors are caught and contained in a generic CouchbaseException.
 
CASMismatchException: If the CAS on the server does not match the CAS supplied to the method, the operation will fail with a 
CASMismatchException
  
# UPDATE method throws under the following conditions:

 RuntimeException: The operation takes longer than the specified timeout: TimeoutException wrapped in a RuntimeException
 BackpressureException: The producer outpaces the SDK: BackpressureException
 RequestCancelledException: The operation had to be cancelled while on the wire or the retry strategy cancelled it instead of retrying: RequestCancelledException
 DocumentDoesNotExistException: The original replace failed because the document does not exist: DocumentDoesNotExistException
 RequestTooBigException: The request content is too big: RequestTooBigException
 CASMismatchException: A CAS value was set and it did not match with the server: CASMismatchException
 TemporaryFailureException: The server is currently not able to process the request, retrying may help: TemporaryFailureException
 CouchbaseOutOfMemoryException: The server is out of memory: CouchbaseOutOfMemoryException
 CouchbaseException: Unexpected errors are caught and contained in a generic CouchbaseException.
 
# REMOVE
DocumentDoesNotExistException: The original replace failed because the document does not exist: DocumentDoesNotExistException
           */
        this.handlerException = new HandlerException(RepositoryException.class, "Couchbase error at [%s]");
        // ClientProtocolException | JsonParseException | JsonMappingException | IOException
        // FIXME Caused by: java.net.BindException: Address already in use: connect
        this.handlerException.config(ClientProtocolException.class, "Error to HTTP protocol [%s]");
        this.handlerException.config(JsonParseException.class, "Error to parser json non-well-formed content [%s]");
        this.handlerException.config(JsonMappingException.class, "Error to deserialization content [%s]");
        this.handlerException.config(UnsupportedEncodingException.class,
                "Error at json content encoding unsupported [%s]");
        this.handlerException.config(IOException.class, "Error from I/O json content [%s]");
        this.handlerException.mute(ParameterNotFoundException.class);
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
        Queryable queryableClone = QueryFactory.clone(queryable, returnType);
        T ret = handleGet(queryableClone, null, null);
        queryable.setTotal(queryableClone.getTotal());
        return ret;
    }
    
    @Override
    public <T> T get(T object)
    {
        NOT_NULL.verify(object);
        Queryable queryable = QueryFactory.of("get", object.getClass(), object);
        T ret = handleGet(queryable, null, null);
        return ret;
    }
    
    @Override
    public <T> T get(Class<T> returnType, Object object)
    {
        NOT_NULL.verify(object);
        Queryable queryable = QueryFactory.of("get", returnType, object);
        T ret = handleGet(queryable, null, null);
        return ret;
    }
    
    @Override
    public <T, R> T get(Queryable queryable, ResultRow<T, R> customResultRow)
    {
        return handleGet(queryable, customResultRow, null);
    }
    
    private <T, R> T handleGet(Queryable queryable, ResultRow<T, R> overloadResultRow, Class<T> overloadReturnType)
    {
        T ret = null;
        Queryable queryableCloned = QueryFactory.clone(queryable, registerType, overloadReturnType);
        Sql sql = sqlContext.getQuery(queryableCloned.getName());
        CommandHandler handler = CommandHandlerFactory.ofSelect(this.cmdAdapter);
        List<T> list = handler
                        .with(queryableCloned)
                        .with(sql)
                        .checkSqlType(SqlType.SELECT)
                        .with(handlerException)
                        .with(overloadResultRow)
                        .run();
        if (list.size() > 1)
            throw new NonUniqueResultException(
                    "No unique result for query [" + queryable.getName() + "] with params [" + queryable.getParams() + "]");
        else if (list.size() == 1)
            ret = list.get(0);
        
        copy(queryable, queryableCloned);
        return ret;
    }
    
    @Override
    public <T> T scalar(Queryable queryable)
    {
        NOT_NULL.verify(queryable);
        T result = null;
        Map<String, T> map = handleGet(queryable, null, null);
        if (map != null)
        {
            if (map.size() > 1)
                throw new NonUniqueResultException("Query [" + queryable.getName()
                        + "] no return scalar value, scalar function must return unique field");
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
        NOT_NULL.verify(queryable, queryable.getParams());
        boolean enriched = false;
        Object o = handleGet(queryable, null, null);
        if (o != null)
        {
            ObjectProxy<?> proxy = ObjectProxyFactory.of((Object) queryable.getParams());
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
        NOT_NULL.verify(overloadReturnType);
        List<T> list = handleList(queryable, null, overloadReturnType);
        return list;
    }
    
    @Override
    public <T, R> List<T> list(Queryable queryable, ResultRow<T, R> customResultRow)
    {
        NOT_NULL.verify(customResultRow);
        return handleList(queryable, customResultRow, null);
    }
    
    private <T, R> List<T> handleList(Queryable queryable, ResultRow<T, R> overloadResultRow, Class<T> overloadReturnType)
    {
        Queryable queryableCloned = QueryFactory.clone(queryable, registerType, overloadReturnType);
        Sql sql = sqlContext.getQuery(queryableCloned.getName());
        CommandHandler handler = CommandHandlerFactory.ofSelect(this.cmdAdapter);
        List<T> list = handler.with(queryableCloned)
                              .with(sql)
                              .checkSqlType(SqlType.SELECT)
                              .with(handlerException)
                              .with(overloadResultRow)
                              .run();
        copy(queryable, queryableCloned);
        return list;
    }
    
    @Override
    public int add(Queryable queryable)
    {
        NOT_NULL.verify(queryable);
        Queryable queryableCloned = QueryFactory.clone(queryable, registerType);
        Sql sql = sqlContext.getQuery(queryableCloned.getName());
        CommandHandler handler = CommandHandlerFactory.ofAdd(this.cmdAdapter);
        int rows = handler.with(queryableCloned)
                          .with(sql)
                          .checkSqlType(SqlType.INSERT)
                          .with(handlerException)
                          .run();
        copy(queryable, queryableCloned);
        return rows;
    }
    
    @Override
    public <T> int add(T entity)
    {
        NOT_NULL.verify(entity);
        Queryable queryable = QueryFactory.of("add", registerType, entity);
        Sql sql = sqlContext.getQuery(queryable.getName());
        CommandHandler handler = CommandHandlerFactory.ofAdd(this.cmdAdapter);
        int rows = handler.with(queryable)
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
        Sql sql = sqlContext.getQuery(queryableCloned.getName()).asUpdateable();
        CommandHandler handler = CommandHandlerFactory.ofUpdate(this.cmdAdapter);
        int rows = handler.with(queryableCloned)
                          .with(sql)
                          .checkSqlType(SqlType.UPDATE)
                          .with(handlerException)
                          .run();
        copy(queryable, queryableCloned);
        return rows;
    }
    
    @Override
    public <T> T update(T entity)
    {
        NOT_NULL.verify(entity);
        Queryable queryable = QueryFactory.of(CouchbaseCommand.REPLACE.getCommandName(), registerType, entity);
        Sql sql = sqlContext.getQuery(queryable.getName()).asUpdateable();
        CommandHandler handler = CommandHandlerFactory.ofUpdate(this.cmdAdapter);
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
        Queryable queryableCloned = QueryFactory.clone(queryable, registerType);
        Sql sql = sqlContext.getQuery(queryableCloned.getName());
        CommandHandler handler = CommandHandlerFactory.ofRemove(this.cmdAdapter);
        int rows = handler.with(queryableCloned)
                          .with(sql)
                          .checkSqlType(SqlType.DELETE)
                          .with(handlerException)
                          .run();
        copy(queryable, queryableCloned);
        return rows;
    }
    
    @Override
    public <T> int remove(T entity)
    {
        NOT_NULL.verify(entity);
        Queryable queryable = QueryFactory.of("remove", registerType, entity);
        Sql sql = sqlContext.getQuery(queryable.getName());
        CommandHandler handler = CommandHandlerFactory.ofRemove(this.cmdAdapter);
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
        throw new UnsupportedOperationException("Couchbase Repository doesn't implement this method yet!");
    }
    
    @Override
    public boolean containsQuery(String name)
    {
        return sqlContext.containsQuery(name);
    }
    
    @Override
    public Transactional getTransaction()
    {
        throw new UnsupportedOperationException("Couchbase Repository doesn't supports transaction");
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
        //            LOG.warn("Error to try close Couchbase session [{}]", adapterConn, e);
        //        }
        sqlContext.close();
    }
    
    @Override
    public <T> T dsl()
    {
//        SearchQuery sq = null;
//        Bucket b = null;
//        b.core().ctx().
        throw new UnsupportedDslOperationException("Couchbase Repository does not support DSL operation");
    }

    private void settingProperties()
    {
        Properties props = this.sqlContext.getRepositoryConfig().getProperties();
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
                        + "] must be an instance of java.util.Properties to connect with Couchbase");
            //TODO exception design, must have ConfigurationException?
        }
        return prop;
    }
    
    private void copy(Queryable original, Queryable clone)
    {
        original.setTotal(clone.getTotal());
        original.setBookmark(clone.getBookmark());
        if (clone.isCached())
            original.cached();
    }

}
