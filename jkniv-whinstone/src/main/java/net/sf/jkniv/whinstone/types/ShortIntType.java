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
 * Conversion type from {@code Java Short} to {@code JDBC Integer}. This conversion has default usage.
 * 
 * <i>Numeric type promotion</i> some drivers keep the {@code Short} values as {@code Integer}
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class ShortIntType implements Convertible<Short, Integer>
{
    public ShortIntType()
    {
    }
    
    public ShortIntType(String pattern)
    {
    }
    
    @Override
    public Integer toJdbc(Short attribute)
    {
        if (attribute == null)
            return null;
        
        return attribute.intValue();
    }

    @Override
    public Short toAttribute(Integer jdbc)
    {
        if (jdbc == null)
            return null;
        
        return jdbc.shortValue();
    }

    @Override
    public Class<Short> getType()
    {
        return Short.class;
    }
    
    @Override
    public ColumnType getColumnType() 
    {
        return JdbcType.SMALLINT;
    }

    @Override
    public String toString()
    {
        return "ShortIntType [type="
                + getType() + ", columnType=" + getColumnType() + "]";
    }
}
