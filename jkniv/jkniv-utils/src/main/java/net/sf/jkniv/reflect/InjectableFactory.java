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

import net.sf.jkniv.reflect.beans.ObjectProxy;
/**
 * Factory to create facility object for inject data at object via methods properties, fields attributes or constructors.
 * 
 * @author Alisson Gomes
 *
 */
public class InjectableFactory
{
    /**
     * Build a new instance of injectable data for objects using properties methods (not field attributes)
     * @param instance object to reflect
     * @param <T> Type of instance object
     * @return implementation that inject values using reflection from properties methods
     */
    public static <T> Injectable<T> newMethodInjection(T instance)
    {
        return new MethodInjection<T>(instance);
    }
    
    /**
     * Build a new instance of injectable data for objects using properties methods (not field attributes)
     * @param proxy for instance object to reflect
     * @param <T> Type of instance object
     * @return implementation that inject values using reflection from properties methods
     */
    public static <T> Injectable<T> newMethodInjection(ObjectProxy<T> proxy)
    {
        return new MethodInjection<T>(proxy);
    }
}
