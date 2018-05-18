package net.sf.jkniv.cache;

public class NoCache<T> implements Cacheable<T>
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
    public T put(String key, T object)
    {
        return null;
    }

    @Override
    public T get(String key)
    {
        return null;
    }
    
}
