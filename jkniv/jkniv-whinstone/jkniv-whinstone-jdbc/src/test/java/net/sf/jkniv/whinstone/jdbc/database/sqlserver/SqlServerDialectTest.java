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
package net.sf.jkniv.whinstone.jdbc.database.sqlserver;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

import org.junit.Ignore;
import org.junit.Test;

import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;
import net.sf.jkniv.whinstone.jdbc.dialect.SqlDialectAbstractTest;
import net.sf.jkniv.whinstone.jdbc.dialect.SqlServerDialect;

@Ignore("no installed in local machine")
public class SqlServerDialectTest extends SqlDialectAbstractTest
{
    /** select name from author LIMIT 50 OFFSET 10 */
    private static final String SQL_SELECT_RESULT = "select name from author";
    /** select distinct name from author LIMIT 50 OFFSET 10 for update */
    private static final String SQL_SELECT_RESULT_SENSITIVE = "Select DIStinct name from author FOR Update";
    /** select distinct name from author LIMIT 50 OFFSET 10 */
    private static final String SQL_SELECT_RESULT_DISTINCT = "select distinct name from author";
    /** select name from author LIMIT 50 OFFSET 10 for update */
    private static final String SQL_SELECT_RESULT_FOR_UPDATE= "select name from author for update";

    private Queryable newQueryable(Queryable q, Sql s)
    {
        SqlDialect d = new SqlServerDialect();
        s.bind(d);
        q.bind(s);
        return q;
    }
    
    
    @Test
    @Override
    public void whenPagingSqlStartWithSelect()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_SELECT, SqlType.SELECT));
        String sqlResult = q.query();
        assertThat(sqlResult, equalToIgnoringCase(enclosePagingQuery(SQL_SELECT)));
    }
    
    
    @Test
    @Override
    public void whenPagingSqlStartWithSelectSensitive()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_SELECT_SENSITIVE, SqlType.SELECT));
        String sqlResult = q.query();
        assertThat(sqlResult, equalToIgnoringCase(enclosePagingQuery(SQL_SELECT_SENSITIVE)));
    }
    
    
    @Test
    @Override
    public void whenPagingSqlStartWithSelectDistinct()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_SELECT_DISTINCT, SqlType.SELECT));
        String sqlResult = q.query();
        assertThat(sqlResult, equalToIgnoringCase(enclosePagingQuery(SQL_SELECT_DISTINCT)));
    }
    
    
    @Test
    @Override
    public void whenPagingSqlStartWithSelectForUpdate()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_SELECT_FOR_UPDDATE, SqlType.SELECT));
        String sqlResult = q.query();
        assertThat(sqlResult, equalToIgnoringCase(enclosePagingQuery(SQL_SELECT_FOR_UPDDATE)));
    }
    
    @Test
    @Override
    public void whenPagingSqlStartWithDelete()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_DELETE, SqlType.DELETE));
        assertThat(q.query(), is(SQL_DELETE));
    }   
    
    
    @Test
    @Override
    public void whenPagingSqlStartWithUpdate()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_UPDATE, SqlType.UPDATE));
        assertThat(q.query(), is(SQL_UPDATE));
    }
    
    
    @Test
    @Override
    public void whenPagingSqlStartWithInsert()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_INSERT, SqlType.INSERT));
        assertThat(q.query(), is(SQL_INSERT));
    }
    
    
    @Test
    @Override
    public void whenDatabaseSupportLimit()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_SELECT, SqlType.SELECT));
        assertThat(q.getDynamicSql().getSqlDialect().supportsLimit(), is(true));
    }
    
    @Test
    //@Override TODO test me with another database
    public void whenDatabaseSupportLimitWithOrderBy()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_SELECT_SENSITIVE_ORDERBY, SqlType.SELECT));
        String sqlResult = q.query();
        assertThat(sqlResult, equalToIgnoringCase(enclosePagingQueryOrderBy(SQL_SELECT_SENSITIVE_ORDERBY)));

    }    
    
    @Test
    @Override
    public void whenDatabaseSupportOffset()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_SELECT, SqlType.SELECT));
        assertThat(q.getDynamicSql().getSqlDialect().supportsLimitOffset(), is(true));
    }
    
    @Test
    @Override
    public void whenDatabaseSupportRownum()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_SELECT, SqlType.SELECT));
        assertThat(q.getDynamicSql().getSqlDialect().supportsRownum(), is(false));
    }
    
    @Test
    @Override
    public void whenCountSqlStartWithSelect()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_SELECT, SqlType.SELECT));
        String sqlResult = q.queryCount();
        assertThat(sqlResult, startsWith(SQL_SELECT_COUNT_RESULT));
    }
    
    @Test
    @Override
    public void whenCountSqlStartWithSelectForUpdate()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_SELECT_FOR_UPDDATE, SqlType.SELECT));
        String sqlResult = q.queryCount();
        assertThat(sqlResult, startsWith(SQL_SELECT_COUNT_RESULT));
    }
    

    private String enclosePagingQuery(String sql) {
        final StringBuilder pagingSelect = new StringBuilder(100);
        pagingSelect.append(" WITH query AS (");
        pagingSelect.append("  SELECT inner_query.*");
        pagingSelect.append("      , ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as _jkniv_rownum_");
        pagingSelect.append("  FROM ( "+sql+" ) inner_query");
        pagingSelect.append(" )");
        pagingSelect.append(" SELECT * FROM query WHERE _jkniv_rownum_ > 10 AND _jkniv_rownum_ <= 50 + 10");
        return pagingSelect.toString();
    }


    private String enclosePagingQueryOrderBy(String sql) {
        final StringBuilder pagingSelect = new StringBuilder(100);
        pagingSelect.append(" WITH query AS (");
        pagingSelect.append("  SELECT inner_query.*");
        pagingSelect.append("      , ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as _jkniv_rownum_");
        pagingSelect.append("  FROM ( "+sql.replaceAll("(?i)select distinct", "select distinct TOP(10+50)")+" ) inner_query");
        pagingSelect.append(" )");
        pagingSelect.append(" SELECT * FROM query WHERE _jkniv_rownum_ > 10 AND _jkniv_rownum_ <= 50 + 10");
        return pagingSelect.toString();
    }

}
