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
package net.sf.jkniv.whinstone.couchbase.dialect;

import net.sf.jkniv.sqlegance.dialect.AnsiDialect;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureFactory;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureSupport;
import net.sf.jkniv.whinstone.params.ParameterException;

/**
 * Dialect to CouchDB
 * 
 * <p>
 * Limit clause:
 *  <pre>
 *   SELECT DISTINCT city
 *   FROM `travel-sample`
 *   WHERE type = "airport"
 *   ORDER BY city
 *   LIMIT 4
 *   OFFSET 200;
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
 * @see <a href="https://docs.couchbase.com/server/current/n1ql/n1ql-language-reference/limit.html">LIMIT clause</a>
 * @see <a href="https://docs.couchbase.com/server/current/n1ql/n1ql-language-reference/offset.html">OFFSET clause</a>
 */
public class CouchbaseDialect6o5 extends AnsiDialect
{
    public CouchbaseDialect6o5()
    {
        super();
        addFeature(SqlFeatureFactory.newInstance(SqlFeatureSupport.LIMIT, true));
        addFeature(SqlFeatureFactory.newInstance(SqlFeatureSupport.LIMIT_OFF_SET, true));
        addFeature(SqlFeatureFactory.newInstance(SqlFeatureSupport.BOOKMARK_QUERY, false));
        addFeature(SqlFeatureFactory.newInstance(SqlFeatureSupport.CONN_HOLDABILITY, false));
        addFeature(SqlFeatureFactory.newInstance(SqlFeatureSupport.PAGING_ROUNDTRIP, false));
    }
    
    /**
     *  LIMIT and OFFSET clause for Couchbase, where LIMIT and OFFSET are parameter from
     *  String.format
     *  
     *  @return Return query pattern: 
     *    <p>
     *   <code>select name from `author` LIMIT 1 OFFSET 2</code>
     * 
     */
    @Override
    public String getSqlPatternPaging()
    {
        return "%1$s LIMIT %2$s OFFSET %3$s";
    }
    /*
    @Override
    public String buildQueryPaging(final String sqlText, int offset, int max)
    {
        String bodyWithLimitAndSkip = sqlText;
        int offsetLimit = sqlText.indexOf("\"limit\"");
        if (offsetLimit > -1)
            throw new ParameterException("Query [" + sqlText
                    + "] has a \"limit\" property defined, cannot use auto paging Queryable for this query.");
        
        int offsetBracket = sqlText.lastIndexOf("}");
        if (offsetBracket >= 0)
            bodyWithLimitAndSkip = sqlText.substring(0, offsetBracket) 
                                    + "\n,\"limit\": " + max + ", \"skip\": " + offset + " }";
        return bodyWithLimitAndSkip;        
    }

    @Override
    public String buildQueryPaging(final String sqlText, int offset, int max, String bookmark)
    {
        String bodyWithLimitAndSkip = sqlText;
        int offsetLimit = sqlText.indexOf("\"limit\"");
        if (offsetLimit > -1)
            throw new ParameterException("Query [" + sqlText
                    + "] has a \"limit\" property defined, cannot use auto paging Queryable for this query.");
        
        int offsetBracket = sqlText.lastIndexOf("}");
        if (offsetBracket >= 0)
            bodyWithLimitAndSkip = sqlText.substring(0, offsetBracket) 
                                    + "\n,\"limit\": " + max + ", \"skip\": " + offset + 
                                    (bookmark != null ? ", \"bookmark\": \"" + bookmark + "\"": "")
                                    + " }";
        return bodyWithLimitAndSkip;        
    }

    @Override
    public String buildQueryCount(String sqlText)
    {
        return sqlText;
    }
    */
}
