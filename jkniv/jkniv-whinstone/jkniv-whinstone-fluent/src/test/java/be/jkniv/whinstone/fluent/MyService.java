package be.jkniv.whinstone.fluent;

import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Repository;

public class MyService
{
 
    
    public void test()
    {
        FluentApi api = null;
        Repository repository = null;
        Queryable queryable = null;
        Sum sum = null;
        
        int a = repository.get(queryable);
        int b = repository.get(queryable);
        int result = sum.doIt(a, b);
        
        //api.get(queryable).get(queryable).fetch().
    }    
}
