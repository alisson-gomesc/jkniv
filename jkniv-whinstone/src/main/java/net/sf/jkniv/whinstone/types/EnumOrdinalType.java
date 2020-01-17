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
package net.sf.jkniv.whinstone.types;

public class EnumOrdinalType implements Convertible<Enum<?>, Integer>
{
    private Class<?> enumType;
    
    public EnumOrdinalType(Class<?> enumType)
    {
        this.enumType = enumType;
    }
    
    @Override
    public Integer toJdbc(Enum<?> attribute)
    {
        if (attribute == null)
            return null;
        
        return attribute.ordinal();
    }

    @Override 
    public Enum<?> toAttribute(Integer jdbc)
    {
        if (jdbc == null)
            return null;
        
        return (Enum<?>) this.enumType.getEnumConstants()[jdbc.intValue()];
    }

    @Override @SuppressWarnings({ "rawtypes", "unchecked" })
    public Class getType()
    {
        return enumType;
    }
    
    @Override
    public ColumnType getColumnType() 
    {
        return JdbcType.INTEGER;
    }

    @Override
    public String toString()
    {
        return "EnumOrdinalType [enumType=" + enumType +  ", type="
                + getType() + ", columnType=" + getColumnType() + "]";
    }
}
