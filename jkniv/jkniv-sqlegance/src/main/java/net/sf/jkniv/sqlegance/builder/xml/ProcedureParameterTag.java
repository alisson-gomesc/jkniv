/* 
 * JKNIV, SQLegance keeping queries maintainable.
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
package net.sf.jkniv.sqlegance.builder.xml;

import java.sql.Types;

import net.sf.jkniv.sqlegance.ParameterMode;
import net.sf.jkniv.sqlegance.RepositoryException;

/**
 * Tag of stored procedure parameters sentence. <b>Stored procedures don't work
 * yet!</b>
 * 
 * @author Alisson Gomes
 * @since 0.0.2
 */
public class ProcedureParameterTag
{
    public static final String TAG_NAME           = "parameter";
    public static final String ATTRIBUTE_PROPERTY = "property";
    public static final String ATTRIBUTE_MODE     = "mode";
    public static final String ATTRIBUTE_SQLTYPE  = "sqlType";
    public static final String ATTRIBUTE_TYPENAME = "typeName";
    
    private int                sqlType;
    private String             typeName;
    private String             property;
    private ParameterMode      mode;
    
    /**
     * Build a parameter with your values.
     * 
     * @param property
     *            Name from property.
     * @param mode
     *            The mode from procedure parameter: IN, OUT, INOUT. The value
     *            is insensitive-case
     * @param sqlType
     *            Name of one constant at java.sql.Types, sample: BOOLEAN, CHAR,
     *            etc. The value is insensitive-case. One type that's not belong
     *            to jav.sql.Types throw one ConfigurationException.
     * @param typeName
     *            the fully-qualified name of an SQL structured type
     *    
     * @exception RepositoryException  TODO javadoc
     */
    public ProcedureParameterTag(String property, String mode, String sqlType, String typeName)
    {
        this.property = property;
        this.sqlType = getSqlType(sqlType);
        this.mode = getParameterMode(mode);
        this.typeName = typeName;
    }
    
    /**
     * Build a parameter with your values.
     * 
     * @param property
     *            Name from property.
     * @param mode
     *            The mode from procedure parameter: IN, OUT, INOUT. The value
     *            is insensitive-case
     * @param sqlType
     *            Name of one constant at java.sql.Types, sample: BOOLEAN, CHAR,
     *            etc. The value is insensitive-case. One type that's not belong
     *            to jav.sql.Types throw one ConfigurationException.
     * @exception RepositoryException TODO javadoc
     */
    public ProcedureParameterTag(String property, String mode, String sqlType)
    {
        this(property, mode, sqlType, null);
    }
    
    /**
     * Build a parameter with your values.
     * 
     * @param property
     *            Name from property.
     * @param mode
     *            The mode from procedure parameter: IN, OUT, INOUT. The value
     *            is insensitive-case
     * @exception RepositoryException TODO  javadoc
     */
    public ProcedureParameterTag(String property, String mode)
    {
        this(property, mode, "NULL", null);
    }
    
    // ARRAY BOOLEAN VARCHAR CHAR DATE TIME TIMESTAMP DOUBLE FLOAT INTEGER
    // BIGINT SMALLINT REAL DECIMAL TINYINT
    // DATALINK CLOB BLOB BIT
    // BINARY VARBINARY STRUCT SQLXML ROWID REF OTHER NUMERIC NCLOB NCHAR
    // LONGVARCHAR LONGVARBINARY LONGNVARCHAR JAVA_OBJECT DISTINCT NULL
    
    private ParameterMode getParameterMode(String mode)
    {
        ParameterMode pmode = ParameterMode.IN;
        
        for (ParameterMode m : ParameterMode.values())
        {
            if (m.toString().equalsIgnoreCase(mode))
                pmode = m;
        }
        return pmode;
    }
    
    private int getSqlType(String sqlType)
    {
        int type = Types.VARCHAR;
        
        if ("ARRAY".equalsIgnoreCase(sqlType))
            type = Types.ARRAY;
        else if ("BOOLEAN".equalsIgnoreCase(sqlType))
            type = Types.BOOLEAN;
        else if ("VARCHAR".equalsIgnoreCase(sqlType))
            type = Types.VARCHAR;
        else if ("CHAR".equalsIgnoreCase(sqlType))
            type = Types.CHAR;
        else if ("NCHAR".equalsIgnoreCase(sqlType)) 
            type = Types.NCHAR;
        else if ("DATE".equalsIgnoreCase(sqlType))
            type = Types.DATE;
        else if ("TIME".equalsIgnoreCase(sqlType))
            type = Types.TIME;
        else if ("TIMESTAMP".equalsIgnoreCase(sqlType))
            type = Types.TIMESTAMP;
        else if ("DOUBLE".equalsIgnoreCase(sqlType))
            type = Types.DOUBLE;
        else if ("FLOAT".equalsIgnoreCase(sqlType))
            type = Types.FLOAT;
        else if ("INTEGER".equalsIgnoreCase(sqlType))
            type = Types.INTEGER;
        else if ("BIGINT".equalsIgnoreCase(sqlType))
            type = Types.BIGINT;
        else if ("SMALLINT".equalsIgnoreCase(sqlType))
            type = Types.SMALLINT;
        else if ("REAL".equalsIgnoreCase(sqlType))
            type = Types.REAL;
        else if ("DECIMAL".equalsIgnoreCase(sqlType))
            type = Types.DECIMAL;
        else if ("TINYINT".equalsIgnoreCase(sqlType))
            type = Types.TINYINT;
        else if ("LONGNVARCHAR".equalsIgnoreCase(sqlType)) 
            type = Types.LONGNVARCHAR;
        else if ("LONGVARBINARY".equalsIgnoreCase(sqlType))
            type = Types.LONGVARBINARY;
        else if ("LONGVARCHAR".equalsIgnoreCase(sqlType))
            type = Types.LONGVARCHAR;
        else if ("BINARY".equalsIgnoreCase(sqlType))
            type = Types.BINARY;
        else if ("BIT".equalsIgnoreCase(sqlType))
            type = Types.BIT;
        else if ("BLOB".equalsIgnoreCase(sqlType))
            type = Types.BLOB;
        else if ("CLOB".equalsIgnoreCase(sqlType))
            type = Types.CLOB;
        else if ("DATALINK".equalsIgnoreCase(sqlType))
            type = Types.DATALINK;
        else if ("DISTINCT".equalsIgnoreCase(sqlType))
            type = Types.DISTINCT;
        else if ("JAVA_OBJECT".equalsIgnoreCase(sqlType))
            type = Types.JAVA_OBJECT;
        else if ("NCLOB".equalsIgnoreCase(sqlType)) 
            type = Types.NCLOB;
        else if ("NUMERIC".equalsIgnoreCase(sqlType))
            type = Types.NUMERIC;
        else if ("OTHER".equalsIgnoreCase(sqlType))
            type = Types.OTHER;
        else if ("REF".equalsIgnoreCase(sqlType))
            type = Types.REF;
        else if ("ROWID".equalsIgnoreCase(sqlType)) 
            type = Types.ROWID;
        else if ("SQLXML".equalsIgnoreCase(sqlType)) 
            type = Types.SQLXML;
        else if ("STRUCT".equalsIgnoreCase(sqlType))
            type = Types.STRUCT;
        else if ("VARBINARY".equalsIgnoreCase(sqlType))
            type = Types.VARBINARY;
        else if ("NULL".equalsIgnoreCase(sqlType))
            type = Types.NULL;
        else
            throw new RepositoryException("Cannot identify the sql type at procedure parameter, check the posible values at java.sql.Types.");
        
        return type;
    }
    
    /**
     * Retrieve the tag name.
     * 
     * @return name from tag <code>paramenter</code>.
     */
    public String getTagName()
    {
        return TAG_NAME;
    }
    
    /**
     * Property name from object parameter, can be a object like Author, Book,
     * etc. or a java.util.Map where the key is the property value.
     * 
     * @return the property value declared at XML file.
     */
    public String getProperty()
    {
        return property;
    }
    
    /**
     * SQL type from parameter
     * 
     * @return type of stored procedure parameter.
     */
    public int getSqlType()
    {
        return sqlType;
    }
    
    /**
     * mode from stored procedure parameter
     * 
     * @return the mode declared at XML file.
     */
    public ParameterMode getMode()
    {
        return mode;
    }
    
    /**
     * The fully-qualified name of an SQL structured type
     * 
     * @return Return names of user-named or REF output parameter (STRUCT,
     *         DISTINCT, JAVA_OBJECT, and named array types) parameter the
     *         fully-qualified SQL type name of the parameter should also be
     *         given, while a REF parameter requires that the fully-qualified
     *         type name of the referenced type be given
     */
    public String getTypeName()
    {
        return typeName;
    }
}
