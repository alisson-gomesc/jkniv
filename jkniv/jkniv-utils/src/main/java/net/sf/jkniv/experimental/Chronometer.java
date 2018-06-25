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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chronometer
{
    private static final Map<String, Chrono> chronos = new HashMap<String, Chrono>();
    private static final DecimalFormat       df      = new DecimalFormat("0.00");
    
    private static int                       padLeft = 0;
    
    public static void timer(String name)
    {
        if (name.length() > padLeft)
            padLeft = name.length() + 1;
        Chronometer.Chrono chrono = chronos.get(name);
        if (chrono == null)
        {
            chrono = new Chronometer().new Chrono(name);
            chronos.put(name, chrono);
        }
        else
            chrono.changeTimer(false);
    }
    
    public static void pause(String name)
    {
        Chronometer.Chrono chrono = chronos.get(name);
        if (chrono != null)
            chrono.changeTimer(true);
    }
    
    public static String getTimer(String name)
    {
        Chronometer.Chrono chrono = chronos.get(name);
        if (chrono != null)
            return chrono.toString();
        
        return null;
    }
    
    public static long milliseconds(String name)
    {
        Chronometer.Chrono chrono = chronos.get(name);
        if (chrono != null)
            return chrono.milliseconds;
        
        return System.currentTimeMillis();
    }

    public static long avg(String name)
    {
        Chronometer.Chrono chrono = chronos.get(name);
        if (chrono != null)
            return chrono.avg();
        
        return 0L;
    }

    public static String log()
    {
        StringBuilder sb = new StringBuilder();
        List<Chrono> list = new ArrayList<Chrono>(chronos.values());
        Collections.sort(list);
        for (Chrono c : list)
            sb.append("\n" + String.format("%1$" + padLeft + "s", c.toString()));
        return sb.toString();
    }
    
    public static void clear()
    {
        Collection<Chrono> list = chronos.values();
        for (Chrono c : list)
            c.clear();
        
    }
    
    class Chrono implements Comparable<Chrono>
    {
        private String name;
        private Date   startTimer;
        private long   milliseconds;
        /** how many times I called */
        private long   times; 
        
        public Chrono(String name)
        {
            this.times++;
            this.name = name;
            this.startTimer = new Date();
            this.milliseconds = 0L;
        }
        
        public void clear()
        {
            this.milliseconds = 0;
            this.startTimer = null;
        }
        
        public synchronized void changeTimer(boolean pause)
        {
            if (pause)
            {
                if (this.startTimer != null)
                    this.milliseconds += System.currentTimeMillis() - this.startTimer.getTime();
                
                this.startTimer = null;
            }
            else
            {
                this.times++;
                if (this.startTimer != null)
                    this.milliseconds += System.currentTimeMillis() - this.startTimer.getTime();
                
                this.startTimer = new Date();
            }
        }
        
        public long avg()
        {
            return this.milliseconds/times;
        }
        
        public String getTime()
        {
            String value = milliseconds + " ms";
            double seconds = milliseconds / 1000L;
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
            return "Chronometer [" + String.format("%1$" + padLeft + "s", (name)) + ": " + getTime() + ", total="+times+", avg="+avg()+" ms]";
        }
        
        @Override
        public int compareTo(Chrono o)
        {
            if (o == null)
                return -1;
            else if (milliseconds < o.milliseconds)
                return 1;
            else if (milliseconds > o.milliseconds)
                return -1;
            else
                return 0;
        }
    }
}
