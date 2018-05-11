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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.ResultRow;
import net.sf.jkniv.sqlegance.ResultSetParser;
import net.sf.jkniv.sqlegance.classification.Groupable;

/**
 * 
 * Inject the ResultSet at flat Object, basic types like: String, Date, Numbers, Clob, Blob...
 * <p>
 * <strong>This class doesn't supports inject value at Oriented-Object model, like nested objects.</strong>
 * 
 * @author Alisson Gomes
 *
 * @param <T> generic type of {@code Class} object to inject value of <code>ResultSet</code>
 */
public class ObjectResultSetParser<T> implements ResultSetParser<T, String>
{
    private final static Logger        LOG = LoggerFactory.getLogger(ObjectResultSetParser.class);
    private final ResultRow<T, String> resultRow;
    private final Groupable<T, T>      groupable;
    private final int                  rows;
    
    public ObjectResultSetParser(ResultRow<T, String> resultRow, Groupable<T, T> groupable)
    {
        this(resultRow, groupable, 0);
    }
    
    public ObjectResultSetParser(ResultRow<T, String> resultRow, Groupable<T, T> groupable, int rows)
    {
        this.resultRow = resultRow;
        this.groupable = groupable;
        this.rows = rows;
        
    }
    
    public List<T> parser(String json) throws SQLException
    {
        List<String> list = new ArrayList<String>();
        list.add(json);
        int rownum = 0;
        //            while (!rs.isExhausted())
        //            {
        //                T row = resultRow.row(rs.one(), rownum++);
        //                groupable.classifier(row); FIXME
        //            }
        return (List<T>) list;//groupable.asList();
    }
    
    public void close(String json)
    {
    }
}
