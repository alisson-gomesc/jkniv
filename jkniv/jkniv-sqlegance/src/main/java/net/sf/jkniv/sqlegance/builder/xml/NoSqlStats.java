package net.sf.jkniv.sqlegance.builder.xml;

import net.sf.jkniv.sqlegance.Statistical;

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
