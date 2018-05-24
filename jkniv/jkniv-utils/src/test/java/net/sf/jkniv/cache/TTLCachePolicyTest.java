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

}
