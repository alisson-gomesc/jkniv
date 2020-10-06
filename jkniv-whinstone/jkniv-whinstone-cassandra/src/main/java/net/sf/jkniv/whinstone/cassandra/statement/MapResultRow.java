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
package net.sf.jkniv.whinstone.cassandra.statement;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.datastax.oss.driver.api.core.cql.Row;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.logger.DataMasking;
import net.sf.jkniv.whinstone.JdbcColumn;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.classification.MapTransform;
import net.sf.jkniv.whinstone.classification.Transformable;
import net.sf.jkniv.whinstone.statement.AbstractResultRow;

/**
 * 
 * Inject the ResultSet at Map instance...
 * 
 * @author Alisson Gomes
 *
 * @param <T> generic type of {@code Class} object to inject value of <code>ResultSet</code>
 */
class MapResultRow<T> extends AbstractResultRow implements ResultRow<T, Row>
{
    //private final static Logger LOG = LoggerFactory.getLogger();
    private static final Logger      SQLLOG = net.sf.jkniv.whinstone.cassandra.LoggerFactory.getLogger();
    private static final DataMasking MASKING = net.sf.jkniv.whinstone.cassandra.LoggerFactory.getDataMasking();
    private final Class<T> returnType;
    private JdbcColumn<Row>[] columns;
    private final Transformable<T> transformable;

    public MapResultRow(Class<T> returnType)
    {
        this(returnType, null);
    }
    
    @SuppressWarnings("unchecked")
    public MapResultRow(Class<T> returnType, JdbcColumn<Row>[] columns)
    {
        super(SQLLOG, MASKING);
        this.returnType = returnType;
        this.columns = columns;
        this.transformable = (Transformable<T>) new MapTransform();
    }
    
    @SuppressWarnings("unchecked")
    public T row(Row row, int rownum) throws SQLException
    {
        Map<String, Object> map = newInstance();
        int i = 0;
        for (JdbcColumn<Row> column : columns)
        {
            setValueOf(column, row, map, i++);
        }

        return (T) map;
    }

    private void setValueOf(JdbcColumn<Row> column, Row row, Map<String, Object> map, int index) throws SQLException
    {
        Object jdbcObject = getValueOf(column, row);
        if(SQLLOG.isTraceEnabled())
            SQLLOG.trace("Using sensitive key [{}] for type [{}] with value [{}]", column.getAttributeName(), (jdbcObject == null ? "null" : jdbcObject.getClass()), jdbcObject);
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
    public void setColumns(JdbcColumn<Row>[] columns)
    {
        this.columns = columns;
    }

}
