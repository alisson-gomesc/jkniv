package net.sf.jkniv.reflect;

public interface Numerical
{
    Number valueOf(Object n);

    Number valueOf(String n);
    
    Number valueOf(Number n);
    
    Class<? extends Number> typeOf();
}
