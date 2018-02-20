package net.sf.jkniv.reflect;

/**
 * Build Byte numbers.
 * 
 * @author Alisson Gomes
 *
 */
class FactoryByte implements Numerical
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
        return Byte.valueOf(n);
    }

    @Override
    public Number valueOf(Number n)
    {
        if (n == null)
            return null;
        return Byte.valueOf(n.byteValue());
    }
    
    @Override
    public Class<? extends Number> typeOf()
    {
        return Byte.class;
    }
}
