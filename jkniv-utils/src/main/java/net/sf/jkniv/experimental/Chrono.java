package net.sf.jkniv.experimental;

import java.text.DecimalFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Chrono implements Comparable<Chrono>
{
    private static final DecimalFormat             df = new DecimalFormat("0.00");
    private String                                 name;
    private String                                 status;
    private AtomicLong                             milliseconds;
    //private AtomicLong   startTime;
    private long                                   times;
    private long                                   min;
    private long                                   max;
    private ConcurrentHashMap<String, ChronoSlice> chronoSlices;
    
    Chrono(String name)
    {
        this.name = name;
        this.chronoSlices = new ConcurrentHashMap<String, ChronoSlice>();
        reset();
    }
    
    private void reset()
    {
        this.times = 0L;
        this.milliseconds = new AtomicLong();
        this.min = Long.MAX_VALUE;
        this.max = 0L;
        this.status = "pause";
        chronoSlices.clear();
    }
    
    protected void clear()
    {
        TimerKeeper.clear();
        reset();
    }
    
    long changeTimer(boolean pause)
    {
        if (pause)
        {
            //System.out.println("pause ["+name+"] thread: " + Thread.currentThread().getName());
            ChronoSlice chronoSlice = chronoSlices.remove(Thread.currentThread().getName());
            if (chronoSlice != null)
            {
                chronoSlice.changeTimer(pause);
                if (chronoSlice.milliseconds > 0)
                {
                    long sliceTime = chronoSlice.milliseconds;
                    this.milliseconds.addAndGet(sliceTime);
                    if (this.min > sliceTime)
                        this.min = sliceTime;
                    if (this.max < sliceTime)
                        this.max = sliceTime;
                }
                if (chronoSlices.size() == 0)
                    this.status = "paused";
            }
        }
        else
        {
            ChronoSlice chronoSlice = chronoSlices.get(Thread.currentThread().getName());
            if (chronoSlice == null)
            {
                chronoSlice = new ChronoSlice();
                chronoSlices.put(Thread.currentThread().getName(), chronoSlice);
            }
            this.times++;
            this.status = "running";
            long sliceTime = chronoSlice.changeTimer(pause);
            
            if (sliceTime > 0L)
            {
                this.milliseconds.addAndGet(sliceTime);
            }
        }
        return this.milliseconds.get();
    }
    
    public double avg()
    {
        return times > 0 ? (double) (this.milliseconds.get() / times) : 0D;
    }
    
    public long times()
    {
        return this.times;
    }
    
    public long time()
    {
        return this.milliseconds.get();
    }
    
    public long max()
    {
        return this.max;
    }
    
    public long min()
    {
        return this.min;
    }
    
    public String getTime()
    {
        String value = milliseconds + " ms";
        double seconds = milliseconds.get() / 1000L;
        if (seconds > 1)
            value = df.format(seconds) + " seg";
        double minutes = seconds / 60L;
        if (minutes > 1)
            value = df.format(minutes) + " min";
        double hours = minutes / 60L;
        if (hours > 1)
            value = df.format(hours) + " h";
        return value;
    }
    
    @Override
    public String toString()
    {
        return "Chrono [name=" + name + ", milliseconds=" + milliseconds.get() + ", times=" + times + ", status="
                + status + ", min=" + min + ", max=" + max + ", avg=" + avg() + "]";
    }
    
    @Override
    public int compareTo(Chrono o)
    {
        if (o == null)
            return -1;
        else if (milliseconds.get() < o.milliseconds.get())
            return 1;
        else if (milliseconds.get() > o.milliseconds.get())
            return -1;
        else
            return 0;
    }
    
    /**
     * slice time for threads for same tag name
     */
    class ChronoSlice
    {
        private long milliseconds;
        private long startTime;
        
        ChronoSlice()
        {
            this.milliseconds = 0L;
            this.startTime = 0L;
        }
        
        long changeTimer(boolean pause)
        {
            if (pause)
            {
                long sliceTime = 0;
                if (startTime > 0)
                {
                    sliceTime = System.currentTimeMillis() - startTime;
                    this.milliseconds += sliceTime;
                }
                startTime = 0;
            }
            else
            {
                if (startTime > 0L)
                {
                    long sliceTime = System.currentTimeMillis() - startTime;
                    this.milliseconds += sliceTime;
                }
                this.startTime = System.currentTimeMillis();
            }
            return this.milliseconds;
        }
        
    }
    
}
