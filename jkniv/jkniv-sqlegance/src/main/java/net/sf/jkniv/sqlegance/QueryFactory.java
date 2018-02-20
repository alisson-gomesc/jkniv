package net.sf.jkniv.sqlegance;

public class QueryFactory
{
    QueryFactory() { /* no instance for this*/    }
    
    
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
