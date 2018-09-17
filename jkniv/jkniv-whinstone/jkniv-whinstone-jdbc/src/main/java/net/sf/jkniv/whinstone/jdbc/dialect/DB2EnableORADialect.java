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

import java.util.regex.Matcher;

import net.sf.jkniv.sqlegance.dialect.AnsiDialect;

/**
 * Dialect to DB2 with compatibility features for Oracle applications.
 * <p>
 * Limit clause:
 *  <code>
 *  SELECT TEXT FROM SESSION.SEARCHRESULTS
     WHERE ROWNUM BETWEEN 20 AND 40
     ORDER BY ID
 *  </code>
 * </p>
 * <ul>
 *  <li>Supports limits? true</li>
 *  <li>Supports limit off set? true</li>
 *  <li>Supports rownum? true</li>
 * </ul>
 * <p>
 * The DB2_COMPATIBILITY_VECTOR registry variable enables one or more DB2® compatibility features. 
 * These features ease the task of migrating applications that were written for relational database 
 * products other than the DB2 product to DB2 Version 9.5 or later.
 * </p>
 * <p>
 * This registry variable is supported on Linux, UNIX, and Windows operating systems.
 * You can enable individual DB2 compatibility features by specify a hexadecimal value 
 * for the registry variable. Each bit in the variable value enables a different feature. Values are as follows:
 * </p>
 * <pre>
 *   NULL (default)
 *   0000 - FFFF
 *   ORA, to take full advantage of the DB2 compatibility features for Oracle applications
 *   SYB, to take full advantage of the DB2 compatibility features for Sybase applications
 *   MYS, to take full advantage of the DB2 compatibility features for MySQL applications
 * </pre>
 * 
 * @see <a href="http://www.ibm.com/support/knowledgecenter/SSEPGG_10.1.0/com.ibm.db2.luw.apdv.porting.doc/doc/r0052867.html">DB2 compatibility Oracle/Sybase/MySQL</a>
 *
 * @author Alisson Gomes 
 *
 */
public class DB2EnableORADialect extends AnsiDialect
{
    public DB2EnableORADialect()
    {
        super();
    }
    
    /**
     * Support LIMIT using rownum. Native clause not exists.
     */
    @Override
    public boolean supportsLimit()
    {
        return true;
    }
    
    /**
     * Support LIMIT using rownum. Native clause not exists.
     */
    @Override
    public boolean supportsLimitOffset()
    {
        return true;
    }
    
    /**
     * To support rownum DB2_COMPATIBILITY_VECTOR must be enable.
     * <code>
     *  db2set DB2_COMPATIBILITY_VECTOR=ORA
     *  db2stop
     *  db2start
     *  </code>
     */
    @Override
    public boolean supportsRownum()
    {
        return true;
    }
    
    /**
     * Using rownum to paging the select, COMPATIBILITY_VECTOR=ORA must be enabled.
     *  
     *  @return Return query pattern: 
     *  <code>
     *   select * from (%1$s) where rownum between %3$s and %2$s
     * </code>
     */
    @Override
    public String getSqlPatternPaging()
    {
        return "select * from (%1$s) where ROWNUM BETWEEN %3$s AND %2$s";
    }
    
    //@Override public void buildSqlLimits()
    @Override
    public String buildQueryPaging(final String sqlText, int offset, int max)
    {
        String sqlTextPaginated = null;
        if (supportsLimit())
        {
            String pagingSelect = getSqlPatternPaging();
            //sqlText = queryable.getSql().getSql(queryable.getParams());
            Matcher matcher = sqlEndWithForUpdate(sqlText);
            String forUpdate = "";
            String sqlWithoutForUpdate = sqlText;
            if (matcher.find())
            {
                forUpdate = sqlText.substring(matcher.start(), matcher.end());// select name from author ^for update^
                sqlWithoutForUpdate = sqlText.substring(0, matcher.start());// ^select name from author^ for update
            }
            sqlTextPaginated = String.format(pagingSelect, sqlWithoutForUpdate,
                    max + offset, offset) + forUpdate;
        }
//        else
//        {
//            this.sql = queryable.getSql().getSql(queryable.getParams());
//        }
//        replaceForQuestionMark();
        return sqlTextPaginated;
    }
}
