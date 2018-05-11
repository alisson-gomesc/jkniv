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
package net.sf.jkniv.whinstone.couchdb.result;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import net.sf.jkniv.sqlegance.JdbcColumn;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.ResultRow;
import net.sf.jkniv.sqlegance.classification.MapTransform;
import net.sf.jkniv.sqlegance.classification.Transformable;
import net.sf.jkniv.sqlegance.logger.LogLevel;
import net.sf.jkniv.whinstone.LoggerFactory;

/**
 * 
 * Inject the ResultSet at Map instance...
 * 
 * @author Alisson Gomes
 *
 * @param <T> generic type of {@code Class} object to inject value of <code>ResultSet</code>
 */
public class MapResultRow<T> implements ResultRow<T, ResultSet>
{
    private static final Logger  LOG = LoggerFactory.getLogger();
    private final Class<T> returnType;
    private final Transformable<T> transformable;
    private JdbcColumn<ResultSet>[] columns;
    
    @SuppressWarnings("unchecked")
    public MapResultRow(Class<T> returnType, JdbcColumn<ResultSet>[] columns)
    {
        this.returnType = returnType;
        this.columns = columns;
        this.transformable = (Transformable<T>) new MapTransform();
    }
    
    @SuppressWarnings("unchecked")
    public T row(ResultSet rs, int rownum) throws SQLException
    {
        Map<String, Object> map = newInstance();
        for (JdbcColumn<ResultSet> column : columns)
            setValueOf(column, rs, map);

        return (T) map;
    }

    private void setValueOf(JdbcColumn column, ResultSet rs, Map<String, Object> map) throws SQLException
    {
        Object jdbcObject = null;
        if (column.isBinary())
            jdbcObject = column.getBytes(rs);
        else
            jdbcObject = column.getValue(rs);
        
        if(LOG.isTraceEnabled())
            LOG.trace("Using [{}] column name as sensitive key for Map with value [{}]", column.getAttributeName(), jdbcObject);
        map.put(column.getAttributeName(), jdbcObject);
    }
    
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
    public void setColumns(JdbcColumn<ResultSet>[] columns)
    {
        this.columns = columns;
    }

}
