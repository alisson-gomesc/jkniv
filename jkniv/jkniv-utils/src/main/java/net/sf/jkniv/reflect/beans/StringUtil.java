/* 
 * JKNIV, utils - Helper utilities for jdk code.
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
 */package net.sf.jkniv.reflect.beans;

class StringUtil
{
    public static String getArgsToPrint(Class<?>[] args)
    {
        StringBuilder sb = new StringBuilder("(");
        if (args != null)
        {
            for (int i = 0; i < args.length; i++)
            {
                if (i > 0)
                    sb.append(", " + args[i].getName());
                else
                    sb.append(args[i].getName());
            }
        }
        sb.append(")");
        return sb.toString();
    }
    
    public static String arrayToString(Object[] values)
    {
        StringBuilder sb = new StringBuilder();
        for (Object o : values)
            sb.append((o != null ? o.toString() : "null") + (sb.length() > 0 ? "," : ""));
        
        return sb.toString();
    }
    
    public static String arrayToClass(Object[] values)
    {
        StringBuilder sb = new StringBuilder();
        for (Object o : values)
            sb.append((o != null ? o.getClass().getName() : "null") + (sb.length() > 0 ? "," : ""));
        
        return sb.toString();
    }

}
