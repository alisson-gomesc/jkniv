package net.sf.jkniv.whinstone.cassandra;

import java.util.concurrent.TimeUnit;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

import net.sf.jkniv.cache.CacheManager;
import net.sf.jkniv.cache.CachePolicy;
import net.sf.jkniv.cache.Cacheable;
import net.sf.jkniv.cache.TTLCachePolicy;

class StatementCache
{
    private final CacheManager<String, PreparedStatement> manager;
    
    private final Session session;
    private final Cacheable<String, PreparedStatement> cache;
    
    public StatementCache(Session session)
    {
        this.session = session;
        CachePolicy policy = new TTLCachePolicy(120, 5, TimeUnit.MINUTES);
        this.manager = new CacheManager<String, PreparedStatement>(TimeUnit.MINUTES.toSeconds(10), 
                                                                   TimeUnit.MINUTES.toSeconds(30), policy);
        this.cache = this.manager.add("cassandra-stmt");
    }
    
    public PreparedStatement prepare(String cql)
    {
        PreparedStatement ps = cache.get(cql);
        if (ps == null)
        {
            ps = session.prepare(cql);
            cache.put(cql, ps);
        }
        return ps;
    }
    
    public void clear()
    {
        this.cache.clear();
    }
    
    public long size()
    {
        return this.cache.size();
    }

}
