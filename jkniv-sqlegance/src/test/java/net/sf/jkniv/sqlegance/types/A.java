package net.sf.jkniv.sqlegance.types;

import java.util.concurrent.TimeUnit;

import net.sf.jkniv.sqlegance.types.Converter.EnumType;

public class A
{
    private B      b;
    private String name;
    
    @Converter(isEnum = EnumType.STRING)
    private TimeUnit minute;
    
    @Converter(isEnum = EnumType.ORDINAL)
    private TimeUnit hour;
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public B getB()
    {
        return b;
    }
    
    public void setB(B b)
    {
        this.b = b;
    }

    public TimeUnit getMinute()
    {
        return minute;
    }

    public void setMinute(TimeUnit minute)
    {
        this.minute = minute;
    }

    public TimeUnit getHour()
    {
        return hour;
    }

    public void setHour(TimeUnit hour)
    {
        this.hour = hour;
    }
    
}
