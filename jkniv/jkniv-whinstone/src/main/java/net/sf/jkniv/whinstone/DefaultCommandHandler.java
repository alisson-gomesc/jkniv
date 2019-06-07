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
package net.sf.jkniv.whinstone;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.builder.RepositoryConfig;

public abstract class DefaultCommandHandler implements CommandHandler
{
    final static Logger                              LOG = LoggerFactory.getLogger(DefaultCommandHandler.class);
    static final Assertable                          NOT_NULL = AssertsFactory.getNotNull();
    private final static Map<String, ObjectCallback> OBJECTS_CALLBACKS = new HashMap<String, ObjectCallback>();
    ConnectionAdapter                                adapterConn;
    CommandHandler                                   handler;
    Command                                          command;
    ObjectProxy<?>                                   proxyParams;
    protected Queryable                              queryable;
    protected Sql                                    sql;
    protected RepositoryConfig                       config;
    protected ResultRow<?, ?>                        overloadResultRow;
    protected HandleableException                    handleableException;
    private   Class<?>                               returnType;
    
    public DefaultCommandHandler(ConnectionAdapter conn)
    {
        this.adapterConn = conn;
    }
    
    @Override
    public CommandHandler with(CommandHandler handler)
    {
        this.handler = handler;
        return this;
    }
    
    @Override
    public CommandHandler with(ResultRow<?, ?> overloadResultRow)
    {
        this.overloadResultRow = overloadResultRow;
        return this;
    }
    
    @Override
    public CommandHandler with(Queryable queryable)
    {
        this.queryable = queryable;
        if (queryable.getParams() != null)
            this.proxyParams = ObjectProxyFactory.newProxy(queryable.getParams());
        loadCallbackEvents();
        return this;
    }
    
    @Override
    public CommandHandler with(Sql sql)
    {
        this.sql = sql;
        return this;
    }
    
//    @Override
//    public CommandHandler returnType(Class<?> returnType)
//    {
//        this.returnType = returnType;
//        return this;
//    }
    
    @Override
    public CommandHandler with(RepositoryConfig repositoryConfig)
    {
        this.config = repositoryConfig;
        return this;
    }
    
    public CommandHandler with(HandleableException handlerException)
    {
        this.handleableException = handlerException;
        return this;
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public <T> T run()
    {
        NOT_NULL.verify(this.adapterConn, this.queryable, this.sql, this.handler);
        if (LOG.isTraceEnabled())
            LOG.trace("Executing [{}] as {} command", queryable, sql.getSqlType());
        
        sql.getValidateType().assertValidate(queryable.getParams());
        
        if (!queryable.isBoundSql())
            queryable.bind(sql);
        
        preCallback();
        this.command = handler.asCommand();
        T t = this.command.execute();
        postCallback();
        if (LOG.isDebugEnabled())
        {
            Number rows = null;
            if (t instanceof List)
                rows = ((List) t).size();
            else if (t instanceof Number)
                rows = (Number) t;
            
            LOG.debug("{} records was affected by {} [{}] query", rows, sql.getSqlType(), queryable.getName());
        }
        return t;
    }
    
    protected ConnectionAdapter getConnectionAdapter()
    {
        return this.adapterConn;
    }
    
    @Override
    public CommandHandler preCallback()
    {
        if (proxyParams != null)
        {
            ObjectCallback objectCallback = OBJECTS_CALLBACKS.get(proxyParams.getTargetClass().getName());
            if (objectCallback != null)
            {
                Set<Method> methods = objectCallback.getPreMethods(sql.getSqlType());
                for (Method m : methods)
                    proxyParams.invoke(m);
            }
        }
        return this;
    }
    
    @Override
    public CommandHandler postCallback()
    {
        if (proxyParams != null)
        {
            ObjectCallback objectCallback = OBJECTS_CALLBACKS.get(proxyParams.getTargetClass().getName());
            if (objectCallback != null)
            {
                Set<Method> methods = objectCallback.getPostMethods(sql.getSqlType());
                for (Method m : methods)
                    proxyParams.invoke(m);
            }
        }
        return this;
    }
    
    @Override
    public CommandHandler postCommit()
    {
        //        if (proxyParams != null)
        //        {
        //            CallbackMethods preCallbackMethods = CacheCallback.get(proxyParams.getTargetClass(), sql.getSqlType());
        //            for (Method m : preCallbackMethods.getCallbacks())
        //                proxyParams.invoke(m);
        //        }
        return this;
    }
    
    @Override
    public CommandHandler postException()
    {
        //        if (proxyParams != null)
        //        {            
        //            CallbackMethods preCallbackMethods = CacheCallback.get(proxyParams.getTargetClass(), sql.getSqlType());
        //            for (Method m : preCallbackMethods.getCallbacks())
        //                proxyParams.invoke(m);
        //        }
        return this;
    }
    
    private void loadCallbackEvents()
    {
        if (proxyParams == null)
            return;
        
        ObjectCallback objectCallback = OBJECTS_CALLBACKS.get(proxyParams.getTargetClass().getName());
        if (objectCallback != null)
            return;// target class already loaded
            
        objectCallback = new ObjectCallback(proxyParams.getTargetClass());
        List<Method> precallbacks = proxyParams.getAnnotationMethods(PreCallBack.class);
        List<Method> postcallbacks = proxyParams.getAnnotationMethods(PostCallBack.class);
        for (Method m : precallbacks)
        {
            PreCallBack precallback = m.getAnnotation(PreCallBack.class);
            for (CallbackScope scope : precallback.scope())
            {
                if (scope.isSelect())
                    objectCallback.addPreMethod(SqlType.SELECT, m);
                else if (scope.isAdd())
                    objectCallback.addPreMethod(SqlType.INSERT, m);//preAdd.add(m);
                else if (scope.isUpdate())
                    objectCallback.addPreMethod(SqlType.UPDATE, m);//preUpdate.add(m);
                else if (scope.isRemove())
                    objectCallback.addPreMethod(SqlType.DELETE, m);//preRemove.add(m);
            }
        }
        for (Method m : postcallbacks)
        {
            PostCallBack postcallback = m.getAnnotation(PostCallBack.class);
            List<CallbackScope> scopes = Arrays.asList(postcallback.scope());
            //boolean containsException = scopes.contains(CallbackScope.EXCEPTION);
            //boolean containsCommit = scopes.contains(CallbackScope.COMMIT);
            for (CallbackScope scope : postcallback.scope())
            {
                /*
                if (containsCommit)
                {
                    if (scope.isAdd())
                        objectCallback.addCommitMethod(SqlType.INSERT, m);
                    else if (scope.isUpdate())
                        objectCallback.addCommitMethod(SqlType.UPDATE, m);
                    else if (scope.isRemove())
                        objectCallback.addCommitMethod(SqlType.DELETE, m);
                }
                else if (containsException)
                {
                    if (scope.isSelect())
                        objectCallback.addExceptionMethod(SqlType.SELECT, m);
                    else if (scope.isAdd())
                        objectCallback.addExceptionMethod(SqlType.INSERT, m);
                    else if (scope.isUpdate())
                        objectCallback.addExceptionMethod(SqlType.UPDATE, m);
                    else if (scope.isRemove())
                        objectCallback.addExceptionMethod(SqlType.DELETE, m);
                }
                else */if (scope.isSelect())
                    objectCallback.addPostMethod(SqlType.SELECT, m);
                else if (scope.isAdd())
                    objectCallback.addPostMethod(SqlType.INSERT, m);
                else if (scope.isUpdate())
                    objectCallback.addPostMethod(SqlType.UPDATE, m);
                else if (scope.isRemove())
                    objectCallback.addPostMethod(SqlType.DELETE, m);//postRemove.add(m);
            }
        }
        OBJECTS_CALLBACKS.put(proxyParams.getTargetClass().getName(), objectCallback);
    }

    
}
