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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarAsIntType implements Convertible<Calendar, Integer>
{
    private final static int[] TYPES = {Types.DATE, Types.TIME, Types.TIMESTAMP};
    private String pattern;
    
    public CalendarAsIntType(String pattern)
    {
        this.pattern = pattern;
    }
    
    @Override
    public Integer toJdbc(Calendar attribute)
    {
        if (attribute == null)
            return null;
        
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return Integer.valueOf(sdf.format(attribute.getTime()));
    }

    @Override
    public Calendar toAttribute(Integer jdbc)
    {
        if (jdbc == null)
            return null;
        
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(String.valueOf(jdbc)));
            return calendar;
        }
        catch (ParseException e)
        {
            throw new ConverterException(e);
        }
    }

    @Override
    public int[] getTypes()
    {
        return TYPES;
    }

    @Override
    public Class<Calendar> getClassType()
    {
        return Calendar.class;
    }
    
}
