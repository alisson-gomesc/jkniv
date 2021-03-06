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
package net.sf.jkniv.whinstone.jdbc.database.derby;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

import org.junit.Test;

import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureSupport;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.jdbc.dialect.Derby10o7Dialect;
import net.sf.jkniv.whinstone.jdbc.dialect.SqlDialectAbstractTest;

public class Derby10o7DialectTest extends SqlDialectAbstractTest
{
    /** SELECT * FROM T ORDER BY I OFFSET 10 ROWS FETCH NEXT 10 ROWS ONLY */
    private static final String SQL_SELECT_RESULT                  = "select name from author OFFSET 10 ROWS FETCH NEXT 50 ROWS ONLY";
    /** SELECT DISTINCT * FROM T ORDER BY I OFFSET 10 ROWS FETCH NEXT 10 ROWS ONLY FOR UPDATE*/
    private static final String SQL_SELECT_RESULT_SENSITIVE        = "select distinct name from author OFFSET 10 ROWS FETCH NEXT 50 ROWS ONLY for update";
    /** SELECT DISTINCT * FROM T ORDER BY I OFFSET 10 ROWS FETCH NEXT 10 ROWS ONLY */
    private static final String SQL_SELECT_RESULT_DISTINCT         = "select distinct name from author OFFSET 10 ROWS FETCH NEXT 50 ROWS ONLY";
    /** SELECT * FROM T ORDER BY I OFFSET 10 ROWS FETCH NEXT 10 ROWS ONLY FOR UPDATE*/
    private static final String SQL_SELECT_RESULT_FOR_UPDATE       = "select name from author OFFSET 10 ROWS FETCH NEXT 50 ROWS ONLY for update";
    
    
    private Queryable newQueryable(Queryable q, Sql s)
    {
        SqlDialect d = new Derby10o7Dialect();
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
        assertThat(sqlResult, equalToIgnoringCase(SQL_SELECT_RESULT));
    }
    
    @Test
    @Override
    public void whenPagingSqlStartWithSelectSensitive()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_SELECT_SENSITIVE, SqlType.SELECT));
        String sqlResult = q.query();
        assertThat(sqlResult, equalToIgnoringCase(SQL_SELECT_RESULT_SENSITIVE));
    }
    
    @Test
    @Override
    public void whenPagingSqlStartWithSelectDistinct()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_SELECT_DISTINCT, SqlType.SELECT));
        String sqlResult = q.query();
        assertThat(sqlResult, equalToIgnoringCase(SQL_SELECT_RESULT_DISTINCT));
    }
    
    @Test
    @Override
    public void whenPagingSqlStartWithSelectForUpdate()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_SELECT_FOR_UPDDATE, SqlType.SELECT));
        String sqlResult = q.query();
        assertThat(sqlResult, equalToIgnoringCase(SQL_SELECT_RESULT_FOR_UPDATE));
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
        assertThat(q.getDynamicSql().getSqlDialect().supportsFeature(SqlFeatureSupport.LIMIT), is(true));
    }
    
    @Test
    @Override
    public void whenDatabaseSupportOffset()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_SELECT, SqlType.SELECT));
        assertThat(q.getDynamicSql().getSqlDialect().supportsFeature(SqlFeatureSupport.LIMIT_OFF_SET), is(true));
    }
    
    @Test
    @Override
    public void whenDatabaseSupportRownum()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_SELECT, SqlType.SELECT));
        assertThat(q.getDynamicSql().getSqlDialect().supportsFeature(SqlFeatureSupport.ROWNUM), is(true));
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
        assertThat(sqlResult, startsWith(SQL_SELECT_COUNT_RESULT ));
    }
    
}
