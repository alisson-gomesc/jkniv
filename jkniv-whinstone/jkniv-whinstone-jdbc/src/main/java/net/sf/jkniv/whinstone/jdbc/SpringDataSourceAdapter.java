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

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.transaction.Isolation;
import net.sf.jkniv.whinstone.ConnectionAdapter;

public class SpringDataSourceAdapter extends AbstractJdbcAdapter
{
    private static final String SPRING_TRANSACTIONAWEREDATASOURCE_PROXY = "org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy";
    private static final String SPRING_DATASOURCE_UTILS                 = "org.springframework.jdbc.datasource.DataSourceUtils";
    private DataSource          dataSourceTarget;
    private ObjectProxy<?>      transactionAwareDataSourceProxy;
    private Isolation           defaultIsolation;
    //TransactionAwareDataSourceProxy dsProxy;
    
    public SpringDataSourceAdapter(DataSource ds, String contextName)//DataSource dataSource, Isolation defaultIsolation, String name)
    {
        super(contextName);
        this.defaultIsolation = Isolation.DEFAULT;
        this.dataSourceTarget = ds;
        //this.dsProxy = new TransactionAwareDataSourceProxy(ds);
        
        this.transactionAwareDataSourceProxy = ObjectProxyFactory
                .of(classForName(SPRING_TRANSACTIONAWEREDATASOURCE_PROXY));
        this.transactionAwareDataSourceProxy.setConstructorArgs(dataSourceTarget);
        this.transactionAwareDataSourceProxy.newInstance();
    }
    
    public ConnectionAdapter open()
    {
        return open(defaultIsolation);
    }
    
    public ConnectionAdapter open(Isolation isolation)
    {
        ConnectionAdapter adapter = null;
        try
        {
            LOG.debug("Getting Connection from Spring TransactionAwareDataSourceProxy class");
            Connection conn = (Connection) this.transactionAwareDataSourceProxy.invoke("getConnection");
            conn.setAutoCommit(false);
            adapter = new JdbcConnectionAdapter(contextName, conn, this.handlerException);
        }
        catch (SQLException e)
        {
            handlerException.handle(e, "SEVERE FAIL, cannot get database connection datasource with Reason: " + e.getMessage());
        }
        return adapter;
    }
    
    @Override
    public void close(ConnectionAdapter conn)
    {
        LOG.debug("Release Connection [{}] from Spring DataSourceProxy", conn);
        doReleaseConnection(conn);
    }
    
    private void doReleaseConnection(ConnectionAdapter conn)
    {
        Class<?>[] types =
        { Connection.class, DataSource.class };
        Method m;
        try
        {
            //DataSourceUtils.doReleaseConnection(conn, dsProxy.getTargetDataSource());
            DataSource dataSourceTarget = null;
            m = classForName(SPRING_DATASOURCE_UTILS).getDeclaredMethod("doReleaseConnection", types);
            m.invoke(null, new Object[]
            { (Connection)conn.unwrap(), dataSourceTarget });
        }
        catch (Exception e)
        // TODO design exception IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
        {
            handlerException.handle(e, "Cannot invoke [doReleaseConnection] from " + SPRING_DATASOURCE_UTILS + " class");
        }
    }
    
    private Class<?> classForName(String name)
    {
        try
        {
            return Class.forName(name);
        }
        catch (ClassNotFoundException e)
        {
            throw new RepositoryException("Cannot returns the Class object associated with the class [" + name + "]",
                    e);
        }
    }
}
