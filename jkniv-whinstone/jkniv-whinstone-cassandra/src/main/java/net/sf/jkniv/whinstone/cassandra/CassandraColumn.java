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
package net.sf.jkniv.whinstone.cassandra;

import java.sql.SQLException;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.DataType;

import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.reflect.beans.PropertyAccess;
import net.sf.jkniv.whinstone.JdbcColumn;
import net.sf.jkniv.whinstone.JdbcColumnMapper;
import net.sf.jkniv.whinstone.UnderscoreToCamelCaseMapper;
import net.sf.jkniv.whinstone.types.CassandraType;
import net.sf.jkniv.whinstone.types.ColumnType;
import net.sf.jkniv.whinstone.types.Convertible;
import net.sf.jkniv.whinstone.types.RegisterType;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class CassandraColumn implements JdbcColumn<Row>
{
    private final static JdbcColumnMapper JDBC_COLUMN_MAPPER = new UnderscoreToCamelCaseMapper();// TODO design property to config;
    private final int                     columnIndex;
    private final String                  columnName;
    private final String                  methodName;
    private final ColumnType              columnType;
    private final boolean                 nestedAttribute;
    private final PropertyAccess          propertyAccess;
    private final RegisterType            registerType;
    
    public CassandraColumn(int columnIndex, String columnName, DataType columnType, RegisterType registerType, Class<?> classTarget)
    {
        super();
        this.columnIndex = columnIndex;
        this.columnName = columnName;
        this.propertyAccess = new PropertyAccess(JDBC_COLUMN_MAPPER.map(columnName), classTarget);
        //this.columnType = CassandraType.valueOf(columnType.name());
        this.columnType = CassandraType.valueStartWith(columnType.toString());
        this.registerType = registerType;
        if(columnName.indexOf(".") > 0)
        {
            this.nestedAttribute = true;
            this.methodName = columnName;
        }
        else
        {
            this.nestedAttribute = false;
            this.methodName = propertyAccess.getWriterMethodName();
        }
    }
    
    @Override
    public PropertyAccess getPropertyAccess()
    {
        return this.propertyAccess;
    }

    @Override
    public String getAttributeName()
    {
        return propertyAccess.getFieldName();
    }
    
    @Override
    public String getMethodName()
    {
        return methodName;
    }
    
    @Override
    public String getName()
    {
        return this.columnName;
    }
    
    @Override
    public int getIndex()
    {
        return this.columnIndex;
    }
    
    @Override
    public boolean isBinary()
    {
        return this.columnType.isBinary();
    }
    
    @Override
    public boolean isNestedAttribute()
    {
        return nestedAttribute;
    }
    
    @Override
    public Object getValue(Row row) throws SQLException
    {
        Object value = row.getObject(columnIndex);
        ObjectProxy<?> proxy = ObjectProxyFactory.of(this.propertyAccess.getTargetClass());
        Convertible<Object, Object> convertible = registerType.toAttribute(this, proxy);
        if (!convertible.getType().isInstance(value))
            value = convertible.toAttribute(value);
        return value;
    }
    
    @Override
    public Object getBytes(Row row) throws SQLException
    {
        return row.getByteBuffer(columnIndex).array();
        //return row.getBytes(columnIndex);
    }

    @Override
    public ColumnType getType()
    {
        return this.columnType;
    }

    @Override
    public String toString()
    {
        return "CassandraColumn [index=" + columnIndex + ", columnName=" + columnName + ", jdbcType=" + columnType
                + "]";
    }
}
