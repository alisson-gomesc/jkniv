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
package net.sf.jkniv.sqlegance;

/**
 * Statistical data for query execution 
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 *
 */
public interface Statistical
{
    /**
     * add time to statistics
     * @param time milliseconds
     */
    void add(long time);
    
    /**
     * Add a exception data to statistical keeping the
     * first, last and total exceptions registered.
     * @param e exception
     */
    void add(Exception e);
    
    /**
     * maximum time execution
     * @return maximum milliseconds execution
     */
    long getMaxTime();
    
    /**
     * minimum time execution
     * @return minimum milliseconds execution
     */
    long getMinTime();
    
    /**
     * average time execution
     * @return average milliseconds execution
     */
    long getAvgTime();
    
    /**
     * total time execution
     * @return total milliseconds execution
     */
    long getTotalTime();

    /**
     * first time execution
     * @return total milliseconds execution
     */
    long getFirstTime();

    /**
     * last time execution
     * @return total milliseconds execution
     */
    long getLastTime();

    /**
     * times executed
     * @return times executed
     */
    long getCount();

    /**
     * the total of execution with error
     * @return quantity of errors happened
     */
    long getTotalException();

    /**
     * The first error happened
     * @return the first error
     */
    Exception getFirstException();

    /**
     * The last error happened
     * @return the last error
     */
    Exception getLastException();
}
