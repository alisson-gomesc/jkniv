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

import java.util.Calendar;
import java.util.Date;

/**
 * Interface overload the methods do inject (by reflection) the right value dynamically at Object instances.
 * 
 * @author Alisson Gomes
 *
 * @param <T> Type of from object that recieve the data
 */
public interface Injectable<T>
{
    void inject(String[] names, Object[] values);
    
    Object inject(String name, Object value);
    
    void inject(String[] names, Object[] values, Class<?>[] types);

    Object inject(String name, Object value, Class<?> type);
    
    Object inject(String name, byte value);

    Object inject(String name, short value);

    Object inject(String name, int value);

    Object inject(String name, long value);

    Object inject(String name, float value);

    Object inject(String name, double value);

    Object inject(String name, boolean value);

    Object inject(String name, char value);
    
    Object inject(String name, Date value);

    Object inject(String name, Calendar value);

}
