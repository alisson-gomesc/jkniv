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
package net.sf.jkniv.experimental;

/**
 * Multi-thread timer keeper.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 *
 */
public class TimerKeeper
{
    private long                                  startTimer;
    private long                                  milliseconds;
    private long                                  times;
    private static final ThreadLocal<TimerKeeper> TIMERS = new ThreadLocal<TimerKeeper>();
    
    private TimerKeeper()
    {
        this.startTimer = System.currentTimeMillis();
        this.milliseconds = 0L;
        this.times = 0;
    }
    
    public static boolean isRunning()
    {
        TimerKeeper timerKeeper = TIMERS.get();
        if (timerKeeper != null)
            return timerKeeper.startTimer > 0L;
        
        return false;
    }
    
    /**
     * start the chronometer timer
     * @return the started time in milliseconds sliced by pause times. 
     * After pause <code>startTimer</code> become to zero.
     */
    public static long start()
    {
        TimerKeeper timerKeeper = TIMERS.get();
        if (timerKeeper == null)
        {
            timerKeeper = new TimerKeeper();
            TIMERS.set(timerKeeper);
            timerKeeper.times++;
        }
        else if (timerKeeper.startTimer == 0)
        {
            timerKeeper.startTimer = System.currentTimeMillis();
            timerKeeper.times++;
        }
        //      else if (timerKeeper.startTimer > 0)
        //      {
        //          timerKeeper.milliseconds += System.currentTimeMillis() - timerKeeper.startTimer;
        //      }
        return timerKeeper.startTimer;
    }
    
    /**
     * Pause the chronometer timer
     * @return the slice from last start until this pause. To obtain the
     * total time call {@link #time()} method.
     */
    public static long pause()
    {
        TimerKeeper timerKeeper = TIMERS.get();
        long sliceTime = 0L;
        if (timerKeeper == null)
            throw new IllegalStateException("start time doesn't called");
        
        if (timerKeeper.startTimer > 0)
            sliceTime = System.currentTimeMillis() - timerKeeper.startTimer;
        
        timerKeeper.milliseconds += sliceTime;
        timerKeeper.startTimer = 0;
        return sliceTime;
    }
    
    public static long time()
    {
        TimerKeeper timerKeeper = TIMERS.get();
        if (timerKeeper == null)
            return 0L;
        
        return timerKeeper.milliseconds;
    }
    
    public static long avg()
    {
        TimerKeeper timerKeeper = TIMERS.get();
        if (timerKeeper == null)
            return 0L;
        
        return timerKeeper.milliseconds / timerKeeper.times;
    }
    
    /**
     * remove the chronometer from thread-local 
     * @return the time in milliseconds from chronometer
     */
    public static long clear()
    {
        TimerKeeper timerKeeper = TIMERS.get();
        if (timerKeeper == null)
            return 0L;
        
        TIMERS.remove();
        return timerKeeper.milliseconds;
    }
    
    public static long times()
    {
        TimerKeeper timerKeeper = TIMERS.get();
        if (timerKeeper == null)
            return 0L;
        
        return timerKeeper.times;
    }
}
