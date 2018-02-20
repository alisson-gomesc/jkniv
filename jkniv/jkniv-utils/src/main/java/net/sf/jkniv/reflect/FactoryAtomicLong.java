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
