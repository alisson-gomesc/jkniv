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
package net.sf.jkniv.whinstone.jdbc.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;

//import org.springframework.jdbc.support.xml.SqlXmlFeatureNotImplementedException;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.sqlegance.Insertable;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.ConnectionAdapter;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.jdbc.PreparedStatementStrategy;

abstract class AbstractCommand implements DbCommand
{
    protected static final Assertable          instanceOf = AssertsFactory.getInstanceOf();
    protected static final HandleableException handlerException;
    
    static
    {
        // TODO handler exception needs better configuration
        handlerException = new HandlerException(RepositoryException.class, "Error, Cannot insert record. %s");
        //handlerException.config(SqlXmlFeatureNotImplementedException.class,
        //        "Optional JDBC feature auto-generated keys isn't supported");
    }
    
    protected final PreparedStatementStrategy stmtStrategy;
    protected final Connection conn;
    protected final Queryable queryable;
    protected final ConnectionAdapter adapterConn;

    /**
     * 
     * @param queryable
     * @param stmtStrategy
     * @param conn
     * @deprecated
     */
    public AbstractCommand(Queryable queryable,
            PreparedStatementStrategy stmtStrategy, Connection conn)
    {
        this.stmtStrategy = stmtStrategy;
        this.queryable = queryable;
        this.conn = conn;
        this.adapterConn =  null;
    }

    public AbstractCommand(Queryable queryable,
            PreparedStatementStrategy stmtStrategy, ConnectionAdapter adapterConn)
    {
        this.stmtStrategy = stmtStrategy;
        this.adapterConn = adapterConn;
        this.queryable = queryable;
        this.conn = null;
    }

    /*
    public int executeUpdate(Queryable queryable, String[] paramsNames) throws SQLException
    {
        int rowsAffected = 0;
        if (queryable.isTypeOfArrayFromPojo() || queryable.isTypeOfCollectionFromPojo())
        {
            Iterator<Object> it = queryable.iterator();
            while (it.hasNext())
            {
                Queryable queryableIt = new QueryName(queryable.getName(), it.next(), queryable.getOffset(),
                        queryable.getMax());
                setValues(stmt, paramsNames, queryableIt);
                rowsAffected += stmt.executeUpdate();
            }
        }
        else
        {
            setValues(stmt, paramsNames, queryable);
            rowsAffected = stmt.executeUpdate();
        }
        return rowsAffected;
    }
    */
    /*
    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    private void setValues(PreparedStatement stmt, String[] paramsNames, Queryable queryable)
    {
        int k = 0; // index params for IN CLAUSE
        for (int i = 0; i < paramsNames.length; i++)
        {
            Object paramValue = null;
            try
            {
                if (sqlLogger.isEnabled(LogLevel.STMT))
                    sqlLogger.log(LogLevel.STMT,
                            "Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", i,
                            paramsNames[i], sqlLogger.mask(paramsNames[i], paramValue),
                            (paramValue == null ? "NULL" : paramValue.getClass()));
                
                if (paramsNames[i].toLowerCase().startsWith("in:"))
                {
                    String paramName = paramsNames[i].substring(3, paramsNames[i].length());
                    paramValue = queryable.getProperty(paramName);
                    Object[] paramsIN = null;
                    int j = 0;
                    if (paramValue != null && paramValue.getClass().isArray())
                        paramsIN = (Object[]) paramValue;
                    else if (paramValue instanceof Collection)
                        paramsIN = ((Collection) paramValue).toArray();
                    
                    if (paramsIN == null)
                        throw new ParameterException(
                                "Cannot set parameter [" + paramsNames[i] + "] from IN clause with NULL");
                    
                    for (; j < paramsIN.length; j++)
                        stmt.setObject(j + i + 1, paramsIN[j]);
                    k = k + j;
                }
                else
                {
                    paramValue = queryable.getProperty(paramsNames[i]);
                    if (paramValue != null && paramValue.getClass().isEnum())
                    {
                        ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(paramValue);
                        stmt.setObject(i + k + 1, proxy.invoke("name"));
                    }
                    else if (paramValue instanceof Date)
                    {
                        SqlDateConverter converter = new SqlDateConverter();
                        java.sql.Timestamp timestamp = converter.convert(java.sql.Timestamp.class, paramValue);
                        stmt.setObject(i + k + 1, timestamp);
                    }
                    else
                    {
                        stmt.setObject(i + k + 1, paramValue);
                    }
                }
            }
            catch (SQLException e)
            {
                throw new RepositoryException("Cannot set parameter [" + paramsNames[i] + "] value ["
                        + sqlLogger.mask(paramsNames[i], paramValue) + "]", e);
            }
        }
    }
    */

    protected PreparedStatement prepareStatement()
    {
        Insertable isql = queryable.getDynamicSql().asInsertable();
        PreparedStatement stmt = null;
        if (isql.isAutoGenerateKey() && isql.getAutoGeneratedKey().isAutoStrategy())
        {
            String[] columns = isql.getAutoGeneratedKey().getColumnsAsArray();
            stmt = stmtStrategy.prepareStatement(conn, columns);
        }
        else
        {
            stmt = stmtStrategy.prepareStatement(conn);
        }
        return stmt;
    }

}
