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

import org.hamcrest.Matchers;

import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

public class TTLCachePolicyTest
{

    @Test
    public void whenCachePolicyContinueAlive() throws InterruptedException
    {
        CachePolicy policy = new TTLCachePolicy(2L);
        long timestamp = System.currentTimeMillis();
        assertThat(policy.isAlive(timestamp), is(true));
        Thread.sleep(3000);
        assertThat(policy.isAlive(timestamp), is(false));
    }
    
    @Test
    public void whenCachePolicyContinueAliveWithTTLandTTI() throws InterruptedException
    {
        CachePolicy policy = new TTLCachePolicy(-1L, -1L);
        long timestamp = System.currentTimeMillis();
        assertThat(policy.isAlive(timestamp, timestamp), is(true));
        Thread.sleep(1800);
        assertThat(policy.isAlive(timestamp, timestamp), is(true));
        Thread.sleep(1000);
        assertThat(policy.isAlive(timestamp, timestamp), is(true));
    }

    @Test
    public void whenCachePolicyExpireWithTTI() throws InterruptedException
    {
        CachePolicy policy = new TTLCachePolicy(5L, 2L);
        long timestamp = System.currentTimeMillis();
        assertThat(policy.isAlive(timestamp, timestamp), is(true));
        Thread.sleep(1800);
        assertThat(policy.isAlive(timestamp, timestamp), is(true));
        Thread.sleep(1000);
        assertThat(policy.isAlive(timestamp, timestamp), is(false));
    }

    
    @Test
    public void whenCachePolicyTTLalwaysExpire() throws InterruptedException
    {
        CachePolicy policy = new TTLCachePolicy(0L);
        long timestamp = System.currentTimeMillis();
        assertThat(policy.isAlive(timestamp), is(false));
        Thread.sleep(3000);
        assertThat(policy.isAlive(timestamp), is(false));
    }

    @Test
    public void whenCachePolicyTTLneverExpire() throws InterruptedException
    {
        CachePolicy policy = new TTLCachePolicy(-1L);
        long timestamp = System.currentTimeMillis();
        assertThat(policy.isAlive(timestamp), is(true));
        Thread.sleep(3000);
        assertThat(policy.isAlive(timestamp), is(true));
    }

    @Test
    public void whenCachePolicyInitialSizeAndSizeof()
    {
        CachePolicy policy0 = new TTLCachePolicy(20,10,150,"512");
        assertThat(policy0.size(), is(150L));
        assertThat(policy0.sizeof(), is(512L));

        CachePolicy policy1 = new TTLCachePolicy(20,10,100,"2m");
        assertThat(policy1.size(), is(100L));
        assertThat(policy1.sizeof(), is(2L*1024L*1024L));

        CachePolicy policy = new TTLCachePolicy(20,10,200,"2M");
        assertThat(policy.size(), is(200L));
        assertThat(policy.sizeof(), is(2L*1024L*1024L));

        CachePolicy policy2 = new TTLCachePolicy(20,10,2000,"2g");
        assertThat(policy2.size(), is(2000L));
        assertThat(policy2.sizeof(), is(2L*1024L*1024L*1024L));

        CachePolicy policy4 = new TTLCachePolicy(20,10,500,"2G");
        assertThat(policy4.size(), is(500L));
        assertThat(policy4.sizeof(), is(2L*1024L*1024L*1024L));
    }

    @Test
    public void whenCachePolicyInitWith2GB()
    {
        CachePolicy policy = new TTLCachePolicy(20,10,100,"2G");
        assertThat(policy.size(), is(100L));
        assertThat(policy.sizeof(), is(2L*1024L*1024L*1024L));
    }

}
