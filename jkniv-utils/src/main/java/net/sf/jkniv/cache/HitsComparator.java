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
 */
package net.sf.jkniv.cache;

import java.util.Comparator;

@SuppressWarnings("rawtypes")
class HitsComparator implements Comparator<Cacheable.Entry>
{

    @Override
    public int compare(Cacheable.Entry c1, Cacheable.Entry c2)
    { 
        int LESS = -1, EQUAL = 0, GREATER = 1;
        return c1.hits() < c2.hits() ? LESS : c1.hits() == c2.hits() ?  EQUAL : GREATER;
    }
    
}