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
package net.sf.jkniv.whinstone.jdbc.statement;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.reflect.Injectable;
import net.sf.jkniv.reflect.InjectableFactory;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.sqlegance.logger.DataMasking;
import net.sf.jkniv.sqlegance.types.Convertible;
import net.sf.jkniv.whinstone.JdbcColumn;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.statement.ConvertibleFactory;

/**
 * 
 * @param <T> Type of objects thats must be returned.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
abstract class AbstractResultRow //implements ResultRow
{
    private final static Logger LOG = LoggerFactory.getLogger(AbstractResultRow.class);
    private final Logger        sqlLog;
    private final DataMasking   masking;
    
    public AbstractResultRow(final Logger log, final DataMasking masking)
    {
        this.sqlLog = log;
        this.masking = masking;
    }
    
    /**
     * 
     * @param <T> {@code ResultSet} implementation, that contains the rows of database
     * @param column metadata
     * @param rs {@code ResultSet} instance
     * @return The value of column
     * @throws SQLException if
     */
    public <R> Object getValueOf(JdbcColumn<R> column, R rs) throws SQLException
    {
        return column.isBinary() ? column.getBytes(rs): column.getValue(rs); 
    }
    
//    public <T,R> void setValueOf(final ObjectProxy<T> proxy,final JdbcColumn<R> column, R rs) throws SQLException
//    {
//        Object jdbcObject = getValueOf(column, rs);
//        setValueOf(proxy, column, jdbcObject);
//    }    

    public <T,R> void setValueOf(final ObjectProxy<T> proxy, final JdbcColumn<R> column, R rs) throws SQLException
    {
        Injectable<?> reflect = InjectableFactory.of(proxy);
        Object jdbcObject = getValueOf(column, rs);
        if (sqlLog.isTraceEnabled())
            sqlLog.trace("Mapping index [{}] column [{}] type of [{}] to value [{}]", column.getIndex(),
                    column.getAttributeName(), (jdbcObject != null ? jdbcObject.getClass().getName() : "null"),
                    masking.mask(column.getAttributeName(), jdbcObject));
        
        if (column.isNestedAttribute())
        {
            // FIXME Convertible for nested attributes
            reflect.inject(column.getAttributeName(), jdbcObject);
        }
        else
        {
            String method = column.getMethodName();
            if (proxy.hasMethod(method))
            {
                Convertible<Object, Object> convertible = ConvertibleFactory.toAttribute(column.getPropertyAccess(), proxy);
                reflect.inject(method, convertible.toAttribute(jdbcObject));
            }
            else
                LOG.warn("Method [{}] doesn't exists for [{}] to set value [{}]", method,
                        proxy.getTargetClass().getName(), jdbcObject);
        }
    }
}
