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

import java.math.BigDecimal;

/**
 * Conversion type from {@code Java Double} to {@code JDBC Decimal}. This conversion has default usage.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class DoubleBigDecimalType implements Convertible<Double, BigDecimal>
{
    public DoubleBigDecimalType()
    {
    }
    
    public DoubleBigDecimalType(String pattern)
    {
    }
    
    @Override
    public BigDecimal toJdbc(Double attribute)
    {
        if (attribute == null)
            return null;
        
        return new BigDecimal(attribute);
    }

    @Override
    public Double toAttribute(BigDecimal jdbc)
    {
        if (jdbc == null)
            return null;
        
        return jdbc.doubleValue();
    }

    @Override
    public Class<Double> getType()
    {
        return Double.class;
    }
    
    @Override
    public ColumnType getColumnType() 
    {
        return JdbcType.DECIMAL;
    }

    @Override
    public String toString()
    {
        return "DoubleBigDecimalType [type="
                + getType() + ", columnType=" + getColumnType() + "]";
    }
}
