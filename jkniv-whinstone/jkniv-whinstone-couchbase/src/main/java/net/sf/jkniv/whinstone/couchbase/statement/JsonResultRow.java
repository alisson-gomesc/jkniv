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

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.java.query.N1qlQueryRow;

import net.sf.jkniv.sqlegance.logger.DataMasking;
import net.sf.jkniv.whinstone.JdbcColumn;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.classification.ObjectTransform;
import net.sf.jkniv.whinstone.classification.Transformable;
import net.sf.jkniv.whinstone.couchbase.commands.JsonMapper;

/**
 * 
 * Inject the JsonObject into POJO instance...
 * 
 * @param <T> generic type of {@code Class} object to inject value of <code>ResultSet</code>
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class JsonResultRow<T> implements ResultRow<T, N1qlQueryRow>
{
    private static final Logger      LOG     = LoggerFactory.getLogger(JsonResultRow.class);
    private static final Logger      SQLLOG  = net.sf.jkniv.whinstone.couchbase.LoggerFactory.getLogger();
    private static final DataMasking MASKING = net.sf.jkniv.whinstone.couchbase.LoggerFactory.getDataMasking();
    private final Class<T>           returnType;
    private final Transformable<T>   transformable;
    
    @SuppressWarnings("unchecked")
    public JsonResultRow(Class<T> returnType)
    {
        this.returnType = returnType;
        this.transformable = (Transformable<T>) new ObjectTransform();
    }
    
    @SuppressWarnings("unchecked")
    public T row(N1qlQueryRow row, int rownum) throws SQLException
    {
        String jsonContent = row.value().toString();
        Object o = JsonMapper.mapper(jsonContent, returnType);
        // TODO implements setting attributes into pojo
        /*
        SQLLOG.trace("Mapping index [{}] column [{}] type of [{}] to value [{}]", column.getIndex(),
                column.getAttributeName(), (jdbcObject != null ? jdbcObject.getClass().getName() : "null"),
                MASKING.mask(column.getAttributeName(), jdbcObject));
    */
        return (T)o;
    }
    
    @Override
    public Transformable<T> getTransformable()
    {
        return transformable;
    }
    
    @Override
    public void setColumns(JdbcColumn<N1qlQueryRow>[] columns)
    {
        // JsonObject has the full object (is not an access columns)
    }
    
}
