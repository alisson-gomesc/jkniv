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

public class UnknowType implements ColumnType
{
    private final static UnknowType INSTANCE = new UnknowType();
    
    private UnknowType()
    {
    }

    public static ColumnType getInstance()
    {
        return INSTANCE;
    }
    
    @Override
    public String name()
    {
        return "UNKNOW";
    }
    
    @Override
    public int value()
    {
        return -1;
    }
    
    @Override
    public boolean isBinary()
    {
        return false;
    }
    
    @Override
    public boolean isBlob()
    {
        return false;
    }
    
    @Override
    public boolean isClob()
    {
        return false;
    }
    
    @Override
    public boolean isDate()
    {
        return false;
    }
    
    @Override
    public boolean isTimestamp()
    {
        return false;
    }
    
    @Override
    public boolean isTime()
    {
        return false;
    }

    @Override
    public String toString()
    {
        return "UnknowType [name=" + name() + ", value=" + value() + ", isBinary=" + isBinary() + ", isBlob="
                + isBlob() + ", isClob=" + isClob() + ", isDate=" + isDate() + ", isTimestamp=" + isTimestamp()
                + ", isTime=" + isTime() + "]";
    }

}
