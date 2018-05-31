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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to manager the Caches and your entries.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class CacheManager<K, V>
{
    private static final Logger            LOG                 = LoggerFactory.getLogger(CacheManager.class);
    private final ScheduledExecutorService scheduler           = Executors.newScheduledThreadPool(1);
    private final ConcurrentMap<String, Cacheable<K,V>> caches;
    private ScheduledFuture<?> poolScheduler; 
    private final long initialDelay;
    private final long period;
    
    public CacheManager()
    {
        this(5L, 5L);
    }

    /**
     * @param period the period between successive executions
     */
    public CacheManager(long period)
    {
        this(5L, period);
    }

    /**
     * @param initialDelay the time to delay first execution
     * @param period the period between successive executions
     */
    public CacheManager(long initialDelay, long period)
    {
        this.initialDelay = initialDelay;
        this.period = period;
        this.caches = new ConcurrentHashMap<String, Cacheable<K, V>>();
    }

    
    public Cacheable<K, V> add(String key, Cacheable<K, V> cache)
    {
        return this.caches.put(key, cache);
    }
    
    public void pooling()
    {
        final Runnable watching = new WatchCaches(caches);
        this.poolScheduler = scheduler.scheduleAtFixedRate(watching, this.initialDelay, this.period, TimeUnit.SECONDS);
        LOG.info("Watching cacheable data");
    }
    
    public void cancel()
    {
        LOG.info("Canceling cacheable manager");
        this.poolScheduler.cancel(true);
        this.clear();
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
            LOG.info("Running cleanup cache");
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
