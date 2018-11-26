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

import java.util.List;

import net.sf.jkniv.sqlegance.NonUniqueResultException;
import net.sf.jkniv.sqlegance.QueryNotFoundException;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.transaction.Transactional;

/**
 * 
 * A repository holds the connections to data base server, allowing it to be queried through Queryable objects.
 * 
 * Repository instances must be thread-safe and usually a single instance is enough per application.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public interface Repository
{
    
    public static final String TRANSACTION_NONE             = "NONE";
    public static final String TRANSACTION_READ_UNCOMMITTED = "UNCOMMITTED";
    public static final String TRANSACTION_READ_COMMITTED   = "COMMITTED";
    public static final String TRANSACTION_REPEATABLE_READ  = "REPEATABLE_READ";
    public static final String TRANSACTION_SERIALIZABLE     = "SERIALIZABLE";
    
    /**
     * Get one object instance from repository using a query.
     * 
     * @param queryable
     *            The Query name with yours parameters
     * @param <T> generic type to be returned 
     * @return Return the object that matches with query. A null reference is
     *         returned if the query no match anyone object.
     * @throws QueryNotFoundException when not found the query name
     * @throws NonUniqueResultException when select or select with group by recovery more one objects
     */
    <T> T get(Queryable queryable);

    /**
     * Get one object instance of type {@code returnType} from repository using a query.
     * @param queryable The Query name with yours parameters
     * @param returnType overload the returned type from XML file
     * @param <T> type of object returned
     * @return Return the object {@code returnType}that matches with query. A null reference is
     *         returned if the query no match anyone object.
     * @throws QueryNotFoundException when not found the query name
     * @throws NonUniqueResultException when select or select with group by recovery more one objects
     */
    <T> T get(Queryable queryable, Class<T> returnType);

    /**
     * Get one object instance from repository using a query.
     * @param queryable The Query name with yours parameters
     * @param resultRow Customized extractor to process each row from query
     * @param <T> type of object returned
     * @param <R> raw type from repository API (Like {@code ResultSet} from JDBC)
     * @return Return the object {@code returnType} that matches with query. A null reference is
     *         returned if the query no match anyone object.
     * @throws QueryNotFoundException when not found the query name
     * @throws NonUniqueResultException when select or select with group by recovery more one objects
     */
    <T,R> T get(Queryable queryable, ResultRow<T,R> resultRow);

    /**
     * Get one object instance from repository using a {@code object} as parameter.
     * @param object object that contains the parameters to retrieve the entity.
     * @param <T> type of object returned
     * @return Return the object {@code returnType} that matches with query. A null reference is
     *         returned if the query no match anyone object.
     * @throws QueryNotFoundException when not found the query name
     * @throws NonUniqueResultException when select or select with group by recovery more one objects
     */
    <T> T get(T object);

    /**
     * Get one object instance of type {@code returnType} from repository using a {@code object} as parameter.
     * @param returnType overload the returned type from XML file
     * @param object parameters from query to retrieve the object
     * @param <T> type of object returned
     * @return Return the object {@code returnType} that matches with query. A null reference is
     *         returned if the query no match anyone object.
     * @throws QueryNotFoundException when not found the query name
     * @throws NonUniqueResultException when select or select with group by recovery more one objects
     */
    <T> T get(Class<T> returnType, Object object);
    
    /**
     * Retrieve a number, numerical quantity, string, date, boolean, some primitive value (wrapped), 
     * the type is checked against jdbc data type.
     * 
     * <ul>
     * <li>java.math.BigDecimal</li>
     * <li>java.lang.Boolean</li>
     * <li>java.lang.Byte</li>
     * <li>java.lang.Double</li>
     * <li>java.lang.Float</li>
     * <li>java.lang.Integer</li>
     * <li>java.lang.Long</li>
     * <li>java.lang.Short</li>
     * <li>java.lang.String</li>
     * <li>java.lang.Character</li>
     * <li>java.util.Date</li>
     * </ul>
     * @param queryable The Query name with yours parameters
     * @param <T> the single type to be returned
     * @return Scalar value
     * @throws QueryNotFoundException when not found the query name
     * @throws NonUniqueResultException when select or select with group by recovery more one objects
     */
    <T> T scalar(Queryable queryable);

    /**
     * Use the {@link Queryable} to retrieve data and appends to the {@link Queryable#getParams()} 
     * parameters, where for each {@code get} method must have a correspondent {@code set}.
     * 
     * @param queryable Query with yours parameters
     * @return return {@code true} if the objects was enriched, {@code false} otherwise.
     * @throws QueryNotFoundException when not found the query name
     */
    boolean enrich(Queryable queryable);// TODO test enrich
                                        // FIXME implements using MAP
    
    /**
     * Retrieve a set of objects {@code T} from repository using a query.
     * 
     * @param queryable The Query name with yours parameters
     * @param <T> generic type to be returned
     * @return Return a set of object that matches with query. A empty list is
     *         returned if the query no match anyone object.
     * @throws QueryNotFoundException when not found the query name
     */
    <T> List<T> list(Queryable queryable);

    /**
     * Retrieve a set of objects {@code returnType} from repository using a query.
     * @param queryable The Query name with yours parameters
     * @param returnType overload the returned type from XML file
     * @param <T> type of object returned
     * @return Return a set of object that matches with query. A empty list is
     *         returned if the query no match anyone object.
     * @throws QueryNotFoundException when not found the query name
     */
    <T> List<T> list(Queryable queryable, Class<T> returnType);

    /**
     * Retrieve a set of objects {@code T} from repository using a query.
     * @param queryable The Query name with yours parameters
     * @param resultRow Customized extractor to process each row from query
     * @param <T> type of object returned
     * @param <R> raw type from repository API (Like {@code ResultSet} from JDBC)
     * @return Return a set of object that matches with query. A empty list is
     *         returned if the query no match anyone object.
     * @throws QueryNotFoundException when not found the query name
     */
    <T,R> List<T> list(Queryable queryable, ResultRow<T, R> resultRow);

    /**
     * Add a new object to repository.
     * 
     * @param queryable name of query must be executed to add object to repository
     * @return Return the numbers of objects inserted.
     * @throws QueryNotFoundException when not found the query name
     */
    int add(Queryable queryable);
    
    /**
     * Add a new object to repository.
     * @param entity object to be added to repository
     * @param <T> type of object returned
     * @return Return the object {@code entity} that matches with query. A null reference is
     *         returned if the query no match anyone object.
     * @throws IllegalArgumentException when entity is null
     */
    <T> T add(T entity);// FIXME return void must be available
    
    /**
     * Update many objects in repository.
     * 
     * @param queryable The Query name with yours parameters
     * @return Return the numbers of objects updated.
     * @throws QueryNotFoundException when not found the query name
     * @throws IllegalArgumentException
     *             when the query is different from update sentence
     */
    int update(Queryable queryable);

    /**
     * Update object(s) in repository.
     * @param entity update the repository with a {@code entity} values mapped at query.
     * @param <T> type of object returned
     * @return the instance that was updated to
     * @throws IllegalArgumentException when entity is null
     */
    <T> T update(T entity);
    
    /**
     * Execute a query to remove one or many objects from repository.
     * 
     * @param queryable The Query name with yours parameters
     * @return the numbers of rows removed.
     * @throws IllegalArgumentException
     *             when the query is different from delete
     */
    int remove(Queryable queryable);
    
    /**
     * Remove the {@code entity} from repository.
     * @param entity object with the parameters 
     * @param <T> type of object returned
     * @throws IllegalArgumentException when entity is null
     * @return the numbers of objects removed.
     */
    <T> int remove(T entity);
    
    /**
     * Push the queries from buffer to repository.
     */
    void flush();
    
    Transactional getTransaction();
    
    /**
     * Close the connection with the database.
     * After close the repository any query cannot be executed against the database 
     * then all subsequent access to the repository throw a {@code RepositoryException} 
     */
    void close();
    
    boolean containsQuery(String name);
}
