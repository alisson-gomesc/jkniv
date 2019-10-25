/* 
 * JKNIV, whinstone one contract to access your database.
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
package net.sf.jkniv.sqlegance.types;

import java.sql.Types;

public class NoConverterType implements Convertible<Object, Object>
{
    private final static int[] TYPES = {Types.CHAR, Types.VARCHAR, Types.NCHAR, Types.NVARCHAR};
    private final static Convertible<Object, Object> INSTANCE = new NoConverterType();
    
    
    public static Convertible<Object, Object> getInstance()
    {
        return INSTANCE;
    }
    
    private NoConverterType()
    {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public Object toJdbc(Object attribute)
    {
        return attribute;
    }

    @Override
    public Object toAttribute(Object jdbc)
    {
        return jdbc;
    }

    @Override
    public int[] getTypes()
    {
        return TYPES;
    }

    @Override
    public Class<Object> getClassType()
    {
        return Object.class;
    }
    
}
