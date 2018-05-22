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
import java.util.concurrent.TimeUnit;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;

/**
 * MemoryCache based implementation of the <tt>Cacheable</tt> interface. 
 * This implementation provides a memory cache to keep the objects mapped keys to values.
 * The <tt>Cacheable</tt> doesn't permit {@code null} values.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> Type of objects stored in cache
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class MemoryCache<K,V> implements Cacheable<K,V>
{
    private static final transient Assertable notNull = AssertsFactory.getNotNull();
    private String      name;
    private CachePolicy policy;
    private Map<K, Entry<V>> cache;
    
    
    public MemoryCache()
    {
        this(new TTLCachePolicy(TimeUnit.MINUTES.toSeconds(10L)));
    }

    public MemoryCache(String name)
    {
        this(new TTLCachePolicy(TimeUnit.MINUTES.toSeconds(10L)), name);
    }

    public MemoryCache(CachePolicy policy)
    {
        this(policy, "nonamed");
    }

    public MemoryCache(CachePolicy policy, String name)
    {
        this.name = name;
        this.policy = policy;
        this.cache = new ConcurrentHashMap<K, Entry<V>>();
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
    public V put(K key, V object)
    {
        notNull.verify(key, object);
        MemoryCache.Entry<V> entry = new MemoryCache.Entry<V>(object);
        MemoryCache.Entry<V> old = this.cache.put(key, entry);
        if (old != null)
            return old.getValue();
        
        return null;
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.cache.Cacheable#get(java.lang.String)
     */
    @Override
    public V get(K key)
    {
        MemoryCache.Entry<V> entry = this.cache.get(key);
        if (entry == null || !policy.isAlive(entry.getTimestamp().getTime()))
            return null;
        
        return entry.getValue();
    }

    public Cacheable.Entry<V> getEntry(K key)
    {
        return this.cache.get(key);
    }

    static class Entry<V> implements Cacheable.Entry<V>
    {
        final Date timestamp;
        V          value;
        
        public Entry(V value)
        {
            this.timestamp = new Date();
            this.value = value;
        }
        
        public final Date getTimestamp()
        {
            return timestamp;
        }
        
        public final V getValue()
        {
            return value;
        }
    }
    
}