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
package net.sf.jkniv.reflect.beans;


/**
 * Convert object value to Boolean value.
 * 
 * if String, all the below values are converted to <code>Boolean.TRUE</code>:
 * <pre>
 *   "true", "1", "Y", "T"
 * </pre>
 * any other is <code>Boolean.FALSE</code>.
 * <br><br>
 *
 * if number, the number one (1) is <code>Boolean.TRUE</code> any other is <code>Boolean.FALSE</code>.
 * 
 * @author Alisson Gomes 
 * @deprecated Needs change Argument Converter
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
class BooleanConverter extends AbstractConverter // TODO Test ME
{
    
    public BooleanConverter()
    {
        // true patterns TODO Test Case true patterns
        super(true);
        this.patterns.add("true");
        this.patterns.add("1");
        this.patterns.add("Y");
        this.patterns.add("T");
    }
    
    public <T> T convert(Class<T> type, Object value)
    {
        T result = null;
        if (value == null && allowNull)
            return (T) Boolean.FALSE;
        
        if (value instanceof Boolean)
            result = (T) value;
        else if (value instanceof Number)
            result = (T) (((Number)value).longValue() == 1 ? Boolean.TRUE : Boolean.FALSE);
        else if (value instanceof String)
            result = match(type, (String) value);
        
        if (result == null && !allowNull)
            throw new IllegalArgumentException("Boolean with value [" + value + "] not match with "
                    + type.getCanonicalName());
        
        return result;
    }
    
    private <T> T match(Class<T> type, String value)
    {
        Boolean result = Boolean.FALSE;
        for (String pattern : patterns)
        {
            if (pattern.equalsIgnoreCase(value))
            {
                result = Boolean.TRUE;
                break;
            }
        }
        return (T) result;
    }
    
}
