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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.reflect.ReflectionException;

@Deprecated
/**
 * @deprecated use NestedInvoke
 */
class BuildNestedObject
{
    private static final Logger     LOG     = LoggerFactory.getLogger(BuildNestedObject.class);
    private static final Assertable notNull = AssertsFactory.getNotNull();
    private static final MethodName getter  = new GetterMethod();
    private static final MethodName setter  = new SetterMethod();
    
    private Object                  instance;
    private Class<?>                targetClass;
    private HandleableException     handleException;
    
    public BuildNestedObject(Object instance)
    {
        notNull.verify(instance);
        this.instance = instance;
        this.targetClass = instance.getClass();
        this.handleException = new HandlerException(ReflectionException.class, "Cannot handler %s");
        this.handleException
                .config(IllegalAccessException.class,
                        "[IllegalAccessException] -> Cannot create new instance of class %s")
                .config(IllegalArgumentException.class,
                        "[IllegalArgumentException] -> Cannot create new instance of class %s")
                // TODO write message
                .config(InvocationTargetException.class, "[InvocationTargetException] -> Cannot invoke method %s");
    }
    
    public void setter(String nestedMethodName, Object... values)
    {
        String[] nestedMethodsNames = getNestedLevels(nestedMethodName);
        Object nestedInstance = null;
        try
        {
            nestedInstance = createNestedInstances(nestedMethodName, nestedMethodsNames);
            setValue(nestedInstance, nestedMethodsNames[nestedMethodsNames.length - 1], values);
        }
        catch (Exception e)//IllegalArgumentException , InvocationTargetException,IllegalAccessException,  InstantiationException
        {
            handleException.handle(e, e.getMessage());
            /*
            handleException.handle(e,"Cannot create instance of [" + fieldType.getClass().getName()
                    + "] for [" + this.targetClass.getName() + methodName + "] to set nested values.");                    
                    */
        }
    }
    
    private Object createNestedInstances(String nestedMethodName, String[] nestedMethodsNames)
            throws IllegalArgumentException, InvocationTargetException, IllegalAccessException, InstantiationException
    {
        if (nestedMethodsNames.length > 4 && LOG.isWarnEnabled())
            LOG.info(
                    "Try to set [{}] values for more than 4 nested methods {}.{}.{}.{}... pay attention for Law of Demeter https://en.wikipedia.org/wiki/Law_of_Demeter",
                    targetClass.getName(), nestedMethodsNames[0], nestedMethodsNames[1], nestedMethodsNames[2],
                    nestedMethodsNames[3]);
        
        Object nestedInstance = null;
        Object useInstance = this.instance;
        Class<?> useTargetClass = this.targetClass;
        for (int i = 0; i < nestedMethodsNames.length - 1; i++)
        {
            String methodName = nestedMethodsNames[i];
            MethodInfo methodInfo = null;
            Object o = null;
            
            methodInfo = getMethodByName(useTargetClass, getter.capitalize(methodName));
            o = invokeDirect(methodInfo.method, useInstance);
            
            if (o == null)
            {
                //Field field = getFieldByName(this.targetClass, getter.uncapitalize(methodName));
                //if (field == null)
                //    throw new ReflectionException("Cannot find [" + this.targetClass.getName() + "." + methodName+ "] to set nested values.");
                
                Class<?> fieldType = methodInfo.method.getReturnType();
                if (fieldType == null)
                    throw new ReflectionException(
                            "Cannot find [" + useTargetClass.getName() + "." + methodName + "] to set nested values.");
                
                if (fieldType.isInterface() || Modifier.isAbstract(fieldType.getModifiers()))
                    throw new ReflectionException("Cannot create instance of interface/abstract ["
                            + fieldType.getClass().getName() + "] for [" + useTargetClass.getName() + "." + methodName
                            + "] to set nested values.");
                
                nestedInstance = fieldType.newInstance();
                MethodInfo methodInfoSeter = getMethodByName(useTargetClass, setter.capitalize(methodName));
                invokeDirect(methodInfoSeter.method, useInstance, nestedInstance);
            }
            else
                nestedInstance = o;
            
            useInstance = nestedInstance;
            useTargetClass = nestedInstance.getClass();
        }
        if (nestedInstance == null)
            throw new ReflectionException("Cannot find property [" + nestedMethodName + "] to inject at for ["
                    + this.targetClass.getName() + "] to set nested values.");
        
        return nestedInstance;
    }
    
    private void setValue(Object nestedInstance, String methodName, Object... value)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        MethodInfo methodInfoSeter = getMethodByName(nestedInstance.getClass(), setter.capitalize(methodName));
        invokeDirect(methodInfoSeter.method, nestedInstance, value);
    }

    private MethodInfo getMethodByName(Class<?> theTargetClass, String methodName)// FIXME performance gap build cache info
    {
        MethodInfo methodInfo = new MethodInfo();
        //int count = 0;
        for (Method m : theTargetClass.getMethods())
        {
            if (m.getName().equals(methodName))
            {
                //count++;
                if (methodInfo.count == 0)
                    methodInfo.method = m;
                methodInfo.methods.add(m);
                methodInfo.count++;
            }
        }
        return methodInfo;
    }
    
    private String[] getNestedLevels(String columnName)
    {
        String[] values = columnName.split("\\.");
        return values;
    }
    
    
    private Object invokeDirect(Method method, Object theInstance, Object... values)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Object ret = null;
        try
        {
            ret = method.invoke(theInstance, values);
        }
        catch (Exception e)
        {
            throw new ReflectionException("Cannot invoke method ["
                    + (method != null ? method.getName() + "(" + arrayToString(method.getParameterTypes()) + ")"
                            : "null")
                    + "] from class [" + (theInstance != null ? theInstance.getClass().getName() : "null")
                    + "] to inject value type [" + arrayToClass(values) + "]. " + e.getMessage(), e);
        }
        return ret;
    }
    
    
    private Field getFieldByName(Class<?> theTargetClass, String attributeName)// FIXME performance gap build cache info
    {
        Field field = null;
        for (Field f : theTargetClass.getDeclaredFields())
        {
            f.setAccessible(true);
            if (f.getName().equals(attributeName))
            {
                field = f;
            }
        }
        return field;
    }
    
    private String arrayToString(Class<?>[] values)
    {
        StringBuilder sb = new StringBuilder();
        
        for (Class<?> c : values)
            sb.append(c.getName() + (sb.length() > 0 ? "," : ""));
        
        return sb.toString();
    }
    
    private String arrayToClass(Object[] values)
    {
        StringBuilder sb = new StringBuilder();
        
        for (Object o : values)
            
            sb.append((o != null ? o.getClass().getName() : "null") + (sb.length() > 0 ? "," : ""));
        
        return sb.toString();
    }
    
    private String arrayToString(Object[] values)
    {
        StringBuilder sb = new StringBuilder();
        
        for (Object o : values)
            
            sb.append((o != null ? o.toString() : "null") + (sb.length() > 0 ? "," : ""));
        
        return sb.toString();
    }
    
}
