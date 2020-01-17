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
package net.sf.jkniv.reflect;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Build AtomicLong numbers.
 * 
 * @author Alisson Gomes
 *
 */
class FactoryAtomicLong implements Numerical
{
    public static final Numerical instance = new FactoryAtomicLong();
    
    @Override
    public Number valueOf(Object n)
    {
        if (n == null)
            return null;
        else if (n instanceof Number)
            return valueOf((Number)n);
        else
            return valueOf(n.toString());
    }

    
    @Override
    public Number valueOf(String n)
    {
        if (n == null)
            return null;
        return new AtomicLong(Long.valueOf(n));
    }

    @Override
    public Number valueOf(Number n)
    {
        if (n == null)
            return null;
        return new AtomicLong(n.longValue());
    }
    
    @Override
    public Class<? extends Number> typeOf()
    {
        return AtomicLong.class;
    }
}
