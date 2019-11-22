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
        addFeature(SqlFeatureFactory.newInstance(SqlFeatureSupport.PAGING_ROUNDTRIP, false));
    }
    
    @Override
    public String buildQueryPaging(final String sqlText, int offset, int max)
    {
        return sqlText;
    }
    
    @Override
    public String buildQueryPaging(final String sqlText, int offset, int max, String bookmark)
    {
        return sqlText;
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
    
    /*
    @Override
    public String getSqlPatternCount()
    {
        // using String.format argument index
        return "select count(1) from (%1$s) jkniv_ct_tmp_table";// TODO BUG when conflict alias name
    }

    private Query getQueryForPaging(SqlContext sqlContext, Queryable queryable, Sql isql)
    {
        Query queryJpa = null;
        Sql sqlCount = null;
        try
        {
            String queryName = queryable.getName() + "#count";
            sqlCount = sqlContext.getQuery(queryName);
            LOG.trace("creating count query [{}] for {}", queryName, queryable.getName());
            Queryable queryCopy = QueryFactory.of(queryName, queryable.getParams(), 0, Integer.MAX_VALUE);
            queryJpa = QueryJpaFactory.newQuery(sqlCount, emFactory.createEntityManager(), queryCopy);
        }
        catch (QueryNotFoundException e)
        {
            // but its very important remove the order clause, because cannot
            // execute this way wrapping with "select count(*) ... where exists" and performance
            String sqlWithoutOrderBy = removeOrderBy(isql.getSql(queryable.getParams()));
            //String entityName = genericType.getSimpleName();
            String sql = "select count (*) from " + isql.getReturnType() + " where exists (" + sqlWithoutOrderBy + ")";
            LOG.trace("creating counttry to count rows using dynamically query [" + sql + "]");
            Queryable queryCopy = QueryFactory.of(queryable.getName(), queryable.getParams(), 0,
                    Integer.MAX_VALUE);
            queryJpa = newQueryForCount(sql, isql.getLanguageType(), em, queryCopy,
                    isql.getParamParser());
        }
        return queryJpa;
    }
    */

}
