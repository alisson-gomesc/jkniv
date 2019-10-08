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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * 
 * TODO document ME
 * TODO Test ME
 * @author Alisson Gomes 
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class NumberConverter extends AbstractConverter
{
    
    public NumberConverter()
    {
        super(true);
    }
        
    public <T> T convert(Class<T> type, Object value)
    {
        T result = null;
        if (value == null && allowNull)
            return null;
        
        if (value instanceof Number)
            result = match(type, (Number) value);
        else if (value instanceof String)
            result = match(type, (String) value);
        
        if (result == null && !allowNull)
            throw new IllegalArgumentException("Date with value [" + value + "] not match with "
                    + type.getCanonicalName());
        
        return result;
    }
    
    private <T> T match(Class<T> type, Number value)
    {
        String canonicalName = type.getCanonicalName();
        T result = null;
        if (type == Integer.class || "int".equals(canonicalName))
            result = (T) Integer.valueOf(value.intValue());
        else if (type == Long.class|| "long".equals(canonicalName))
            result = (T) Long.valueOf(value.longValue());
        else if (type == Double.class|| "double".equals(canonicalName))
            result = (T) Double.valueOf(value.doubleValue());
        else if (type == Float.class|| "float".equals(canonicalName))
            result = (T) Float.valueOf(value.floatValue());
        else if (type == BigDecimal.class)
            result = (T) BigDecimal.valueOf(value.doubleValue());
        else if (type == Short.class || "short".equals(canonicalName))
            result = (T) Short.valueOf(value.shortValue());
        else if (type == BigInteger.class)
            result = (T) BigInteger.valueOf(value.longValue());
        
        return result;
    }
    
    private <T> T match(Class<T> type, String value)
    {
        Number number = null;
        for (String pattern : patterns)
        {
            try
            {
                number = new DecimalFormat(pattern).parse(value);
                if (number != null)
                    break;
            }
            catch (ParseException e)
            {
            }
        }
        if (number == null)
            throw new RuntimeException("Cannot parse string number [" + value + "] using patterns [" + patterns + "]");
        
        return match(type, number);
    }    
}
