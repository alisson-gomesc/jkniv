package net.sf.jkniv.reflect;

/**
 * Build Short numbers.
 * 
 * @author Alisson Gomes
 *
 */
class FactoryShort implements Numerical
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
        return Short.valueOf(n);
    }

    @Override
    public Number valueOf(Number n)
    {
        if (n == null)
            return null;
        return Short.valueOf(n.shortValue());
    }

    @Override
    public Class<? extends Number> typeOf()
    {
        return Short.class;
    }
}
