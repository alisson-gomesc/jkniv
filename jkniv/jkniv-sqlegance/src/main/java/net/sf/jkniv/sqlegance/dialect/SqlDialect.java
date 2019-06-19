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
package net.sf.jkniv.sqlegance.dialect;

import java.sql.Connection;
import java.sql.PreparedStatement;

import net.sf.jkniv.sqlegance.Sql;

/**
 * Represents a SQL syntax from a specific database.
 * 
 * The implementations provider correct syntax to execute a paging query, that limits the number
 * of rows returned and SQL to count of total records.
 * 
 * <p><b>Note:</b> The implementation MUST BE state-less and thread-safe because one instance parse all queries from one
 * {@code Repository}.
 * 
 * @author Alisson Gomes 
 */
public interface SqlDialect
{
    /**
     * Dialect name
     * @return the dialect name
     */
    String name();
    
    /*
     * Native syntax of SQL vendor support some form of limiting query results?
     *
     * @return <strong>True</strong> if this dialect supports some form of LIMIT, <strong>false</strong> otherwise.
     *
    boolean supportsLimit();
    
    /*
     * Native syntax of SQL vendor support some form of offset query results?
     *
     * @return <strong>True</strong> if this dialect supports some form of OFFSET, <strong>false</strong> otherwise.
     *
    boolean supportsLimitOffset();
    
    /*
     * Native syntax of SQL vendor support some form of enumerate the rows results?
     *
     * @return <strong>True</strong> if this dialect supports some form of ROWNUM, <strong>false</strong> otherwise.
     *
    boolean supportsRownum();
    

    // SQL Server supports holdability at the connection level only. Use the connection.setHoldability() method. 
    boolean supportsStmtHoldability();
    
    boolean supportsConnHoldability();
    */
    boolean supportsFeature(SqlFeatureSupport feature);
    /*
    Associates the specified value with the specified key in this map. 
    If the map previously contained a mapping for the key, the old value is replaced.

    Specified by: put(...) in Map, Overrides: put(...) in AbstractMap
    Parameters:key key with which the specified value is to be associatedvalue 
    value to be associated with the specified key
    Returns:the previous value associated with key, or null if there was no mapping for key. (A null return can also indicate that the map previously associated null with key.)
    */
    /**
     * Override a SQL ANSI feature
     * @param sqlFeature override a {@link SqlFeature} supports.
     * @return the previous value associated with key {@code sqlFeature}
     */
    SqlFeature addFeature(SqlFeature sqlFeature);
    
    /**
     * Return the limit of elements in an {@code INPUT} parameter the database supports.
     *
     * @return the limit of parameters from statement, default is a big number {@code Integer.MAX_VALUE}
     */
    int getMaxOfParameters();
    
    /**
     * Max number of parameters supported by JDBC driver
     * @param max maximum value of parameter in the query
     */
    void setMaxOfParameters(int max);
    
    String getSqlPatternCount();
    
    String getSqlPatternPaging();
    
    PreparedStatement prepare(Connection conn, Sql isql, String query);
    
    //Sql getISql();
    
//    String query();
    
//    String queryCount();
    
//    Queryable getQueryable();
    
 //   int countParams();
    
//    String[] getParamsNames();
    
//    Object[] getParamsValues();
    
//    void setQueryable(Queryable queryable);
    
    /**
     * Build a paginate query accordingly data base dialect
     * @param sqlText final SQL with parameters to bind
     * @param offset number from first row from query result
     * @param max maximum number of rows from query result.
     * @return paginate query for specific data base dialect
     */
    String buildQueryPaging(final String sqlText, int offset, int max);
    
    String buildQueryCount(final String sqlText);
}
