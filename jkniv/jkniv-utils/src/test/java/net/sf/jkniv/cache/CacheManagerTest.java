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

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.concurrent.TimeUnit;

import org.hamcrest.Matchers;

import static org.hamcrest.CoreMatchers.*;

import org.junit.Ignore;
import org.junit.Test;

public class CacheManagerTest
{
    
    @Test
    public void whenCacheManagerExpireWithTTL() throws InterruptedException
    {
        CacheManager<String, Integer> cacheManager = new CacheManager<String, Integer>();
        CachePolicy policy = new TTLCachePolicy(3000L, 10000L, TimeUnit.MILLISECONDS);
        Cacheable<String, Integer> cache = new MemoryCache<String, Integer>(policy);
        cacheManager.pooling();
        Integer v = 1;
        cache.put("A", v);
        cacheManager.add("test", cache);

        v = cache.get("A");
        assertThat(v, is(1));
        Thread.sleep(1000L);
        
        v = cache.get("A");
        assertThat(v, is(1));
        Thread.sleep(1000L);
        
        v = cache.get("A");
        assertThat(v, is(1));
        Thread.sleep(1000L);
        
        v = cache.get("A");
        assertThat(v, nullValue());
    }
    
    @Test @Ignore
    public void whenCacheManagerExpireWithTTLAndClean() throws InterruptedException
    {
        CacheManager<String, Integer> cacheManager = new CacheManager<String, Integer>();
        CachePolicy policy = new TTLCachePolicy(3000L, 10000L, TimeUnit.MILLISECONDS);
        Cacheable<String, Integer> cache = new MemoryCache<String, Integer>(policy);
        cacheManager.pooling();
        Integer v = 1;
        cache.put("A", v);
        cacheManager.add("test", cache);

        v = cache.get("A");
        assertThat(v, is(1));
        Thread.sleep(1000L);
        assertThat(cacheManager.size(), is(1L));
        
        v = cache.get("A");
        assertThat(v, is(1));
        Thread.sleep(1000L);
        assertThat(cacheManager.size(), is(1L));
        
        v = cache.get("A");
        assertThat(v, is(1));
        Thread.sleep(3500L);
        assertThat(cacheManager.size(), is(0L));
        
        v = cache.get("A");
        assertThat(v, nullValue());
    }
}
