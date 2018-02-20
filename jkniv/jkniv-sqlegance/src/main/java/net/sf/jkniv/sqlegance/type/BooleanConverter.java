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

public class BooleanConverter implements Convertible<Boolean, Number>
{
    private final static int[] TYPES = {Types.BIT, Types.SMALLINT, Types.BIGINT, Types.INTEGER};
    
    public static final BooleanConverter INSTANCE = new BooleanConverter();
    
    @Override
    public Number toJdbc(Boolean attribute)
    {
        if (attribute == null)
            return 0;
        
        return (attribute.booleanValue() ? 1 : 0 );
    }

    @Override
    public Boolean toAttribute(Number jdbc)
    {
        if (jdbc == null)
            return Boolean.FALSE;
        
        return (jdbc.longValue() == 1 ? Boolean.TRUE : Boolean.FALSE);
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
