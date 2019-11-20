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

public class TrueType implements Convertible<Boolean, String>
{
    private final static int[] TYPES = {Types.CHAR, Types.VARCHAR, Types.NCHAR, Types.NVARCHAR};
    //private final static String FALSE = "FALSE", TRUE = "TRUE";
    //private final static TrueType INSTANCE = new TrueType();
    //private final static ThreadLocal<String> PATTERN = new ThreadLocal<String>();
    private String truePattern;
    private String falsePattern;

    public TrueType(String pattern)
    {
        String[] patterns = pattern.split("\\|");
        if (patterns.length != 2)
            throw new ConverterException("TrueType expect a separator \"|\" to handle true and false values, for example \"1|0\". The value was: "+pattern);
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
