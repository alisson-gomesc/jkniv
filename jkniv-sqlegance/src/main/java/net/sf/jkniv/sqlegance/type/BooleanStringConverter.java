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

public class BooleanStringConverter implements Convertible<Boolean, String>
{
    private final static int[] TYPES = {Types.CHAR, Types.VARCHAR, Types.NCHAR, Types.NVARCHAR};
    private final static String FALSE = "FALSE", TRUE = "TRUE";
    public final static BooleanStringConverter INSTANCE = new BooleanStringConverter();

    @Override
    public String toJdbc(Boolean attribute)
    {
        if (attribute == null)
            return FALSE;
        
        return (attribute.booleanValue() ? TRUE : FALSE );
    }

    @Override
    public Boolean toAttribute(String jdbc)
    {
        if (jdbc == null)
            return Boolean.FALSE;
        
        return (TRUE.equalsIgnoreCase(jdbc) ? Boolean.TRUE : Boolean.FALSE);
    }

    @Override
    public int[] getTypes()
    {
        return TYPES;
    }

    @Override
    public Class<Boolean> getClassType()
    {
        return Boolean.class;
    }
    
}
