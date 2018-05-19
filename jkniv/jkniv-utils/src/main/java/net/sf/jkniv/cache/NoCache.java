package net.sf.jkniv.cache;

public class NoCache<K, V> implements Cacheable<K, V>
{
    private static final String name = "NoCaChe";
    private static final CachePolicy policy = new TTLCachePolicy(0L);
    
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
    
}
