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
package net.sf.jkniv.cache;

/**
 * Cache keep the objects with a {@code Policy} to indicate if continue alive.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public interface CachePolicy
{
    /**
     * Verify if cache is live indicating to discard the cache content.
     * @return <b>true</b> when object is alive, <b>false</b> otherwise
     */
    boolean isAlive(long miliseconds);
    
    /**
     * Returns the number of elements in cache.
     * @return the number of elements in cache.
     */
    long limit();
    
    /**
     * Returns the limit size of objects in cache.
     * @return the limit size of objects in cache.
     */
    long sizeof();
}
