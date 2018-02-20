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
package net.sf.jkniv.whinstone.jpa2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.JdbcColumn;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.ResultRow;
import net.sf.jkniv.sqlegance.classification.MapTransform;
import net.sf.jkniv.sqlegance.classification.Transformable;
import net.sf.jkniv.sqlegance.logger.LogLevel;
import net.sf.jkniv.sqlegance.logger.SqlLogger;

/**
 * 
 * Inject the ResultSet at Map instance...
 * 
 * @author Alisson Gomes
 *
 * @param <T> generic type of {@code Class} object to inject value of <code>ResultSet</code>
 */
class MapResultRow<T> implements ResultRow<T, Object[]>
{
    //private final SqlLogger  sqlLogger;
    private final Class<T> returnType;
    //private final JdbcColumn[] columns;
    private Transformable<T> transformable;
    
    public MapResultRow(Class<T> returnType)/*, JdbcColumn[] columns, SqlLogger sqlLogger)*/
    {
        this.returnType = returnType;
        this.transformable = (Transformable<T>) new MapTransform();
        //this.columns = columns;
        //this.sqlLogger = sqlLogger;
    }
    
    @SuppressWarnings("unchecked")
    public T row(Object[] row, int rownum) throws SQLException
    {
        Map<String, Object> map = newInstance();        
        for (Object o : row)
        {
            ObjectProxy<T> proxy = ObjectProxyFactory.newProxy(returnType);
            proxy.setConstructorArgs(o);
            T casted = proxy.newInstance();
            //castedList.add(casted);
        }
/*
        for (JdbcColumn column : columns)
            setValueOf(column, rs, map);
*/
        return (T) map;
    }
/*
    private void setValueOf(JdbcColumn column, ResultSet rs, Map<String, Object> map) throws SQLException
    {
        Object jdbcObject = null;
        if (column.isBinary())
            jdbcObject = column.getBytes(rs);
        else
            jdbcObject = column.getValue(rs);
        
        //sqlLogger.log(LogLevel.RESULTSET, "Using [{}] column name as sensitive key for Map", column.getAttributeName());
        map.put(column.getAttributeName(), jdbcObject);
    }
    */
    @SuppressWarnings("unchecked")
    private Map<String, Object> newInstance()
    {
        Map<String, Object> map = null;
        try
        {
            if (returnType.isInterface())
                map = new HashMap<String, Object>();
            else                
                map = (Map<String, Object>) returnType.newInstance();
        }
        catch (InstantiationException e)
        {
            throw new RepositoryException("Cannot create a new instance of ["+returnType+"]. " + e.getMessage(), e);
        }
        catch (IllegalAccessException e)
        {
            throw new RepositoryException("Cannot create a new instance of ["+returnType+"]. " + e.getMessage(), e);
        }
        return map;
    }

    @Override
    public Transformable<T> getTransformable()
    {
        return transformable;
    }
    
    @Override
    public void setColumns(JdbcColumn<Object[]>[] columns)
    {
        //this.columns = columns;
    }


}
