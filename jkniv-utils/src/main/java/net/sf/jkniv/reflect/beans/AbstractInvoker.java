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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.exception.HandleableException;

abstract class AbstractInvoker
{
    private static final Logger LOG = LoggerFactory.getLogger(AbstractInvoker.class);
    protected static final Assertable     notNull = AssertsFactory.getNotNull();
    protected final HandleableException   handleException;
    private final Map<String, MethodInfo> cacheMethods;
    private final Map<Class, TranslateType> castables;
    
    public AbstractInvoker(HandleableException handleException)
    {
        this.handleException = handleException;
        this.cacheMethods = new HashMap<String, MethodInfo>(3);
        this.castables = new HashMap<Class, TranslateType>();
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
    
    public Method getMethodByName(String methodName, Class<?> targetClass)
    {
        MethodInfo methodInfo = getMethodByNameCached(methodName, targetClass);
        return methodInfo.method;
    }
    
    protected MethodInfo getMethodByNameCached(String methodName, Class<?> theTargetClass)
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
        MethodInfo info = getMethodByNameCached(methodName, theTargetClass);
        return (info.count >= 1);
    }
    
    public void register(TranslateType castable, Class type)
    {
        TranslateType before = castables.put(type, castable);
        if (before != null)
            LOG.warn("The translate type {} was replaced for {}", before, castable);
    }
    
    protected Object[] makeAssignableTo(Class<?>[] argTypes, Object[] argValues)
    {
        Object[] argsAssignables = new Object[argValues.length];
        
        for (int i = 0; i < argTypes.length; i++)
        {
            Object instance = argValues[i];
            if (instance != null)
            {
                Class<?> t = argTypes[i];
                Class<?> r = argValues[i].getClass();
                if (t.isAssignableFrom(r))
                {
                    argsAssignables[i] = instance;
                }
                else // not the same type needs translate the source to destiny type
                {
                    TranslateType cast = castables.get(r);
                    if (cast != null)
                    {
                        argsAssignables[i] = cast.convert(t, instance);
                    }
//                    if (t.isEnum())
//                    {
//                        TypeConvertible converter = new EnumConverter();
//                        argsAssignables[i] = converter.convert(t, instance);
//                    }
//                    else if (isNumberType(t))
//                    {
//                        TypeConvertible converter = new NumberConverter();
//                        argsAssignables[i] = converter.convert(t, instance);
//                    }
//                    else if (t == Boolean.class || t == boolean.class)
//                    {
//                        TypeConvertible converter = new BooleanConverter();
//                        argsAssignables[i] = converter.convert(t, instance);
//                    }
                    
                    // FIXME test me with java.oracle.TIMESTAMP
//                    else if (isDateType(t))
//                    {
//                        TypeConvertible converter = new DateConverter();
//                        argsAssignables[i] = converter.convert(t, instance);
//                    }
//                    else if (isCalendarType(t))
//                    {
//                        TypeConvertible converter = new CalendarConverter();
//                        argsAssignables[i] = converter.convert(t, instance);
//                    }
                    else
                    {
                        LOG.error("Type of [" + t.getCanonicalName() + "] is not assignable to [" + r.getCanonicalName()
                                + "] trying using same way without converter! Write a TranslateType for types.");
                        argsAssignables[i] = instance;
                    }
                }
            }
            else 
                argsAssignables[i] = instance;
        }
        return argsAssignables;
    }

    private static boolean isNumberType(Class<?> type)
    {
        String canonicalName = type.getCanonicalName();
        boolean isNumber = false;
        if (Integer.class.getCanonicalName().equals(canonicalName)|| "int".equals(canonicalName))
            isNumber = true;
        else if (Long.class.getCanonicalName().equals(canonicalName)|| "long".equals(canonicalName))
            isNumber = true;
        else if (Double.class.getCanonicalName().equals(canonicalName) || "double".equals(canonicalName))
            isNumber = true;
        else if (Float.class.getCanonicalName().equals(canonicalName)|| "float".equals(canonicalName))
            isNumber = true;
        else if (BigDecimal.class.getCanonicalName().equals(canonicalName))
            isNumber = true;
        else if (Short.class.getCanonicalName().equals(canonicalName)|| "short".equals(canonicalName))
            isNumber = true;
        else if (BigInteger.class.getCanonicalName().equals(canonicalName))
            isNumber = true;
        
        return isNumber;
    }
}
