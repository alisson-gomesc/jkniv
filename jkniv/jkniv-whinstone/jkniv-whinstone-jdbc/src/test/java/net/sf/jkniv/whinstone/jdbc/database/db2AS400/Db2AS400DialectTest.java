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
package net.sf.jkniv.whinstone.jdbc.database.db2AS400;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

import org.junit.Test;

import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.dialect.AnsiDialect;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.jdbc.dialect.DB2EnableORADialect;
import net.sf.jkniv.whinstone.jdbc.dialect.OracleDialect;
import net.sf.jkniv.whinstone.jdbc.dialect.SqlDialectAbstractTest;

public class Db2AS400DialectTest extends SqlDialectAbstractTest
{
    /** select name from author LIMIT 50 OFFSET 10 */
    private static final String SQL_SELECT_RESULT = "select * from ( select row_.*, rownum rownum_ from (  select name from author  ) row_ where rownum <= 60) where rownum_ > 10";
    /** select distinct name from author LIMIT 50 OFFSET 10 for update */
    private static final String SQL_SELECT_RESULT_SENSITIVE = "select * from ( select row_.*, rownum rownum_ from (  select distinct name from author  ) row_ where rownum <= 60) where rownum_ > 10 for update";
    /** select distinct name from author LIMIT 50 OFFSET 10 */
    private static final String SQL_SELECT_RESULT_DISTINCT = "select * from ( select row_.*, rownum rownum_ from (  select distinct name from author  ) row_ where rownum <= 60) where rownum_ > 10";
    /** select name from author LIMIT 50 OFFSET 10 for update */
    private static final String SQL_SELECT_RESULT_FOR_UPDATE= "select * from ( select row_.*, rownum rownum_ from (  select name from author  ) row_ where rownum <= 60) where rownum_ > 10 for update";

    private Queryable newQueryable(Queryable q, Sql s)
    {
        SqlDialect d = new OracleDialect();//DB2EnableORADialect();// FIXME test MUST BE DB2AS400 dialect!
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
        assertThat(q.getDynamicSql().getSqlDialect().supportsLimit(), is(true));
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
        assertThat(q.getDynamicSql().getSqlDialect().supportsRownum(), is(true));
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
    


}
