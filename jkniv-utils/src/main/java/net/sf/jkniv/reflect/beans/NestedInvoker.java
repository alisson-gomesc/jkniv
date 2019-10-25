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
 */package net.sf.jkniv.reflect.beans;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.reflect.ReflectionException;

class NestedInvoker extends AbstractInvoker implements Invokable
{
    private static final Logger LOG = LoggerFactory.getLogger(NestedInvoker.class);
    private static final MethodName GETTER  = new GetterMethod();
    private static final MethodName SETTER  = new SetterMethod();
    private final Invokable pojoInvoker;

    public NestedInvoker(HandleableException handleException)
    {
        this(new PojoInvoker(handleException), handleException);
    }

    public NestedInvoker(PojoInvoker pojo, HandleableException handleException)
    {
        super(handleException);
        this.pojoInvoker = new PojoInvoker(handleException);
    }

    public NestedInvoker(PojoInvoker pojo, BasicInvoker basic, HandleableException handleException)
    {
        super(handleException);
        this.pojoInvoker = new PojoInvoker(basic, handleException);
    }

    @Override
    public <T> T invoke(Class<T> targetClass, Object... values)
    {
        throw new UnsupportedOperationException("Nested Invoke cannot call basic invoke without instance object reference");
    }

    @Override
    public Object invoke(Method method, Object theInstance, Object... values)
    {
        throw new UnsupportedOperationException("Nested Invoke cannot call java.lang.reflect.Method, use PojoInvoke for this");
    }

    @Override
    public Object invoke(String methodName, Object theInstance, Object... values)
    {
        String[] nestedMethodsNames = getNestedLevels(methodName);
        Object nestedInstance = null;
        Object ret = null;
        try
        {
            nestedInstance = createNestedInstances(methodName, nestedMethodsNames, theInstance);
            MethodInfo methodInfoSeter = getMethodByName(SETTER.capitalize(nestedMethodsNames[nestedMethodsNames.length - 1]), nestedInstance.getClass());
            ret = pojoInvoker.invoke(methodInfoSeter.method, nestedInstance, values);
            //setValue(nestedInstance, nestedMethodsNames[nestedMethodsNames.length - 1], values);
        }
        catch (Exception e)//IllegalArgumentException , InvocationTargetException,IllegalAccessException,  InstantiationException
        {
            handleException.handle(e, e.getMessage());
            /*
            handleException.handle(e,"Cannot create instance of [" + fieldType.getClass().getName()
                    + "] for [" + this.targetClass.getName() + methodName + "] to set nested values.");                    
                    */
        }
        return ret;
    }

    
    private Object createNestedInstances(String nestedMethodName, String[] nestedMethodsNames, Object theInstance)
            throws IllegalArgumentException, InvocationTargetException, IllegalAccessException, InstantiationException
    {
        Class<?> useTargetClass = theInstance.getClass();
        if (nestedMethodsNames.length > 4 && LOG.isWarnEnabled())
            LOG.info(
                    "Trying to set [{}] values for more than 4 nested methods {}.{}.{}.{}... pay attention for Law of Demeter https://en.wikipedia.org/wiki/Law_of_Demeter",
                    useTargetClass.getName(), nestedMethodsNames[0], nestedMethodsNames[1], nestedMethodsNames[2],
                    nestedMethodsNames[3]);
        
        Object nestedInstance = null;
        Object useInstance = theInstance;
        for (int i = 0; i < nestedMethodsNames.length - 1; i++)
        {
            String methodName = nestedMethodsNames[i];
            MethodInfo methodInfo = null;
            Object o = null;
            String methodNameCaptilized = GETTER.capitalize(methodName);
            methodInfo = getMethodByName(methodNameCaptilized, useTargetClass);
            notNull.verify(new ReflectionException("Cannot find method [" + methodNameCaptilized  + "] for [" + useTargetClass.getName() + "]"), methodInfo.method);// TODO design custom message

            o = pojoInvoker.invoke(methodInfo.method, useInstance);
            //o = invokeDirect(methodInfo.method, useInstance);
            
            if (o == null)
            {
                Class<?> fieldType = methodInfo.method.getReturnType();
                if (fieldType == null)
                    throw new ReflectionException(
                            "Cannot find [" + useTargetClass.getName() + "." + methodName + "] to set nested values.");
                
                if (fieldType.isInterface() || Modifier.isAbstract(fieldType.getModifiers()))
                    throw new ReflectionException("Cannot create instance of interface/abstract ["
                            + fieldType.getClass().getName() + "] for [" + useTargetClass.getName() + "." + methodName
                            + "] to set nested values.");
                
                nestedInstance = fieldType.newInstance();
                MethodInfo methodInfoSeter = getMethodByName(SETTER.capitalize(methodName), useTargetClass);
                pojoInvoker.invoke(methodInfoSeter.method, useInstance, nestedInstance);
                //invokeDirect(methodInfoSeter.method, useInstance, nestedInstance);
            }
            else
                nestedInstance = o;
            
            useInstance = nestedInstance;
            useTargetClass = nestedInstance.getClass();
        }
        if (nestedInstance == null)
            throw new ReflectionException("Cannot find property [" + nestedMethodName + "] to inject at for ["
                    + useTargetClass.getName() + "] to set nested values.");
        
        return nestedInstance;
    }

    /**
     * 
     * @param nestedMethodName nested method name like: <code>book.auhtor.name</code>
     * @return array of methods names {book, author, name}
     */
    private String[] getNestedLevels(String nestedMethodName)
    {
        String[] values = nestedMethodName.split("\\.");
        return values;
    }


    
}
