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
import java.sql.SQLException;
import java.util.Properties;

import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.transaction.Isolation;
import net.sf.jkniv.whinstone.ConnectionAdapter;

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
        ConnectionAdapter adapter = null;
        // TODO application must manually load any JDBC drivers prior to version 4.0
        //if (driver != null && !drivers.contains(driver))
        //    JdbcUtils.registerDriver(driver);
        
        try
        {
            LOG.debug("Getting Connection from DriverManager");
            Connection conn = DriverManager.getConnection(url, props);
            setIsolation(conn, isolation);
            adapter = new JdbcConnectionAdapter(conn);
        }
        catch (SQLException e)
        {
            handlerException.handle(e,
                    "SEVERE FAIL, cannot get database connection url [" + url + "] config=" + props);
        }
        return adapter;
    }    
}
