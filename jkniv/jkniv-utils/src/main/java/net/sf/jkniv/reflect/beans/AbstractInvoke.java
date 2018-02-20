/* 
 * JKNIV, utils - Helper utilities for jdk code.
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
package net.sf.jkniv.reflect.beans;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.exception.HandleableException;

abstract class AbstractInvoke
{
    protected static final Assertable       notNull    = AssertsFactory.getNotNull();
    protected final HandleableException   handleException;
    private final Map<String, MethodInfo> cacheMethods;
    
    public AbstractInvoke(HandleableException handleException)
    {
        this.handleException = handleException;
        this.cacheMethods = new HashMap<String, MethodInfo>(3);
    }
    
    protected Class<?>[] getTypes(Object[] args)
    {
        if (args == null)
            return null;
        
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++)
        {
            if (args[i] != null)
            {
                types[i] = args[i].getClass();
            }
        }
        return types;
    }
    
    protected MethodInfo getMethodByName(String methodName, Class<?> theTargetClass)// FIXME performance gap build cache info
    {
        String key = theTargetClass.getCanonicalName() + "." + methodName;
        MethodInfo methodInfo = cacheMethods.get(key);
        
        if (methodInfo == null || methodInfo.count == 0)
        {
            methodInfo = new MethodInfo();
            for (Method m : theTargetClass.getMethods())
            {
                if (m.getName().equals(methodName))
                {
                    if (methodInfo.count == 0 || m.getParameterTypes().length == 0)
                    {
                        methodInfo.method = m;
                        methodInfo.noParameters = (m.getParameterTypes().length == 0);
                    }
                    methodInfo.methods.add(m);
                    methodInfo.count++;
                }
            }
        }
        cacheMethods.put(key, methodInfo);
        return methodInfo;
    }
    
    protected Method getMethodByType(String methodName, Class<?> theTargetClass, Class<?>... parameterTypes)
    {
        Method method = null;
        try
        {
            // near method from primitive types BigDecimal->Double->Float->Long->Integer->Short
            method = theTargetClass.getMethod(methodName, parameterTypes);
        }
        catch (Exception e)
        {
            this.handleException.handle(e, "with name " + methodName + StringUtil.getArgsToPrint(parameterTypes));
        }
        return method;
    }

    public boolean hasMethod(String methodName, Class<?> theTargetClass)
    {
        MethodInfo info = getMethodByName(methodName, theTargetClass);
        return (info.count >= 1);
    }

}
