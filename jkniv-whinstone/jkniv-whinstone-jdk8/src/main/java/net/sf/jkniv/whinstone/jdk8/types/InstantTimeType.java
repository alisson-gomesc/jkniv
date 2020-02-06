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

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import net.sf.jkniv.whinstone.types.CassandraType;
import net.sf.jkniv.whinstone.types.ColumnType;
import net.sf.jkniv.whinstone.types.Convertible;

/**
 * Conversion type from {@code Java Instant} to {@code JDBC TIMESTAMP}
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class InstantTimeType implements Convertible<Instant, Time>
{
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    public InstantTimeType()
    {
    }
    
    public InstantTimeType(String pattern)
    {
    }
    
    @Override
    public Time toJdbc(Instant attribute)
    {
        if (attribute == null)
            return null;
        
        LocalDateTime localDateTime = LocalDateTime.ofInstant(attribute, ZoneOffset.UTC);
        String dateTimeFormatter = FORMATTER.format(localDateTime);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
        Time time = null;
        try
        {
            time = new Time(sdf.parse(dateTimeFormatter).getTime());
        }
        catch (ParseException e)
        {
            time = new Time(attribute.toEpochMilli());// last chance, but pass Date to Time Zone
        }
        return time;
    }

    @Override
    public Instant toAttribute(Time jdbc)
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
        return CassandraType.TIME;
    }

    @Override
    public String toString()
    {
        return "InstantTimeType [type=" + getType() + ", columnType=" + getColumnType() + "]";
    }
}
