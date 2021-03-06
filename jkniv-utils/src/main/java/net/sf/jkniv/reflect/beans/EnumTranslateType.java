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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
class EnumTranslateType  implements TranslateType //extends AbstractConverter
{
    private static final Logger LOG = LoggerFactory.getLogger(EnumTranslateType.class);
    
    public EnumTranslateType()
    {
        super();
    }
    
    public <T> T convert(Class<T> type, Object value)
    {
        T result = null;
        if (!type.isEnum())
            throw new IllegalArgumentException("The type must be Enum, but was [" + type + "]");
        
        if (value instanceof String)
            result = match(type, (String) value);
        else if (value instanceof Integer)
            result = match(type, (Integer) value);

        return result;
    }
    
    private <T> T match(Class<T> type, String value)
    {
        Class<Enum> enumTypes = (Class<Enum>) type;
        T result = null;
        for (Enum enumValue : enumTypes.getEnumConstants())
        {
            if (enumValue.name().equals(value))
            {
                result = type.cast(enumValue);
                break;
            }
        }
        return result;
    }
    
    private <T> T match(Class<T> type, Integer value)
    {
        Class<Enum> enumTypes = (Class<Enum>) type;
        T result = null;
        for (Enum enumValue : enumTypes.getEnumConstants())
        {
            if (value.compareTo(enumValue.ordinal()) == 0)
            {
                result = (T) enumValue;
                break;
            }
        }
        return result;
    }
}
