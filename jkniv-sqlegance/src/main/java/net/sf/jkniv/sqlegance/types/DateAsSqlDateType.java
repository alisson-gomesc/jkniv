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

public class DateAsSqlDateType implements Convertible<java.sql.Time, java.util.Date>
{
    private final static int[] TYPES = {Types.DATE, Types.TIME, Types.TIMESTAMP};
    
    public DateAsSqlDateType()
    {
    }
    
    public DateAsSqlDateType(String pattern)
    {
    }
    
    @Override
    public java.util.Date toJdbc(java.sql.Time attribute)
    {
        if (attribute == null)
            return null;
        
        return new java.util.Date(attribute.getTime());
    }

    @Override
    public java.sql.Time toAttribute(java.util.Date jdbc)
    {
        if (jdbc == null)
            return null;

        return new java.sql.Time(jdbc.getTime());
    }

    @Override
    public int[] getTypes()
    {
        return TYPES;
    }

    @Override
    public Class<java.sql.Time> getClassType()
    {
        return java.sql.Time.class;
    }
    
}