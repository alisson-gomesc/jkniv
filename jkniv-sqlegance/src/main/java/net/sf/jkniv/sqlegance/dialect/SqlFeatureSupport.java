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
package net.sf.jkniv.sqlegance.dialect;

/**
 * Database SQL vendor features support.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public enum SqlFeatureSupport
{
    UNKNOW,
    /** Native syntax of SQL vendor support some form of limiting query results? */
    LIMIT,
    /** Native syntax of SQL vendor support some form of offset query results? */
    LIMIT_OFF_SET,
    /** Native syntax of SQL vendor support some form of enumerate the rows results? */
    ROWNUM,
    /** supports holdability at the connection level */
    CONN_HOLDABILITY,
    /** supports holdability at the statement level */
    STMT_HOLDABILITY,
    /** a page selected to mark the reader's place */
    BOOKMARK_QUERY,
    /** check if database needs a new round trip to get total of records */
    PAGING_ROUNDTRIP,
    /** Mechanism for generating unique identification */
    SEQUENCE
    
    /* breaks the query results into pages */
    //PAGE_QUERY,
    /* allow random jumps between pages */
    //OFFSET_QUERY
    //SET_HOLDABILITY,
    //CLOSE_CURSORS_AT_COMMIT
    ;
    
}
