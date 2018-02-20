package net.sf.jkniv.overload;

import org.junit.Test;

public class RuntimeOverloadTest
{
    
    @Test
    public void when()
    {
        Futile foo = new Foo();
        
        Object o = new Object();
        Object s = "foobar";
        Object i = new Integer(12);
        
        foo.m(o);
        foo.m(s);
        foo.m(i);
        
    }
}
