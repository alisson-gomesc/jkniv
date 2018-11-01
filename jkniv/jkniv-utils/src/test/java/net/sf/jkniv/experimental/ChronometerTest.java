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
package net.sf.jkniv.experimental;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

public class ChronometerTest
{
    private static final double TOLERANCE_SUPERMIN = 5D;
    private static final double TOLERANCE_MIN = 20;
    private static final double TOLERANCE_MAX = 200;
    private static final double TOLERANCE = 0.2D;
    private static final long HIGH_TIME = 500L;
    private static final long LOW_TIME = 100L;

    @Test
    public void whenSimpleChronometer() throws InterruptedException
    {
        System.out.println("Init Simple Chronometer");
        Thread.sleep(100);
        Chronometer.timer("#1");
        Thread.sleep(LOW_TIME);
        Chrono c1 = Chronometer.pause("#1");
        System.out.println(Chronometer.log());
        
        assertThat(LOW_TIME*1D, is(closeTo(c1.time(), TOLERANCE_SUPERMIN)));
        assertThat(LOW_TIME*1D, is(closeTo(c1.max(),  TOLERANCE_SUPERMIN)));
        assertThat(LOW_TIME*1D, is(closeTo(c1.min(),  TOLERANCE_SUPERMIN)));
        assertThat(   1L, is(c1.times()));
        assertThat(c1.min(), is(c1.max()));
        
        Chronometer.clear();
    }


    @Test
    public void whenChronometerWithOneThread() throws InterruptedException
    {
        System.out.println("Init Chronometer test");
        Thread.sleep(100);
        double TIME1 = (LOW_TIME*1D)+(LOW_TIME*3D)+(HIGH_TIME*4d);
        double TIME2 = LOW_TIME*3D;
        double TIME3 = HIGH_TIME*4;
        Chronometer.timer("#1");
        Thread.sleep(LOW_TIME);
        Chronometer.timer("#2");
        Thread.sleep((long)TIME2);
        Chrono c2 = Chronometer.pause("#2");
        Chronometer.timer("#3");
        Thread.sleep((long)TIME3);
        Chrono c3 = Chronometer.pause("#3");
        Chrono c1 = Chronometer.pause("#1");
        System.out.println(Chronometer.log());
        
        assertThat(TIME1, is(closeTo(c1.time(), TOLERANCE_SUPERMIN)));
        assertThat(TIME1, is(closeTo(c1.max(),  TOLERANCE_SUPERMIN)));
        assertThat(TIME1, is(closeTo(c1.min(),  TOLERANCE_SUPERMIN)));
        assertThat(   1L, is(c1.times()));
        assertThat(c1.min(), is(c1.max()));
        
        assertThat(TIME2, is(closeTo(c2.time(), TOLERANCE_SUPERMIN)));
        assertThat(TIME2, is(closeTo(c2.max(),  TOLERANCE_SUPERMIN)));
        assertThat(TIME2, is(closeTo(c2.min(),  TOLERANCE_SUPERMIN)));
        assertThat(   1L, is(c2.times()));
        assertThat(c2.min(), is(c2.max()));
        
        assertThat(TIME3, is(closeTo(c3.time(), TOLERANCE_SUPERMIN)));
        assertThat(TIME3, is(closeTo(c3.max(),  TOLERANCE_SUPERMIN)));
        assertThat(TIME3, is(closeTo(c3.min(),  TOLERANCE_SUPERMIN)));
        assertThat(c3.min(), is(c3.max()));
        assertThat(   1L, is(c3.times()));
        Chronometer.clear();
    }
    

    @Test
    public void whenChronometerWithMultipleThreads() throws InterruptedException
    {
        System.out.println("Init Chronometer test multiple threads");
        Thread.sleep(100);

        for(int i=3; i>0; i--)
        {
            String tag = "Thread #" + i;
            MyProcess temp= new MyProcess(tag, HIGH_TIME*i);
            temp.start();
            Chronometer.timer(tag);
            Thread.sleep(LOW_TIME*i);
            Chronometer.pause(tag);
        }
        Thread.sleep(HIGH_TIME*5);
        System.out.println(Chronometer.log());
        Chrono chrono1 = Chronometer.remove("Thread #1");
        Chrono chrono2 = Chronometer.remove("Thread #2");
        Chrono chrono3 = Chronometer.remove("Thread #3");
        
        assertThat(HIGH_TIME*1D, is(closeTo(chrono1.time(), TOLERANCE_MAX)));
        assertThat(HIGH_TIME*1D, is(closeTo(chrono1.max(),  TOLERANCE_MIN)));
        assertThat( LOW_TIME*1D, is(closeTo(chrono1.min(),  TOLERANCE_MIN)));
        assertThat(          2L, is(chrono1.times()));
        assertThat(HIGH_TIME*2D, is(closeTo(chrono2.time(), TOLERANCE_MAX*1.2D)));
        assertThat( LOW_TIME*2D, is(closeTo(chrono2.min(),  TOLERANCE_MIN*1.2D)));
        assertThat(HIGH_TIME*2D, is(closeTo(chrono2.max(),  TOLERANCE_MIN*1.2D)));
        assertThat(          2L, is(chrono2.times()));
        assertThat(HIGH_TIME*3D, is(closeTo(chrono3.time(), TOLERANCE_MAX*1.7D)));
        assertThat( LOW_TIME*3D, is(closeTo(chrono3.min(),  TOLERANCE_MIN*1.5D)));
        assertThat(HIGH_TIME*3D, is(closeTo(chrono3.max(),  TOLERANCE_MIN*1.5D)));
        assertThat(          2L, is(chrono3.times()));
        Chronometer.clear();
    }
    
    class MyProcess extends Thread { 

        private long sleep;
        public MyProcess (String s, long sleep) { 
          super(s);
          this.sleep = sleep;
        }

        public void run() {
            Chronometer.timer(getName());
            try
            {
                Thread.sleep(sleep);
            }
            catch (InterruptedException e) { e.printStackTrace(); }
            Chrono milli = Chronometer.pause(getName());
            double time = sleep;
            System.out.printf("%s (%d)-> %d\n", getName(), sleep,(long) milli.time());
        }
      }
    
    @Test
    public void whenUseLongAtomic()
    {
        AtomicLong v = new AtomicLong();
        
        assertThat(v.get(), is(0L));
        v.incrementAndGet();
        assertThat(v.get(), is(1L));
        v.getAndAdd(5);
        assertThat(v.get(), is(6L));
        v.set(8);
        assertThat(v.get(), is(8L));
        v.addAndGet(2L);
        assertThat(v.get(), is(10L));
    }
}
