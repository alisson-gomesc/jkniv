package net.sf.jkniv.sqlegance.builder.xml;

import net.sf.jkniv.sqlegance.Statistical;

public class NoSqlStats implements Statistical
{
    @Override
    public void add(long time)
    {
    }
    
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
    public long getCount()
    {
        return 0;
    }    
}
