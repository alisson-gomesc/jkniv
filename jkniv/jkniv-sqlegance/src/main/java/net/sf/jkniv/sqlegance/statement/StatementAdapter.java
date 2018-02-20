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
package net.sf.jkniv.sqlegance.statement;

import java.sql.ResultSet;
import java.util.List;
import java.util.Set;

import net.sf.jkniv.sqlegance.KeyGeneratorType;
import net.sf.jkniv.sqlegance.OneToMany;
import net.sf.jkniv.sqlegance.Repository;
import net.sf.jkniv.sqlegance.ResultRow;
import net.sf.jkniv.sqlegance.ResultSetParser;

/**
 * Adapter for Statments {@link java.sql.Statement}, {@link java.sql.PreparedStatement}, 
 * {@code java.sql.CallableStatement} or {@code javax.persistence.Query}
 * 
 * @param <T> type of object to return by {@link Repository}
 * @param <R> The driver result of a query like {@link ResultSet}
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public interface StatementAdapter<T, R>
{
    
    /**
     * Bind an argument to a named parameter.
     * @param name of parameter
     * @param value of parameter
     * @return this instance
     */
    StatementAdapter<T,R> bind(String name, Object value);

    /**
     * Bind an argument to a position parameter.
     * @param value of parameter
     * @return this instance
     */
    StatementAdapter<T, R> bind(Object value);
    //* @param position index of parameter positions, initial is 1
    //StatementAdapter<T, R> bind(int position, Object value);

    /**
     * Bind the varargs parameters to statement
     * @param values of parameters as arbitrary number
     * @return instance of this object
     */
    StatementAdapter<T, R> bind(Object... values);

    StatementAdapter<T, R> returnType(Class<T> returnType);
    
    StatementAdapter<T, R> resultRow(ResultRow<T, R> resultRow);
    
    StatementAdapter<T, R> scalar();
    
    StatementAdapter<T, R> oneToManies(Set<OneToMany> oneToManies);
    
    StatementAdapter<T, R> groupingBy(List<String> groupingBy);

    StatementAdapter<T, R> keyGeneratorType(KeyGeneratorType keyGeneratorType);

    KeyGeneratorType getKeyGeneratorType();

    ResultSetParser<T, R> generatedKeys();
    
    List<T> rows();
    
    void batch();
    
    int execute();
    
/*    
    void addBatch();
 
    boolean execute();
     
     
     int executeUpdate();

     Object getMetaData(); // TODO implements interface to ResultSetMetaData
*/
    /**
     * reset the internal index position parameter to zero.
     * @return return current index position before reset
     */
    int reset();
   
    
    
}
