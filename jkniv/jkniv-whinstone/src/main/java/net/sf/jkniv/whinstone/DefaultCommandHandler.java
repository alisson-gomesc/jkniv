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
import java.sql.Statement;
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
import net.sf.jkniv.sqlegance.dialect.SqlFeatureSupport;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public abstract class DefaultCommandHandler implements CommandHandler
{
    final static Logger                              LOG               = LoggerFactory
            .getLogger(DefaultCommandHandler.class);
    static final Assertable                          NOT_NULL          = AssertsFactory.getNotNull();
    private final static Map<String, ObjectCallback> OBJECTS_CALLBACKS = new HashMap<String, ObjectCallback>();
    CommandAdapter                                   cmdAdapter;
    Command                                          command;
    ObjectProxy<?>                                   proxyParams;
    protected Queryable                              queryable;
    protected Sql                                    sql;
    protected RepositoryConfig                       config;
    protected ResultRow<?, ?>                        overloadResultRow;
    protected HandleableException                    handleableException;
    
    public DefaultCommandHandler(CommandAdapter cmdAdapter)
    {
        this.cmdAdapter = cmdAdapter;
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
        {
            this.proxyParams = ObjectProxyFactory.of(queryable.getParams());
            CallbackProcessor processor = new CallbackProcessor(this.proxyParams);
            if (!OBJECTS_CALLBACKS.containsKey(proxyParams.getTargetClass().getName()))
            {
                ObjectCallback objectCallback = processor.loadCallbackEvents();
                OBJECTS_CALLBACKS.put(proxyParams.getTargetClass().getName(), objectCallback);
            }
        }
        //loadCallbackEvents();
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
        this.handleableException = handlerException;
        return this;
    }
    
    @Override
    public <T> T run()
    {
        NOT_NULL.verify(this.cmdAdapter, this.queryable, this.sql);
        T t = null;
        Number rows = 0;
        if (LOG.isTraceEnabled())
            LOG.trace("Executing [{}] as {} command", queryable, sql.getSqlType());
        try
        {
            // JPA with a entity doesn't assert validate
            // how can fix/improve this?
            sql.getValidateType().assertValidate(queryable.getParams());
            if (!queryable.isBoundSql())
                queryable.bind(sql);
            try
            {
                preCallback();
                this.command = asCommand();
                t = this.command.execute();
                if (t instanceof Number)
                    rows = (Number) t;
                if (queryable.getDynamicSql().getSqlDialect().supportsFeature(SqlFeatureSupport.PAGING_ROUNDTRIP))
                    queryable.setTotal(rows.longValue());
                else
                    queryable.setTotal(Statement.SUCCESS_NO_INFO);
                if (LOG.isDebugEnabled())
                    LOG.debug("{} records was affected by {} [{}] query", rows, sql.getSqlType(), queryable.getName());
                postCallback();
            }
            catch (Exception e)
            {
                queryable.setTotal(Statement.EXECUTE_FAILED);
                postException();
                handleableException.handle(e);
            }
        }
        finally
        {
            this.cmdAdapter.close();
        }
        return t;
    }
    
    protected CommandAdapter getCommandAdapter()
    {
        return this.cmdAdapter;
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
    public CommandHandler postCommit()// TODO Callback implmements POST_COMMIT
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
    public CommandHandler postException()// TODO Callback implmements POST_EXCEPTION
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
    public CommandHandler checkSqlType(SqlType expected)
    {
        if (sql == null)
            throw new IllegalArgumentException("Null Sql reference wasn't expected");
        
        if (sql.getSqlType() != expected)
            throw new IllegalArgumentException("Cannot execute sql [" + sql.getName() + "] as " + sql.getSqlType()
                    + ", " + expected + " was expect");
        
        return this;
    }
    
}
