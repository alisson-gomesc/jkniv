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
package net.sf.jkniv.whinstone.types;

/***
 * Data types supported by cassandra.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public enum CassandraType implements ColumnType
{
    CUSTOM, // (0), // id number from cassandra DataType is not public, must be resolved by ordinal or name
    ASCII, // (1), 
    BIGINT, // (2), 
    BLOB, // (3), 
    BOOLEAN, // (4), 
    COUNTER, // (5), 
    DECIMAL, // (6), 
    DOUBLE, // (7), 
    FLOAT, // (8), 
    INT, // (9), 
    TEXT, // (10), 
    TIMESTAMP, // (11), 
    UUID, // (12), 
    VARCHAR, // (13), 
    VARINT, // (14), 
    TIMEUUID, // (15), 
    INET, // (16), 
    DATE, // (17), 
    TIME, // (18), 
    SMALLINT, // (19), 
    TINYINT, // (20), 
    DURATION, // (21), 
    LIST, // (32), 
    MAP, // (33), 
    SET, // (34), 
    UDT, // (48), 
    TUPLE, // (49)
    ;
    
    public int value()
    {
        return this.ordinal();
    }
    
    @Override
    public boolean isBinary()
    {
        return isBlob();        
    }
    
    @Override
    public boolean isBlob()
    {
        return (this == BLOB);
    }
    
    @Override
    public boolean isClob()
    {
        return false;
    }
    
    @Override
    public boolean isDate()
    {
        return (this == DATE);
    }
    
    @Override
    public boolean isTimestamp()
    {
        return (this == TIMESTAMP);
    }
    
    @Override
    public boolean isTime()
    {
        return (this == TIME);
    }

    public static ColumnType valueOf(int typeValue)
    {
        ColumnType answer = null;
        for (ColumnType jdbcType : CassandraType.values())
        {
            if (jdbcType.value() == typeValue)
            {
                answer = jdbcType;
                break;
            }
        }
        return answer;
    }

    public static ColumnType valueStartWith(String typeValue)
    {
        ColumnType answer = null;
        for (ColumnType jdbcType : CassandraType.values())
        {
            if (typeValue.toUpperCase().startsWith(jdbcType.name()))
            {
                answer = jdbcType;
                break;
            }
        }
        return answer;
    }
    
}
