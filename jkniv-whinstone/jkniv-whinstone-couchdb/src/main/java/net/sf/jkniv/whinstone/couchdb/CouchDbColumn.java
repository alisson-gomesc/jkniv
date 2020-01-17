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
package net.sf.jkniv.whinstone.couchdb;

import java.sql.SQLException;
import java.sql.Types;

import net.sf.jkniv.reflect.beans.PropertyAccess;
import net.sf.jkniv.whinstone.JdbcColumn;
import net.sf.jkniv.whinstone.JdbcColumnMapper;
import net.sf.jkniv.whinstone.UnderscoreToCamelCaseMapper;
import net.sf.jkniv.whinstone.types.ColumnType;

class CouchDbColumn implements JdbcColumn<String>
{
    private final int                     columnIndex;
    private final String                  columnName;
    private final String                  attributeName;
    private final String                  methodName;
    //private final int                     jdbcType;
    private final ColumnType              columnType;
    private boolean nestedAttribute;
    private PropertyAccess propertyAccess;
    private final static JdbcColumnMapper jdbcColumnMapper = new UnderscoreToCamelCaseMapper();// TODO design property to config;
    
    public CouchDbColumn(int columnIndex, String columnName, int jdbcType)
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
            this.propertyAccess = new PropertyAccess(jdbcColumnMapper.map(columnName));
            this.attributeName = propertyAccess.getFieldName();
            this.methodName = propertyAccess.getWriterMethodName();
//            this.attributeName = jdbcColumnMapper.map(columnName);
//            this.methodName = capitalizeSetter(attributeName);
        }
        this.columnType = null;
        //this.columnType = jdbcType;
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
        return this.columnType.isBinary();
    }
    
    public boolean isClob()
    {
        throw new UnsupportedOperationException("CouchDb Repository doesn't implement Column data type parse!");
        // FIXME implements read CLOB as string, 
        // FIXME implements write CLOB to database
        //return (this.jdbcType == Types.CLOB);
    }
    
    public boolean isBlob()
    {
        throw new UnsupportedOperationException("CouchDb Repository doesn't implement Column data type parse!");
        // FIXME implements write BLOB to database
        //return (this.jdbcType == DataType.Name.BLOB.ordinal());
    }
    
    public boolean isDate()
    {
        throw new UnsupportedOperationException("CouchDb Repository doesn't implement Column data type parse!");
        //return (this.jdbcType == DataType.Name.DATE.ordinal());
    }
    
    public boolean isTimestamp()
    {
        throw new UnsupportedOperationException("CouchDb Repository doesn't implement Column data type parse!");
        //return (this.jdbcType == DataType.Name.TIMESTAMP.ordinal());
    }
    
    public boolean isTime()
    {
        throw new UnsupportedOperationException("CouchDb Repository doesn't implement Column data type parse!");
        //return (this.jdbcType == DataType.Name.TIME.ordinal());
    }
    
    public boolean isNestedAttribute()
    {
        return nestedAttribute;
    }
    
    @Override
    public ColumnType getType()
    {
        return this.columnType;
    }

    
    @Override
    public Object getValue(String row) throws SQLException
    {
        return row;
//        
//        Object value = row.getObject(columnIndex);
//        if (value != null)
//        {
//            if (isDate())
//                value = new Date(row.getDate(columnIndex).getMillisSinceEpoch());
//            else if (isTimestamp())
//                value = new Date(row.getTimestamp(columnIndex).getTime());
//            else if (isTime())
//                value = new Date(row.getTime(columnIndex));
//        }
//        return value;
    }
    
    @Override
    public Object getBytes(String row) throws SQLException
    {
        return row;
        //return row.getBytes(columnIndex);
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
    }*/

    @Override
    public PropertyAccess getPropertyAccess()
    {
        return this.propertyAccess;
    }
    
    @Override
    public String toString()
    {
        return "DefaultJdbcColumn [index=" + columnIndex + ", columnName=" + columnName + ", jdbcType=" + columnType
                + "]";
    }
}
