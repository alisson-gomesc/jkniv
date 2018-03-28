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
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Row;

import net.sf.jkniv.reflect.Injectable;
import net.sf.jkniv.reflect.InjectableFactory;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.JdbcColumn;
import net.sf.jkniv.sqlegance.ResultRow;
import net.sf.jkniv.sqlegance.classification.ObjectTransform;
import net.sf.jkniv.sqlegance.classification.Transformable;
import net.sf.jkniv.sqlegance.logger.SqlLogger;

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
public class FlatObjectResultRow<T> implements ResultRow<T, Row>
{
    private final static Logger LOG = LoggerFactory.getLogger(FlatObjectResultRow.class);
    //private final SqlLogger    sqlLogger;
    private final Class<T>     returnType;
    private final Transformable<T> transformable;
    private JdbcColumn<Row>[] columns;

    public FlatObjectResultRow(Class<T> returnType, SqlLogger log)
    {
        this(returnType, null, log);
    }

    @SuppressWarnings("unchecked")
    public FlatObjectResultRow(Class<T> returnType, JdbcColumn<Row>[] columns, SqlLogger log)
    {
        this.returnType = returnType;
        this.columns = columns;
        this.transformable = (Transformable<T>) new ObjectTransform();
        //this.sqlLogger = log;
    }
    
    public T row(Row rs, int rownum) throws SQLException
    {
        ObjectProxy<T> proxy = ObjectProxyFactory.newProxy(returnType);
        for (JdbcColumn<Row> column : columns)
            setValueOf(column, rs, proxy);
        
        return proxy.getInstance();
    }
    
    private void setValueOf(JdbcColumn<Row> column, Row rs, ObjectProxy<T> proxy) throws SQLException
    {
        Injectable<T> reflect = InjectableFactory.newMethodInjection(proxy);
        Object jdbcObject = null;
        if (column.isBinary())
            jdbcObject = column.getBytes(rs);
        else
            jdbcObject = column.getValue(rs);
        
        if (column.isNestedAttribute())
            reflect.inject(column.getAttributeName(), jdbcObject);
        else
        {
            String method = column.getMethodName();
            if (proxy.hasMethod(method))
                reflect.inject(method, jdbcObject);
            else
                LOG.warn("Method [{}] doesn't exists for [{}] to set value [{}]", method, proxy.getTargetClass().getName(), jdbcObject);
        }
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
