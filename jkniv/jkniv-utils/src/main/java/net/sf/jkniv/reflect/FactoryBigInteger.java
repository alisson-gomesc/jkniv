package net.sf.jkniv.reflect;

import java.math.BigInteger;

/**
 * Build BigInteger numbers.
 * 
 * @author Alisson Gomes
 *
 */
class FactoryBigInteger implements Numerical
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
        return new BigInteger(n);
    }

    @Override
    public Number valueOf(Number n)
    {
        if (n == null)
            return null;
        return BigInteger.valueOf(n.longValue());
    }
    
    @Override
    public Class<? extends Number> typeOf()
    {
        return BigInteger.class;
    }
}
