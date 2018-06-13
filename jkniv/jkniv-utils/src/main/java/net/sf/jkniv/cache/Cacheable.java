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

/**
 * An object that maps keys to values. A map cannot contain duplicate keys;
 * each key can map to at most one value.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> Type of objects stored in cache
 *
 * @author Alisson Gomes
 * @since 0.6.0
 */
public interface Cacheable<K, V>
{
    /**
     * cache name
     * @return the cache name 
     */
    String getName();
    
    /**
     * policy that maintenance the capacity and expire time of data
     * @return rules to cache maintain the data 
     */
    CachePolicy getPolicy();
    
    /**
     * Policy to control the cache data
     * @param policy that maintenance the capacity and expire time of data
     */
    void setPolicy(CachePolicy policy);
    
    /**
     * Associates the specified value with the specified key in this cache
     * If the cache previously contained a mapping for the key, the old value 
     * is replaced by the specified value.
     * @param key with which the specified value is to be associated
     * @param value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     * @throws IllegalArgumentException if some the <tt>key</tt> or <tt>value</tt> are {@code null}.
     */
    V put(K key, V value);

    /**
     * Returns the value to which the specified key is cached,
     * or {@code null} if this cache contains no mapping for the key.
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is cached, or
     *         {@code null} if this map contains no mapping for the key
     */
    V get(K key);
    
    /**
     * Returns a {@link Entry} view of the mappings contained in this cache.
     * @param key the key whose associated with entry is to be returned
     * @return a set view of the {@link Entry} contained in this cache, or
     *         {@code null} if this map contains no mapping for the key
     */
    Cacheable.Entry<V> getEntry(K key);
    
    Set<java.util.Map.Entry<K, Cacheable.Entry<V>>> entrySet();
    
    Cacheable.Entry<V> remove(K key);

    void clear();
    
    int size();
    
    /**
     * A cache entry (key-value pair).  The <tt>Cacheable.getEntry</tt> method returns
     * a cache value view.
     *
     * @see Cacheable#getEntry(Object)
     */
    interface Entry<V>
    {
        /**
         * Timestamp from cached value
         * @return the timestamp when the value was cached
         */
        Date getTimestamp();
        
        /**
         * Last access the cached value
         * @return the timestamp from last access 
         */
        Date getLastAccess();
        
        /**
         * Entry value from cache
         * @return the value to which the entry cached, or
         *         {@code null} if this cache contains no entry
         */
        V getValue();
    }
    
}
