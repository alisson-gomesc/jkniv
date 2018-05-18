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

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryCache<T> implements Cacheable<T>
{
    private String      name;
    private CachePolicy policy;
    private Map<String, Entry<T>> cache;
    
    
    public MemoryCache()
    {
        this(new TTLCachePolicy(0L));
    }

    public MemoryCache(CachePolicy policy)
    {
        this.policy = policy;
        this.cache = new ConcurrentHashMap<String, Entry<T>>();
    }

    /* (non-Javadoc)
     * @see net.sf.jkniv.cache.Cacheable#getName()
     */
    @Override
    public String getName()
    {
        return name;
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.cache.Cacheable#getPolicy()
     */
    @Override
    public CachePolicy getPolicy()
    {
        return policy;
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.cache.Cacheable#put(java.lang.String, T)
     */
    @Override
    public T put(String key, T object)
    {
        MemoryCache.Entry<T> entry = new MemoryCache.Entry<T>(object);
        MemoryCache.Entry<T> old = this.cache.put(key, entry);
        if (old != null)
            return old.getValue();
        
        return null;
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.cache.Cacheable#get(java.lang.String)
     */
    @Override
    public T get(String key)
    {
        MemoryCache.Entry<T> entry = this.cache.get(key);
        if (!policy.isAlive(entry.getTimestamp().getTime()))
            return null;
        
        return entry.getValue();
    }
    
    static class Entry<T>
    {
        final Date timestamp;
        T          value;
        
        public Entry(T value)
        {
            this.timestamp = new Date();
            this.value = value;
        }
        
        public final Date getTimestamp()
        {
            return timestamp;
        }
        
        public final T getValue()
        {
            return value;
        }
    }
    
}
