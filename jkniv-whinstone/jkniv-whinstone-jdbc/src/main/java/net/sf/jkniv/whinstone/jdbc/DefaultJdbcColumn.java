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
package net.sf.jkniv.whinstone.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import net.sf.jkniv.reflect.beans.PropertyAccess;
import net.sf.jkniv.sqlegance.types.ColumnType;
import net.sf.jkniv.sqlegance.types.JdbcType;
import net.sf.jkniv.whinstone.JdbcColumn;
import net.sf.jkniv.whinstone.JdbcColumnMapper;
import net.sf.jkniv.whinstone.UnderscoreToCamelCaseMapper;

public class DefaultJdbcColumn implements JdbcColumn<ResultSet>
{
    private final int                     columnIndex;
    private final String                  columnName;
    private final ColumnType              columnType;
    private final String                  methodName;
    //private final String                  attributeName;
    //private final int                     jdbcType;
    private boolean nestedAttribute;
    private PropertyAccess propertyAccess;
    
    // TODO design property to config UnderscoreToCamelCaseMapper
    private final static JdbcColumnMapper JDBC_COLUMN_MAPPER = new UnderscoreToCamelCaseMapper();
    
    public DefaultJdbcColumn(int columnIndex, String columnName, int jdbcType)
    {
        this(columnIndex, columnName, jdbcType, null);
    }
    
    public DefaultJdbcColumn(int columnIndex, String columnName, int jdbcTypeValue, Class<?> classTarget)
    {
        super();
        this.columnIndex = columnIndex;
        this.columnName = columnName;
        this.nestedAttribute = false;
        this.propertyAccess = new PropertyAccess(JDBC_COLUMN_MAPPER.map(columnName), classTarget);
        //this.attributeName = propertyAccess.getFieldName();
        this.columnType = JdbcType.valueOf(jdbcTypeValue);
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
        return propertyAccess;
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
    public boolean isClob()
    {
        // FIXME implements read CLOB as string, 
        // FIXME implements write CLOB to database
        return this.columnType.isClob();
    }
    
    @Override
    public boolean isBlob()
    {
        // FIXME implements write BLOB to database
        return this.columnType.isBlob();
    }
    
    @Override
    public boolean isDate()
    {
        return this.columnType.isDate();
    }
    
    @Override
    public boolean isTimestamp()
    {
        return this.columnType.isTimestamp();
    }
    
    @Override
    public boolean isTime()
    {
        return this.columnType.isTime();
    }
    
    @Override
    public boolean isNestedAttribute()
    {
        return nestedAttribute;
    }
    
    @Override
    public Object getValue(ResultSet rs) throws SQLException
    {
        Object value = rs.getObject(columnIndex);
        if (value != null)
        {
            if (isDate())
                value = new Date(rs.getDate(columnIndex).getTime());
            else if (isTimestamp())
                value = new Date(rs.getTimestamp(columnIndex).getTime());
            else if (isTime())
                value = new Date(rs.getTime(columnIndex).getTime());
        }
        return value;
    }
    
    @Override
    public Object getBytes(ResultSet rs) throws SQLException
    {
        return rs.getBytes(columnIndex);
    }
    
    @Override
    public ColumnType getType()
    {
        return this.columnType;
    }

    /*
     * Append prefix <code>set<code> to attributeColumnName and capitalize it.
     * @param attributeColumnName attribute name to capitalize with <code>set</code> prefix
     * @return return capitalize attribute name, sample: identityName -> setIdentityName
     *
    private String capitalizeSetter(String attributeColumnName)// TODO design config capitalize algorithm
    {
        String capitalize = "";
        
        if (attributeColumnName != null)
        {
            int length = attributeColumnName.length();
            capitalize = attributeColumnName.substring(0, 1).toUpperCase(Locale.ENGLISH);
            if (length > 1)
                capitalize += attributeColumnName.substring(1, length);
        }
        //sqlLogger.log(LogLevel.RESULTSET, "Mapping column [{}] to property [{}]", attributeColumnName, "set" + capitalize);
        return "set" + capitalize;
    }
*/
    @Override
    public String toString()
    {
        return "DefaultJdbcColumn [index=" + columnIndex + ", columnName=" + columnName + ", jdbcType=" + columnType
                + "]";
    }
}
