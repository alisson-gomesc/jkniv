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

public class ObjectProxyFactory
{
    /**
     * Build proxy object for class type of <code>className</code>
     * @param className canonical name for class name
     * @param <T> Type of result object
     * @return Proxy for instanceof T
     */
    public static <T> ObjectProxy<T> of(String className)
    {
        return new DefaultObjectProxy<T>(className);
    }
        
    /**
     * Build proxy object for instance of object <code>target</code>
     * @param target instance of object that proxy will be represent
     * @param <T> Type of result object
     * @return Proxy for <code>target</code>
     */
    public static <T> ObjectProxy<T> of(T target)
    {
        return new DefaultObjectProxy<T>(target);
    }
    
    /**
     * Build proxy object for class type of <code>targetClazz</code>
     * @param targetClass target class for object proxy
     * @param <T> Type of result object
     * @return Proxy for instanceof <code>targetClass</code>
     */
    public static <T> ObjectProxy<T> of(Class<T> targetClass)
    {
        return new DefaultObjectProxy<T>(targetClass);
    }
    
    /*
      TODO implements nested supports
    public static <T> ObjectProxy<T> newInstanceNested(String className)
    {
        return new DefaultObjectProxy<T>(className);
    }
    
    public static <T> ObjectProxy<T> newInstanceNested(T target)
    {
        return new DefaultObjectProxy<T>(target);
    }
    
    public static <T> ObjectProxy<T> newInstanceNested(Class<T> targetClazz)
    {
        return new DefaultObjectProxy<T>(targetClazz);
    }
    */
    
}
