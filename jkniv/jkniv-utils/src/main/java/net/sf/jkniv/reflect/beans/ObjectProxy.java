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

import javax.management.ReflectionException;

/**
 * Utility interface to work with object reflection
 * 
 * @author Alisson Gomes
 *
 * @param <T> Type of class to be instanced
 */
public interface ObjectProxy<T>
{
    /*
    * <code>Constructor</code> provides information about, and access to, a single
    * constructor for a class.
*/
    
    /**
     * Arguments to build object using contructor
     * @param constructorArgs argument values
     */
    void setConstructorArgs(Object... constructorArgs);
    
    /**
     * Sequential type arguments from constructor 
     * @param contructorTypes type of arguments
     * @throws net.sf.jkniv.reflect.ReflectionException if some type is null
     */
    void setConstructorTypes(Class<?>... contructorTypes);
    
    /**
     * build a new instance using constructor with arguments types or default
     * @return a new instance of object, throws exception if an instance already exists
     * @throws net.sf.jkniv.reflect.ReflectionException if an instance already exists
     */
    T newInstance();
    
    /**
     * check if instance exists
     * @return {@code true} when instance exists, {@code false} otherwise
     */
    boolean hasInstance();
    
    boolean isInstanceof(Class<?> clazz);
    
    boolean hasMethod(String methodName);
    
    T getInstance();
    
    Class<T> getTargetClass();
    
    Object invoke(String methodName, Object... args); 
    
    /**
     * copy all data from properties started with {@code get} or {@code is} to instance of this proxy
     * @param o origin object data
     * @return instance of object from proxy with the data from {@code o}
     * @throws net.sf.jkniv.reflect.ReflectionException when some wrong occurs
     */
    T from(Object o);

    /**
     * merge all data from properties started with {@code get} or {@code is} to instance of this proxy
     * @param o origin object data
     * @return instance of object from proxy with the data from {@code o}
     * @throws net.sf.jkniv.reflect.ReflectionException when some wrong occurs
     */
    T merge(Object o);

}
