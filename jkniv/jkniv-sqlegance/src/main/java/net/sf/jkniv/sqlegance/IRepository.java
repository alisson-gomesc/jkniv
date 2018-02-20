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
package net.sf.jkniv.sqlegance;

import java.util.List;

/**
 * Main interface to persistent a collections-like objects. The implementation
 * of this interface are at the other component named whinstone-*(jpa, jdbc, etc)
 * 
 * @author Alisson Gomes
 * @param <T>
 *            Type of object
 * @since 0.0.2
 * @deprecated use Repository. The Repository interface break compatibility with this interface
 */
public interface IRepository<T>
{
    static String PROPERTY_SHOW_CONFIG = "jkniv.repository.show_config";
    //static String PROPERTY_SHOW_CONFIG = "jkniv.repository.show_config";
    //static String PROPERTY_PERSISTENCE_UNIT_NAME = "jkniv.repository.persistence_unit_name";
    /**
     * Add a new Object at repository.
     * 
     * @param object instance to be persistenced
     */
    void add(T object);
    
    /**
     * Remove an existent object at repository.
     * 
     * @param object instance to be removed
     */
    void remove(T object);
    
    /**
     * Execute a query to remove one or many objects from repository.
     * 
     * @param query
     *            Query with parameters
     * @return Return the numbers of objects removed.
     * @throws IllegalArgumentException
     *             when the query is different from delete
     */
    int remove(IQuery query);
    
    /**
     * Up date the object value at repository.
     * 
     * @param object instance to be updated
     * @return TODO javadoc
     */
    T update(T object);
    
    /**
     * Up date by query many objects.
     * 
     * @param query
     *            Query with parameters
     * @return Return the numbers of objects updated.
     * @throws IllegalArgumentException
     *             when the query is different from update sentence
     */
    int update(IQuery query);
    
    /**
     * Get one object instance from repository using a query with name "T.get",
     * where 'T' it's a generic type.
     * 
     * @param object instance to be retrieved
     * @return Return the object that matches with query. A null reference is
     *         returned if the query no match anyone object.
     */
    T get(T object);
    
    /**
     * Get one object instance from repository using a query.
     * 
     * @param query
     *            Query with parameters
     * @return Return the object that matches with query. A null reference is
     *         returned if the query no match anyone object.
     */
    T get(IQuery query);
    
    /**
     * Get a set of objects from repository using a query with name "T.list",
     * where 'T' it's a generic type.
     * 
     * @return Return a set of object that matches with query. A empty list is
     *         returned if the query no match anyone object.
     */
    List<T> list();
    
    /**
     * Get a set of objects from repository using a query.
     * 
     * @param query
     *            Query with parameters
     * @return Return a set of object that matches with query. A empty list is
     *         returned if the query no match anyone object.
     */
    List<T> list(IQuery query);
    
    /**
     * Get a set of objects 'G' from repository using a query.
     * 
     * @param query
     *            Query with parameters
     * @param clazz
     *            Type of object from list of object
     * @param <G> generic type to return as instance
     * @return Return a set of object that matches with query. A empty list is
     *         returned if the query no match anyone object.
     */
    <G> List<G> list(IQuery query, Class<G> clazz);
    
    /*
     * Perform a query and return a scalar value.
     * @param query
     * @return Return a singular value from a query. A null value is returned
     * when the query cannot found any value. Number getValue(Queryable query);
     */
    
    /**
     * Command to execute SQL statements that are in the buffer.
     */
    void flush();
}
