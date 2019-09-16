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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.sqlegance.SqlType;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 *
 */
class ObjectCallback
{
    private final static Logger LOG = LoggerFactory.getLogger(ObjectCallback.class);
    private final static Assertable NOT_NULL = AssertsFactory.getNotNull();
    final static ObjectCallback EMPTY = new ObjectCallback(String.class);
    private Class<?> targetClass;
    private Map<SqlType, Set<Method>> preMethods;
    private Map<SqlType, Set<Method>> postMethods;
    private Map<SqlType, Method> commitMethod;
    private Map<SqlType, Method> exceptionMethod;
    
    static {
        final Set<Method> M = Collections.emptySet();
        EMPTY.preMethods.put(SqlType.SELECT, M);
        EMPTY.preMethods.put(SqlType.INSERT, M);
        EMPTY.preMethods.put(SqlType.UPDATE, M);
        EMPTY.preMethods.put(SqlType.DELETE, M);
        EMPTY.postMethods.put(SqlType.SELECT, M);
        EMPTY.postMethods.put(SqlType.INSERT, M);
        EMPTY.postMethods.put(SqlType.UPDATE, M);
        EMPTY.postMethods.put(SqlType.DELETE, M);
        EMPTY.commitMethod.put(SqlType.SELECT, null);
        EMPTY.commitMethod.put(SqlType.INSERT, null);
        EMPTY.commitMethod.put(SqlType.UPDATE, null);
        EMPTY.commitMethod.put(SqlType.DELETE, null);
        EMPTY.exceptionMethod.put(SqlType.SELECT, null);
        EMPTY.exceptionMethod.put(SqlType.INSERT, null);
        EMPTY.exceptionMethod.put(SqlType.UPDATE, null);
        EMPTY.exceptionMethod.put(SqlType.DELETE, null);
    }
    
    public ObjectCallback(Class<?> targetClass)
    {
        NOT_NULL.verify(targetClass);
        this.targetClass = targetClass;
        this.preMethods = new HashMap<SqlType, Set<Method>>();
        this.postMethods = new HashMap<SqlType, Set<Method>>();
        this.commitMethod = new HashMap<SqlType, Method>();
        this.exceptionMethod = new HashMap<SqlType, Method>();
    }

    public Class<?> getTargetClass()
    {
        return targetClass;
    }

    public Set<Method> getPreMethods(SqlType sqlType)
    {
        Set<Method> methods = preMethods.get(sqlType);
        if(methods == null)
            return EMPTY.preMethods.get(sqlType);
        
        return methods;
    }

//    public void addPreMethod(SqlType sqlType, Set<Method> preMethods)
//    {
//        this.preMethods.put(sqlType, preMethods);
//    }

    public void addPreMethod(SqlType sqlType, Method m)
    {
        Set<Method> methods = this.preMethods.get(sqlType);
        if (methods == null)
        {
            methods = new HashSet<Method>();
            this.preMethods.put(sqlType, methods);    
        }
        methods.add(m);
    }

    public Set<Method> getPostMethods(SqlType sqlType)
    {
        Set<Method> methods = postMethods.get(sqlType);
        if(methods == null)
            return EMPTY.postMethods.get(sqlType);

        return methods;
    }

//    public void addPostMethod(SqlType sqlType, Set<Method> postMethods)
//    {
//        this.postMethods.put(sqlType, postMethods);
//    }

    public void addPostMethod(SqlType sqlType, Method m)
    {
        Set<Method> methods = this.postMethods.get(sqlType);
        if (methods == null)
        {
            methods = new HashSet<Method>();
            this.postMethods.put(sqlType, methods);    
        }
        methods.add(m);
    }

    public Method getCommitMethod(SqlType sqlType)
    {
        return commitMethod.get(sqlType);
    }

    public void addCommitMethod(SqlType sqlType, Method method)
    {
        Method m = this.commitMethod.put(sqlType, method);
        if (m != null)
            LOG.warn("There are more one callback method to [{}] after commit. {} was replaced for {}", sqlType, m, method);
    }

    public Method getExceptionMethod(SqlType sqlType)
    {
        return exceptionMethod.get(sqlType);
    }

    public void addExceptionMethod(SqlType sqlType, Method method)
    {
        Method m = this.exceptionMethod.put(sqlType, method);
        if (m != null)
            LOG.warn("There are more one callback method to [{}] after exception. {} was replaced for {}", sqlType, m, method);
    }

    @Override
    public String toString()
    {
        return "ObjectCallback [clazz=" + targetClass + ", preMethods=" + preMethods + ", postMethods=" + postMethods
                + ", commitMethod=" + commitMethod + ", rollbackMethod=" + exceptionMethod + "]";
    }
}
