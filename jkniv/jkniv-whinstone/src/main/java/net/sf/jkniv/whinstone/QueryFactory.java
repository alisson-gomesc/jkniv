/* 
 * JKNIV, whinstone one contract to access your database.
 * 
 * Copyright (C) 2017, the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.jkniv.whinstone;

import java.util.HashMap;
import java.util.Map;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;

public class QueryFactory
{
    private static final Assertable notNull = AssertsFactory.getNotNull();
    
    QueryFactory() { /* no instance for this */ }
        
    /**
     * Creates a Query object parameterized starting at first row and retrieve all rows, isolation default, no timeout and online (no batch).
     * 
     * @param name query name
     * @param args dynamically arguments to create {@code Queryable}.
     * <p>
     * 1o first param it's key name and 2o your value
     * <p>
     * 3o it's key 4o your value and so on.
     * @return Queryable object with unlimited result
     */
    public static Queryable of(String name, Object... args)
    {
        return buildQueryable(name, args);
    }

    /**
     * Creates a {@link Queryable} object parameterized starting at first row and retrieve all rows, 
     * isolation default, no timeout and online (no batch).
     * 
     * @param name query name
     * @param params parameters of query
     * @return Queryable object with parameters and unlimited result
     */
    public static Queryable of(String name, Object params)
    {
        return new QueryName(name, params, 0, Integer.MAX_VALUE);
    }

    /**
     * Creates a {@link Queryable} object parameterized starting at first row and retrieve all rows, 
     * isolation default, no timeout and online (no batch).
     * 
     * @param name query name
     * @param params array of parameters
     * @return Queryable object with parameters and unlimited result
     */
    public static Queryable ofArray(String name, Object... params)
    {
        return new QueryName(name, params, 0, Integer.MAX_VALUE);
    }

//    public static Queryable of(String name, List params)
//    {
//        return new QueryName(name, params, 0, Integer.MAX_VALUE);
//    }
    
    /**
     * Build a new {@code Queryable} object
     * @param name query name
     * @param offset the first row
     * @param max row numbers
     * @param args dynamically arguments to create {@code Queryable}.
     * <p>
     * 1o first param it's key name and 2o your value
     * <p>
     * 3o it's key 4o your value and so on.
     * @return Queryable object with parameters with limited result by {@code max}
     */
    public static Queryable of(String name, int offset, int max, Object... args)
    {
        QueryName q = (QueryName) buildQueryable(name, args);
        q.setOffset(offset);
        q.setMax(max);
        return q;
    }

    /**
     * Build a new {@code Queryable} object
     * 
     * @param name a name for query
     * @param params parameters from query
     * @param offset the first row
     * @param max row numbers
     * @return Queryable object with parameters and limited result starting at {@code offset} and {@code max} rows.
     */
    public static Queryable of(String name, Object params, int offset, int max)
    {
        return new QueryName(name, params, offset, max);
    }

    /**
     * Build a new {@code Queryable} object without parameters
     * 
     * @param name a name for query
     * @param offset the first row
     * @param max row numbers
     * @return Queryable object with parameters and limited result starting at {@code offset} and {@code max} rows.
     */
    public static Queryable of(String name, int offset, int max)
    {
        return new QueryName(name, null, offset, max);
    }

    /**
     * Build a new {@code Queryable} object
     * @param name query name
     * @param clazz return type that overload return type from XML
     * @param args dynamically arguments to create {@code Queryable}.
     * <p>
     * 1o first param it's key name and 2o your value
     * <p>
     * 3o it's key 4o your value and so on.
     * @param <T> type of return type 
     * @return Queryable object with parameters and unlimited result and specific return type
     */
    public static <T> Queryable of(String name, Class<T> clazz, Object... args)
    {
        QueryName q = (QueryName) buildQueryable(name, args);
        q.setReturnType(clazz);
        return q;
    }

    /**
     * Creates a {@link Queryable} object parameterized starting at first row and retrieve all rows, 
     * isolation default, no timeout and online (no batch).
     * 
     * @param name query name
     * @param clazz return type that overload return type from XML
     * @param params parameters of query
     * @param <T> class type of overload return
     * @return Queryable object with parameters and unlimited result
     */
    public static <T> Queryable of(String name, Class<T> clazz, Object params)
    {
        QueryName q = new QueryName(name, params);
        q.setReturnType(clazz);
        return q;
    }
    
    /**
     * Build a new {@code Queryable} object
     * @param name query name
     * @param clazz return type that overload return type from XML
     * @param <T> type of return type 
     * @return Queryable object with parameters and unlimited result and specific return type
     */
    public static <T> Queryable of(String name, Class<T> clazz)
    {
        QueryName q = new QueryName(name);
        q.setReturnType(clazz);
        return q;
    }
    
    /**
     * Clone {@code queryable} object with a return type if no {@code null}
     * @param queryable query name
     * @param returnType type of return that overload return type from XML
     * @param <T> type of return type 
     * @return Queryable object with parameters and unlimited result
     */
    public static <T> Queryable clone(Queryable queryable, Class<T> returnType)
    {
        QueryName q = new QueryName(queryable.getName(), queryable.getParams(), queryable.getOffset(), queryable.getMax());

        q.setReturnType(returnType);
        
        if(queryable.isCacheIgnore())
            q.cacheIgnore();
        
        if(queryable.isScalar())
            q.scalar();
        
        return q;
    }
    
    /**
     * TODO test me
     * @param name query name
     * @param returnType type of return that overload return type from XML
     * @param offset the first row
     * @param max row numbers
     * @param args dynamically arguments to create {@code Queryable}.
     * @param <T> type of return type 
     * <p>
     * 1o first param it's key name and 2o your value
     * <p>
     * 3o it's key 4o your value and so on.
     * @return Queryable object with parameters and limited result starting at {@code offset} and {@code max} rows. 
     */
    public static <T> Queryable of(String name, Class<T> returnType, int offset, int max, Object... args)
    {
        QueryName q = (QueryName) buildQueryable(name, args);
        q.setOffset(offset);
        q.setMax(max);
        q.setReturnType(returnType);
        return q;
    }
   
    
    /**
     * Create new instance of {@code Queryable} without out pagination.
     * @param name query name
     * @param args dynamically arguments to create {@code queryable}.
     * <p>
     * 1º first param it's name of {@code queryable} object
     * <p>
     * 2º and 3º are pair of name and value of first parameter and so on 4º/5º are second, etc.
     * @return new instance of Queryable with your parameters
     */
    private static Queryable buildQueryable(String name, Object... args)
    {
        notNull.verify(args);
        Map<String, Object> params = new HashMap<String, Object>();
        int i = 0;
        String key = null;
        Object value = null;
        for (Object o : args)
        {
            if (i % 2 == 0)
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
            i++;
        }
        if (i == 0)
            return new QueryName(name);
        
        return new QueryName(name, params);
    }
    
    /**
     * Creates a Query object parameterized starting at first row and retrieve all rows, isolation default, no timeout and online (no batch).
     * 
     * @param name query name
     * @param params parameters of query
     * @return Queryable object with parameters and unlimited result
     */
    private static Queryable newInstance(String name, Object params)
    {
        return new QueryName(name, params, 0, Integer.MAX_VALUE);
    }
    
    /**
     * Creates a Query object parameterized starting at first row and retrieve all rows, isolation default, no timeout and online (no batch).
     * 
     * @param name a name of query
     * @return Queryable object without parameters and unlimited result
     */
    private static Queryable newInstance(String name)
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
    private static Queryable newInstance(String name, Object params, int offset, int max)
    {
        return new QueryName(name, params, offset, max);
    }
    
}
