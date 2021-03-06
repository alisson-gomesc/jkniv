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
import java.sql.DriverManager;
import java.util.Properties;

import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.transaction.Isolation;
import net.sf.jkniv.whinstone.ConnectionAdapter;
import net.sf.jkniv.whinstone.transaction.TransactionContext;
import net.sf.jkniv.whinstone.transaction.TransactionSessions;

public class DriverManagerAdapter extends AbstractJdbcAdapter
{
    private Properties                       props;
    private Isolation                        defaultIsolation;
    private String                           driver;
    private String                           url;
    
    public DriverManagerAdapter(Properties props, String contextName)//(Properties props, Isolation defaultIsolation, String name)
    {
        super(contextName);
        this.props = props;
        this.defaultIsolation = Isolation.DEFAULT;
        this.driver = props.getProperty(RepositoryProperty.JDBC_DRIVER.key());
        this.url = props.getProperty(RepositoryProperty.JDBC_URL.key());
        if (driver != null)
            register();
    }
    
    /**
     * Attempts to establish a connection to the database
     * @return a Connection to the URL
     * @throws net.sf.jkniv.sqlegance.RepositoryException if cannot establish a connection
     */
    public ConnectionAdapter open() 
    {
        return open(defaultIsolation);
    }
    
    /**
     * Attempts to establish a connection to the database with specific isolation 
     * @param isolation transaction level for connection
     * @return a Connection to the URL
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
                LOG.debug("Getting new connection from DriverManager");
                Connection jdbcConn = DriverManager.getConnection(url, props);
                setIsolation(jdbcConn, isolation);
                adapter = new JdbcConnectionAdapter(contextName, jdbcConn, this.handlerException);
            }
            catch (Exception e)//SQLException
            {
                handlerException.handle(e,
                        "SEVERE FAIL, cannot get database connection url [" + url + "] config=" + props);
            }
            
        }
        return adapter;
    }

    private void register()
    {
        try
        {
            Class.forName(driver);
        }
        catch (ClassNotFoundException e)
        {
            new net.sf.jkniv.reflect.ReflectionException("ClassNotFoundException for register ["+driver+"]", e);
        }
    }
}
