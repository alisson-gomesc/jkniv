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
package net.sf.jkniv.whinstone.couchbase.statement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;

import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.ResultSetParser;
import net.sf.jkniv.whinstone.classification.Groupable;

/**
 * 
 * Inject the ResultSet at flat Object, basic types like: String, Date, Numbers, Clob, Blob...
 * <p>
 * <strong>This class doesn't supports inject value at Oriented-Object model, like nested objects.</strong>
 * 
 * @param <T> generic type of {@code Class} object to inject value of <code>ResultSet</code>
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
class ObjectResultSetParser<T> implements ResultSetParser<T, N1qlQueryResult>
{
    private final static Logger LOG = LoggerFactory.getLogger(ObjectResultSetParser.class);
    private final ResultRow<T, N1qlQueryRow> resultRow;
    private final Groupable<T, T> groupable;
    //private final int          rows;
    
    public ObjectResultSetParser(ResultRow<T, N1qlQueryRow> resultRow, Groupable<T, T> groupable)
    {
        this.resultRow = resultRow;
        this.groupable = groupable;
    }

    @Override
    public List<T> parser(N1qlQueryResult rs) throws SQLException
    {
        Iterator<N1qlQueryRow> it = rs.rows();
        int rownum = 0;
        while (it.hasNext())
        {
            N1qlQueryRow row = it.next();
            T pojo = resultRow.row(row, rownum++);
            groupable.classifier(pojo);
        }
        List<T> l =groupable.asList();
        return l;
    }
}
