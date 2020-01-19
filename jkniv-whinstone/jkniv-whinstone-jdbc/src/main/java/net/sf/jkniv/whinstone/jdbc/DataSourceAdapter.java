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

import java.sql.Connection;

import javax.sql.DataSource;

import net.sf.jkniv.sqlegance.transaction.Isolation;
import net.sf.jkniv.whinstone.ConnectionAdapter;
import net.sf.jkniv.whinstone.transaction.TransactionContext;
import net.sf.jkniv.whinstone.transaction.TransactionSessions;

public class DataSourceAdapter extends AbstractJdbcAdapter
{
    private DataSource dataSource;
    private Isolation  defaultIsolation;
    
    public DataSourceAdapter(DataSource ds, String contextName)
    {
        super(contextName);
        this.dataSource = ds;
        this.defaultIsolation = Isolation.DEFAULT;
    }
    
    /**
     * Attempts to establish a connection to the database 
     * @return a Connection from DataSource
     * @throws net.sf.jkniv.sqlegance.RepositoryException if cannot establish a connection
     */
    public ConnectionAdapter open()
    {
        return open(defaultIsolation);
    }
    
    /**
     * Attempts to establish a connection to the database with specific isolation 
     * @param isolation transaction level for connection
     * @return a Connection from DataSource
     * @throws net.sf.jkniv.sqlegance.RepositoryException if cannot establish a connection
     */
    public ConnectionAdapter open(Isolation isolation)
    {
        ConnectionAdapter adapter = getConnection(isolation);
        return adapter;
    }
    
    private ConnectionAdapter getConnection(Isolation isolation)
    {
        ConnectionAdapter adapter = null;
        TransactionContext transactionContext = TransactionSessions.get(this.contextName);
        
        if (transactionContext != null && transactionContext.isActive())
        {
            LOG.debug("Taking existent Connection from Transaction Context");
            adapter = transactionContext.getConnection();
        }
        if (adapter == null)
        {
            try
            {
                LOG.trace("Getting new connection from DataSource");
                Connection jdbcConn = dataSource.getConnection();
                setIsolation(jdbcConn, isolation);
                adapter = new JdbcConnectionAdapter(contextName, jdbcConn, handlerException);
            }
            catch (Exception e)//SQLException
            {
                handlerException.handle(e, "SEVERE FAIL, cannot get database connection datasource [" + dataSource
                        + "] Reason: " + e.getMessage());
            }
        }
        return adapter;
    }
    
}
