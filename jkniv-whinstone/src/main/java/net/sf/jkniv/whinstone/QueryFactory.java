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
import net.sf.jkniv.whinstone.types.RegisterType;

public class QueryFactory
{
    private static final Assertable NOT_NULL = AssertsFactory.getNotNull();
    
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
        NOT_NULL.verify(name);
        return buildQueryable(name, null, args);
    }

    public static Queryable of(String name, RegisterType registerType, Object... args)
    {
        NOT_NULL.verify(name, registerType);
        return buildQueryable(name, registerType, args);
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
        NOT_NULL.verify(name, params);
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
        NOT_NULL.verify(name, params);
        return new QueryName(name, params, 0, Integer.MAX_VALUE);
    }

    public static Queryable ofArray(String name, RegisterType registerType, Object... params)
    {
        NOT_NULL.verify(name, registerType, params);
        return new QueryName(name, params, 0, Integer.MAX_VALUE, registerType);
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
        NOT_NULL.verify(name, name, params);
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
        NOT_NULL.verify(name);
        return new QueryName(name, null, offset, max);
    }

    /**
     * Build a new {@code Queryable} object
     * @param name query name
     * @param returnType return type that overload return type from XML
     * @param args dynamically arguments to create {@code Queryable}.
     * <p>
     * 1o first param it's key name and 2o your value
     * <p>
     * 3o it's key 4o your value and so on.
     * @param <T> type of return type 
     * @return {@link Queryable} object with parameters and unlimited result and specific return type
     */
    public static <T> Queryable of(String name, Class<T> returnType, Object... args)
    {
        NOT_NULL.verify(name, returnType, args);
        QueryName q = (QueryName) buildQueryable(name, null, args);
        q.setReturnType(returnType);
        return q;
    }

    /**
     * Creates a {@link Queryable} object parameterized starting at first row and retrieve all rows, 
     * isolation default, no timeout and online (no batch).
     * 
     * @param name query name
     * @param returnType return type that overload return type from XML
     * @param params parameters of query
     * @param <T> class type of overload return
     * @return {@link Queryable} object with parameters and unlimited result
     */
    public static <T> Queryable of(String name, Class<T> returnType, Object params)
    {
        NOT_NULL.verify(name, returnType, params);
        QueryName q = new QueryName(name, params);
        q.setReturnType(returnType);
        return q;
    }
    
    /**
     * Build a new {@link Queryable} object
     * @param name query name
     * @param returnType return type that overload return type from XML
     * @param <T> type of return type 
     * @return {@link Queryable} object with parameters and unlimited result and specific return type
     */
    public static <T> Queryable of(String name, Class<T> returnType)
    {
        NOT_NULL.verify(name, returnType);
        QueryName q = new QueryName(name);
        q.setReturnType(returnType);
        return q;
    }
 
    public static <T> Queryable clone(Queryable queryable)
    {
        NOT_NULL.verify(queryable);
        return clone(queryable.getName(), queryable, queryable.getParams(), queryable.getRegisterType(), null);
    }

    /**
     * Clone {@code queryable} object with a return type if no {@code null}
     * @param queryable query name
     * @param returnType type of return that overload return type from XML
     * @param <T> type of return type 
     * @return clone of Queryable instance
     */
    public static <T> Queryable clone(Queryable queryable, Class<T> returnType)
    {
        NOT_NULL.verify(queryable, returnType);
        return clone(queryable.getName(), queryable, queryable.getParams(), queryable.getRegisterType(), returnType);
    }

    /**
     * Clone {@code queryable} object with a return type if no {@code null}
     * @param queryable query name
     * @param registerType registry of type data
     * @param <T> type of return type 
     * @return clone of Queryable instance
     */
    public static <T> Queryable clone(Queryable queryable, RegisterType registerType)
    {
        NOT_NULL.verify(queryable, registerType);
        return clone(queryable.getName(), queryable, queryable.getParams(), registerType, null);
    }

    public static <T> Queryable clone(Queryable queryable, RegisterType registerType, Class<T> returnType)
    {
        NOT_NULL.verify(queryable, registerType);
        return clone(queryable.getName(), queryable, queryable.getParams(), registerType, returnType);
    }
    
    /**
     * Clone {@code queryable} object with a return type if no {@code null}
     * @param name of new query
     * @param queryable query instance
     * @param params parameter of new query
     * @param registerType registry of type data
     * @param returnType type of return that overload return type from XML
     * @param <T> type of return type 
     * @return new instance of Queryable instance
     */
    public static <T> Queryable clone(String queryName, Queryable queryable, Object params, RegisterType registerType, Class<T> returnType)
    {
        QueryName q = new QueryName(queryName, params, queryable.getOffset(), queryable.getMax(), registerType);
        
        if (returnType != null)
            q.setReturnType(returnType);
        else if (queryable.hasReturnType())
            q.setReturnType(queryable.getReturnType());
        
        if(queryable.isCacheIgnore())
            q.cacheIgnore();
        
        if(queryable.isScalar())
            q.scalar();
        
        return q;
    }

//  public static Queryable of(String name, List params)
//  {
//      return new QueryName(name, params, 0, Integer.MAX_VALUE);
//  }
//  
//  /**
//   * Build a new {@code Queryable} object
//   * @param name query name
//   * @param offset the first row
//   * @param max row numbers
//   * @param args dynamically arguments to create {@code Queryable}.
//   * <p>
//   * 1o first param it's key name and 2o your value
//   * <p>
//   * 3o it's key 4o your value and so on.
//   * @return Queryable object with parameters with limited result by {@code max}
//   */
//  public static Queryable of(String name, int offset, int max, Object... args)
//  {
//      QueryName q = (QueryName) buildQueryable(name, args);
//      q.setOffset(offset);
//      q.setMax(max);
//      return q;
//  }
    /*
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
     *
    public static <T> Queryable of(String name, Class<T> returnType, int offset, int max, Object... args)
    {
        QueryName q = (QueryName) buildQueryable(name, args);
        q.setOffset(offset);
        q.setMax(max);
        q.setReturnType(returnType);
        return q;
    }
   */
    
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
    private static Queryable buildQueryable(String name, RegisterType registerType, Object... args)
    {
        NOT_NULL.verify(args);
        Map<String, Object> params = new HashMap<String, Object>();
        Object value = null;
        Queryable queryable = null;
        if (args.length == 1)
        {
            queryable = new QueryName(name, args[0], 0, Integer.MAX_VALUE, registerType);
        }
        else
        {
            int i = 0;
            String key = null;
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
            if (i > 1 )
                queryable = new QueryName(name, params, 0, Integer.MAX_VALUE, registerType);
            else
                queryable = new QueryName(name, null, 0, Integer.MAX_VALUE, registerType);
        }
        return queryable;
    }
}
