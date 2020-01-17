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

import net.sf.jkniv.reflect.beans.PropertyAccess;
import net.sf.jkniv.whinstone.types.ColumnType;

/**
 * Represents a column of database.
 * 
 * @param <R> The result of a query (ResultSet).
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public interface JdbcColumn<R>
{
    String getName();
    
    String getAttributeName();
    
    String getMethodName();
    
    PropertyAccess getPropertyAccess();

    int getIndex();
    
    ColumnType getType();
    
    boolean isBinary();

    boolean isClob();

    boolean isBlob();

    boolean isDate();
    
    boolean isTimestamp();

    boolean isTime();

    boolean isNestedAttribute();
    
    Object getValue(R resultSet) throws SQLException;
    
    Object getBytes(R resultSet) throws SQLException;
}
