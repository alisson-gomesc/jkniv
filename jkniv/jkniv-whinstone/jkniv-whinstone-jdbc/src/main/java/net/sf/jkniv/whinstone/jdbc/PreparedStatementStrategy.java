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
package net.sf.jkniv.whinstone.jdbc;

/**
 * TODO docme
 * 
 * @author Alisson
 *
 */
public interface PreparedStatementStrategy
{
  //  void setSqlDialect(SqlDialect sqlDialect);
    
//    void setSqlLogger(SqlLogger logger);
    
//    SqlLogger getSqlLogger();
    
    /*
     * Creates a PreparedStatement object that will generate ResultSet objects with the given type, 
     * concurrency, and holdability.
     * The parameters values is setting
     * @param conn Opened connection to database
     * @return a new PreparedStatement object, containing the pre-compiled SQL statement.
     * @throws net.sf.jkniv.sqlegance.RepositoryException wrapper SQLException
     * @see java.sql.SQLException
     */
    //PreparedStatement prepareStatement(Connection conn);
    
    /*
     * Creates a {@code PreparedStatement} object object capable of returning the auto-generated 
     * keys designated by the given array. The parameters values is setting
     * @param conn Opened connection to database
     * @param columnNames an array of column names indicating the columns that should be returned from the inserted row or rows
     * @return a new PreparedStatement object, containing the pre-compiled SQL statement.
     * @throws net.sf.jkniv.sqlegance.RepositoryException wrapper SQLException
     * @see java.sql.SQLException
     */
    //PreparedStatement prepareStatement(Connection conn, String[] columnNames);
    
    //PreparedStatement prepareStatementCount(Connection conn);
}
