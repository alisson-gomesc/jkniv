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

/**
 * A Enumeration converter value.
 * 
 * For save then name values of Enumeration.
 * <pre>
 * {@literal @}Converter(converter = TimeUnit.class, isEnum = EnumType.STRING)
 * </pre>
 * 
 * For save then ordinal value of Enumeration.
 * <pre>
 * {@literal @}Converter(converter = TimeUnit.class, isEnum = EnumType.ORDINAL)
 * </pre>
 * 
 * If any value is used for {@code isEnum} the {@code EnumType.STRING} is used per default.
 * <pre>
 * {@literal @}Converter(converter = TimeUnit.class)
 * </pre>
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class EnumNameType implements Convertible<Enum<?>, String>
{
    private Class<?> enumType;
    
    public EnumNameType(Class<?> enumType)
    {
        this.enumType = enumType;
    }
    
    @Override
    public String toJdbc(Enum<?> attribute)
    {
        if (attribute == null)
            return null;
        
        return attribute.name();
    }

    @Override @SuppressWarnings({ "unchecked" })
    public Enum<?> toAttribute(String jdbc)
    {
        if (jdbc == null)
            return null;
        
        return Enum.valueOf(getType(), jdbc);
    }

    @Override @SuppressWarnings({ "rawtypes", "unchecked" })
    public Class getType()
    {
        return this.enumType;
    }
    
    @Override
    public ColumnType getColumnType() 
    {
        return JdbcType.VARCHAR;
    }

    @Override
    public String toString()
    {
        return "EnumNameType [enumType=" + enumType + ", type="
                + getType() + ", columnType=" + getColumnType() + "]";
    }
}
