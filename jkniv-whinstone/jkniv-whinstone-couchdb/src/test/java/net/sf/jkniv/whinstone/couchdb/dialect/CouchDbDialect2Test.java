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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.sqlegance.builder.xml.SqlTag;
import net.sf.jkniv.sqlegance.builder.xml.TagFactory;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureSupport;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.params.ParameterException;

public class CouchDbDialect2Test
{
    private static SqlContext sqlContext;
    @Rule
    public ExpectedException  catcher = ExpectedException.none();
    
    @BeforeClass
    public static void setUp()
    {
        sqlContext = SqlContextFactory.newInstance("/repository-sql-find-page.xml");
    }
    
    @Test
    public void whenCreateQueryAlrightHasLimit()
    {
        catcher.expect(ParameterException.class);
        //catcher.expectMessage(".");
        Sql sql = sqlContext.getQuery("authors-page-override");
        String sqlText = sql.getSql();
        CouchDbDialect2o0 dialect = new CouchDbDialect2o0();
        dialect.buildQueryPaging(sqlText, 0, 10);
    }

    @Test
    public void whenBuildQueryPage()
    {
        Sql sql = sqlContext.getQuery("authors-page");
        String sqlText = sql.getSql();
        CouchDbDialect2o0 dialect = new CouchDbDialect2o0();
        String sqlPage = dialect.buildQueryPaging(sqlText, 0, 10);
        
        assertThat(sqlPage, is("{\n     \"selector\": {\"nationality\": {\"$in\": :nations}}\n    \n,\"limit\": 10, \"skip\": 0 }") );
    }

    @Test
    public void whenBuildQueryPageWithBookmark()
    {
        Sql sql = sqlContext.getQuery("authors-page");
        String sqlText = sql.getSql();
        CouchDbDialect2o0 dialect = new CouchDbDialect2o0();
        String sqlPage = dialect.buildQueryPaging(sqlText, 0, 10, "AzDgfhIKlo");
        
        assertThat(sqlPage, is("{\n     \"selector\": {\"nationality\": {\"$in\": :nations}}\n    \n,\"limit\": 10, \"skip\": 0, \"bookmark\": \"AzDgfhIKlo\" }") );
    }
    
    @Test
    public void whenBuildQueryCount()
    {
        Sql sql = sqlContext.getQuery("authors-page");
        Queryable q = newQueryable(getQueryName(), sql);
        String sqlResult = q.queryCount();
        assertThat(sqlResult, nullValue());
    }

    
    private Queryable newQueryable(Queryable q, Sql s)
    {
        SqlDialect d = new CouchDbDialect2o0();
        s.bind(d);
        q.bind(s);
        return q;
    }
    
    protected Queryable getQueryName()
    {
        Queryable q = QueryFactory.of("sql-test", Collections.emptyMap(), 10, 50);
        return q;
    }

    protected Sql getSql(String query, SqlType type)
    {
        SqlTag tag = null;
        if (type == SqlType.SELECT)
            tag = (SqlTag) TagFactory.newSelect("id", LanguageType.NATIVE);
        else if (type == SqlType.INSERT)
            tag = (SqlTag) TagFactory.newInsert("id", LanguageType.NATIVE);
        else if (type == SqlType.DELETE)
            tag = (SqlTag) TagFactory.newDelete("id", LanguageType.NATIVE);
        else if (type == SqlType.UPDATE)
            tag = (SqlTag) TagFactory.newUpdate("id", LanguageType.NATIVE);
        
        tag.addTag(query);
        return tag;
    }
    
    @Test
    public void whenDatabaseSupportLimit()
    {
        Sql sql = sqlContext.getQuery("authors-page");
        Queryable q = newQueryable(getQueryName(), sql);
        assertThat(q.getDynamicSql().getSqlDialect().supportsFeature(SqlFeatureSupport.LIMIT), is(true));
    }
    
    @Test
    public void whenDatabaseSupportOffset()
    {
        Sql sql = sqlContext.getQuery("authors-page");
        Queryable q = newQueryable(getQueryName(), sql);
        assertThat(q.getDynamicSql().getSqlDialect().supportsFeature(SqlFeatureSupport.LIMIT_OFF_SET), is(true));
    }
    
    @Test
    public void whenDatabaseSupportRownum()
    {
        Sql sql = sqlContext.getQuery("authors-page");
        Queryable q = newQueryable(getQueryName(), sql);
        assertThat(q.getDynamicSql().getSqlDialect().supportsFeature(SqlFeatureSupport.ROWNUM), is(false));
    }

    @Test
    public void whenDatabaseSupportConnHoldability()
    {
        Sql sql = sqlContext.getQuery("authors-page");
        Queryable q = newQueryable(getQueryName(), sql);
        assertThat(q.getDynamicSql().getSqlDialect().supportsFeature(SqlFeatureSupport.CONN_HOLDABILITY), is(false));
    }

    @Test
    public void whenDatabaseSupportStmtHoldability()
    {
        Sql sql = sqlContext.getQuery("authors-page");
        Queryable q = newQueryable(getQueryName(), sql);
        assertThat(q.getDynamicSql().getSqlDialect().supportsFeature(SqlFeatureSupport.STMT_HOLDABILITY), is(false));
    }

    @Test
    public void whenDatabaseSupportPagingRoundtrip()
    {
        Sql sql = sqlContext.getQuery("authors-page");
        Queryable q = newQueryable(getQueryName(), sql);
        assertThat(q.getDynamicSql().getSqlDialect().supportsFeature(SqlFeatureSupport.PAGING_ROUNDTRIP), is(false));
    }

    @Test
    public void whenDatabaseSupportBookmarkQuery()
    {
        Sql sql = sqlContext.getQuery("authors-page");
        Queryable q = newQueryable(getQueryName(), sql);
        assertThat(q.getDynamicSql().getSqlDialect().supportsFeature(SqlFeatureSupport.BOOKMARK_QUERY), is(false));
    }

}
