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
import java.util.Calendar;
import java.util.Date;

/*
 * 
 */
class ConstructorInjection<T> implements Injectable<T>
{
    private Object[]                constructorArgs;
    private Class<?>[]              constructorTypes;
    private Constructor<T>[]        constructors;

    public void inject(String[] names, Object[] values)
    {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public void inject(String[] names, Object[] values, Class<?>[] types)
    {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public Object inject(String name, Object value)
    {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public Object inject(String name, Object value, Class<?> type)
    {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public Object inject(String name, byte value)
    {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public Object inject(String name, short value)
    {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public Object inject(String name, int value)
    {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public Object inject(String name, long value)
    {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public Object inject(String name, float value)
    {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public Object inject(String name, double value)
    {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public Object inject(String name, boolean value)
    {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public Object inject(String name, char value)
    {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
    
    public Object inject(String name, Date value)
    {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
    
    public Object inject(String name, Calendar value)
    {
        throw new UnsupportedOperationException("Not implemented yet!");
    }    
}
