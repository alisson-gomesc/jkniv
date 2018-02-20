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
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.reflect.ReflectionUtils;
import net.sf.jkniv.sqlegance.RepositoryException;

/**
 * 
 * @author Alisson
 * @deprecated Use JdbcConnection interface with JdbcFactory
 */
final class JdbcUtils
{
    private static final Logger              LOG              = LoggerFactory.getLogger(JdbcUtils.class);
    private static final Set<String>        drivers          = new HashSet<String>();
    private static final HandleableException handlerException = new HandlerException(RepositoryException.class, "Cannot get database connection");
    
    private JdbcUtils()
    {
        // Not is allowed instance of this utility class
    }
    
//    public static Connection getConnection(DataSource dataSource)
//    {
//        return getConnection(dataSource, Isolation.DEFAULT);
//    }
    
//    public static Connection getConnection(DataSource dataSource, Isolation isolation)
//    {
//        Connection conn = null;
//        try
//        {
//            conn = dataSource.getConnection();
//            setIsolation(conn, isolation);
//        }
//        catch (SQLException e)
//        {
//            handlerException.handleUnchecked(e, "SEVERE FAIL, cannot get database connection datasource [" + dataSource
//                    + "] Reason: " + e.getMessage());
//        }
//        return conn;
//    }
//    
//    public static Connection getConnection(Properties props)
//    {
//        return getConnection(props, Isolation.DEFAULT);
//    }
//    
//    /**
//     * 
//     * @param props
//     * @return
//     * @throws RespositoryException if cannot get a new connection
//     */
//    public static Connection getConnection(Properties props, Isolation isolation)
//    {
//        Connection conn = null;
//        String driver = props.getProperty(SqleganceConfigKeys.JDBC_DRIVER.key());
//        String url = props.getProperty(SqleganceConfigKeys.JDBC_URL.key());
//        if (driver != null && !drivers.contains(driver))
//            JdbcUtils.registerDriver(driver);
//        
//        try
//        {
//            conn = DriverManager.getConnection(url, props);
//            setIsolation(conn, isolation);
//        }
//        catch (SQLException e)
//        {
//            handlerException.handleUnchecked(e,
//                    "SEVERE FAIL, cannot get database connection url [" + url + "] config=" + props);
//        }
//        return conn;
//    }
//    
//    private static void setIsolation(Connection conn, Isolation isolation)
//    {
//        try
//        {
//            if (isolation != null && isolation != Isolation.DEFAULT)
//                conn.setTransactionIsolation(isolation.level());
//        }
//        catch (SQLException e)
//        {
//            try
//            {
//                handlerException.handleUnchecked(e, "Cannot change Transaction isolation level from ["
//                        + Isolation.get(conn.getTransactionIsolation()) + "] to [" + isolation + "]");
//            }
//            catch (SQLException sqle)
//            {
//                LOG.warn("Cannot read transaction isolation level. reason: " + sqle.getMessage(), sqle);
//            }
//        }
//    }
//    
    /**
     * Drivers before JDBC 4 needs register the driver.
     * @param driver
     */
    public final static void registerDriver(String driver)
    {
        if (!drivers.contains(driver))
        {
            ReflectionUtils.forName(driver);
            drivers.add(driver);
        }
    }
    
//    /**
//     * Summarize the columns from SQL result in binary data or not.
//     * @param metadata
//     * @return Array of columns with name and index
//     * @throws SQLException
//     */
//    public final static JdbcColumn[] getJdbcColumns(ResultSetMetaData metadata) throws SQLException
//    {
//        JdbcColumn[] columns = new JdbcColumn[metadata.getColumnCount()];
//        
//        for (int i = 0; i < columns.length; i++)
//        {
//            int columnNumber = i + 1;
//            String columnName = getColumnName(metadata, columnNumber);
//            int columnType = metadata.getColumnType(columnNumber);
//            //boolean binaryData = false;
//            //if (columnType == Types.CLOB || columnType == Types.BLOB)
//            //    binaryData = true;
//            columns[i] = new DefaultJdbcColumn(columnNumber, columnName, columnType);
//        }
//        return columns;
//    }
//    
//    private static String getColumnName(ResultSetMetaData metaData, int columnIndex) throws SQLException
//    {
//        try
//        {
//            return metaData.getColumnLabel(columnIndex);
//        }
//        catch (SQLException e)
//        {
//            return metaData.getColumnName(columnIndex);
//        }
//    }

    /**
     * @param stmt
     * @param rs
     * @throws DaoException
     */
    public final static void close(Connection conn, PreparedStatement stmt, ResultSet rs)
    {
        close(rs);
        close(stmt);
        close(conn);
    }
    
    public final static void close(PreparedStatement stmt)
    {
        if (stmt != null)
        {
            try
            {
                stmt.close();
            }
            catch (SQLException e)
            {
                LOG.warn("Cannot close prepared statement!", e);
            }
            stmt = null;
        }
    }
    
    public final static void close(Statement stmt)
    {
        if (stmt != null)
        {
            try
            {
                stmt.close();
            }
            catch (SQLException e)
            {
                LOG.warn("Cannot close statement!", e);
            }
            stmt = null;
        }
    }
    
    public final static void close(ResultSet rs)
    {
        if (rs != null)
        {
            try
            {
                rs.close();
            }
            catch (SQLException e)
            {
                LOG.warn("Cannot close resultset!", e);
            }
            rs = null;
        }
    }
    
    public final static void close(CallableStatement call)
    {
        
        if (call != null)
        {
            try
            {
                call.close();
            }
            catch (SQLException e)
            {
                LOG.warn("Cannot close callable!", e);
            }
            call = null;
        }
    }
    
    public final static void close(Connection conn)
    {
        if (conn != null)
        {
            try
            {
                if (!conn.isClosed())
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
                LOG.warn("Cannot close connection!", e);
            }
            conn = null;
        }
    }
    
}
