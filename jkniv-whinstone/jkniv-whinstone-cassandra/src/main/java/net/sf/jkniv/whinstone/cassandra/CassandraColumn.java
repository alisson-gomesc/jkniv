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
import java.util.Date;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.Row;

import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.reflect.beans.PropertyAccess;
import net.sf.jkniv.whinstone.JdbcColumn;
import net.sf.jkniv.whinstone.JdbcColumnMapper;
import net.sf.jkniv.whinstone.UnderscoreToCamelCaseMapper;
import net.sf.jkniv.whinstone.types.CassandraType;
import net.sf.jkniv.whinstone.types.ColumnType;
import net.sf.jkniv.whinstone.types.Convertible;
import net.sf.jkniv.whinstone.types.ConvertibleFactory;
import net.sf.jkniv.whinstone.types.JdbcType;

public class CassandraColumn implements JdbcColumn<Row>
{
    private final int                     columnIndex;
    private final String                  columnName;
    private final String                  methodName;
    private final ColumnType              columnType;
    //private final String                  attributeName;
    //private final int                     jdbcType;
    private boolean nestedAttribute;
    private PropertyAccess propertyAccess;
    private final static JdbcColumnMapper JDBC_COLUMN_MAPPER = new UnderscoreToCamelCaseMapper();// TODO design property to config;
    
    public CassandraColumn(int columnIndex, String columnName, DataType.Name columnType, Class<?> classTarget)
    {
        super();
        this.columnIndex = columnIndex;
        this.columnName = columnName;
        this.nestedAttribute = false;
        this.propertyAccess = new PropertyAccess(JDBC_COLUMN_MAPPER.map(columnName), classTarget);
        this.columnType = CassandraType.valueOf(columnType.name());
        if(columnName.indexOf(".") > 0)
        {
            this.nestedAttribute = true;
            this.methodName = columnName;
        }
        else
        {
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
    
//    @Override
//    public boolean isClob()
//    {
//        // FIXME implements read CLOB as string, 
//        // FIXME implements write CLOB to database
//        return this.columnType.isClob();
//    }
//    
//    @Override
//    public boolean isBlob()
//    {
//        // FIXME implements write BLOB to database
//        return this.columnType.isBlob();
//    }
//    
//    @Override
//    public boolean isDate()
//    {
//        return this.columnType.isDate();
//    }
//    
//    @Override
//    public boolean isTimestamp()
//    {
//        return this.columnType.isTimestamp();
//    }
//    
//    @Override
//    public boolean isTime()
//    {
//        return this.columnType.isTime();
//    }
    
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
        Convertible<Object, Object> convertible = ConvertibleFactory.toAttribute(this, proxy);
        if (!convertible.getType().isInstance(value))
            value = convertible.toAttribute(value);
        return value;

        /*
        Object value = row.getObject(columnIndex);
        if (value != null)
        {
            if (this.columnType.isDate())
                value = new Date(row.getDate(columnIndex).getMillisSinceEpoch());
            else if (this.columnType.isTimestamp())
                value = new Date(row.getTimestamp(columnIndex).getTime());
            else if (this.columnType.isTime())
                value = new Date(row.getTime(columnIndex));
        }
        return value;
        */
    }
    
    @Override
    public Object getBytes(Row row) throws SQLException
    {
        return row.getBytes(columnIndex);
    }

    @Override
    public ColumnType getType()
    {
        return this.columnType;
    }

    @Override
    public String toString()
    {
        return "DefaultJdbcColumn [index=" + columnIndex + ", columnName=" + columnName + ", jdbcType=" + columnType
                + "]";
    }
}
