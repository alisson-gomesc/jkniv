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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;

/**
 * MemoryCache based implementation of the <tt>Cacheable</tt> interface. 
 * This implementation provides a memory cache to keep the objects mapped keys to values.
 * The <tt>Cacheable</tt> doesn't permit {@code null} values.
 *
 * Default policy parameters:
 * <ul>
 *  <li>Time-to-Live (TTL) default value is 10 minutes</li>
 *  <li>Time-to-idle (TTI) default value is forever</li>
 *  <li>Cache size default value is 1000 elements</li>
 * </ul>
 * @param <K> the type of keys maintained by this map
 * @param <V> Type of objects stored in cache
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class MemoryCache<K, V> implements Cacheable<K, V>
{
    private static final transient Assertable    notNull = AssertsFactory.getNotNull();
    private String                               name;
    private CachePolicy                          policy;
    private ConcurrentMap<K, Cacheable.Entry<V>> cache;
    private Cacheable.Entry<V> minorHit;
    private K keyMinorHit;
    
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
        this.cache = new ConcurrentHashMap<K, Cacheable.Entry<V>>();
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

    @Override
    public void setPolicy(CachePolicy policy)
    {
        this.policy = policy;
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.cache.Cacheable#put(java.lang.String, T)
     */
    @Override
    public V put(K key, V object)
    {
        notNull.verify(key, object);
        Cacheable.Entry<V> entry = new MemoryCache.Entry<V>(object);
        if (this.cache.size() >= policy.size())
        {
            if (this.keyMinorHit != null)
                remove(this.keyMinorHit);
        }
        else
        {
            Cacheable.Entry<V> old = this.cache.put(key, entry);
            if (old != null)
                return old.getValue();
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.cache.Cacheable#get(java.lang.String)
     */
    @Override
    public V get(K key)
    {
        Cacheable.Entry<V> entry = getEntry(key);
        if (entry == null )
            return null;
        
        return entry.getValue();
    }
    
    public Cacheable.Entry<V> getEntry(K key)
    {
        Cacheable.Entry<V> entry = this.cache.get(key);
        
        if (entry == null )
            return null;

        markMinorHit(entry);
        
        if (policy.isAlive(entry.getTimestamp().getTime(), entry.getLastAccess().getTime()))
            return entry;
        
        this.cache.remove(key);
        return null;
    }
    
    private void markMinorHit(Cacheable.Entry<V> entry)
    {
        if (minorHit == null)
            minorHit = entry;
        else
        {
            if(minorHit.hits() > entry.hits())
                minorHit = entry;
        }
    }
    
    public Set<Map.Entry<K, Cacheable.Entry<V>>> entrySet()
    {
        return this.cache.entrySet();
    }
    
    public Cacheable.Entry<V> remove(K key)
    {
        return this.cache.remove(key);
    }
    
    @Override
    public void clear()
    {
        this.cache.clear();
        this.minorHit = null;
    }
    
    @Override
    public long size()
    {
        return this.cache.size();
    }
    
    @Override
    public String toString()
    {
        return "MemoryCache [name=" + name + ", policy=" + policy + ", cacheSize=" + cache.size() + "]";
    }


    static class Entry<V> implements Cacheable.Entry<V>
    {
        final Date timestamp;
        Date       lastAccess;
        V          value;
        int hits;
        
        public Entry(V value)
        {
            this.timestamp = new Date();
            this.lastAccess = new Date();
            this.value = value;
            this.hits = 0;
        }
        
        @Override
        public final Date getTimestamp()
        {
            return timestamp;
        }
        
        @Override
        public Date getLastAccess()
        {
            return this.lastAccess;
        }
        
        @Override
        public final V getValue()
        {
            this.lastAccess = new Date();
            hits++;
            return value;
        }
        
        @Override
        public int hits()
        {
            return this.hits;
        }
        
    }
    
}
