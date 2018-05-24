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

import java.util.concurrent.TimeUnit;

/*

no expiry    - this means cache mappings will never expire,
time-to-live - this means cache mappings will expire after a fixed duration following their creation,
time-to-idle - this means cache mappings will expire after a fixed duration following the time they were last accessed.

 */
/**
 * Cache policy using concepts from Time to live (TTL) to limit the lifetime of data.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class TTLCachePolicy implements CachePolicy
{
    //private long timestamp;
    private long ttl;   // time-to-live
    private long tti;   // time-to-idle
    private long limit;
    private long sizeof;
    
    /**
     * Build a policy with TTL as parameter to keep the objects alive in cache
     * @param ttl time in seconds
     */
    public TTLCachePolicy(long ttl)
    {
        this(ttl, -1L);
    }
    
    /**
     * Build a policy with TTL and TTI as parameter to keep the objects alive in cache
     * @param ttl time-to-live in seconds
     * @param tti time-to-idle in seconds
     */
    public TTLCachePolicy(long ttl, long tti)
    {
        this(ttl, tti, TimeUnit.SECONDS);
    }

    /**
     * Build a policy with TTL and TTI as parameter to keep the objects alive in cache
     * @param ttl time-to-live in seconds
     * @param tti time-to-idle in seconds
     */
    public TTLCachePolicy(long ttl, long tti, TimeUnit unit)
    {
        this.ttl = unit.toMillis(ttl);
        this.tti = unit.toMillis(tti);
    }

    @Override
    public boolean isAlive(long ttl)
    {
        if (this.ttl < 0)
            return true;

        return !expireTTL(ttl);
    }
    
    @Override
    public boolean isAlive(long ttl, long tti)
    {
        if (this.ttl < 0)
            return true;
        
        if (expireTTL(ttl))
            return false;

        if (this.tti < 0)
            return true;

        if (expireTTI(tti))
            return false;
        return true;
    }
    
    private boolean expireTTL(long ttl)
    {
        System.out.printf("\n %d - %d = %b", (ttl + this.ttl), System.currentTimeMillis(), ((ttl + this.ttl) <= System.currentTimeMillis()));
        return ((ttl + this.ttl) <= System.currentTimeMillis());
    }
    
    private boolean expireTTI(long tti)
    {
        return ((tti + this.tti) <= System.currentTimeMillis());
    }
    /*
     * 0---------5-----------10--------15
     * ......4......................14...
     * 
     *      
     */
    
    // FIXME implements limit from CachePolicy
    @Override
    public long limit()
    {
        return this.limit;
    }
    
    // FIXME implements sizeof from CachePolicy
    @Override
    public long sizeof()
    {
        return this.sizeof;
    }
    
    @Override
    public String toString()
    {
        return "TTLCachePolicy [ttl=" + ttl + ", limit=" + limit + ", sizeof=" + sizeof + "]";
    }
    
}
