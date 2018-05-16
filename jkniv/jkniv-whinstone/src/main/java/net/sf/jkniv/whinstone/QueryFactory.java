package net.sf.jkniv.whinstone;

import java.util.HashMap;
import java.util.Map;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;

public class QueryFactory
{
    private static final Assertable notNull = AssertsFactory.getNotNull();
    
    QueryFactory()
    {
        /* no instance for this */ }
        
    /**
     * Create new instance of Queryable without out pagination.
     * @param args dynamically arguments to create queryable.
     * <p>
     * 1º first param it's name of queryable object
     * <p>
     * 2º and 3º are pair of name and value of first parameter and so on 4º/5º are second, etc.
     * @return new instance of Queryable with your parameters
     */
    public static Queryable asQueryable(Object... args)
    {
        notNull.verify(args);
        Map<String, Object> params = new HashMap<String, Object>();
        int i = 0;
        String name = null;
        String key = null;
        Object value = null;
        for (Object o : args)
        {
            if (i > 0)
            {
                if (i % 2 == 1)
                {
                    key = o.toString();
                }
                else
                {
                    value = o;
                    params.put(key, value);
                    key = null;
                    value = null;
                }
            }
            else
                name = o.toString();
            i++;
        }
        return new QueryName(name, params);
    }
    
    /**
     * Creates a Query object parameterized starting at first row and retrieve all rows, isolation default, no timeout and online (no batch).
     * 
     * @param name a name of query
     * @param params parameters of query
     * @return Queryable object with parameters and unlimited result
     */
    public static Queryable newInstance(String name, Object params)
    {
        return new QueryName(name, params, 0, Integer.MAX_VALUE);
    }
    
    /**
     * Creates a Query object parameterized starting at first row and retrieve all rows, isolation default, no timeout and online (no batch).
     * 
     * @param name a name of query
     * @return Queryable object without parameters and unlimited result
     */
    public static Queryable newInstance(String name)
    {
        return new QueryName(name, null, 0, Integer.MAX_VALUE);
    }
    
    /**
     * Creates a Query object parameterized with: isolation default, no timeout and online (no batch).
     * 
     * @param name a name for query
     * @param params parameters from query
     * @param offset the first row
     * @param max row numbers
     * @return Queryable object with parameters and limited result starting at {@code offset} and {@code max} rows.
     */
    public static Queryable newInstance(String name, Object params, int offset, int max)
    {
        return new QueryName(name, params, offset, max);
    }
    
}
