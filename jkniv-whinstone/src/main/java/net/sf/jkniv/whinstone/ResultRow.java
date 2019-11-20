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

import java.sql.SQLException;

import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.whinstone.classification.Transformable;

/**
 * Extract the result for each row from {@code ResultSet}.
 * <p>
 * <b>Note:</b> ResultRow must be {@code stateless}.
 * 
 * @param <T> Type of objects thats must be returned.
 * @param <R> Type of objects thats database connection return as row, JDBC is {link ResultSet} 
 * JPA is {@code Object[]}.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public interface ResultRow<T,R>
{
    /**
     * Retrieve column values from the current row, implementations must don't call {@code next} 
     * neither {@code close} methods.
     * 
     * @param rs A ResultSet or Object[] pointing to its current row of data
     * @param rownum The row number
     * @return the instance of object with {@code ResultSet} data.
     * @throws SQLException errors that occurs when access {@code ResultSet} methods.
     */
    T row(R rs, int rownum) throws SQLException;
    
    Transformable<T> getTransformable();
    
    void setColumns(JdbcColumn<R>[] columns);
    
    /*
     * Set into {@code proxy} the value of {@code jdbcObject}
     * @param column metadata
     * @param jdbcObject value of column row
     * @param proxy for object that represents a row {@code T}
     */
    //void setValueOf(JdbcColumn<?> column, Object jdbcObject, ObjectProxy<?> proxy);

}
