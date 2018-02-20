package net.sf.jkniv.reflect;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Build AtomicInteger numbers.
 * 
 * @author Alisson Gomes
 *
 */
class FactoryAtomicInteger implements Numerical
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
        return new AtomicInteger(Integer.valueOf(n));
    }

    @Override
    public Number valueOf(Number n)
    {
        if (n == null)
            return null;
        return new AtomicInteger(n.intValue());
    }

    @Override
    public Class<? extends Number> typeOf()
    {
        return AtomicInteger.class;
    }
}
