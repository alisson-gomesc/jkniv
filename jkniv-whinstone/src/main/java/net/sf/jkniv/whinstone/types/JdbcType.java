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
package net.sf.jkniv.whinstone.types;

import java.sql.Types;

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
public enum JdbcType implements ColumnType
{
    //Types.BINARY 
    //Types.DATALINK 
    //Types.DECIMAL 
    //Types.DISTINCT
    //Types.JAVA_OBJECT 
    //Types.LONGNVARCHAR 
    //Types.LONGVARBINARY
    //Types.LONGVARCHAR 
    //Types.NUMERIC 
    //Types.OTHER 
    //Types.REF
    //Types.REF_CURSOR 
    //Types.ROWID 
    //Types.SQLXML 
    //Types.STRUCT 
    //Types.TIME
    //Types.TIME_WITH_TIMEZONE 
    //Types.TIMESTAMP
    //Types.TIMESTAMP_WITH_TIMEZONE    
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
    
    private final static Logger LOG = LoggerFactory.getLogger(JdbcType.class);
    private int                 value;
    
    private JdbcType(int v)
    {
        this.value = v;
    }
    
    public int value()
    {
        return value;
    }
    
    @Override
    public boolean isBinary()
    {
        return (this.value == Types.CLOB || this.value == Types.BLOB);
    }
    
    @Override
    public boolean isBlob()
    {
        // FIXME implements write BLOB to database
        return (this.value == Types.BLOB);
    }

    @Override
    public boolean isClob()
    {
        return (this.value == Types.CLOB);
    }
    
    @Override
    public boolean isDate()
    {
        return (this.value == Types.DATE);
    }
    
    @Override
    public boolean isTimestamp()
    {
        return (this.value == Types.TIMESTAMP);
    }
    
    @Override
    public boolean isTime()
    {
        return (this.value == Types.TIME);
    }

    public static ColumnType valueOf(int jdbcTypeValue)
    {
        JdbcType answer = null;
        for(JdbcType jdbcType : JdbcType.values())
        {
            if (jdbcType.value() == jdbcTypeValue)
            {
                answer = jdbcType;
                break;
            }
        }
        return answer;
    }
}
