package net.sf.jkniv.reflect;

/**
 * Build Double numbers.
 * 
 * @author Alisson Gomes
 *
 */
class FactoryDouble implements Numerical
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
        return Double.valueOf(n);
    }

    @Override
    public Number valueOf(Number n)
    {
        if (n == null)
            return null;
        return Double.valueOf(n.doubleValue());
    }
    
    @Override
    public Class<? extends Number> typeOf()
    {
        return Double.class;
    }
}
