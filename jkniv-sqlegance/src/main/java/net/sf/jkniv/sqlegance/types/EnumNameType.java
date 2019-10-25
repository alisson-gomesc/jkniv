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

import java.sql.Types;

public class EnumNameType implements Convertible<Enum<?>, String>
{
    private final static int[] TYPES = {Types.CHAR, Types.VARCHAR, Types.NCHAR, Types.NVARCHAR};
    private Enum<?> enumType;
    
    public EnumNameType(Enum<?> enumType)
    {
        this.enumType = enumType;
    }
    
    @Override
    public String toJdbc(Enum<?> attribute)
    {
        if (attribute == null)
            return null;
        
        return attribute.name();
    }

    @Override @SuppressWarnings({ "unchecked" })
    public Enum<?> toAttribute(String jdbc)
    {
        if (jdbc == null)
            return null;
        
        return Enum.valueOf(getClassType(), jdbc);
    }

    @Override
    public int[] getTypes()
    {
        return TYPES;
    }

    @Override @SuppressWarnings({ "rawtypes", "unchecked" })
    public Class getClassType()
    {
        return this.enumType.getClass();
    }
    
}
