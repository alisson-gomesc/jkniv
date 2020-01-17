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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.reflect.ReflectionException;

/**
 * Utility interface to work with object reflection
 * 
 * @author Alisson Gomes
 *
 * @param <T> Type of class to be instanced
 */
public interface ObjectProxy<T>
{
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
    
    /**
     * check if class has a {@code methodName} to be invoked.
     * @param methodName complete method name like {@code isDeleted}, {@code setName}...
     * @return {@code true} when the class has the name, {@code false} otherwise
     */
    boolean hasMethod(String methodName);
    
    T getInstance();
    
    Class<T> getTargetClass();
    
    Object invoke(String methodName, Object... args); 

    Object invoke(Method method, Object... args); 

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

    /**
     * When the proxy catch a Exception to handler by {@code ReflectionException}
     * a mute rule is used to not throw the exception. 
     * @param ex exception class type
     * @return this instance
     */
    ObjectProxy<T> mute(Class<? extends Exception> ex);
    
    /**
     * check if this proxy class has {@code annotation}
     * @param annotation name of annotation
     * @return {@code true} when the class has this annotation, {@code false} otherwise
     */
    boolean hasAnnotation(String annotation);

    /**
     * check if this proxy class has {@code annotation}
     * @param annotation name of annotation
     * @return {@code true} when the class has this annotation, {@code false} otherwise
     */
    boolean hasAnnotation(Class<? extends Annotation> annotation);

    /**
     * Retrieve all public methods annotated with {@code annotation}
     * @param annotation looked for
     * @return a list of all public methods annotated with {@code annotation} or empty list otherwise.
     */
    List<Method> getMethodsAnnotatedWith(final Class<? extends Annotation> annotation);

    /**
     * check if this proxy class has {@code annotation} for a method name
     * @param <T> generic type to return
     * @param annotation name of the annotation
     * @param methodName name of the method
     * @param paramTypes type of parameters
     * @return the Annotation instance or {@code null} if not found
     * @throws ReflectionException when the {@code methodName} not exists
     */
    <T extends Annotation> T getAnnotationMethod(Class<? extends Annotation> annotation, String methodName, Class<?>... paramTypes);

    /**
     * check if this proxy class has {@code annotation} for a method name
     * @param <T> generic type to return
     * @param annotation name of the annotation
     * @param fieldName attribute name
     * @return the Annotation instance or {@code null} if not found
     * @throws ReflectionException when the {@code attributeName} not exists
     */
    <T extends Annotation> T getAnnotationField(Class<? extends Annotation> annotation, String fieldName);

    /**
     * get the {@link Field} declared by the class or sub-class represented by this proxy
     * @param name field to looking for
     * @return the Field instance or {@code null} if not found
     */
    Field getDeclaredField(String name);
    
    /**
     * get the {@link Method} declared by the class or sub-class represented by this proxy
     * @param name method to looking for
     * @return the Method instance or {@code null} if not found
     */
    Method getDeclaredMethod(String name);
    
    ObjectProxy<T> with(HandleableException handle);
    
    void register(TranslateType cast, Class type);
}
