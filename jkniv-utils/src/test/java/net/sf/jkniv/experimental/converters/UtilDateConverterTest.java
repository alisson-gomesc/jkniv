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
package net.sf.jkniv.experimental.converters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import java.util.Date;

import org.junit.Test;

public class UtilDateConverterTest
{
    
    @Test
    public void whenUtilDateIsAssignableFromSqlDate() 
    {
        Class<?> type = java.util.Date.class;
        java.sql.Date value = new java.sql.Date(new Date().getTime());
        assertThat(type.isAssignableFrom(value.getClass()), is(true));
    }

    
    @Test
    public void whenSqlDateIsConvertedToUtilDate() 
    {
        DateConverter converter = new DateConverter();
        java.sql.Date value = new java.sql.Date(new Date().getTime());
        Class<java.util.Date> type = java.util.Date.class;
        
        Date d =  converter.convert(type, value);
        
        assertThat(d, instanceOf(type));
    }
    
}
