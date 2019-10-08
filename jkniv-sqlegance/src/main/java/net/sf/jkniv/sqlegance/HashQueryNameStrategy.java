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
package net.sf.jkniv.sqlegance;

public class HashQueryNameStrategy implements QueryNameStrategy
{
    public String toGetName(Object o)
    {
        if (o instanceof Class)
            return ((Class<?>) o).getSimpleName() + "#get";
        else
            return o.getClass().getSimpleName() + "#get";
    }
    
    public String toAddName(Object o)
    {
        if (o instanceof Class)
            return ((Class<?>) o).getSimpleName() + "#add";
        else
            return o.getClass().getSimpleName() + "#add";
    }
    
    public String toRemoveName(Object o)
    {
        if (o instanceof Class)
            return ((Class<?>) o).getSimpleName() + "#remove";
        else
            return o.getClass().getSimpleName() + "#remove";
    }
    
    public String toUpdateName(Object o)
    {
        if (o instanceof Class)
            return ((Class<?>) o).getSimpleName() + "#update";
        else
            return o.getClass().getSimpleName() + "#update";
    }
    
    public String toListName(Object o)
    {
        if (o instanceof Class)
            return ((Class<?>) o).getSimpleName() + "#list";
        else
            return o.getClass().getSimpleName() + "#list";
    }
}
