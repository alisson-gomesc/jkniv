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

import net.sf.jkniv.sqlegance.Statistical;

/**
 * Dummy/Empty implementation for {@link Statistical}
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 *
 */
public class NoSqlStats implements Statistical
{
    private final static Statistical NO_STATS = new NoSqlStats();
    
    private NoSqlStats()
    {
    }
    
    public static Statistical getInstance()
    {
        return NO_STATS;
    }
    
    @Override
    public void add(long time) {}

    @Override
    public void add(Exception e) {}

    @Override
    public long getMaxTime()
    {
        return 0;
    }
    
    @Override
    public long getMinTime()
    {
        return 0;
    }
    
    @Override
    public long getAvgTime()
    {
        return 0;
    }
    
    @Override
    public long getTotalTime()
    {
        return 0;
    }

    @Override
    public long getFirstTime()
    {
        return 0;
    }

    @Override
    public long getLastTime()
    {
        return 0;
    }    

    @Override
    public long getCount()
    {
        return 0;
    }

    @Override
    public long getTotalException()
    {
        return 0;
    }

    @Override
    public Exception getFirstException()
    {
        return null;
    }

    @Override
    public Exception getLastException()
    {
        return null;
    }

    @Override
    public String toString()
    {
        return "NoSqlStats [maxTime=0, minTime=0, avgTime=0, totalTime=0, getFirstTime=0"
                + ", lastTime=0, count=0, totalException=0, firstException=null, lastException=null]";
    }
    
    

}
