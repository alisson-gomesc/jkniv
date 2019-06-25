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
package net.sf.jkniv.whinstone.cassandra.result;

import java.sql.SQLException;

import org.slf4j.Logger;

import com.datastax.driver.core.Row;

import net.sf.jkniv.reflect.NumberFactory;
import net.sf.jkniv.reflect.Numerical;
import net.sf.jkniv.sqlegance.logger.DataMasking;
import net.sf.jkniv.whinstone.JdbcColumn;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.cassandra.LoggerFactory;
import net.sf.jkniv.whinstone.classification.Transformable;

/**
 * 
 * Inject the ResultSet at java basic type instance...
 * 
 * @author Alisson Gomes
 *
 * @param <T> generic type of {@code Class} object to inject value of <code>ResultSet</code>
 */
public class NumberResultRow<T> implements ResultRow<T, Row>
{
    private static final Logger      SQLLOG = net.sf.jkniv.whinstone.cassandra.LoggerFactory.getLogger();
    private static final DataMasking MASKING = net.sf.jkniv.whinstone.cassandra.LoggerFactory.getDataMasking();
    private JdbcColumn<Row>[] columns;
    private final Numerical numerical;

    public NumberResultRow(Class<T> returnType)
    {
        this(returnType, null);
    }

    public NumberResultRow(Class<T> returnType, JdbcColumn<Row>[] columns)
    {
        this.columns = columns;
        this.numerical = NumberFactory.getInstance(returnType.getCanonicalName());
    }
    
    @SuppressWarnings("unchecked")
    public T row(Row rs, int rownum) throws SQLException
    {
        Object jdbcObject = null;
        if (columns[0].isBinary())
            jdbcObject = columns[0].getBytes(rs);
        else
            jdbcObject = columns[0].getValue(rs);
        
        if(SQLLOG.isTraceEnabled())
            SQLLOG.trace("Mapping index [0] column [{}] type of [{}] to value [{}]", 
                columns[0].getAttributeName(), 
                (numerical != null ? numerical.getClass().getCanonicalName() : "null"), 
                MASKING.mask(columns[0].getAttributeName(), jdbcObject));
        
//        if(SQLLOG.isTraceEnabled())
//            SQLLOG.trace("Column index [0] named [{}] type of [{}] with value [{}]", 
//                    columns[0].getAttributeName(), 
//                    numerical.getClass().getCanonicalName(), jdbcObject);
        return (T) numerical.valueOf(jdbcObject);
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
