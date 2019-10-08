/* 
 * JKNIV, SQLegance keeping queries maintainable.
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
package net.sf.jkniv.sqlegance.type;

import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeNumberConverter implements Convertible<Date, Number>
{
    private final static int[] TYPES = {Types.INTEGER, Types.BIGINT};
    private final static SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmss");
    
    public static final DateTimeNumberConverter INSTANCE = new DateTimeNumberConverter();
    
    @Override
    public Number toJdbc(Date attribute)
    {
        if (attribute == null)
            return null;
        
        return Integer.valueOf(SDF.format(attribute));
    }

    @Override
    public Date toAttribute(Number jdbc)
    {
        Date date = null;
        if (jdbc == null)
            return null;
        
        try
        {
            date = SDF.parse(String.valueOf(jdbc));
        }
        catch (ParseException e)
        {
            throw new ConvertException("Cannot convert number ["+jdbc+"] to date the format must be [yyyyMMddHHmmss]", e);
        }
        return date;
    }

    @Override
    public int[] getTypes()
    {
        return TYPES;
    }

    @Override
    public Class<Date> getClassType()
    {
        return Date.class;
    }
    
}
