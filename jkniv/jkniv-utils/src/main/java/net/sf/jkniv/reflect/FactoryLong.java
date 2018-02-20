package net.sf.jkniv.reflect;

/**
 * Build Long numbers.
 * 
 * @author Alisson Gomes
 *
 */
class FactoryLong implements Numerical
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
        return Long.valueOf(n);
    }

    @Override
    public Number valueOf(Number n)
    {
        if (n == null)
            return null;
        return Long.valueOf(n.longValue());
    }
    
    @Override
    public Class<? extends Number> typeOf()
    {
        return Long.class;
    }
}
