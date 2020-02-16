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
package net.sf.jkniv.whinstone.jdk8.types;

import java.time.Duration;

import net.sf.jkniv.whinstone.types.ColumnType;
import net.sf.jkniv.whinstone.types.Convertible;
import net.sf.jkniv.whinstone.types.JdbcType;

/**
 * Conversion type from {@code Java java.time.Duration} to {@code JDBC DURATION}.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class DurationStringType implements Convertible<Duration, String>
{
    private String pattern;
    
    public DurationStringType()
    {
        super();
    }
    
    public DurationStringType(String pattern)
    {
        this();
        this.pattern = pattern;
    }
    
    @Override
    public String toJdbc(Duration attribute)
    {
        if (attribute == null)
            return null;
        
        return attribute.toString();
    }

    @Override
    public Duration toAttribute(String jdbc)
    {
        if (jdbc == null)
            return null;
        
        return Duration.parse(jdbc);
    }

    @Override
    public Class<Duration> getType()
    {
        return Duration.class;
    }
    
    @Override
    public ColumnType getColumnType() 
    {
        return JdbcType.VARCHAR;
    }

    @Override
    public String toString()
    {
        return "DurationStringType [pattern=" + pattern + ", type="
                + getType() + ", columnType=" + getColumnType() + "]";
    }
}
