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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;

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
    private static final transient Logger LOG = LoggerFactory.getLogger(TTLCachePolicy.class);
    private static final transient Assertable notNull = AssertsFactory.getNotNull();

    private final static long KB_FACTOR = 1000;
    private final static long KIB_FACTOR = 1024;
    private final static long MB_FACTOR = 1000 * KB_FACTOR;
    private final static long MIB_FACTOR = 1024 * KIB_FACTOR;
    private final static long GB_FACTOR = 1000 * MB_FACTOR;
    private final static long GIB_FACTOR = 1024 * MIB_FACTOR;
    
    
        //private long timestamp;
    private long ttl;   // time-to-live
    private long tti;   // time-to-idle
    private long size;
    private long sizeof;
    
    /**
     * Build a policy with TTL as parameter to keep the objects alive in cache
     * @param ttl time in seconds
     */
    public TTLCachePolicy(long ttl)
    {
        this(ttl, DEFAULT_TTI);
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
     * @param unit time unit for TTL and TTI
     */
    public TTLCachePolicy(long ttl, long tti, TimeUnit unit)
    {
        this(ttl, tti, unit, DEFAULT_SIZE, DEFAULT_SIZEOF+"");
    }

    /**
     * Build a policy with TTL and TTI as parameter to keep the objects alive in cache
     * @param ttl time-to-live in seconds
     * @param tti time-to-idle in seconds
     * @param size limit for number of object
     * @param sizeof limit for size for sum of objects
     */
    public TTLCachePolicy(long ttl, long tti, long size, String sizeof)
    {
        this(ttl, tti, TimeUnit.SECONDS, size, sizeof);
    }
    /**
     * Build a policy with TTL and TTI as parameter to keep the objects alive in cache
     * @param ttl time-to-live in seconds
     * @param tti time-to-idle in seconds
     * @param unit time unit for TTL and TTI
     * @param size limit for number of object
     * @param sizeof limit for size for sum of objects
     */
    public TTLCachePolicy(long ttl, long tti, TimeUnit unit, long size, String sizeof)
    {
        notNull.verify(unit, sizeof);
        this.ttl = unit.toMillis(ttl);
        this.tti = unit.toMillis(tti);
        this.size = size;
        this.sizeof = toBytes(sizeof);
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
        boolean ret = ((ttl + this.ttl) <= System.currentTimeMillis());
        if(LOG.isDebugEnabled())
            LOG.debug("Cache Expire TTL[{}] timestamp data [{}] current [{}] resting = {} => {}", (ttl + this.ttl), ttl, System.currentTimeMillis(), (System.currentTimeMillis()-(ttl + this.ttl)), ret);
        return ret;
    }
    
    private boolean expireTTI(long tti)
    {
        boolean ret = ((tti + this.tti) <= System.currentTimeMillis());
        if(LOG.isDebugEnabled())
            LOG.debug("Cache Expire TTI[{}] timestamp data [{}] current [{}] resting = {} => {}", (tti + this.tti), tti, System.currentTimeMillis(), (System.currentTimeMillis()-(tti + this.tti)), ret);
        return ret;
    }
    
    // FIXME implements limit from CachePolicy
    @Override
    public long size()
    {
        return this.size;
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
        return "TTLCachePolicy [ttl=" + ttl + ", tti=" + tti + ", size=" + size + ", sizeof=" + sizeof + "]";
    }
    

    /**
     * 
     * @param sizeof <size>[g|G|m|M|k|K]
     * @return
     */
    private long toBytes(String sizeof) 
    {
        long bytes = -1L; // without sizeof limits
        String sizeofNormalized = (sizeof != null ? sizeof.toLowerCase() : "");
        String sizeofNumber = (sizeof.length() > 1 ? sizeof.substring(0, sizeof.length()-1) : "");
        if (sizeofNormalized.endsWith("k"))
            bytes = KIB_FACTOR * Long.valueOf(sizeofNumber);
        else if (sizeofNormalized.endsWith("m"))
            bytes = MIB_FACTOR * Long.valueOf(sizeofNumber);
        else if (sizeofNormalized.endsWith("g"))
            bytes = GIB_FACTOR * Long.valueOf(sizeofNumber);
        else if (sizeofNormalized.length() > 0)
            bytes = Long.valueOf(sizeofNormalized);
        
        return bytes;
    }
}
