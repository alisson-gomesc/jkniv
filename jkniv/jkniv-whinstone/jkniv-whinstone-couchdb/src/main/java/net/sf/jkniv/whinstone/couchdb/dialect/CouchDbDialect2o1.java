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
package net.sf.jkniv.whinstone.couchdb.dialect;

import net.sf.jkniv.sqlegance.dialect.SqlFeatureFactory;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureSupport;

/**
 * Dialect to CouchDB
 * 
 * <p>
 * Limit clause:
 *  <pre>
 *  {
 *    "selector": {
 *      "year": {"$gt": 2010}
 *    },
 *    "fields": ["_id", "_rev", "year", "title"],
 *    "sort": [{"year": "asc"}],
 *    "limit": 2,
 *    "skip": 0
 *  }
 *  </pre>
 * 
 * <ul>
 *  <li>Supports limits? true</li>
 *  <li>Supports limit off set? true</li>
 *  <li>Supports rownum? false</li>
 * </ul>
 *
 * @author Alisson Gomes 
 * @since 0.6.0
 * @see <a href="https://docs.couchdb.org/en/2.1.2/api/database/find.html">Find Pagination</a>
 */
public class CouchDbDialect2o1 extends CouchDbDialect2o0
{
    public CouchDbDialect2o1()
    {
        super();
        addFeature(SqlFeatureFactory.newInstance(SqlFeatureSupport.BOOKMARK_QUERY, true));//couchdb 2.0 doesn't support bookmark
    }    
}
