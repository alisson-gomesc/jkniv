package net.sf.jkniv.cache;

public interface Cacheable<T>
{
    
    String getName();
    
    CachePolicy getPolicy();
    
    T put(String key, T object);
    
    T get(String key);
    
}
