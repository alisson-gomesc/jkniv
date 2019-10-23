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

import net.sf.jkniv.sqlegance.dialect.SqlFeatureFactory;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureSupport;

/**
 * Dialect to Derby version 10.7
 * 
 * <p>
 *  Limit clause:
 *   <code>select name from author OFFSET 5 ROWS FETCH NEXT 10 ROWS ONLY</code>
 * </p>
 * 
 * <ul>
 *  <li>Supports limits? true</li>
 *  <li>Supports limit off set? true</li>
 *  <li>Supports rownum? true</li>
 * </ul>
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class Derby10o7Dialect extends Derby10o4Dialect
{
    public Derby10o7Dialect()
    {
        super();
        addFeature(SqlFeatureFactory.newInstance(SqlFeatureSupport.LIMIT, true));
        addFeature(SqlFeatureFactory.newInstance(SqlFeatureSupport.LIMIT_OFF_SET, true));
        addFeature(SqlFeatureFactory.newInstance(SqlFeatureSupport.ROWNUM, true));
    }
    
    @Override
    public String getSqlPatternPaging()
    {
        return "%1$s offset %3$s rows fetch next %2$s rows only";
    }
    

}
