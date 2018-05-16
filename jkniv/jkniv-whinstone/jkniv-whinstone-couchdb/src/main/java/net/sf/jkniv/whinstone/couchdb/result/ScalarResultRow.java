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

import org.slf4j.Logger;

import net.sf.jkniv.whinstone.JdbcColumn;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.classification.Transformable;
import net.sf.jkniv.whinstone.couchdb.LoggerFactory;

/**
 * 
 * Inject the ResultSet at java basic type instance...
 * 
 * @author Alisson Gomes
 *
 * @param <T> generic type of {@code Class} object to inject value of <code>ResultSet</code>
 */
public class ScalarResultRow<T> implements ResultRow<T, ResultSet>
{
    private static final Logger  LOG = LoggerFactory.getLogger();
    private JdbcColumn<ResultSet>[] columns;

    public ScalarResultRow()
    {
        this(null);
    }

    public ScalarResultRow(JdbcColumn<ResultSet>[] columns)
    {
        this.columns = columns;
    }
    
    @SuppressWarnings("unchecked")
    public T row(ResultSet rs, int rownum) throws SQLException
    {
        Object jdbcObject = null;
        if (columns[0].isBinary())
            jdbcObject = columns[0].getBytes(rs);
        else
            jdbcObject = columns[0].getValue(rs);
        if(LOG.isTraceEnabled())
            LOG.trace("Mapping index [0] column [{}] to set scalar value", columns[0].getAttributeName());
        return (T) jdbcObject;
    }

    @Override
    public Transformable<T> getTransformable()
    {
        return null;
    }    
    
    @Override
    public void setColumns(JdbcColumn<ResultSet>[] columns)
    {
        this.columns = columns;
    }
}
