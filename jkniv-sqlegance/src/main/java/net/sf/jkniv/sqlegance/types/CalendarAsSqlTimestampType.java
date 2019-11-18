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
import java.util.Calendar;

public class CalendarAsSqlTimestampType implements Convertible<java.util.Calendar, java.sql.Timestamp>
{
    private final static int[] TYPES = {Types.DATE, Types.TIME, Types.TIMESTAMP};
    
    public CalendarAsSqlTimestampType()
    {
    }
    
    public CalendarAsSqlTimestampType(String pattern)
    {
    }
    
    @Override
    public java.sql.Timestamp toJdbc(java.util.Calendar attribute)
    {
        if (attribute == null)
            return null;
        
        return new java.sql.Timestamp(attribute.getTime().getTime());
    }

    @Override
    public java.util.Calendar  toAttribute(java.sql.Timestamp jdbc)
    {
        if (jdbc == null)
            return null;

        Calendar cal = Calendar.getInstance();
        cal.setTime(jdbc);
        return cal;
    }

    @Override
    public int[] getTypes()
    {
        return TYPES;
    }

    @Override
    public Class<java.util.Calendar> getClassType()
    {
        return java.util.Calendar.class;
    }
    
}
