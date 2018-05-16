/* 
 * JKNIV, SQLegance keeping queries maintainable.
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
package net.sf.jkniv.whinstone.dialect;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringStartsWith.startsWith;

import org.junit.Test;

import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.dialect.AnsiDialect;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.SqlDialectAbstractTest;

public class AnsiDialectTest extends SqlDialectAbstractTest
{
    private Queryable newQueryable(Queryable q, Sql s)
    {
        SqlDialect d = new AnsiDialect();
        s.bind(d);
        q.bind(s);
        return q;
    }

    @Test
    @Override
    public void whenPagingSqlStartWithSelect()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_SELECT, SqlType.SELECT));
        assertThat(q.query(), is(SQL_SELECT));
    }

    @Test
    @Override
    public void whenPagingSqlStartWithSelectSensitive()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_SELECT, SqlType.SELECT));
        assertThat(q.query(), is(SQL_SELECT));
    }

    @Test
    @Override
    public void whenPagingSqlStartWithSelectDistinct()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_SELECT, SqlType.SELECT));
        assertThat(q.query(), is(SQL_SELECT));
    }

    @Test
    @Override
    public void whenPagingSqlStartWithSelectForUpdate()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_SELECT_FOR_UPDDATE, SqlType.SELECT));
        assertThat(q.query(), is(SQL_SELECT_FOR_UPDDATE));
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
        assertThat(q.getDynamicSql().getSqlDialect().supportsLimit(), is(false));
    }

    @Test
    @Override
    public void whenDatabaseSupportOffset()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_SELECT, SqlType.SELECT));
        assertThat(q.getDynamicSql().getSqlDialect().supportsLimitOffset(), is(false));
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
        assertThat(q.queryCount(), startsWith(SQL_SELECT_COUNT_RESULT));
    }

    @Test
    @Override
    public void whenCountSqlStartWithSelectForUpdate()
    {
        Queryable q = newQueryable(getQueryName(),getSql(SQL_SELECT_FOR_UPDDATE, SqlType.SELECT));
        assertThat(q.queryCount(), startsWith(SQL_SELECT_COUNT_RESULT));
    }
}
