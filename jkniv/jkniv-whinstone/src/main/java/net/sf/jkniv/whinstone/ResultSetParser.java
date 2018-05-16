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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Responsible to extract ResultSet rows and create a list of POJO.
 * 
 * @param <T> POJO to receive the values from result of query.
 * @param <R> The driver result of a query like {@link ResultSet}
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public interface ResultSetParser<T, R>
{
    /**
     * Execute the parser at ResultSet iterating over them.
     * @param rs Result
     * @return list of POJO
     * @throws SQLException when some access error happens
     */
    List<T> parser(R rs) throws SQLException;
}
