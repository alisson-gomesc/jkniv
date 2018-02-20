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
package net.sf.jkniv.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to use Java Reflection API.
 * 
 * @author Alisson Gomes
 * @deprecated will be refactored 
 */
public class ReflectionUtils
{
    
    private ReflectionUtils()
    {
    }
    
    /**
     * Beyond determines if the class or interface represented by classOne parameter is either the same as, 
     * or is a superclass or superinterface of, the class or interface represented by the specified 
     * classTwo parameter, check with primitives types.
     * 
     * @param classOne class or interface source to check
     * @param classTwo class or interface destiny to check
     * @return return true if are compatibility types (class, interface, superclass, superinterface and primitive), false otherwise.
     * 
     * @see java.lang.Class#isAssignableFrom(Class)
     */
    public static boolean isAssignableFrom(Class<?> classOne, Class<?> classTwo)
    {
        boolean is = false;
        if (classOne.isPrimitive())
        {
            is = isAssignableFromPrimitive(classOne, classTwo);
        }
        else if (classTwo.isPrimitive())
        {
            is = isAssignableFromPrimitive(classTwo, classOne);
        }
        else
        {
            is = classOne.isAssignableFrom(classTwo);
        }
        return is;
    }
    
    /**
     * Check if object is assignable from primitive type (int, boolean, long, etc)
     * @param primitive primitive type class, eg: int.class.
     * @param object the target object to verify.
     * @return return true if object is compatibility with primitive, false otherwise.
     */
    public static boolean isAssignableFromPrimitive(Class<?> primitive, Class<?> object)
    {
        boolean is = false;
        String className = object.getSimpleName().toLowerCase();
        if (className.startsWith(primitive.getName()))
            is = true;
        
        return is;
    }
    
    
    
    public static Method[] getAllDeclaredMethods(Class<?> clazz)
    {
        List<Method> list = new ArrayList<Method>();
        Class<?> superClass = clazz;
        Method[] methods = superClass.getDeclaredMethods();
        do
        {
            for (Method m : methods)
            {
                list.add(m);
            }
            superClass = superClass.getSuperclass();
            if (superClass == null)
                break;
            methods = superClass.getDeclaredMethods();
        } while (methods.length > 0);
        
        return list.toArray(new Method[list.size()]);
    }
    
    public static Field[] getAllDeclaredFields(Class<?> clazz)
    {
        List<Field> list = new ArrayList<Field>();
        Class<?> superClass = clazz;
        Field[] fields = superClass.getDeclaredFields();
        do
        {
            for (Field f : fields)
            {
                list.add(f);
            }
            superClass = superClass.getSuperclass();
            fields = superClass.getDeclaredFields();
        } while (fields.length > 0);
        
        return list.toArray(new Field[list.size()]);
    }
    
    public static <T> Class<T> forName(String className) {
        return forName(className, null);
    }
    
    /**
     * Returns the Class object associated with the class or interface with the given string name.
     * @param className class name to be create your type
     * @param defaultClassName default type
     * @param <T> generic type of {@code Class} object associated with the class
     * @throws  net.sf.jkniv.reflect.ReflectionException when no definition for the class with the specified name could be found
     * encapsulating java.lang.ClassNotFoundException and java.lang.LinkageError.
     * @see java.lang.Class#forName(String)
     * @return Return the type of <code>className</code> if no errors occurs, 
     * or type of <code>defaultClassName</code> if some exception it's throw.
     */
    public static <T> Class<T> forName(String className, Class<T> defaultClassName)
    {
        Class<T> clazz = null;
        try
        {
            clazz = (Class<T>) Class.forName(className);
        }
        catch (Throwable e)
        {
            if (defaultClassName == null)
                handleException(e, className);
            else
            {
                clazz = defaultClassName;
            }
        }
        return clazz;
    }
    
    public static <T> T newInstance(Class<T> clazz)
    {
        T instance = null;
        try
        {
            instance = clazz.newInstance();
        }
        catch (Throwable e)
        {
            handleException(e, clazz.getName());
        }
        
        return instance;
    }

    public static <T> T newInstance(Class<T> clazz, Class<?>[] parameterTypes, Object[] initArgs)
    {
        T instance = null;
        try
        {
            Constructor<T> cons = clazz.getConstructor(parameterTypes);
            instance = cons.newInstance(initArgs);
        }
        catch (Throwable e)
        {
            handleException(e, clazz.getName());
        }
        
        return instance;
    }

    
    public static void handleException(Throwable e, String value)
    {
        if (e instanceof ClassNotFoundException)
        {
            throw new ReflectionException("ClassNotFoundException -> no definition for the class [" + value
                    + "] with the specified name could be found", e);
        }
        else if (e instanceof LinkageError)
        {
            throw new ReflectionException("[LinkageError] -> class [" + value
                    + "] has some dependency on another class or incompatibly after compilation", e);
        }
        else if (e instanceof InstantiationException)
        {
            throw new ReflectionException("[InstantiationException] -> Cannot create new instance of class [" + value
                    + "]", e);
            
        }
        else if (e instanceof IllegalAccessException)
        {
            throw new ReflectionException("[IllegalAccessException] -> Cannot create new instance of class [" + value
                    + "]", e);
        }
        else if (e instanceof NoSuchMethodException)
        {
            throw new ReflectionException("[NoSuchMethodException] -> Cannot invoke or get method [" + value
                    + "]", e);
        }
        else if (e instanceof SecurityException)
        {
            throw new ReflectionException("[SecurityException] -> Cannot invoke or get method [" + value
                    + "]", e);
        }
        //invoke: IllegalAccessException,IllegalArgumentException, InvocationTargetException
        //NoSuchMethodException SecurityException 
        //IllegalArgumentException, InvocationTargetException
        //NoSuchMethodException, SecurityException
        throw new ReflectionException("Some error no caught happened", e);
    }
    
    /**
     * Returns the canonical name of the underlying class as defined by the Java Language Specification. 
     * Returns "null" string value if the underlying class does not have a canonical name or if object
     * parameter is a null reference.
     * @param o object to get canonical name
     * @return the name of the class or interface represented by this object from a null-safe way.
     * @see java.lang.Class#getCanonicalName()
     */
    public static String getClassCanonicalName(Object o)
    {
        String name = null;
        name = (o == null ? "null" : o.getClass().getCanonicalName());
        return (name == null ? "null" : name);
    }

    /**
     * Returns the name of the entity (class, interface, array class, primitive type, or void) 
     * represented by this Class object, as a String. Returns "null" string
     * value if object parameter is a null reference.
     * @param o object to get name
     * @return the name of the class or interface represented by this object from a null-safe way.
     * @see java.lang.Class#getName()
     */
    public static String getClassName(Object o)
    {
        String name = null;
        name = (o == null ? "null" : o.getClass().getName());
        return (name == null ? "null" : name);
    }
    
    /**
     * Returns the simple name of the underlying class as given in the source code. 
     * Returns an empty string if the underlying class is anonymous and "null" string
     * value if object parameter is a null reference.
     * @param o object to get simple name
     * @return the name of the class or interface represented by this object from a null-safe way.
     * @see java.lang.Class#getSimpleName()
     */
    public static String getClassSimpleName(Object o)
    {
        String name = null;
        name = (o == null ? "null" : o.getClass().getSimpleName());
        return (name == null ? "null" : name);
    }
}
