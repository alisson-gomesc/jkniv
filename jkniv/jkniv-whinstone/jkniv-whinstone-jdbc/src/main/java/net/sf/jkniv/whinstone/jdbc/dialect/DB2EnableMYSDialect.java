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
package net.sf.jkniv.whinstone.jdbc.dialect;

import net.sf.jkniv.sqlegance.dialect.AnsiDialect;

/**
 * Dialect to DB2 with compatibility features for MySQL applications.
 * <p>
 *  Limit clause:
 *  <code>select name from author LIMIT 1 OFFSET 2</code>
 * </p>
 * <ul>
 *  <li>Supports limits? true</li>
 *  <li>Supports limit off set? true</li>
 *  <li>Supports rownum? false</li>
 * </ul>
 *
 *<p>
 * The DB2_COMPATIBILITY_VECTOR registry variable enables one or more DB2® compatibility features. 
 * These features ease the task of migrating applications that were written for relational database 
 * products other than the DB2 product to DB2 Version 9.5 or later.
 *</p>
 *<p>
 * This registry variable is supported on Linux, UNIX, and Windows operating systems.
 * You can enable individual DB2 compatibility features by specify a hexadecimal value 
 * for the registry variable. Each bit in the variable value enables a different feature. Values are as follows:
 *</p>
 *<pre>
 *   NULL (default)
 *   0000 - FFFF
 *   ORA, to take full advantage of the DB2 compatibility features for Oracle applications
 *   SYB, to take full advantage of the DB2 compatibility features for Sybase applications
 *   MYS, to take full advantage of the DB2 compatibility features for MySQL applications
 *</pre>
 *
 * @see <a href="http://www.ibm.com/support/knowledgecenter/SSEPGG_10.1.0/com.ibm.db2.luw.apdv.porting.doc/doc/r0052867.html">DB2 compatibility Oracle/Sybase/MySQL</a>
 *
 * @author Alisson Gomes 
 *
 */
public class DB2EnableMYSDialect extends AnsiDialect
{
    public DB2EnableMYSDialect()
    {
        super();
    }
        
    @Override
    public boolean supportsLimit()
    {
        return true;
    }
    
    @Override
    public boolean supportsLimitOffset()
    {
        return true;
    }
    
    /**
     *  LIMIT and OFFSET clause for DB2 with DB2_COMPATIBILITY_VECTOR=MYS enabled, 
     *  where LIMIT and OFFSET are parameter from String.format
     *  
     *  @return Return query pattern: 
     *  
     *   select name from author LIMIT 1 OFFSET 2
     * 
     */
    @Override
    public String getSqlPatternPaging()
    {
        return "%1$s LIMIT %2$s OFFSET %3$s";
    }
}
