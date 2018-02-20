package net.sf.jkniv.reflect;

/**
 * Build Float numbers.
 * 
 * @author Alisson Gomes
 *
 */
class FactoryFloat implements Numerical
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
        return Float.valueOf(n);
    }

    @Override
    public Number valueOf(Number n)
    {
        if (n == null)
            return null;
        return Float.valueOf(n.floatValue());
    }
    
    @Override
    public Class<? extends Number> typeOf()
    {
        return Float.class;
    }
}
