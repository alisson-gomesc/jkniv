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
package net.sf.jkniv.sqlegance.types;

/**
 * A TRUE|FALSE converter value.
 * 
 * The {@code pattern} format is: true|false, where the values can be any string value.
 * 
 * <pre>
 * {@literal @}Converter(converter = BooleanIntType.class,pattern = "1|0")
 * </pre>
 */
public class BooleanIntType implements Convertible<Boolean, Integer>
{
    private int truePattern;
    private int falsePattern;

    public BooleanIntType(String pattern)
    {
        String[] patterns = pattern.split("\\|");
        if (patterns.length != 2)
            throw new ConverterException("BooleanStringType expect a separator \"|\" to handle true and false values, for example \"1|0\". The value was: "+pattern);
        this.truePattern = Integer.valueOf(patterns[0]);
        this.falsePattern = Integer.valueOf(patterns[1]);
    }
    
    @Override
    public Integer toJdbc(Boolean attribute)
    {
        if (attribute == null)
            return null;
        
        return (attribute.booleanValue() ? truePattern : falsePattern);
    }

    @Override
    public Boolean toAttribute(Integer jdbc)
    {
        if (jdbc == null)
            return null;
        
        return (jdbc.equals(truePattern) ? Boolean.TRUE : Boolean.FALSE);
    }

    @Override
    public Class<Boolean> getType()
    {
        return Boolean.class;
    }
    
    @Override
    public ColumnType getColumnType() 
    {
        return JdbcType.INTEGER;
    }

    @Override
    public String toString()
    {
        return "BooleanIntType [truePattern=" + truePattern + ", falsePattern=" + falsePattern + ", type="
                + getType() + ", columnType=" + getColumnType() + "]";
    }


}
