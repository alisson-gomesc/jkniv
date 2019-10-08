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
package net.sf.jkniv.sqlegance.statement;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * The class that defines the constants that are used to identify generic SQL
 * types, called JDBC types. The int values are identical from java.sql.Types
 * 
 * @author Alisson Gomes
 * @see java.sql.Types
 * @since 0.6.0
 */
enum SqlDataType
{
    /**
     * <P>
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>BIT</code>.
     */
    BIT(Types.BIT),
    
    /**
     * <P>
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>TINYINT</code>.
     */
    TINYINT(Types.TINYINT),
    
    /**
     * <P>
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>SMALLINT</code>.
     */
    SMALLINT(Types.SMALLINT),
    
    /**
     * <P>
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>INTEGER</code>.
     */
    INTEGER(Types.INTEGER),
    
    /**
     * <P>
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>BIGINT</code>.
     */
    BIGINT(Types.BIGINT),
    
    /**
     * <P>
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>FLOAT</code>.
     */
    FLOAT(Types.FLOAT),
    
    /**
     * <P>
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>REAL</code>.
     */
    REAL(Types.REAL),
    
    /**
     * <P>
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>DOUBLE</code>.
     */
    DOUBLE(Types.DOUBLE),
    
    /**
     * <P>
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>NUMERIC</code>.
     */
    NUMERIC(Types.NUMERIC),
    
    /**
     * <P>
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>DECIMAL</code>.
     */
    DECIMAL(Types.DECIMAL),
    
    /**
     * <P>
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>CHAR</code>.
     */
    CHAR(Types.CHAR),
    
    /**
     * <P>
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>VARCHAR</code>.
     */
    VARCHAR(Types.VARCHAR),
    
    /**
     * <P>
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>LONGVARCHAR</code>.
     */
    LONGVARCHAR(Types.LONGVARCHAR),
    
    /**
     * <P>
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>DATE</code>.
     */
    DATE(Types.DATE),
    
    /**
     * <P>
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>TIME</code>.
     */
    TIME(Types.TIME),
    
    /**
     * <P>
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>TIMESTAMP</code>.
     */
    TIMESTAMP(Types.TIMESTAMP),
    
    /**
     * <P>
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>BINARY</code>.
     */
    BINARY(Types.BINARY),
    
    /**
     * <P>
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>VARBINARY</code>.
     */
    VARBINARY(Types.VARBINARY),
    
    /**
     * <P>
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type
     * <code>LONGVARBINARY</code>.
     */
    LONGVARBINARY(Types.LONGVARBINARY),
    
    /**
     * <P>
     * The constant in the Java programming language that identifies the generic
     * SQL value <code>NULL</code>.
     */
    NULL(Types.NULL),
    
    /**
     * The constant in the Java programming language that indicates that the SQL
     * type is database-specific and gets mapped to a Java object that can be
     * accessed via the methods <code>getObject</code> and
     * <code>setObject</code>.
     */
    OTHER(Types.OTHER),
    
    /**
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>JAVA_OBJECT</code>.
     * 
     * @since 1.2
     */
    JAVA_OBJECT(Types.JAVA_OBJECT),
    
    /**
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>DISTINCT</code>.
     * 
     * @since 1.2
     */
    DISTINCT(Types.DISTINCT),
    
    /**
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>STRUCT</code>.
     * 
     * @since 1.2
     */
    STRUCT(Types.STRUCT),
    
    /**
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>ARRAY</code>.
     * 
     * @since 1.2
     */
    ARRAY(Types.ARRAY),
    
    /**
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>BLOB</code>.
     * 
     * @since 1.2
     */
    BLOB(Types.BLOB),
    
    /**
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>CLOB</code>.
     * 
     * @since 1.2
     */
    CLOB(Types.CLOB),
    
    /**
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>REF</code>.
     * 
     * @since 1.2
     */
    REF(Types.REF),
    
    /**
     * The constant in the Java programming language, somtimes referred to as a
     * type code, that identifies the generic SQL type <code>DATALINK</code>.
     *
     * @since 1.4
     */
    DATALINK(Types.DATALINK),
    
    /**
     * The constant in the Java programming language, somtimes referred to as a
     * type code, that identifies the generic SQL type <code>BOOLEAN</code>.
     *
     * @since 1.4
     */
    BOOLEAN(Types.BOOLEAN),
    
    // ------------------------- JDBC 4.0 -----------------------------------
    
    /**
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>ROWID</code>
     *
     * @since 1.6
     */
    ROWID(Types.ROWID),
    
    /**
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>NCHAR</code>
     *
     * @since 1.6
     */
    NCHAR(Types.NCHAR),
    
    /**
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>NVARCHAR</code>.
     *
     * @since 1.6
     */
    NVARCHAR(Types.NVARCHAR),
    
    /**
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>LONGNVARCHAR</code>
     * .
     *
     * @since 1.6
     */
    LONGNVARCHAR(Types.LONGVARCHAR),
    
    /**
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>NCLOB</code>.
     *
     * @since 1.6
     */
    NCLOB(Types.NCLOB),
    
    /**
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type <code>XML</code>.
     *
     * @since 1.6
     */
    SQLXML(2009),
    
    // --------------------------JDBC 4.2 -----------------------------
    
    /**
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type {@code REF CURSOR}.
     *
     * @since 1.8
     */
    REF_CURSOR(2012),
    
    /**
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type
     * {@code TIME WITH TIMEZONE}.
     *
     * @since 1.8
     */
    TIME_WITH_TIMEZONE(2013),
    
    /**
     * The constant in the Java programming language, sometimes referred to as a
     * type code, that identifies the generic SQL type
     * {@code TIMESTAMP WITH TIMEZONE}.
     *
     * @since 1.8
     */
    TIMESTAMP_WITH_TIMEZONE(2014);
    
    private final static Logger LOG = LoggerFactory.getLogger(SqlDataType.class);
    private int                 value;
    private SimpleDateFormat    sdfDate;
    private SimpleDateFormat    sdfTime;
    private SimpleDateFormat    sdfTimestamp;
    private DecimalFormat       dfNumber;
    
    private SqlDataType(int v)
    {
        this.value = v;
    }
    
    public int getValue()
    {
        return value;
    }
    
    public void registerFormatDate(SimpleDateFormat sdfDate)
    {
        this.sdfDate = sdfDate;
    }
    
    public void registerFormatDateTime(SimpleDateFormat sdfTime)
    {
        this.sdfTime = sdfTime;
    }
    
    public void registerFormatTimestamp(SimpleDateFormat sdfTimestamp)
    {
        this.sdfTimestamp = sdfTimestamp;
    }
    
    public void registerFormatNumber(DecimalFormat dfNumber)
    {
        this.dfNumber = dfNumber;
    }
    
    public static SqlDataType getType(Object value)
    {
        SqlDataType type = null;
        if (value == null)
            type = NULL;
        else if (value.getClass().isArray())
            type = ARRAY;
        else if (value instanceof BigInteger)
            type = BIGINT;
        else if (value instanceof Long || "long".equals(value.getClass().getName()))
            type = BIGINT;
        else if (value instanceof Integer || "int".equals(value.getClass().getName()))
            type = INTEGER;
        else if (value instanceof Short || "short".equals(value.getClass().getName()))
            type = SMALLINT;
        else if (value instanceof Byte || "byte".equals(value.getClass().getName()))
            type = TINYINT;
        else if (value instanceof Boolean || "boolean".equals(value.getClass().getName()))
            type = BOOLEAN;// BIT;
        else if (value instanceof Float || "float".equals(value.getClass().getName()))
            type = FLOAT;// REAL;
        else if (value instanceof Double || "double".equals(value.getClass().getName()))
            type = DOUBLE;
        else if (value instanceof Date)
        {
            Date d = (Date) value;
            if (d.getHours() != 0 || d.getMinutes() != 0 || d.getSeconds() != 0)
                type = TIME;
            else
                type = DATE;
        }
        else if (value instanceof Calendar)
            type = TIMESTAMP;
        else if (value instanceof String)
            type = VARCHAR;// CHAR, NCHAR, NVARCHAR
        else if (value instanceof byte[] || value instanceof ByteArrayInputStream)
            type = BLOB;// CLOB,NCLOB
        else if (value instanceof StringReader)
            type = CLOB;
        else
            LOG.warn("Cannot SQL type for " + value.getClass().getName());
        
        /*
         * Types.BINARY Types.DATALINK Types.DECIMAL Types.DISTINCT
         * Types.JAVA_OBJECT Types.LONGNVARCHAR Types.LONGVARBINARY
         * Types.LONGVARCHAR Types.NUMERIC Types.OTHER Types.REF
         * Types.REF_CURSOR Types.ROWID Types.SQLXML Types.STRUCT Types.TIME
         * Types.TIME_WITH_TIMEZONE Types.TIMESTAMP
         * Types.TIMESTAMP_WITH_TIMEZONE
         */
        return type;
    }
    
    public static void setValue(PreparedStatement stmt, int index, Object value)
    {
        SqlDataType type = getType(value);
        
        try
        {
            if (value == null)
                stmt.setNull(index, type.getValue());
            else if (value instanceof String)
                stmt.setString(index, (String) value);//type = VARCHAR;// CHAR, NCHAR, NVARCHAR
            else if (value.getClass().isArray())
                stmt.setArray(index, (Array) value);
            else if (value instanceof BigInteger)
                stmt.setBigDecimal(index, (BigDecimal) value);
            else if (value instanceof BigDecimal)
                stmt.setBigDecimal(index, (BigDecimal) value);
            else if (value instanceof Long || "long".equals(value.getClass().getName()))
                stmt.setLong(index, (Long)value);
            else if (value instanceof Integer || "int".equals(value.getClass().getName()))
                stmt.setInt(index, (Integer)value);
            else if (value instanceof Short || "short".equals(value.getClass().getName()))
                stmt.setShort(index, (Short)value);//type = SMALLINT;
            else if (value instanceof Byte || "byte".equals(value.getClass().getName()))
                stmt.setByte(index, (Byte)value);//type = TINYINT;
            else if (value instanceof Boolean || "boolean".equals(value.getClass().getName()))
                stmt.setBoolean(index, (Boolean)value);//type = BOOLEAN;// BIT;
            else if (value instanceof Float || "float".equals(value.getClass().getName()))
                stmt.setFloat(index, (Float)value);//type = FLOAT;// REAL;
            else if (value instanceof Double || "double".equals(value.getClass().getName()))
                stmt.setDouble(index, (Double)value);//type = DOUBLE;
            else if (value instanceof Time)
                stmt.setTime(index, (Time)value);//type = TIME;
            else if (value instanceof Date)
                stmt.setDate(index, new java.sql.Date(((Date)value).getTime()));//type = DATE;
            else if (value instanceof Calendar)
                stmt.setTimestamp(index, new java.sql.Timestamp(((Date)value).getTime()));//type = DATE;
            else if (value.getClass().isArray())
                stmt.setArray(index, (Array) value);//
            else if (value instanceof InputStream)
                stmt.setBlob(index, (InputStream)value);//
            else if (value instanceof Reader)
                stmt.setClob(index, (Reader)value);//type = CLOB;
            else
                LOG.warn("Cannot SQL type for " + value.getClass().getName());
            
        }
        catch(SQLException sqle)
        {
            LOG.error("cannot set SQL value", sqle);// FIXME BUG sql data type
        }
        /*
         * Types.BINARY 
         * Types.DATALINK 
         * Types.DECIMAL 
         * Types.DISTINCT
         * Types.JAVA_OBJECT 
         * Types.LONGNVARCHAR 
         * Types.LONGVARBINARY
         * Types.LONGVARCHAR 
         * Types.NUMERIC 
         * Types.OTHER Types.REF
         * Types.REF_CURSOR 
         * Types.ROWID 
         * Types.SQLXML 
         * Types.STRUCT 
         * Types.TIME
         * Types.TIME_WITH_TIMEZONE 
         * Types.TIMESTAMP
         * Types.TIMESTAMP_WITH_TIMEZONE
         */
        //return type;
    }
}
