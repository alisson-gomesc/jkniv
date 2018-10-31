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

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import org.junit.Test;

public class TimerKeeperTest
{
    private static final double TOLERANCE_MIN = 100D;
    private static final double TOLERANCE_MAX = 100D;
    
    @Test
    public void whenTimerThreads() throws InterruptedException
    {
        for(int i=3; i>0; i--)
        {
            MyProcess temp= new MyProcess("Thread #" + i, 1000*i);
            temp.start();
        }
        Thread.sleep(4000);
        System.out.println("FINSISH");
    }
    
    class MyProcess extends Thread { 

        private long sleep;
        public MyProcess (String s, long sleep) { 
          super(s);
          this.sleep = sleep;
        }

        public void run() { 
            TimerKeeper.start();
            try
            {
                Thread.sleep(sleep);
            }
            catch (InterruptedException e) { e.printStackTrace(); }
            TimerKeeper.pause();
            double milli = TimerKeeper.clear();
            double time = sleep;
            System.out.printf("%s (%d)-> %d\n", getName(), sleep,(long) milli);
            assertThat(time, is(closeTo(milli, TOLERANCE_MIN)));
        }
      }
}
