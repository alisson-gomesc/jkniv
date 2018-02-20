package net.sf.jkniv.reflect;

import java.math.BigDecimal;

/**
 * Build BigDecimal numbers.
 * 
 * @author Alisson Gomes
 *
 */
class FactoryBigDecimal implements Numerical
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
        return new BigDecimal(n);
    }

    @Override
    public Number valueOf(Number n)
    {
        if (n == null)
            return null;
        return BigDecimal.valueOf(n.doubleValue());
    }
    
    @Override
    public Class<? extends Number> typeOf()
    {
        return BigDecimal.class;
    }
}
