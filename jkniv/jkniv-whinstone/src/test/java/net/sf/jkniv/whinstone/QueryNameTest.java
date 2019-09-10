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
package net.sf.jkniv.whinstone;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.cache.NoCache;
import net.sf.jkniv.domain.flat.AuthorFlat;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.dialect.AnsiDialect;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureSupport;
import net.sf.jkniv.sqlegance.params.ParamParser;
import net.sf.jkniv.whinstone.params.ParameterNotFoundException;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

@SuppressWarnings("unchecked")
public class QueryNameTest
{
    @Rule
    public ExpectedException  catcher = ExpectedException.none();

    private StatementAdapter<AuthorFlat, ResultSet> stmt;
    private Sql sql;
    private AnsiDialect dialect;
    private ParamParser paramParser;
    
    @Before
    public void setUp()
    {
        this.stmt = mock(StatementAdapter.class);
        this.sql = mock(Selectable.class);
        this.dialect= mock(AnsiDialect.class);
        this.paramParser= mock(ParamParser.class);
    }
    
    @Test
    public void whenBookmarkIsEmptyAndRecieveValue()
    {
        Queryable q = QueryFactory.of("query-name");
        assertThat(q.getBookmark(), nullValue());
        q.setBookmark("yuhlsdmk1");
        assertThat(q.getBookmark(), is("yuhlsdmk1"));
    }
    
    @Test
    public void whenQueryIsPage()
    {
        Queryable queryNoPage = QueryFactory.of("query-name");
        assertThat(queryNoPage.isPaging(), is(false));
        Queryable queryWithPage = QueryFactory.of("query-name", 10, 10);
        assertThat(queryWithPage.isPaging(), is(true));
    }

    @Test
    public void whenQueryIsCache()
    {
        Queryable query = QueryFactory.of("query-name");
        assertThat(query.isCached(), is(false));
        assertThat(query.isCacheIgnore(), is(false));
        query.cached();
        assertThat(query.isCached(), is(true));
        assertThat(query.isCacheIgnore(), is(false));
        
        query.cacheIgnore();
        assertThat(query.isCacheIgnore(), is(true));
    }

    @Test
    public void whenQueryReturnType()
    {
        Queryable query = QueryFactory.of("query-name");
        assertThat(query.getReturnType(), nullValue());

        query = QueryFactory.of("query-name", AuthorFlat.class);
        assertThat(query.getReturnType(), notNullValue());
        assertThat(query.getReturnType().getName(), is(AuthorFlat.class.getName()));
    }

    @Test
    public void whenQueryBoundParameters()
    {
        Queryable query = QueryFactory.of("query-name");
        assertThat(query.isBoundParams(), is(false));

        Queryable queryWithParams = QueryFactory.of("query-name", "id", 1);
        assertThat(queryWithParams.isBoundParams(), is(false));
    }

    @Test
    public void whenQuerySettingProperties()
    {
        Queryable query = QueryFactory.of("query-name");
        query.setMax(20);
        query.setOffset(5);
        query.setTotal(15);
        assertThat(query.getMax(), is(20));
        assertThat(query.getOffset(), is(5));
        assertThat(query.getTotal(), is(15L));
    }


    @Test
    public void whenBoundPagingSql()
    {
        Queryable query = QueryFactory.of("query-name", 5, 10);
        given(this.sql.isSelectable()).willReturn(true);
        given(this.sql.getSqlDialect()).willReturn(this.dialect);
        given(this.sql.getParamParser()).willReturn(this.paramParser);
        given(this.sql.getSql(anyObject())).willReturn("select id, name, description from author where id = :id and name = :name");
        given(this.paramParser.find(anyString())).willReturn(new String[]{"id","name"});
        given(this.paramParser.replaceForQuestionMark(anyString(), anyObject())).willReturn("select id, name, description from author where id = ? and name = ?");
        given(this.dialect.supportsFeature(SqlFeatureSupport.BOOKMARK_QUERY)).willReturn(false);
        
        query.bind(this.sql);
        assertThat(query.isBoundSql(), is(true));
        assertThat(query.isBoundParams(), is(false));
        assertThat(query.query(), is("select id, name, description from author where id = ? and name = ?"));
    }

    @Test
    public void whenQueryThrowExceptionBecauseNoBoundSqlForQueryCount()
    {
        catcher.expect(IllegalStateException.class);
        catcher.expectMessage("Needs to bind Sql before to call Queryable.queryCount");
        Queryable query = QueryFactory.of("query-name");
        query.queryCount();
    }
    
    @Test
    public void whenQueryThrowExceptionBecauseNoBoundSqlForQuery()
    {
        catcher.expect(IllegalStateException.class);
        catcher.expectMessage("Needs to bind Sql before to call Queryable.query");
        Queryable query = QueryFactory.of("query-name");
        query.query();
    }
    
    @Test
    public void whenQueryThrowExceptionBecauseNoBoundSqlForQueryParamsNames()
    {
        catcher.expect(IllegalStateException.class);
        catcher.expectMessage("Needs to bind Sql before to call Queryable.getParamsName");
        Queryable query = QueryFactory.of("query-name");
        query.getParamsNames();
    }
    
    

}