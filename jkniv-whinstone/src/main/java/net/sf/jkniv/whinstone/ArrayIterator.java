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
package net.sf.jkniv.whinstone;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class ArrayIterator implements Iterator<Param>
{
    private final Object[] array;
    private int   currentIndex = 0;
    private int   length;

    public ArrayIterator(Map<String,Object> map)
    {
        this.currentIndex = 0;
        this.array = new Object[map.size()];
        Set<Entry<String,Object>> entries = map.entrySet();
        int i=0;
        for(Entry<String, Object> entry : entries)
            this.array[i] = entry.getValue();
        //this.array = map.toArray();
        this.length = map.size();
    }

    public ArrayIterator(Collection<?> col)
    {
        this.currentIndex = 0;
        this.array = col.toArray();
        this.length = col.size();
    }

    public ArrayIterator(Object[] array, int length)
    {
        this.currentIndex = 0;
        this.array = array;
        this.length = length;
    }
    
    @Override
    public boolean hasNext()
    {
        return currentIndex < length;
    }
    
    @Override
    public Param next()
    {
        return new Param(Array.get(array, currentIndex++), currentIndex-1);
    }
    
    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("cannot remove elements from array");
    }
    
}
