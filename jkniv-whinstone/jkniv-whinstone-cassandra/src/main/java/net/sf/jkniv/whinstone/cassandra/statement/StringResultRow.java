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

import org.slf4j.Logger;

import com.datastax.driver.core.Row;

import net.sf.jkniv.sqlegance.logger.DataMasking;
import net.sf.jkniv.whinstone.JdbcColumn;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.classification.Transformable;
import net.sf.jkniv.whinstone.statement.AbstractResultRow;

/**
 * 
 * Inject the ResultSet at java basic type instance...
 * 
 * @author Alisson Gomes
 *
 * @param <T> generic type of {@code Class} object to inject value of <code>ResultSet</code>
 */
class StringResultRow<T> extends AbstractResultRow implements ResultRow<T, Row>
{
    private static final Logger      SQLLOG = net.sf.jkniv.whinstone.cassandra.LoggerFactory.getLogger();
    private static final DataMasking MASKING = net.sf.jkniv.whinstone.cassandra.LoggerFactory.getDataMasking();
    private JdbcColumn<Row>[] columns;

    public StringResultRow()
    {
        this(null);
    }

    public StringResultRow(JdbcColumn<Row>[] columns)
    {
        super(SQLLOG, MASKING);
        this.columns = columns;
    }
    
    @SuppressWarnings("unchecked")
    public T row(Row rs, int rownum) throws SQLException
    {
        Object jdbcObject = getValueOf(columns[0], rs);
        if(SQLLOG.isTraceEnabled())
            SQLLOG.trace("Mapping index [0] column [{}] type of [{}] to value [{}]", 
                columns[0].getAttributeName(), 
                (jdbcObject != null ? jdbcObject.getClass().getName() : "null"), 
                MASKING.mask(columns[0].getAttributeName(), jdbcObject));
        
        return (T)(jdbcObject != null ? jdbcObject.toString() : null);
    }

    @Override
    public Transformable<T> getTransformable()
    {
        return null;
    }    
    
    @Override
    public void setColumns(JdbcColumn<Row>[] columns)
    {
        this.columns = columns;
    }
}
