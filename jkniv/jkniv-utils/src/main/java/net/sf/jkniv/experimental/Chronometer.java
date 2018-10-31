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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Multi-thread chronometer for multiple tags.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 *
 */
public class Chronometer
{
    private static final Map<String, Chrono> chronos      = new HashMap<String, Chrono>();
    private static int                       padLeft      = 0;
    private static final Chrono              EMPTY_CHRONO = new Chrono("");
    
    public synchronized static void timer(String name)
    {
        if (name.length() > padLeft)
            padLeft = name.length() + 1;
        Chrono chrono = chronos.get(name);
        if (chrono == null)
        {
            chrono = new Chrono(name);
            chronos.put(name, chrono);
        }
        chrono.changeTimer(false);
    }
    
    public static Chrono pause(String name)
    {
        Chrono chrono = chronos.get(name);
        if (chrono != null)
        {
            chrono.changeTimer(true);
            return chrono;
        }
        return EMPTY_CHRONO;
    }
    
    public static Chrono remove(String name)
    {
        Chrono chrono = chronos.remove(name);
        chrono.changeTimer(true);
        return chrono;
    }
    
    public static String getTimer(String name)
    {
        Chrono chrono = chronos.get(name);
        if (chrono != null)
            return chrono.toString();
        
        return null;
    }
    
    public static Chrono getChronoTimer(String name)
    {
        return chronos.get(name);
    }
    
    public static long milliseconds(String name)
    {
        Chrono chrono = chronos.get(name);
        if (chrono != null)
            return chrono.time();
        
        return 0L;
    }
    
    public static double avg(String name)
    {
        Chrono chrono = chronos.get(name);
        if (chrono != null)
            return chrono.avg();
        
        return 0D;
    }
    
    public static long min(String name)
    {
        Chrono chrono = chronos.get(name);
        if (chrono != null)
            return chrono.min();
        
        return 0L;
    }
    
    public static long max(String name)
    {
        Chrono chrono = chronos.get(name);
        if (chrono != null)
            return chrono.max();
        
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
}
