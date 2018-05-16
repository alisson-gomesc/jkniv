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

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.transaction.Isolation;
import net.sf.jkniv.whinstone.transaction.Transactional;


/**
 * Responsable to open connections with a specific database.
 * 
 * @author Alisson Gomes
 *
 */
public interface ConnectionFactory
{
    /**
     * Attempts to establish a connection to the database
     * @return a Connection from the URL or DataSource
     * @throws net.sf.jkniv.sqlegance.RepositoryException if cannot establish a connection
     */
    ConnectionAdapter open();

    /**
     * Attempts to establish a connection to the database with specific isolation 
     * @param isolation isolation level from transaction
     * @return a Connection from the URL or DataSource
     * @throws net.sf.jkniv.sqlegance.RepositoryException if cannot establish a connection
     */
    ConnectionAdapter open(Isolation isolation);
    
    /**
     * Create new Transaction Manager for JDBC transactions
     * @return a LOCAL, GLOBAL or EJB transaction manager.
     */
    Transactional getTransactionManager();
    
    /**
     * Name from repository context
     * @return name of context
     */
    String getContextName();

    /**
     * <code>null-safe</code> close connection.
     * Throws {@code SQLException} is logged as warning.
     * @param conn connection to close
     */
    void close(ConnectionAdapter conn);
        
    /**
     * <code>null-safe</code> close PreparedStatement.
     * Throws {@code SQLException} is logged as warning.
     * @param stmt statement to close
     */
    void close(PreparedStatement stmt);
    
    /**
     * <code>null-safe</code> close Statement.
     * Throws {@code SQLException} is logged as warning.
     * @param stmt statement to close
     */
    void close(Statement stmt);
    
    /**
     * <code>null-safe</code> close ResultSet.
     * Throws {@code SQLException} is logged as warning.
     * @param rs ResultSet to close
     */
    void close(ResultSet rs);
    
    /**
     * <code>null-safe</code> close CallableStatement.
     * Throws {@code SQLException} is logged as warning.
     * @param call CallableStatement to close
     */
    void close(CallableStatement call);

}
