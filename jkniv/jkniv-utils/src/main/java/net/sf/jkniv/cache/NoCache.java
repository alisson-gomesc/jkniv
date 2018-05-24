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

import java.util.Set;

public class NoCache<K, V> implements Cacheable<K, V>
{
    private static final String name = "NoCache";
    private static final CachePolicy policy = new TTLCachePolicy(0L);
    private static final Cacheable<Object,Object> instance = new NoCache<Object,Object>();
    
    private NoCache()
    {
    }
    
    @SuppressWarnings("unchecked")
    public static <K,V> Cacheable<K,V>  getInstance()
    {
        return (Cacheable<K,V>)instance;
    }
    
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public CachePolicy getPolicy()
    {
        return policy;
    }

    @Override
    public V put(K key, V object)
    {
        return null;
    }

    @Override
    public V get(K key)
    {
        return null;
    }

    @Override
    public Cacheable.Entry<V> getEntry(K key)
    {
        return null;
    }
    
    @Override
    public Set<java.util.Map.Entry<K, net.sf.jkniv.cache.Cacheable.Entry<V>>> entrySet()
    {
        return null;
    }
    
    @Override
    public Cacheable.Entry<V> remove(K key)
    {
        return null;
    }

    @Override
    public void clear()
    {
    }
    
    @Override
    public int size()
    {
        return 0;
    }

}
