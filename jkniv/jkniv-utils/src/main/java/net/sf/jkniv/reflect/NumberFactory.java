package net.sf.jkniv.reflect;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class NumberFactory
{
    
    public static Numerical getInstance(Number n)
    {
        Numerical factory = new FactoryLong();
        if (n != null)
        {
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
            // new java 8 number types
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
            // new java 8 number types
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
