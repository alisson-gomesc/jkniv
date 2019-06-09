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

import net.sf.jkniv.whinstone.statement.StatementAdapter;

/**
 * Adapter to abstract the Jdbc Connection {@link java.sql.Connection} 
 * representing a connection/session to a specific database (RDBMS or NoSQL).
 * 
 * @author Alisson Gomes
 *
 */
public interface ConnectionAdapter
{
    String getContextName();
    
    void commit() throws SQLException;
    
    void rollback() throws SQLException;
    
    void close(); //throws SQLException;

    boolean isClosed() throws SQLException;
    
    boolean isAutoCommit() throws SQLException;
    
    void autoCommitOn() throws SQLException;

    void autoCommitOff() throws SQLException;

    Object getMetaData();
    
/*
    PreparedStatement stmt, Class<T> returnType, ResultRow<T, ResultSet> rsRowParser,
    SqlDialect sqlDialect, SqlLogger sqlLogger)
*/

    //Command newCommand(Sql sql);

    <T,R> Command asSelectCommand(Queryable queryable, ResultRow<T, R> overloadResultRow);

    <T, R> Command asUpdateCommand(Queryable queryable);

    <T, R> Command asDeleteCommand(Queryable queryable);

    <T, R> Command asAddCommand(Queryable queryable);

    <T,R> StatementAdapter<T,R> newStatement(Queryable queryable);
    
    /**
     * create an adapter for a Prepared Statement
     * @param sql statement
     * @param <T> TODO documents T param
     * @param <R> TODO documents R param
     * @return Adapter for Prepared Statement
     */
    <T, R> StatementAdapter<T, R> newStatement(String sql);
    
    Object unwrap();

    /**
     * check if database needs a new round trip to get total paging
     * @return <b>true</b> when a new request to database must be done to get total of pages, <b>false</b> otherwise.
     */
    boolean supportsPagingByRoundtrip();

}
