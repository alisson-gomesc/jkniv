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
import java.sql.Types;
import java.util.Date;
import java.util.Locale;

import net.sf.jkniv.sqlegance.JdbcColumn;
import net.sf.jkniv.sqlegance.JdbcColumnMapper;
import net.sf.jkniv.sqlegance.UnderscoreToCamelCaseMapper;

public class DefaultJdbcColumn implements JdbcColumn<ResultSet>
{
    private final int                     columnIndex;
    private final String                  columnName;
    private final String                  attributeName;
    private final String                  methodName;
    private final int                     jdbcType;
    private boolean nestedAttribute;
    private final static JdbcColumnMapper jdbcColumnMapper = new UnderscoreToCamelCaseMapper();// TODO design property to config;
    
    public DefaultJdbcColumn(int columnIndex, String columnName, int jdbcType)
    {
        super();
        this.columnIndex = columnIndex;
        this.columnName = columnName;
        this.nestedAttribute = false;
        if(columnName.indexOf(".") > 0)
        {
            this.nestedAttribute = true;
            this.methodName = columnName;
            this.attributeName = columnName;
        }
        else
        {
            this.attributeName = jdbcColumnMapper.map(columnName);
            this.methodName = capitalizeSetter(attributeName);
        }
        this.jdbcType = jdbcType;
        //this.jdbcColumnMapper = new UnderscoreToCamelCaseMapper();// TODO design property to config
    }
    
    public String getAttributeName()
    {
        return this.attributeName;
    }
    
    public String getMethodName()
    {
        return methodName;
    }
    
    public String getName()
    {
        return this.columnName;
    }
    
    public int getIndex()
    {
        return this.columnIndex;
    }
    
    public boolean isBinary()
    {
        return (this.jdbcType == Types.CLOB || jdbcType == Types.BLOB);
    }
    
    public boolean isClob()
    {
        // FIXME implements read CLOB as string, 
        // FIXME implements write CLOB to database
        return (this.jdbcType == Types.CLOB);
    }
    
    public boolean isBlob()
    {
        // FIXME implements write BLOB to database
        return (this.jdbcType == Types.BLOB);
    }
    
    public boolean isDate()
    {
        return (this.jdbcType == Types.DATE);
    }
    
    public boolean isTimestamp()
    {
        return (this.jdbcType == Types.TIMESTAMP);
    }
    
    public boolean isTime()
    {
        return (this.jdbcType == Types.TIME);
    }
    
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
    
    /**
     * Append prefix <code>set<code> to attributeColumnName and capitalize it.
     * @param attributeColumnName attribute name to capitalize with <code>set</code> prefix
     * @return return capitalize attribute name, sample: identityName -> setIdentityName
     */
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

    @Override
    public String toString()
    {
        return "DefaultJdbcColumn [index=" + columnIndex + ", columnName=" + columnName + ", jdbcType=" + jdbcType
                + "]";
    }
}
