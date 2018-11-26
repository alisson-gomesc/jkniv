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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.sqlegance.transaction.Isolation;

/**
 * @author Alisson Gomes
 *
 */
public class JdbcUtilsTest extends BaseSpringJUnit4
{
    @Autowired
    DataSource dataSourceDerby;


    @Test
    public void whenHaveDataSouceGetConnection() throws SQLException
    {
        Connection conn = dataSourceDerby.getConnection();
        Assert.assertFalse("Connection Is closed", conn.isClosed());
        JdbcUtils.close(conn);
        Assert.assertTrue("Connection Is open", conn.isClosed());
        Assert.assertNotNull(conn);
    }

    @Test @Ignore("JdbcConnection and JdbcFactory replace JbcdUtils")
    public void whenHaveDataSouceGetConnectionReadUnCommitted() throws SQLException
    {
        Connection conn = null;//JdbcUtils.getConnection(dataSource, Isolation.READ_UNCOMMITTED);
        Assert.assertTrue( Isolation.get(conn.getTransactionIsolation()) == Isolation.READ_UNCOMMITTED); 
        Assert.assertFalse("Connection Is closed", conn.isClosed());
        JdbcUtils.close(conn);
        Assert.assertTrue("Connection Is open", conn.isClosed());
        Assert.assertNotNull(conn);
    }
    
    @Test @Ignore("JdbcConnection and JdbcFactory replace JbcdUtils")
    public void whenHaveDataSouceGetConnectionReadCommitted() throws SQLException
    {
        Connection conn = null;//JdbcUtils.getConnection(dataSource, Isolation.READ_COMMITTED);
        Assert.assertTrue( Isolation.get(conn.getTransactionIsolation()) == Isolation.READ_COMMITTED); 
        Assert.assertFalse("Connection Is closed", conn.isClosed());
        JdbcUtils.close(conn);
        Assert.assertTrue("Connection Is open", conn.isClosed());
        Assert.assertNotNull(conn);
    }
    
    @Test @Ignore("JdbcConnection and JdbcFactory replace JbcdUtils")
    public void whenHaveDataSouceGetConnectionRepeatableRead() throws SQLException
    {
        Connection conn = null;//JdbcUtils.getConnection(dataSource, Isolation.REPEATABLE_READ);
        Assert.assertTrue( Isolation.get(conn.getTransactionIsolation()) == Isolation.REPEATABLE_READ); 
        Assert.assertFalse("Connection Is closed", conn.isClosed());
        JdbcUtils.close(conn);
        Assert.assertTrue("Connection Is open", conn.isClosed());
        Assert.assertNotNull(conn);
    }

    @Test @Ignore("JdbcConnection and JdbcFactory replace JbcdUtils")
    public void whenHaveDataSouceGetConnectionSerializable() throws SQLException
    {
        Connection conn = null;//JdbcUtils.getConnection(dataSource, Isolation.SERIALIZABLE);
        Assert.assertTrue( Isolation.get(conn.getTransactionIsolation()) == Isolation.SERIALIZABLE); 
        Assert.assertFalse("Connection Is closed", conn.isClosed());
        JdbcUtils.close(conn);
        Assert.assertTrue("Connection Is open", conn.isClosed());
        Assert.assertNotNull(conn);
    }

    
    @Test
    public void whenAllIsNullIsSafeCloseThen()
    {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        JdbcUtils.close(con, stmt, rs);
    }

    @Test
    public void whenConnectionIsNullIsSafeClose()
    {
        Connection con = null;
        JdbcUtils.close(con);
    }

    @Test
    public void whenStatementIsNullIsSafeClose()
    {
        Statement stmt = null;
        JdbcUtils.close(stmt);
    }

    @Test
    public void whenPreparedStatementIsNullIsSafeClose()
    {
        PreparedStatement stmt = null;
        JdbcUtils.close(stmt);
    }
    
    @Test
    public void whenResultSetIsNullIsSafeClose()
    {
        ResultSet rs = null;
        JdbcUtils.close(rs);
    }

    @Test
    public void whenCallableIsNullIsSafeClose()
    {
        CallableStatement call = null;
        JdbcUtils.close(call);
    }
}
