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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Convert object value to java.util.Date
 * 
 * @author Alisson Gomes 
 *
 */
@SuppressWarnings(
{ "unchecked", "rawtypes" })
public class DateConverter extends AbstractConverter // TODO Test me
{
    public DateConverter()// FIXME design to supports locale and another formats
    {
        super(true);
        this.patterns.add("yyyy-MM-dd HH:mm:ss.SSS");
        this.patterns.add("yyyy-MM-dd HH:mm:ss");
        this.patterns.add("yyyy-MM-dd HH:mm");
        this.patterns.add("yyyy-MM-dd");
        this.patterns.add("yyyyMMdd");
    }
    
    public <T> T convert(Class<T> type, Object value)
    {
        T result = null;
        if (value == null && allowNull)
            return null;
        
        if (!type.isAssignableFrom(java.util.Date.class))
            throw new IllegalArgumentException("The type must be java.util.Date, but was [" + type + "]");
        
        if (value instanceof java.util.Date)
            result = (T) value;
        else if (value instanceof Long)
            result = (T) new Date((Long) value);
        else if (value instanceof String)
            result = match(type, (String) value);
        else if (type.isAssignableFrom(value.getClass()))// TODO test me when value is java.sql.Time | java.sql.Timestamp | java.sql.Date
            result = (T)value;
        
        if (result == null)
            throw new IllegalArgumentException(
                    "Date with value [" + value + "] not match with " + type.getCanonicalName());
        
        return result;
    }
    
    private <T> T match(Class<T> type, String value)
    {
        Date date = null;
        try
        {
            date = new Date(Date.parse((String) value));
        }
        catch (Exception ignore)
        {
        }
        
        if (date == null)
        {
            for (String pattern : patterns)
            {
                try
                {
                    date = new SimpleDateFormat(pattern).parse(value);
                    if (date != null)
                        break;
                }
                catch (ParseException ignore)
                {
                }
            }
        }
        if (date == null)
            throw new RuntimeException("Cannot parse string date [" + value + "] using patterns [" + patterns + "]");
        return (T)date;
        
    }
    
}
