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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager the live of cache entries.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class CacheManager<K, V>
{
    private static final Logger            LOG                 = LoggerFactory.getLogger(CacheManager.class);
    private final ScheduledExecutorService scheduler           = Executors.newScheduledThreadPool(1);
    private final Map<String, Cacheable<K,V>> caches;
    private final Map<String, CachePolicy> policies;
    private ScheduledFuture<?> poolScheduler; 
    private final long delay;
    private final long period;
    private final CachePolicy policy;
    
    /**
     * Create a new cache manager with 5 seconds for initial delay to first execution
     * and 5 minutes for period between successive executions.
     */
    public CacheManager()
    {
        this(5L, TimeUnit.MINUTES.toSeconds(5L));
    }

    /**
     * Create a new cache manager with 5 seconds for initial delay to first execution
     * and configurable parameter for {@code period} to between successive executions.
     * @param period the period between successive executions
     */
    public CacheManager(long period)
    {
        this(5L, period);
    }

    /**
     * Create a new cache manager with configurable {@code initialDelay} and {@code period}.
     * @param initialDelay seconds to the time to delay first execution
     * @param period seconds to the period between successive executions
     */
    public CacheManager(long initialDelay, long period)
    {
        this(initialDelay, period, new TTLCachePolicy(CachePolicy.DEFAULT_TTL, CachePolicy.DEFAULT_TTI, TimeUnit.MINUTES));
    }
    

    /**
     * Create a new cache manager with configurable {@code initialDelay} and {@code period}.
     * @param delay seconds to the time to delay first execution
     * @param period seconds to the period between successive executions
     * @param policy default policy for cache entries {@link #add(String, Cacheable)}
     */
    public CacheManager(long delay, long period, CachePolicy policy)
    {
        this.delay = delay;
        this.period = period;
        this.caches = new HashMap<String, Cacheable<K, V>>();
        this.policies = new HashMap<String, CachePolicy>();
        this.policy = policy;
    }

    public CachePolicy add(String key, CachePolicy policy)
    {
        return this.policies.put(key, policy);
    }

    public Cacheable<K, V> add(String key, Cacheable<K, V> cache)
    {
        return this.caches.put(key, cache);
    }

    public Cacheable<K, V> add(String key, String policyName, Cacheable<K, V> cache)
    {
        CachePolicy policy = this.policies.get(policyName);
        if (policy == null)
            policy = this.policy;
        
        cache.setPolicy(policy);
        return this.caches.put(key, cache);
    }

    
    public Cacheable<K, V> add(String key)
    {
        Cacheable<K, V> cacheable = new MemoryCache<K, V>(this.policy, key);
        this.caches.put(key, cacheable);
        return cacheable;
    }

    public void pooling()
    {
        final Runnable watching = new WatchCaches(caches);
        this.poolScheduler = scheduler.scheduleAtFixedRate(watching, this.delay, this.period, TimeUnit.SECONDS);
        LOG.info("Watching cacheable data");
    }
    
    public void cancel()
    {
        LOG.info("Canceling cacheable manager");
        this.clear();
        this.poolScheduler.cancel(true);
    }

    private void clear()
    {
        Set<Map.Entry<String, Cacheable<K, V>>> entries = caches.entrySet();
        for (Map.Entry<String, Cacheable<K, V>> entry : entries)
        {
            Cacheable<K, V> cacheable = entry.getValue();
            cacheable.clear();
        }
        caches.clear();
    }

    public long size()
    {
        long size = 0L;
        for (Map.Entry<String, Cacheable<K, V>> entry : caches.entrySet())
        {
            size += entry.getValue().size();
        }
        return size;
    }

    class WatchCaches implements Runnable
    {
        private final Map<String, Cacheable<K, V>> caches;
        
        public WatchCaches(Map<String, Cacheable<K, V>> caches)
        {
            this.caches = caches;
        }
        
        @Override
        public void run()
        {
            LOG.info("Manager [{}] caches", caches.size());
            Set<Map.Entry<String, Cacheable<K, V>>> entries = caches.entrySet();
            for (Map.Entry<String, Cacheable<K, V>> entry : entries)
            {
                Cacheable<K, V> cacheable = entry.getValue();
                Set<Map.Entry<K, Cacheable.Entry<V>>> entrySet = cacheable.entrySet();
                
                for (Map.Entry<K, Cacheable.Entry<V>> cacheableEntry : entrySet)
                {
                    Cacheable.Entry<V> value = cacheableEntry.getValue();
                    if (!cacheable.getPolicy().isAlive(value.getTimestamp().getTime()))
                    {
                        cacheable.remove(cacheableEntry.getKey());
                        LOG.info("Removing cache [{}]", cacheableEntry.getKey());
                    }
                }
            }
        }
    }
    
}
