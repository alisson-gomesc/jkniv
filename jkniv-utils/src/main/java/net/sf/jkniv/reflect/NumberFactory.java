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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class NumberFactory
{
    private static Map<String, Numerical> MAP = new HashMap<String, Numerical>();
    
    static {
        MAP.put(Integer.class.getCanonicalName(), FactoryInteger.instance);
        MAP.put(Long.class.getCanonicalName(), FactoryLong.instance);
        MAP.put(Double.class.getCanonicalName(), FactoryDouble.instance);
        MAP.put(Float.class.getCanonicalName(), FactoryFloat.instance);
        MAP.put(BigDecimal.class.getCanonicalName(), FactoryBigDecimal.instance);
        MAP.put(Short.class.getCanonicalName(), FactoryShort.instance);
        MAP.put(BigInteger.class.getCanonicalName(), FactoryBigInteger.instance);
        MAP.put(AtomicLong.class.getCanonicalName(), FactoryAtomicLong.instance);
        MAP.put(AtomicInteger.class.getCanonicalName(), FactoryAtomicInteger.instance);
        MAP.put(Byte.class.getCanonicalName(), FactoryByte.instance);
    }
    
    public static Numerical getInstance(Number n)
    {
        Numerical factory = new FactoryLong();
        if (n != null)
        {
            factory = MAP.get(n.getClass().getCanonicalName());
            if (factory == null)
                throw new UnsupportedOperationException("Cannot build a factory number to ["+n.getClass()+"] type");
            /*
            if (n instanceof Integer)
                factory = new FactoryInteger();
            else if (n instanceof Long)
                factory = new FactoryLong();
            else if (n instanceof Double)
                factory = new FactoryDouble();
            else if (n instanceof Float)
                factory = new FactoryFloat();
            else if (n instanceof BigDecimal)
                factory = new FactoryBigDecimal();
            else if (n instanceof Short)
                factory = new FactoryShort();
            else if (n instanceof BigInteger)
                factory = new FactoryBigInteger();
            else if (n instanceof AtomicLong)
                factory = new FactoryAtomicLong();
            else if (n instanceof AtomicInteger)
                factory = new FactoryAtomicInteger();
            else if (n instanceof Byte)
                factory = new FactoryByte();
            else
                throw new UnsupportedOperationException("Cannot build a factory number to ["+n.getClass()+"] type");
                */
            // TODO new java 8 number types
            /*
            else if ("java.util.concurrent.atomic.DoubleAccumulator".equals(type.getCanonicalName()))
                isNumber = true;
            else if ("java.util.concurrent.atomic.DoubleAdder".equals(type.getCanonicalName()))
                isNumber = true;
            else if ("java.util.concurrent.atomic.LongAccumulator".equals(type.getCanonicalName()))
                isNumber = true;
            else if ("java.util.concurrent.atomic.LongAdder".equals(type.getCanonicalName()))
                isNumber = true;
                */
        }
        return factory;
    }
    
    public static Numerical getInstance(String n)
    {
        Numerical factory = new FactoryLong();
        if (n != null)
        {
            factory = MAP.get(n);
            if (factory == null)
                throw new UnsupportedOperationException("Cannot build a factory number to ["+n.getClass()+"] type");

            /*
            if (n.equals(Integer.class.getCanonicalName()))
                factory = new FactoryInteger();
            else if (n.equals(Long.class.getCanonicalName()))
                factory = new FactoryLong();
            else if (n.equals(Double.class.getCanonicalName()))
                factory = new FactoryDouble();
            else if (n.equals(Float.class.getCanonicalName()))
                factory = new FactoryFloat();
            else if (n.equals(BigDecimal.class.getCanonicalName()))
                factory = new FactoryBigDecimal();
            else if (n.equals(Short.class.getCanonicalName()))
                factory = new FactoryShort();
            else if (n.equals(BigInteger.class.getCanonicalName()))
                factory = new FactoryBigInteger();
            else if (n.equals(AtomicLong.class.getCanonicalName()))
                factory = new FactoryAtomicLong();
            else if (n.equals(AtomicInteger.class.getCanonicalName()))
                factory = new FactoryAtomicInteger();
            else if (n.equals(Byte.class.getCanonicalName()))
                factory = new FactoryByte();
            else
                throw new UnsupportedOperationException("Cannot build a factory number to ["+n.getClass()+"] type");
                */
            //TODO new java 8 number types
            /*
            else if ("java.util.concurrent.atomic.DoubleAccumulator".equals(type.getCanonicalName()))
                isNumber = true;
            else if ("java.util.concurrent.atomic.DoubleAdder".equals(type.getCanonicalName()))
                isNumber = true;
            else if ("java.util.concurrent.atomic.LongAccumulator".equals(type.getCanonicalName()))
                isNumber = true;
            else if ("java.util.concurrent.atomic.LongAdder".equals(type.getCanonicalName()))
                isNumber = true;
                */
        }
        return factory;
    }

}
