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
package net.sf.jkniv.whinstone.statement;

import java.sql.ResultSet;
import java.util.List;

import net.sf.jkniv.sqlegance.KeyGeneratorType;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.ResultRow;

/**
 * Adapter for Statements {@link java.sql.Statement}, {@link java.sql.PreparedStatement}, 
 * {@link java.sql.CallableStatement} or {@link javax.persistence.Query}
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
     * @return instance of this object
     */
    StatementAdapter<T,R> bind(String name, Object value);

    /**
     * Bind an argument to a position parameter.
     * @param value of parameter
     * @return instance of this object
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

    /**
     * result row
     * @param resultRow how to process the rows
     * @return instance of this object
     */
    StatementAdapter<T, R> with(ResultRow<T, R> resultRow);
    
    StatementAdapter<T, R> with(AutoKey generateKey);

    /**
     * bind the keys generated
     */
    void bindKey();

    List<T> rows();
    
    void batch();
    
    int execute();
    
    /**
     * reset the internal index position parameter to zero.
     * @return return current index position before reset
     */
    int reset();
   
    void close();
    
    /**
     * Set the number of rows that should be fetched when the statement hit the database.
     * @param rows the number of rows to fetch
     */
    void setFetchSize(int rows);
}
