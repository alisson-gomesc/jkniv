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
package net.sf.jkniv.whinstone.jpa2.dialect;

import net.sf.jkniv.sqlegance.dialect.AnsiDialect;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureFactory;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureSupport;

/**
 * Dialect to JPA
 * 
 * <p>
 * Limit clause:
 *  <code>select name from author LIMIT 1 </code>
 * </p>
 * 
 * <ul>
 *  <li>Supports limits? true</li>
 *  <li>Supports limit off set? true</li>
 *  <li>Supports rownum? false</li>
 * </ul>
 *
 *
 * @author Alisson Gomes 
 * @since 0.6.0
 */
public class JpaDialect extends AnsiDialect
{
    public JpaDialect()
    {
        super();
        addFeature(SqlFeatureFactory.newInstance(SqlFeatureSupport.LIMIT, true));
        addFeature(SqlFeatureFactory.newInstance(SqlFeatureSupport.LIMIT_OFF_SET, true));
        addFeature(SqlFeatureFactory.newInstance(SqlFeatureSupport.CONN_HOLDABILITY, false));
        addFeature(SqlFeatureFactory.newInstance(SqlFeatureSupport.BOOKMARK_QUERY, false));
        addFeature(SqlFeatureFactory.newInstance(SqlFeatureSupport.PAGING_ROUNDTRIP, true));
    }

    /*
     *  LIMIT clause for Cassandra, where LIMIT is parameter from String.format
     *  
     *  @return Return query pattern: 
     *  <p>
     *  <code>select name from author LIMIT 1</code>
     * 
     *
    @Override
    public String getSqlPatternPaging()
    {
        return "%1$s LIMIT %2$s";
    }
    */
}
