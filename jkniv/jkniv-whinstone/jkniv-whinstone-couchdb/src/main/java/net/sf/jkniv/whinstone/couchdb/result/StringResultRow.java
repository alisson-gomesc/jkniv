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

import java.sql.SQLException;

import org.slf4j.Logger;

import net.sf.jkniv.sqlegance.JdbcColumn;
import net.sf.jkniv.sqlegance.ResultRow;
import net.sf.jkniv.sqlegance.classification.Transformable;
import net.sf.jkniv.whinstone.LoggerFactory;

/**
 * 
 * Inject the ResultSet at java basic type instance...
 * 
 * @author Alisson Gomes
 *
 * @param <T> generic type of {@code Class} object to inject value of <code>ResultSet</code>
 */
public class StringResultRow<T> implements ResultRow<T, String>
{
    private final static Logger  sqlLogger = LoggerFactory.getLogger();
    private JdbcColumn<String>[] columns;

    public StringResultRow()
    {
        this(null);
    }

    public StringResultRow(JdbcColumn<String>[] columns)
    {
        this.columns = columns;
    }
    
    @SuppressWarnings("unchecked")
    public T row(String  json, int rownum) throws SQLException
    {
        Object jdbcObject = null;
//        if (columns[0].isBinary())
//            jdbcObject = columns[0].getBytes(rs);
//        else
//            jdbcObject = columns[0].getValue(rs);

        // FIXME couchdb logger ResultSet
        sqlLogger.trace("Column index [0] named [{}] to set String", columns[0].getAttributeName());
        return (T)(jdbcObject != null ? jdbcObject.toString() : null);
    }

    @Override
    public Transformable<T> getTransformable()
    {
        return null;
    }    
    
    @Override
    public void setColumns(JdbcColumn<String>[] columns)
    {
        this.columns = columns;
    }
}
