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
 * Default dialect do Oracle
 * 
 * <p>
 * Limit clause:
 *  <code>select name from author LIMIT 1 OFFSET 2</code>
 *  <code>
 *  select * from ( select row_.*, rownum rownum_ from 
     (select name from author ) 
     row_ where rownum &lt;= 5) where rownum_ &gt; 15
 *  </code>
 * </p>
 * 
 * <ul>
 *  <li>Supports limits? true</li>
 *  <li>Supports limit off set? true</li>
 *  <li>Supports rownum? true</li>
 * </ul>
 *
 * @author Alisson Gomes
 *
 */
public class OracleDialect extends AnsiDialect
{
    public OracleDialect()
    {
        super();
    }
    
//    public OracleDialect(Queryable queryable)
//    {
//        super(queryable);
//    }
        
    /**
     * Support LIMIT using rownum. Native clause not exists.
     */
    @Override
    public boolean supportsLimit()
    {
        return true;
    }
    
    /**
     * Support OFFSET using rownum. Native clause not exists.
     */
    @Override
    public boolean supportsLimitOffset()
    {
        return true;
    }
    
    @Override
    public boolean supportsRownum()
    {
        return true;
    }
    
    /**
     * @return  Return query pattern:
     * <code>
     *   select * from ( select row_.*, rownum rownum_ from 
     *    (%1$s) 
     *   row_ where rownum &lt;= %2$s) where rownum_ &gt; %3$s
     * </code>
     */
    @Override
    public String getSqlPatternPaging()
    {
        final StringBuilder pagingSelect = new StringBuilder(100);
        pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
        pagingSelect.append(" %1$s ");
        pagingSelect.append(" ) row_ where rownum <= %2$s) where rownum_ > %3$s");
        return pagingSelect.toString();
    }
    
    //@Override protected void buildSqlLimits()
    @Override
    public String buildQueryPaging(final String sqlText, int offset, int max)
    {
        String sqlTextPaginated = null;
        if (supportsLimit())
        {
            String pagingSelect = getSqlPatternPaging();
            Matcher matcher = sqlEndWithForUpdate(sqlText);
            String forUpdate = "";
            String sqlTextWithouForUpdate = sqlText;
            if (matcher.find())
            {
                forUpdate = sqlText.substring(matcher.start(), matcher.end());// select name from author ^for update^
                sqlTextWithouForUpdate = sqlText.substring(0, matcher.start());// ^select name from author^ for update
            }
            sqlTextPaginated = String.format(pagingSelect, sqlTextWithouForUpdate, max + offset, offset) + forUpdate;
        }
//        else
//        {
//            this.sql = queryable.getSql().getSql(queryable.getParams());
//        }
//        replaceForQuestionMark();
        return sqlTextPaginated;
    }
}
