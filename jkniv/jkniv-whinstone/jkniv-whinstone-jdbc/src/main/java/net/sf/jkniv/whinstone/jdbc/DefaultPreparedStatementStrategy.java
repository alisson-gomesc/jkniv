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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.whinstone.Queryable;

/**
 * TODO javadoc
 * 
 * @author Alisson Gomes
 * @deprecated not used any more
 */
class DefaultPreparedStatementStrategy implements PreparedStatementStrategy
{
    private static final Logger LOG    = LoggerFactory.getLogger(DefaultPreparedStatementStrategy.class);
    private static final Logger SQLLOG = net.sf.jkniv.whinstone.jdbc.LoggerFactory.getLogger();
    private String              sql;
    private String              sqlCount;
    private Queryable           queryable;
    private String[]            paramsNames;
    
    public DefaultPreparedStatementStrategy(Queryable queryable)
    {
        this.queryable = queryable;
        setSqlWithQuestionMark();
    }
    
    /**
     * Replace sql marks to question mark '?' for Prepared Statements
     * @param sqlTemp
     */
    private void setSqlWithQuestionMark()
    {
        this.paramsNames = queryable.getParamsNames();
        this.sql = queryable.query();
        if (queryable.isPaging())
            this.sqlCount = queryable.queryCount();
    }
    
    /*
     * Creates a PreparedStatement object that will generate ResultSet objects with the given type, concurrency, and holdability.
     * The parameters values is setting
     * @param conn Opened connection to database
     * @return a new PreparedStatement object, containing the pre-compiled SQL statement.
     * @throws net.sf.jkniv.sqlegance.RepositoryException wrapper SQLException
     * @see java.sql.SQLException
     *
    public PreparedStatement prepareStatement(Connection conn)
    {
        PreparedStatement stmt = null;
        Sql isql = queryable.getDynamicSql();
        if (LOG.isTraceEnabled())
            LOG.trace("Preparing SQL statement type [{}], concurrency [{}], holdability [{}] with [{}] parameters",
                    isql.getResultSetType(), isql.getResultSetConcurrency(), isql.getResultSetHoldability(),
                    paramsNames.length);
        stmt = queryable.getDynamicSql().getSqlDialect().prepare(conn, isql, queryable.query());
//        int rsType = isql.getResultSetType().getTypeScroll();
//        int rsConcurrency = isql.getResultSetConcurrency().getConcurrencyMode();
//        int rsHoldability = isql.getResultSetHoldability().getHoldability();        
//        try
//        {
//            
//            if (queryable.getDynamicSql().getSqlDialect().supportsStmtHoldability())
//                stmt = conn.prepareStatement(sql, rsType, rsConcurrency, rsHoldability);
//            else
//            {
//                // SQLServer doesn't support Holdability
//                stmt = conn.prepareStatement(sql, rsType, rsConcurrency);
//                conn.setHoldability(rsHoldability);
//            }
//            if (isql.getTimeout() > 0)
//                stmt.setQueryTimeout(isql.getTimeout());
//        }
//        catch (SQLException sqle)
//        {
//            throw new RepositoryException("Cannot prepare statement [" + sqle.getMessage() + "]", sqle);
//        }
        return stmt;
    }
*/    
    /*
     * Creates a {@code PreparedStatement} object object capable of returning the auto-generated 
     * keys designated by the given array.
     * 
     * @param conn Opened connection to database
     * @param columnNames an array of column names indicating the columns that should be returned from the inserted row or rows 
     * @return a new PreparedStatement object, containing the pre-compiled SQL statement.
     * @throws net.sf.jkniv.sqlegance.RepositoryException wrapper SQLException
     * @see java.sql.SQLException
     *
    @Override
    public PreparedStatement prepareStatement(Connection conn, String[] columnNames)
    {
        PreparedStatement stmt = null;
        Sql isql = queryable.getDynamicSql();
        try
        {
            if (LOG.isTraceEnabled())
                LOG.trace("Preparing SQL statement type with [{}] column names", paramsNames.length);
            
            if (columnNames.length > 0)
                stmt = conn.prepareStatement(sql, columnNames);
            else
                stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            //if (paramsNames.length > 0)
            //    setValues(stmt);
            
            if (isql.getTimeout() > 0)
                stmt.setQueryTimeout(isql.getTimeout());
        }
        catch (SQLException sqle)
        {
            throw new RepositoryException("Cannot prepare statement!", sqle);
        }
        return stmt;
    }
    */
    
    /*
    @Override
    public PreparedStatement prepareStatementCount(Connection conn)
    {
        PreparedStatement stmt = null;
        try
        {
            stmt = conn.prepareStatement(sqlCount);
        }
        catch (SQLException sqle)
        {
            throw new RepositoryException("Cannot prepare count statement to [" + queryable.getName() + "]!", sqle);
        }
        return stmt;
    }
    */
    
    /*
    public PreparedStatement prepareStatementCount(Connection conn)
    {
        PreparedStatement stmt = null;
        try
        {
            String sqlCount = sqlDialect.count(sql)
            stmt = conn.prepareStatement(sql);
            if (paramsNames.length > 0)
                setValues(stmt);
            
            if (isql.getTimeout() > 0)
                stmt.setQueryTimeout(isql.getTimeout());
        }
        catch (SQLException sqle)
        {
            throw new RepositoryException("Cannot prepare statement!", sqle);
        }
        return stmt;
    }
    */
    
    /*
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void setValues(PreparedStatement stmt)
    {
        Queryable queryable = sqlDialect.getQueryable();
        Object[] p = null;
        if (queryable.getParams() instanceof Collection)
        {
            //p = ((List<?>) queryable.getParams()).toArray();
            p = ((Collection)queryable.getParams()).toArray();
        }
        else if (queryable.getParams() != null && queryable.getParams().getClass().isArray())
        {
            p = (Object[]) queryable.getParams();
        }
        int k = 0; // index params clause IN
        for (int i = 0; i < paramsNames.length; i++)
        {
            // TODO re-design make better
            Object paramValue = null;
            if (p == null)
                paramValue = queryable.getProperty(paramsNames[i]);//getProperty(queryable.getParams(), paramsNames[i]);
            else
                paramValue = p[i];
            
            try
            {
                if (sqlLogger.isEnabled(LogLevel.STMT))
                {
                    sqlLogger.log(LogLevel.STMT, "Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", i,
                            paramsNames[i], sqlLogger.mask(paramsNames[i], paramValue), (paramValue == null ? "NULL" : paramValue.getClass()));
                }
                
                if(paramsNames[i].toLowerCase().startsWith("in:"))
                {
                    String paramName = paramsNames[i].substring(3, paramsNames[i].length());
                    if (paramValue == null)
                    {
                        paramValue = queryable.getProperty(paramName);//getProperty(queryable.getParams(), paramName);
                    }
                    Object[] paramsIN = null;
                    int j=0;
                    if (paramValue != null && paramValue.getClass().isArray())
                        paramsIN = (Object[])paramValue;
                    else if (paramValue instanceof Collection)
                        paramsIN = ((Collection) paramValue).toArray();
                    else if (p != null)
                        paramsIN = p;
    
                    if (paramsIN == null)
                        throw new ParameterException(
                                "Cannot set parameter [" + paramsNames[i] + "] from IN clause with NULL");
                        
                    for(; j<paramsIN.length; j++)
                        stmt.setObject(j + i + 1, paramsIN[j]);
                    k = k+j;
                }
                else if (paramValue != null && paramValue.getClass().isEnum())
                {
                    ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(paramValue);
                    stmt.setObject(i +k+ 1, proxy.invoke("name"));
                }
                else if (paramValue instanceof Date)
                {
                    SqlDateConverter converter = new SqlDateConverter();
                    java.sql.Timestamp timestamp = converter.convert(java.sql.Timestamp.class, paramValue);
                    stmt.setObject(i +k+ 1, timestamp);
                }
                else
                {
                    stmt.setObject(i +k+ 1, paramValue);
                }
            }
            catch (SQLException e)
            {
                throw new RepositoryException(
                        "Cannot set parameter [" + paramsNames[i] + "] value [" + sqlLogger.mask(paramsNames[i], paramValue) + "]", e);
            }
        }
    }
    */
    
    /*
    private Object getProperty(Object params, String name)
    {
        Object o = null;
        if (params == null)// TODO test me, queryable object with null params and query using params
            throw new ParameterNotFoundException(
                    "Query with parameter ["+name+"] is wrong, cannot found value with null instance from Queryable.params");
        try
        {
            o = PropertyUtils.getProperty(params, name);// FIXME implements getProperty access get method not class field!
        }
        catch (Exception e)
        {
            throw new ParameterNotFoundException(
                    "Cannot find the property [" + name + "] at param object [" + (params != null ? params.getClass().getName() : "null") + "] ");
        }
        return o;
    }
    */
    
//    @Override
//    public SqlLogger getSqlLogger()
//    {
//        return sqlLogger;
//    }
    /*
    
    @Override
    public void setSqlLogger(SqlLogger logger)
    {
        this.sqlLogger = logger;
    }
    
    @Override
    public void setSqlDialect(SqlDialect sqlDialect)
    {
        this.sqlDialect = sqlDialect;
        //this.paramParser = sqlDialect.getISql().getParamParser();
        setSqlWithQuestionMark();
    }
    */
}
