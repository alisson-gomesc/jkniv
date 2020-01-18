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

import java.sql.Timestamp;
import java.time.Instant;

import net.sf.jkniv.whinstone.types.ColumnType;
import net.sf.jkniv.whinstone.types.Convertible;
import net.sf.jkniv.whinstone.types.JdbcType;

/**
 * Conversion type from {@code Java Instant} to {@code JDBC TIMESTAMP}
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class InstantTimestampType implements Convertible<Instant, Timestamp>
{
    public InstantTimestampType()
    {
    }
    
    public InstantTimestampType(String pattern)
    {
    }
    
    @Override
    public Timestamp toJdbc(Instant attribute)
    {
        if (attribute == null)
            return null;
        
        return Timestamp.from(attribute);
    }

    @Override
    public Instant toAttribute(Timestamp jdbc)
    {
        if (jdbc == null)
            return null;

        return jdbc.toInstant();
    }

    @Override
    public Class<Instant> getType()
    {
        return Instant.class;
    }
    
    @Override
    public ColumnType getColumnType() 
    {
        return JdbcType.TIMESTAMP;
    }

    @Override
    public String toString()
    {
        return "InstantTimestampType [type=" + getType() + ", columnType=" + getColumnType() + "]";
    }
}
