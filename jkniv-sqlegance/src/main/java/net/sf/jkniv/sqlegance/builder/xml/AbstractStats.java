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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.Statistical;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
abstract class AbstractStats implements Statistical
{
    private static final Logger LOG = LoggerFactory.getLogger(AbstractStats.class);
    private AtomicLong max;
    private AtomicLong min;
    private AtomicLong total;
    private AtomicLong count;
    private AtomicLong firstTime;
    private AtomicLong lastTime;
    private AtomicLong totalException;
    private Exception  firstException;
    private Exception  lastException;
    
    public AbstractStats()
    {
        this.max = new AtomicLong();
        this.min = new AtomicLong(Long.MAX_VALUE);
        this.total = new AtomicLong();
        this.firstTime = null;
        this.lastTime = new AtomicLong();
        this.count = new AtomicLong();
        this.totalException = new AtomicLong();
    }
    
    @Override
    public void add(long time)
    {
        if (time > this.max.get())
            this.max.set(time);
        if (time < this.min.get())
            this.min.set(time);
        if (firstTime == null)
            this.firstTime = new AtomicLong(time);
        this.lastTime.set(time);
        this.count.getAndIncrement();
        this.total.addAndGet(time);
        LOG.trace("added {} ms with total of {} the {} time", time, this.total, this.count);
    }
    
    @Override
    public void add(Exception e)
    {
        this.totalException.incrementAndGet();
        if (e != null)
        {
            if (this.firstException == null)
                this.firstException = e;
            
            this.lastException = e;
        }
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
    public long getFirstTime()
    {
        return (this.firstTime != null ? this.firstTime.get() : 0L);
    }
    
    @Override
    public long getLastTime()
    {
        return this.lastTime.get();
    }
    
    @Override
    public long getTotalException()
    {
        return this.totalException.get();
    }
    
    @Override
    public Exception getFirstException()
    {
        return this.firstException;
    }
    
    @Override
    public Exception getLastException()
    {
        return this.lastException;
    }

    @Override
    public String toString()
    {
        return "AbstractStats [max=" + max + ", min=" + min + ", total=" + total + ", count=" + count + ", firstTime="
                + firstTime + ", lastTime=" + lastTime + ", totalException=" + totalException + ", firstException="
                + (firstException != null ? firstException.getMessage() : "") 
                + ", lastException=" + (lastException != null ? lastException.getMessage() : "") + "]";
    }
    
   
}
