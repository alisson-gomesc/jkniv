package net.sf.jkniv.cache;

/**
 * An object that maps keys to values.  A map cannot contain duplicate keys;
 * each key can map to at most one value.
 * @author Alisson Gomes
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> Type of objects stored in cache
 */
public interface Cacheable<K,V>
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
     * Associates the specified value with the specified key in this map
     * (optional operation).  If the map previously contained a mapping for
     * the key, the old value is replaced by the specified value.
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>,
     *         if the implementation supports <tt>null</tt> values)     
     * @throws IllegalArgumentException if some property of the specified element
     *         prevents it from being added to this set (`{@code null} values for example.
     */
    V put(K key, V value);

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or
     *         {@code null} if this map contains no mapping for the key
     */
    V get(K key);
    
}
