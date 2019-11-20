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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.reflect.ReflectionException;

class BasicInvoker extends AbstractInvoker implements Invokable
{
    public BasicInvoker(HandleableException handleException)
    {
        super(handleException);
    }
    
    public Object invoke(Method method, Object theInstance, Object... values)
    {
        Object ret = null;
        try
        {
            ret = method.invoke(theInstance, values);
        }
        catch (Exception e)//IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            this.handleException.handle(e, e.getMessage() + " method [" + method + "] type of("
                    + StringUtil.arrayToClass(values) + ") values of (" + StringUtil.arrayToString(values) + ")");
        }
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T invoke(Class<T> targetClass, Object... values)
    {
        T value = (T)((values == null || values.length == 0) ? null : values[0]);
        T ret = null;
        if (value == null)
            return ret;
        
        if (targetClass.getName().equals(value.getClass().getName()))
            return value;
        
        try
        {
            Method m = targetClass.getMethod("valueOf", value.getClass());
            ret = (T) invoke(m, null, value);
        }
        catch (Exception e)
        {
            try
            {
                Constructor<T> constructor = targetClass.getConstructor(value.getClass());
                ret = constructor.newInstance(values);
            }
            catch (Exception ex)
            {
                
                this.handleException.handle(ex,
                        "Cannot found constructor to [" + targetClass + "] with value [" + value + "]");
            }
        }
        return ret;
    }
       
    @Override
    public Object invoke(String methodName, Object theInstance, Object... values)
    {
        Method method = null;
        Class<?>[] types = getTypes(values);
        Class<?> instanceType = theInstance.getClass();
        MethodInfo methodInfo =  getMethodByNameCached(methodName, instanceType);
        if (methodInfo.count == 1 || (methodInfo.noParameters && types.length == 0) )
            method = methodInfo.method;
        else if (methodInfo.count > 1 && types.length > 0)
            method = getMethodByType(methodName, instanceType, types);
        else if (methodInfo.count < 1)
            notNull.verify(
                    new ReflectionException(
                            "Cannot find method [" + methodName + "] for [" + instanceType.getName() + "]"),
                    method);
        else
            handleException.throwMessage("There are more one method with name[%s] cannot choose one without params",
                    methodName);
        
        return invoke(method, theInstance, values);
    }

}
