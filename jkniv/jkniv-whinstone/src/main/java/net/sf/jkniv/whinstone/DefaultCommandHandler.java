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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    final static Logger     LOG     = LoggerFactory.getLogger(DefaultCommandHandler.class);
    static final Assertable notNull = AssertsFactory.getNotNull();
    ConnectionAdapter     adapterConn;
    CommandHandler        handler;
    Command               command;
    protected Queryable             queryable;
    protected Sql                   sql;
    protected RepositoryConfig      config;
    protected ResultRow<?, ?>       overloadResultRow;
    protected HandleableException     handleableException;
    ObjectProxy<?>          proxyParams;

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
    
    @Override
    public CommandHandler with(RepositoryConfig repositoryConfig)
    {
        this.config = repositoryConfig;
        return this;
    }
    
    public CommandHandler with(HandleableException handlerException)
    {
     
        return this;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public <T> T run()
    {
        notNull.verify(this.adapterConn, this.queryable, this.sql, this.handler);
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
    
    @Override
    public CommandHandler preCallback()
    {
        if (proxyParams != null)
        {
            CallbackMethods preCallbackMethods = CacheCallback.get(proxyParams.getTargetClass(), sql.getSqlType());
            for (Method m : preCallbackMethods.getCallbacks())
                proxyParams.invoke(m);
        }
        return this;
    }
    
    @Override
    public CommandHandler postCallback()
    {
        if (proxyParams != null)
        {
            CallbackMethods preCallbackMethods = CacheCallback.get(proxyParams.getTargetClass(), sql.getSqlType());
            for (Method m : preCallbackMethods.getCallbacks())
                proxyParams.invoke(m);
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
    
    protected ConnectionAdapter getConnectionAdapter()
    {
        return this.adapterConn;
    }
    
    private void loadCallbackEvents()
    {
        if(proxyParams == null)
            return;
        
        CallbackMethods callbacks = CacheCallback.get(proxyParams.getTargetClass(), SqlType.SELECT);
        if (callbacks != CallbackMethods.EMPTY)
            return;// target class already loaded
        
        List<Method> precallbacks = proxyParams.getAnnotationMethods(PreCallBack.class);
        List<Method> postcallbacks = proxyParams.getAnnotationMethods(PostCallBack.class);
        
        List<Method> preAdd = new ArrayList<Method>();
        List<Method> postAdd = new ArrayList<Method>();
        List<Method> preSelect = new ArrayList<Method>();
        List<Method> postSelect = new ArrayList<Method>();
        List<Method> preUpdate = new ArrayList<Method>();
        List<Method> postUpdate = new ArrayList<Method>();
        List<Method> preRemove = new ArrayList<Method>();
        List<Method> postRemove = new ArrayList<Method>();
        List<Method> postAddException = new ArrayList<Method>();
        List<Method> postAddCommit = new ArrayList<Method>();
        List<Method> postSelectException = new ArrayList<Method>();
        List<Method> postUpdateException = new ArrayList<Method>();
        List<Method> postUpdateCommit = new ArrayList<Method>();
        List<Method> postRemoveException = new ArrayList<Method>();
        List<Method> postRemoveCommit = new ArrayList<Method>();
        
        for (Method m : precallbacks)
        {
            PreCallBack precallback = m.getAnnotation(PreCallBack.class);
            for (CallbackScope scope : precallback.scope())
            {
                if (scope.isSelect())
                    preSelect.add(m);
                else if (scope.isAdd())
                    preAdd.add(m);
                else if (scope.isUpdate())
                    preUpdate.add(m);
                else if (scope.isRemove())
                    preRemove.add(m);
            }
        }
        for (Method m : postcallbacks)
        {
            PostCallBack postcallback = m.getAnnotation(PostCallBack.class);
            List<CallbackScope> scopes = Arrays.asList(postcallback.scope());
            boolean containsException = false;//scopes.contains(CallbackScope.EXCEPTION);
            boolean containsCommit = false;//scopes.contains(CallbackScope.COMMIT);
            for (CallbackScope scope : postcallback.scope())
            {
                if (containsCommit)
                {
                    if (scope.isAdd())
                        postAddCommit.add(m);
                    else if (scope.isUpdate())
                        postUpdateCommit.add(m);
                    else if (scope.isRemove())
                        postRemoveCommit.add(m);
                }
                else if (containsException)
                {
                    if (scope.isSelect())
                        postSelectException.add(m);
                    else if (scope.isAdd())
                        postAddException.add(m);
                    else if (scope.isUpdate())
                        postUpdateException.add(m);
                    else if (scope.isRemove())
                        postRemoveException.add(m);
                    
                }
                else if (scope.isSelect())
                    postSelect.add(m);
                else if (scope.isAdd())
                    postAdd.add(m);
                else if (scope.isUpdate())
                    postUpdate.add(m);
                else if (scope.isRemove())
                    postRemove.add(m);
            }
        }   
        CacheCallback.put(proxyParams.getTargetClass(), SqlType.SELECT, preSelect);
        CacheCallback.put(proxyParams.getTargetClass(), SqlType.INSERT, preAdd);
        CacheCallback.put(proxyParams.getTargetClass(), SqlType.UPDATE, preUpdate);
        CacheCallback.put(proxyParams.getTargetClass(), SqlType.DELETE, preRemove);

        CacheCallback.put(proxyParams.getTargetClass(), SqlType.SELECT, postSelect);
        CacheCallback.put(proxyParams.getTargetClass(), SqlType.INSERT, postAdd);
        CacheCallback.put(proxyParams.getTargetClass(), SqlType.UPDATE, postUpdate);
        CacheCallback.put(proxyParams.getTargetClass(), SqlType.DELETE, postRemove);
    }

}
