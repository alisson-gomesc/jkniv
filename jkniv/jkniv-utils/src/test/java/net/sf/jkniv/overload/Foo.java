package net.sf.jkniv.overload;

public class Foo implements Futile
{
    
    @Override
    public void m(Object o)
    {
        System.out.println("m (Object)");
    }

    public void m(Object o, Class<Object> clazz)
    {
        System.out.println("m (Object)");
    }
    
    @Override
    public void m(String s)
    {
        System.out.println("m (String)");
    }

    @Override
    public void m(String s, Class<String> clazz)
    {
        System.out.println("m (String)");
    }

    @Override
    public void m(Integer i)
    {
        System.out.println("m (Integer)");
    }

    @Override
    public void m(Integer i, Class<Integer> clazz)
    {
        System.out.println("m (Integer)");
    }
    
}
