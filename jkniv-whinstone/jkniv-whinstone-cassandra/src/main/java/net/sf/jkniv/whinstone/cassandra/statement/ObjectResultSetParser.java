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
import java.util.List;

import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.ResultSetParser;
import net.sf.jkniv.whinstone.classification.Groupable;

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
class ObjectResultSetParser<T> implements ResultSetParser<T, ResultSet>
{
    //private final static Logger LOG = LoggerFactory.getLogger(ObjectResultSetParser.class);
    private final ResultRow<T, Row> resultRow;
    private final Groupable<T, T> groupable;
    
    public ObjectResultSetParser(ResultRow<T, Row> resultRow, Groupable<T, T> groupable)
    {
        this.resultRow = resultRow;
        this.groupable = groupable;
    }
    
    @Override
    public List<T> parser(ResultSet rs) throws SQLException
    {
        try
        {
            int rownum = 0;
            for (Row row : rs.all()) 
            {
                T record = resultRow.row(row, rownum++);
                groupable.classifier(record);
            }
        }
        finally
        {
            close(rs);
        }
        return groupable.asList();
    }
    
    private void close(ResultSet rs)
    {
        // The ResultSet is not "linked" to a connection or any other resource that needs closing.
    }
}
