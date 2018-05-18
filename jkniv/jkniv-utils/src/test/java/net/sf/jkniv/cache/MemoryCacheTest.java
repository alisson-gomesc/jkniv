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

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class MemoryCacheTest
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  

    @Test
    public void whenPutInvalidValueInMemoryCache()
    {
        catcher.expect(IllegalArgumentException.class);
        catcher.expectMessage("[Assertion failed] - this argument is required; it must not be null");
        Cacheable<String> cache = new MemoryCache<String>();
        cache.put("1", null); // illegal argument
    }

    @Test
    public void whenPutInvalidKeyInMemoryCache()
    {
        catcher.expect(IllegalArgumentException.class);
        catcher.expectMessage("[Assertion failed] - this argument is required; it must not be null");
        Cacheable<String> cache = new MemoryCache<String>();
        cache.put(null, "A"); // illegal argument
    }
    
    @Test
    public void whenPutInvalidKeyAndValueInMemoryCache()
    {
        catcher.expect(IllegalArgumentException.class);
        catcher.expectMessage("[Assertion failed] - this argument is required; it must not be null");
        Cacheable<String> cache = new MemoryCache<String>();
        cache.put(null, null); // illegal argument
    }
    
    
    @Test
    public void whenMemoryCacheAlwaysExpire() throws InterruptedException
    {
        Cacheable<String> cache = new MemoryCache<String>();
        String oldValue = cache.put("1", "A");
        String value = cache.get("1");
        assertThat(oldValue, nullValue());
        assertThat(value, nullValue());
    }
    
    @Test
    public void whenCachePutGetReplaceAndExpireObjects() throws InterruptedException
    {
        CachePolicy policy = new TTLCachePolicy(2L);
        Cacheable<String> cache = new MemoryCache<String>(policy);

        String oldValue = cache.put("1", "A");
        String value = cache.get("1");
        
        assertThat(oldValue, nullValue());
        assertThat(value, is("A"));

        oldValue = cache.put("1", "B");
        value = cache.get("1");
        
        assertThat(oldValue, is("A"));
        assertThat(value, is("B"));

        Thread.sleep(3000);
        value = cache.get("1");
        
        assertThat(value, nullValue());
    }

}
