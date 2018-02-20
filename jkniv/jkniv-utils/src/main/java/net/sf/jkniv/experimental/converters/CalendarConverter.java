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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Convert object value to java.util.Calendar
 * 
 * @author Alisson Gomes 
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CalendarConverter extends AbstractConverter // TODO Test me
{
    public CalendarConverter()// FIXME design to supports locale and another formats
    {
        super(true);
//        this.patterns.add("yyyy-MM-dd HH:mm:ss");
//        this.patterns.add("yyyy-MM-dd HH:mm");
//        this.patterns.add("yyyy-MM-dd");
//        this.patterns.add("yyyyMMdd");
    }
        
    public <T> T convert(Class<T> type, Object value)
    {
        T result = null;
        if (value == null && allowNull)
            return null;
        
        if (!type.isAssignableFrom(java.util.Calendar.class))
            throw new IllegalArgumentException(
                    "The type must be java.util.Calendar, but was [" + type + "]");
        
        
        if (value instanceof java.util.Calendar || value instanceof java.util.GregorianCalendar)
            result = (T)value;
        else if (value instanceof Long) 
        {
            Date d = new Date((Long)value);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            result = (T)c;            
        }
        else if (value instanceof String) 
        {
            Date d = new DateConverter().convert(Date.class,  value);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            result = (T) c;
        }
        if (result == null)
            throw new IllegalArgumentException("Date with value [" + value + "] not match with "
                    + type.getCanonicalName());
        
        return result;
    }
}
