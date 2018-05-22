/* 
 * JKNIV, SQLegance keeping queries maintainable.
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

import java.util.Iterator;

import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.whinstone.params.AutoBindParams;
import net.sf.jkniv.whinstone.params.ParameterNotFoundException;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

/**
 * This class represent a query object to find the query to be performed and
 * your parameters like: query parameters, offset and max objects to return.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public interface Queryable
{
    /**
     * The query name used to localize the query at in XML file.
     * 
     * @return Return the name of the query.
     */
    String getName();
    
    /**
     * Parameters of query. Can be objects like Author, Book, etc. or a
     * java.util.Map.
     * 
     * @return the parameters to use at query.
     */
    Object getParams();
    
    /**
     * Recover the value of the named parameter (can be nested), with no type conversions.
     * 
     * @param name indexed and/or nested name of the property to be recovered
     * @return the property value
     * @throws ParameterNotFoundException when cannot find the property.
     */
    Object getProperty(String name);
    
    /**
     * Get the number from first row from query result. If not set, rows will be
     * retrieved beginning from row 0.
     * 
     * @return return a row number, numbered from 0.
     */
    int getOffset();
    
    /**
     * Set the number from first row from query result. If not set, rows will be
     * retrieved beginning from row 0.
     * 
     * @param value initial value of row number
     */
    void setOffset(int value);
    
    /**
     * Get the maximum number of rows from query result.
     * 
     * @return Return the maximum number.
     */
    int getMax();
    
    /**
     * Set the maximum number of rows from query result.
     * 
     * @param value max row number to be fetch
     */
    void setMax(int value);
    
    /** FIXME design rules and make unit test
     * Get total of rows that query can retrieve, filled after query is
     * executed. Useful to make pagination.
     * 
     * @return return the number of rows from query result.
     * <ul>
     *  <li>{@code n} - the real rows total</li> 
     *  <li>{@code -1} - the query isn't executed yet</li> 
     *  <li>{@code -2} - the query was executed successfully but the number of rows is unavailable</li> 
     *  <li>{@code -3} - the query was executed with error and the number of rows is unavailable</li> 
     * </ul>
     */
    long getTotal();
    
    /**
     * Set total of rows if run non paging query. 
     * @param total of records running full select
     */
    void setTotal(long total);

    /**
     * Is there a max row limit indicated?
     *
     * @return true whether a max row limit was indicated, false otherwise.
     */
    boolean isPaging();
    
    /**
     * Is there a offset to skip first rows?
     * 
     * @return return true if have query must be paging, false otherwise.
     */
    boolean hasRowsOffset();
/*    * Verify if query is paginated, that must return the total records of the
    * query.
    * 
    * @param query The query
    * @return Return true if the query is paginated, false otherwise.
*/
    /**
     * Mark the query as scalar value, means must be return just one value
     */
    void scalar();
    // FIXME design when invoked as scalar the query cannot be execute another way, always is scalar
    // return IllegalStateException when this happen
    
    /**
     * verify if the query is scalar value.
     * @return <code>true</code> when the retrieve unique value, <code>false</code> otherwise
     */
    boolean isScalar();
    
    /**
     * Check if SQL object was bind to this instance.
     * @return {@code true} when was bind, {@code false} otherwise.
     */
    boolean isBoundSql();
    
//    /**
//     * Check if return type from was bind to this instance
//     * @return {@code true} when was bind, {@code false} otherwise.
//     */
//    boolean isBoundReturnType();
    
    /**
     * Check if the parameters from queryable was bind to {@link StatementAdapter} instance.
     * 
     * @return {@code true} when was bind, {@code false} otherwise.
     */
    boolean isBoundParams();
    
    boolean isTypeOfNull();
    
    boolean isTypeOfBasic();

    boolean isTypeOfArrayFromBasicTypes();

    boolean isTypeOfCollectionFromBasicTypes();

    boolean isTypeOfArrayFromPojo();

    boolean isTypeOfArrayFromMap();

    boolean isTypeOfCollectionFromMap();
    
    boolean isTypeOfCollectionFromPojo();

    boolean isTypeOfCollectionFromArray();
    
    /**
     * verify if the parameter is an array
     * @return <code>true</code> when they are, <code>false</code> otherwise
     */
    boolean isTypeOfArray();
    
    /**
     * verify if the parameter is a collection
     * @return <code>true</code> when they are, <code>false</code> otherwise
     */
    boolean isTypeOfCollection();
    
    /**
     * verify if the parameter is a collection or array
     * @return <code>true</code> when they are, <code>false</code> otherwise
     */
    boolean isTypeOfBulk();
    
    /**
     * verify if the parameter is a map
     * @return <code>true</code> when they are, <code>false</code> otherwise
     */
    boolean isTypeOfMap();

    // TODO test iterator over basic types (String, Date, Numbers...)
    /**
     * 
     * Returns an iterator over the collection of {@code params} or array of {@code params}. 
     * There are no guarantees concerning the order in which the elements are returned
     * 
     * @return an <tt>Iterator</tt> over the elements in {@code params}
     * @throws UnsupportedOperationException when {@code params} isn't collection or array.
     * @throws NullPointerException when {@code params} is {@code null}.
     */
    Iterator<Object> iterator();
    
    /**
     * Extract the values from {@code params} matching the names array.
     * 
     * @param names array of names to get yours values
     * @return an {@code array} of values from {@code params}, base-zero array is returned when 
     * the names doesn't match.
     */
    Object[] values(String[] names);
    
    /**
     * Bind Sql to {@link Queryable} instance
     * 
     * @param sql The instance of static or dynamic SQL statement 
     */
    void bind(Sql sql);
    
    <T, R> AutoBindParams bind(StatementAdapter<T, R> adapter);
    
    /**
     * Dynamic SQL statement from XML file.
     * 
     * @return SQL statement.
     */
    Sql getDynamicSql();
    
    /**
     * Final SQL statement ready to prepared statement.
     * 
     * <p><b>Note: </b>Needs to bind {@link #bind(Sql)} before to call this method.
     * 
     * @return Final SQL statement using question mark read to prepared statement
     */
    String query();
    
    /**
     * Final SQL statement ready to prepared statement to count the total of records from original query. 
     * 
     * <p><b>Note: </b>Needs to bind {@link #bind(Sql)} before to call this method.
     * 
     * @return SQL statement using question mark read to prepared statement
     */
    String queryCount();
    
    /**
     * Name of parameters that bound with final SQL statement
     * 
     * @return parameters names that was query bound, array based-zero is return if query no have parameters
     */
    String[] getParamsNames();
    
    /**
     * Overload the return type from XML query.
     * @return the class to return by repository using this query
     */
    Class getReturnType();
    // FIXME design when invoked return type it's overload the sql the list or get method cannot change de value
    // return IllegalStateException when this happen

//    /**
//     * Overload the return type from XML query.
//     * @param clazz to return by repository using this query
//     */
//    void setReturnType(Class clazz);
//    
}
