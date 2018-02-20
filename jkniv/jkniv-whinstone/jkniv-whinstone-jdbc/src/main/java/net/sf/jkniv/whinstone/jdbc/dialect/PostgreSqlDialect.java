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
 * Dialect to PostgreSQL
 * 
 * <p>
 * Limit clause:
 *  <code>select name from author LIMIT 1 OFFSET 2</code>
 * </p>
 * 
 * <ul>
 *  <li>Supports limits? true</li>
 *  <li>Supports limit off set? true</li>
 *  <li>Supports rownum? false</li>
 * </ul>
 *
 * @author Alisson Gomes 
 *
 */
public class PostgreSqlDialect extends AnsiDialect
{
    public PostgreSqlDialect()
    {
        super();
    }
    
//    public PostgreSqlDialect(Queryable queryable)
//    {
//        super(queryable);
//    }
    
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
     *  LIMIT and OFFSET clause for PostgreSQL, where LIMIT and OFFSET are parameter from
     *  String.format
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
