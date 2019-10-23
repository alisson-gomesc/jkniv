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
 * Convert object value to java.sql.Date, java.sql.Times or java.sql.Timestamp
 * 
 * @author Alisson Gomes 
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class SqlDateConverter extends AbstractConverter // TODO Test me
{
    public SqlDateConverter()
    {
        super(true);
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
        if (!type.isAssignableFrom(java.sql.Time.class) && !type.isAssignableFrom(java.sql.Timestamp.class)
                && !type.isAssignableFrom(java.sql.Date.class))
            throw new IllegalArgumentException(
                    "The type must be java.sql.Date, java.sql.Time or java.sql.TimeStamp, but was [" + type + "]");
        
        if (value instanceof java.util.Date)
            result = match(type, (Date) value);
        else if (value instanceof String)
            result = match(type, (String) value);
        else if (value instanceof Long)
            result = match(type, (Long) value);
        
        if (result == null && !allowNull)
            throw new IllegalArgumentException("Date with value [" + value + "] not match with "
                    + type.getCanonicalName());
        
        return result;
    }
    
    private <T> T match(Class<T> type, java.util.Date value)
    {
        T result = null;
        if (type.getCanonicalName().equals(java.sql.Date.class.getCanonicalName()))
            result = (T) convertToDate(value);
        else if (type.getCanonicalName().equals(java.sql.Time.class.getCanonicalName()))
            result = (T) convertToTime(value);
        else if (type.getCanonicalName().equals(java.sql.Timestamp.class.getCanonicalName()))
            result = (T) convertToTimestamp(value);
        return result;
    }
    
    private <T> T match(Class<T> type, String value)
    {
        Date date = null;
        for (String pattern : patterns)
        {
            try
            {
                date = new SimpleDateFormat(pattern).parse(value);
                if (date != null)
                    break;
            }
            catch (ParseException e)
            {
            }
        }
        if (date == null)
            throw new RuntimeException("Cannot parse string date ["+value+"] using patterns ["+patterns+"]");
        return match(type, date);
        
    }
    
    private <T> T match(Class<T> type, Long value)
    {
        return match(type, new Date(value));
    }
    
    private java.sql.Timestamp convertToTimestamp(Date value)
    {
        java.sql.Timestamp result = new java.sql.Timestamp(value.getTime());
        return result;
    }
    
    private java.sql.Time convertToTime(Date value)
    {
        java.sql.Time result = new java.sql.Time(value.getTime());
        return result;
    }
    
    private java.sql.Date convertToDate(Date value)
    {
        java.sql.Date result = new java.sql.Date(value.getTime());
        return result;
    }    
}
