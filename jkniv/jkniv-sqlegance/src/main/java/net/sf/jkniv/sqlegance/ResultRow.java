/* 
 * JKNIV, SQLegance keeping queries maintainable.
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

import java.sql.SQLException;

import net.sf.jkniv.sqlegance.classification.Transformable;

/**
 * Extract the result for each row from {@code ResultSet}.
 * 
 * ResultRow must be stateless.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 * @param <T> Type of objects thats must be returned.
 * @param <R> Type of objects thats database connection return as row, JDBC is {@code ResultSet} JPA is {@code Object[]}.
 */
public interface ResultRow<T,R>
{
    /**
     * Retrieve column values from the current row, implementations doesn't call {@code next} 
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
}
