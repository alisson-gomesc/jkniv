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
 * {@literal @}Converter(converter = BooleanCharType.class,pattern = "Y|N")
 * </pre>
 */
public class BooleanCharType implements Convertible<Boolean, String>
{
    private String truePattern;
    private String falsePattern;

    public BooleanCharType(String pattern)
    {
        String[] patterns = pattern.split("\\|");
        if (patterns.length != 2)
            throw new ConverterException("BooleanCharType expect a separator \"|\" to handle true and false values, for example \"1|0\". The value was: "+pattern);
        this.truePattern = patterns[0];
        this.falsePattern = patterns[1];
    }
    
    @Override
    public String toJdbc(Boolean attribute)
    {
        if (attribute == null)
            return null;
        
        return (attribute.booleanValue() ? truePattern : falsePattern);
    }

    @Override
    public Boolean toAttribute(String jdbc)
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
        return JdbcType.CHAR;
    }

    @Override
    public String toString()
    {
        return "BooleanCharType [truePattern=" + truePattern + ", falsePattern=" + falsePattern + ", type="
                + getType() + ", columnType=" + getColumnType() + "]";
    }
}
