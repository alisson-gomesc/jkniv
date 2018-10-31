/* 
 * JKNIV, SQLegance keeping queries maintainable.
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
package net.sf.jkniv.sqlegance.builder.xml;

import java.util.concurrent.atomic.AtomicLong;

import net.sf.jkniv.sqlegance.Statistical;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public abstract class AbstractStats implements Statistical
{
    private AtomicLong max;
    private AtomicLong min;
    private AtomicLong total;
    private AtomicLong count;
    
    public AbstractStats()
    {
        this.max = new AtomicLong();
        this.min = new AtomicLong(Long.MAX_VALUE);
        this.total = new AtomicLong();
        this.count = new AtomicLong();
    }
    
    @Override
    public void add(long time)
    {
        if (time > this.max.get())
            this.max.set(time);
        if (time < this.min.get())
            this.min.set(time);
        
        this.count.getAndIncrement();
        this.total.addAndGet(time);
    }
    
    @Override
    public long getMaxTime()
    {
        return this.max.get();
    }
    
    @Override
    public long getMinTime()
    {
        return this.min.get();
    }
    
    @Override
    public long getAvgTime()
    {
        return this.total.get() / this.count.get();
    }
    
    @Override
    public long getTotalTime()
    {
        return this.total.get();
    }
    
    @Override
    public long getCount()
    {
        return this.count.get();
    }
    
    @Override
    public String toString()
    {
        return "AbstractStats [min=" + getMinTime() + ", max=" + getMaxTime() + ", avg=" + (getTotalTime() / getCount())
                + ", total=" + getTotalTime() + ", count=" + getCount() + "]";
    }
    
}
