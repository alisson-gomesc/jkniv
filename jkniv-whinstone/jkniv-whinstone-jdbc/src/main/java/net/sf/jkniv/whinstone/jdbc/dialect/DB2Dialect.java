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

import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.dialect.AnsiDialect;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureFactory;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureSupport;
import net.sf.jkniv.whinstone.Queryable;

/**
 * Default dialect to DB2.
 * <p>
 *  Limit clause:
 *   <code>select name from author FETCH FIRST 20 ROWS ONLY</code>
 * </p>
 * <ul>
 *  <li>Supports limits? true</li>
 *  <li>Supports limit off set? false</li>
 *  <li>Supports rownum? false</li>
 * </ul>
 *
 * @author Alisson Gomes 
 *
 */
public class DB2Dialect extends AnsiDialect
{
    public DB2Dialect()
    {
        super();
        addFeature(SqlFeatureFactory.newInstance(SqlFeatureSupport.LIMIT, true));
        addFeature(SqlFeatureFactory.newInstance(SqlFeatureSupport.LIMIT_OFF_SET, false));
    }
    
    /**
     *  LIMIT and OFFSET clause for DB2 with DB2_COMPATIBILITY_VECTOR=MYS enabled, 
     *  where LIMIT and OFFSET are parameter from String.format
     *  
     *  @return Return query pattern: 
     *  
     * <pre> 
     *   select name from author  FETCH FIRST   20   ROWS ONLY
     * </pre>
     */
    @Override
    public String getSqlPatternPaging()
    {
        return "%1$s FETCH FIRST %2$s ROWS ONLY";
    }
}