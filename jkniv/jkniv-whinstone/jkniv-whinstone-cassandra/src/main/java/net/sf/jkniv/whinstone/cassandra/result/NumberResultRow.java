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

import com.datastax.driver.core.ResultSet;

import net.sf.jkniv.reflect.NumberFactory;
import net.sf.jkniv.reflect.Numerical;
import net.sf.jkniv.sqlegance.JdbcColumn;
import net.sf.jkniv.sqlegance.ResultRow;
import net.sf.jkniv.sqlegance.classification.Transformable;
import net.sf.jkniv.sqlegance.logger.LogLevel;
import net.sf.jkniv.sqlegance.logger.SqlLogger;

/**
 * 
 * Inject the ResultSet at java basic type instance...
 * 
 * @author Alisson Gomes
 *
 * @param <T> generic type of {@code Class} object to inject value of <code>ResultSet</code>
 */
public class NumberResultRow<T> implements ResultRow<T, ResultSet>
{
    private final SqlLogger  sqlLogger;
    private JdbcColumn<ResultSet>[] columns;
    private final Numerical numerical;

    public NumberResultRow(Class<T> returnType, SqlLogger log)
    {
        this(returnType, null, log);
    }

    public NumberResultRow(Class<T> returnType, JdbcColumn<ResultSet>[] columns, SqlLogger log)
    {
        this.columns = columns;
        this.sqlLogger = log;
        this.numerical = NumberFactory.getInstance(returnType.getCanonicalName());
    }
    
    @SuppressWarnings("unchecked")
    public T row(ResultSet rs, int rownum) throws SQLException
    {
        Object jdbcObject = null;
        if (columns[0].isBinary())
            jdbcObject = columns[0].getBytes(rs);
        else
            jdbcObject = columns[0].getValue(rs);
        
        sqlLogger.log(LogLevel.RESULTSET, "Column index [0] named [{}] to set number type of [{}]", columns[0].getAttributeName(), numerical.getClass().getCanonicalName());
        return (T) numerical.valueOf(jdbcObject);
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
