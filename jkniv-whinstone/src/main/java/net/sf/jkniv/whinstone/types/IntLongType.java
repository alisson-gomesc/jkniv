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
 * Conversion type from {@code Java Integer} to {@code JDBC BIGINT}.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class IntLongType implements Convertible<Integer, Long>
{
    public IntLongType()
    {
    }
    
    public IntLongType(String pattern)
    {
    }
    
    @Override
    public Long toJdbc(Integer attribute)
    {
        if (attribute == null)
            return null;
        
        return attribute.longValue();
    }

    @Override
    public Integer toAttribute(Long jdbc)
    {
        if (jdbc == null)
            return null;
        
        return jdbc.intValue();
    }

    @Override
    public Class<Integer> getType()
    {
        return Integer.class;
    }
    
    @Override
    public ColumnType getColumnType() 
    {
        return JdbcType.BIGINT;
    }

    @Override
    public String toString()
    {
        return "IntLongType [type="
                + getType() + ", columnType=" + getColumnType() + "]";
    }
}
