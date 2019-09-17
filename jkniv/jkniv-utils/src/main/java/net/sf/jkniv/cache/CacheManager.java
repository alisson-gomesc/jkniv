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
 * To enable logging for cache configure logger category:
 * 
 * <pre>
 *   &#60;logger name="jkniv.whinstone.CACHE" additivity="false"&#62;
 *     &#60;level value="debug" /&#62;
 *     &#60;appender-ref ref="console" /&#62;
 *   &#60;/logger&#62;
 * </pre>
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class CacheManager<K, V>
{
    private static final Logger                LOG       = LoggerFactory.getLogger("jkniv.utils.CACHE");
    private final ScheduledExecutorService     scheduler = Executors.newScheduledThreadPool(1);
    private final Map<String, Cacheable<K, V>> caches;
    private final Map<String, CachePolicy>     policies;
    private ScheduledFuture<?>                 poolScheduler;
    private final long                         delay;
    private final long                         period;
    private final CachePolicy                  policy;
    
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
        this(initialDelay, period,
                new TTLCachePolicy(CachePolicy.DEFAULT_TTL, CachePolicy.DEFAULT_TTI, TimeUnit.MINUTES));
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
    
    /**
     * Add new policy with specific name
     * @param policyName name of policy
     * @param policy policy to manager a cache
     * @return the previous value associated with <code>policyName</code>, 
     * or <code>null</code> if there was no mapping for <code>policyName</code>
     */
    public CachePolicy add(String policyName, CachePolicy policy)
    {
        return this.policies.put(policyName, policy);
    }
    
    /**
     * Add a new cache for manager
     * @param key cache identify 
     * @param cache the managed cache
     * @return the previous value associated with <code>key</code>, 
     * or <code>null</code> if there was no mapping for <code>key</code>
     */
    public Cacheable<K, V> add(String key, Cacheable<K, V> cache)
    {
        return this.caches.put(key, cache);
    }
    
    /**
     * Add a new cache for manager
     * @param key cache identify
     * @param policyName name of policy, the cache no exist a default cache is associated
     * @param cache the managed cache
     * @return the previous value associated with <code>key</code>, 
     * or <code>null</code> if there was no mapping for <code>key</code>
     */
    public Cacheable<K, V> add(String key, String policyName, Cacheable<K, V> cache)
    {
        CachePolicy policy = this.policies.get(policyName);
        if (policy == null)
        {
            LOG.warn("There is no a cache policy named [{}] associating default cache for [{}]", policyName, key);
            policy = this.policy;
        }
        cache.setPolicy(policy);
        return this.caches.put(key, cache);
    }
    
    /**
     * Create a new Memory cache to be managed with a default policy
     * @param key cache identify
     * @return the new Cache created
     */
    public Cacheable<K, V> add(String key)
    {
        Cacheable<K, V> cacheable = new MemoryCache<K, V>(this.policy, key);
        this.caches.put(key, cacheable);
        return cacheable;
    }
    
    public void pooling()
    {
        //[jkniv-cache-man]
        final Runnable watching = new WatchCaches(caches);
        this.poolScheduler = scheduler.scheduleAtFixedRate(watching, this.delay, this.period, TimeUnit.SECONDS);
        LOG.info("Watching cacheable data for [{}] caches", size());
    }
    
    public void cancel()
    {
        LOG.info("Canceling cacheable manager");
        this.clear();
        if (this.poolScheduler != null)
            this.poolScheduler.cancel(true);
    }
    
    private void clear()
    {
        Set<Map.Entry<String, Cacheable<K, V>>> entries = caches.entrySet();
        for (Map.Entry<String, Cacheable<K, V>> entry : entries)
        {
            LOG.info("Cache [{}] has {} objects removed", entry.getValue().getName(), entry.getValue().size());
            Cacheable<K, V> cacheable = entry.getValue();
            cacheable.clear();
        }
        caches.clear();
    }
    
    /**
     * Returns the total of elements in all cache entries in this manager. 
     * @return the sum of all cache elements in this manager 
     */
    public long total()
    {
        long size = 0L;
        for (Map.Entry<String, Cacheable<K, V>> entry : caches.entrySet())
        {
            size += entry.getValue().size();
        }
        return size;
    }
    
    /**
     * Returns the number of caches in this manager
     * @return the number of cache.
     */
    public int size()
    {
        return this.caches.size();
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
            int i = 0;
            LOG.info("Managing [{}] caches", caches.size());
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
                        LOG.debug("The object [{}] was removed from cache {}", cacheableEntry.getKey(), cacheable.getName());
                        i++;
                    }
                }
                LOG.info("[{}] objects are removed from [{}] cache", i, cacheable.getName());
            }
        }
    }
    
}
