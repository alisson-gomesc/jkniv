package net.sf.jkniv.overload;

public interface Futile
{
    public void m(Object o);
    public void m(Object i, Class<Object> clazz);
    
    public void m(String s);
    public void m(String i, Class<String> clazz);
    
    public void m(Integer i); 
    public void m(Integer i, Class<Integer> clazz);
}
