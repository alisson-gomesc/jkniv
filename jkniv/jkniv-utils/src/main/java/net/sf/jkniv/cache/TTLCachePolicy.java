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

/**
 * Cache policy using concepts from Time to live (TTL) to limit the lifetime of data.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class TTLCachePolicy implements CachePolicy
{
    //private long timestamp;
    private long ttl;// time-to-live
    private long limit;
    private long sizeof;
    
    /**
     * Build a policy with TTL as parameter to keep the objects alive in cache
     * @param ttl time in seconds
     */
    public TTLCachePolicy(long ttl)
    {
        this.ttl = TimeUnit.SECONDS.toMillis(ttl);
        //this.timestamp = System.currentTimeMillis();
    }
    
    @Override
    public boolean isAlive(long timestamp)
    {
        if (this.ttl < 0)
            return true;
        
        //System.out.printf("\n ttl=%d +=%d current=%d", ttl, (timestamp+ttl), System.currentTimeMillis());
        return ( (timestamp+ttl) > System.currentTimeMillis());
    }

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
