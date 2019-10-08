/* 
 * JKNIV, SQLegance keeping queries maintainable.
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
package net.sf.jkniv.sqlegance.type;

/**
 * Convert or make the parser from Jdbc type to java object (class attribute)
 * or vice versa.
 * 
 * <p><b>Note:</b> the implementation must be thread-safe and null-safe.</p>
 * 
 * @author alisson gomes
 *
 * @param <A>  the type of the class attribute
 * @param <B>  the type of the jdbc data column
 */
public interface Convertible<A, B>
{
    /**
     * Convert attribute to jdbc format or type
     * @param attribute the value of to be converted
     * @return the value to be stored for jdbc driver
     */
    B toJdbc(A attribute);
    
    /**
     * Convert the jdbc value to attribute format or type
     * @param jdbc the value stored at database
     * @return the value of attribute converted
     */
    A toAttribute(B jdbc);
    
    /**
     * The jdbc types code from database driver that can be convertible to java type
     * @return jdbc code from jdbc driver
     */
    int[] getTypes();
    
    Class<A> getClassType();
    
}
