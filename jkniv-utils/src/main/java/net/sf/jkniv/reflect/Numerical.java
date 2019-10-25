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

/**
 * Create number instance representing a specific implementation.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public interface Numerical
{
    /**
     * Returns a numerical object holding the value of the specified {@code Object.toString()}. 
     * @param n the instance of object to be parsed to a number
     * @return Return the parsed number
     */
    Number valueOf(Object n);

    /**
     * Returns a numerical object holding the value of the specified String. 
     * @param n the string to be parsed to a number
     * @return Return the parsed number
     */
    Number valueOf(String n);
    
    /**
     * Returns a numerical object holding the value of the specified String. 
     * @param n the number to be cast to specific number type
     * @return Return the casted number
     */
    Number valueOf(Number n);
    
    /**
     * The type of the number that this numerical implementation works
     * @return class that represent a number.
     */
    Class<? extends Number> typeOf();
}
