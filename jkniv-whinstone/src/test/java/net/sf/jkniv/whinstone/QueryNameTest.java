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
package net.sf.jkniv.whinstone;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.domain.flat.AuthorFlat;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.dialect.AnsiDialect;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureSupport;
import net.sf.jkniv.sqlegance.params.ParamMarkType;
import net.sf.jkniv.sqlegance.params.ParamParser;
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
        this.dialect = mock(AnsiDialect.class);
        this.paramParser= mock(ParamParser.class);
    }
    
    @Test
    public void whenBookmarkIsEmptyAndRecieveValue()
    {
        Queryable q = QueryFactory.of("dummy");
        assertThat(q.getBookmark(), nullValue());
        q.setBookmark("yuhlsdmk1");
        assertThat(q.getBookmark(), is("yuhlsdmk1"));
    }
    
    @Test
    public void whenQueryIsPaging()
    {
        Queryable queryNoPage = QueryFactory.of("dummy");
        assertThat(queryNoPage.isPaging(), is(false));
        assertThat(queryNoPage.hasRowsOffset(), is(false));
        Queryable queryWithPage = QueryFactory.of("dummy", 10, 10);
        assertThat(queryWithPage.isPaging(), is(true));
        assertThat(queryWithPage.hasRowsOffset(), is(true));
        //verify(dialect).buildQueryPaging(anyString(), anyInt(), anyInt());
        //when(mockedObject.play(Matchers.<Class<T>>any())).thenReturn(object);
    }

    @Test
    public void whenQueryIsPagingWithBookmark()
    {
        given(this.dialect.supportsFeature(SqlFeatureSupport.BOOKMARK_QUERY)).willReturn(true);
        //given(this.dialect.buildQueryPaging(SqlFeatureSupport.BOOKMARK_QUERY)).willReturn(true);
        Queryable queryNoPage = QueryFactory.of("dummy");
        assertThat(queryNoPage.isPaging(), is(false));
        assertThat(queryNoPage.hasRowsOffset(), is(false));
        Queryable queryWithPage = QueryFactory.of("dummy", 10, 10);
        assertThat(queryWithPage.isPaging(), is(true));
        assertThat(queryWithPage.hasRowsOffset(), is(true));
    }

    @Test
    public void whenQueryIsCache()
    {
        Queryable query = QueryFactory.of("dummy");
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
        Queryable query = QueryFactory.of("dummy");
        assertThat(query.getReturnType().getName(), is(Map.class.getName()));

        query = QueryFactory.of("dummy", AuthorFlat.class);
        assertThat(query.getReturnType(), notNullValue());
        assertThat(query.getReturnType().getName(), is(AuthorFlat.class.getName()));
    }

    @Test
    public void whenQueryBoundParameters()
    {
        Queryable query = QueryFactory.of("dummy");
        assertThat(query.isBoundParams(), is(false));

        Queryable queryWithParams = QueryFactory.of("dummy", "id", 1);
        assertThat(queryWithParams.isBoundParams(), is(false));
    }

    @Test
    public void whenQuerySettingProperties()
    {
        Queryable query = QueryFactory.of("dummy");
        query.setMax(20);
        query.setOffset(5);
        query.setTotal(15);
        assertThat(query.getMax(), is(20));
        assertThat(query.getOffset(), is(5));
        assertThat(query.hasRowsOffset(), is(true));
        assertThat(query.getTotal(), is(15L));
    }


    @Test
    public void whenBoundPagingSql()
    {
        Queryable query = QueryFactory.of("dummy", 5, 10);
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
        Queryable query = QueryFactory.of("dummy");
        query.queryCount();
    }
    
    @Test
    public void whenQueryThrowExceptionBecauseNoBoundSqlForQuery()
    {
        catcher.expect(IllegalStateException.class);
        catcher.expectMessage("Needs to bind Sql before to call Queryable.query");
        Queryable query = QueryFactory.of("dummy");
        query.query();
    }
    
    @Test
    public void whenQueryThrowExceptionBecauseNoBoundSqlForQueryParamsNames()
    {
        catcher.expect(IllegalStateException.class);
        catcher.expectMessage("Needs to bind Sql before to call Queryable.getParamsName");
        Queryable query = QueryFactory.of("dummy");
        query.getParamsNames();
    }
 
    @Test
    public void whenQueryCheckTypeOfParameters()
    {
        assertThat(QueryFactory.of("dummy", "Bob Dylan").isTypeOfBasic(), is(true));
        assertThat(QueryFactory.of("dummy", Integer.valueOf("1")).isTypeOfBasic(), is(true));
        assertThat(QueryFactory.of("dummy", Short.valueOf("1")).isTypeOfBasic(), is(true));
        assertThat(QueryFactory.of("dummy", Long.valueOf("1")).isTypeOfBasic(), is(true));
        assertThat(QueryFactory.of("dummy", Float.valueOf("1.0")).isTypeOfBasic(), is(true));
        assertThat(QueryFactory.of("dummy", Double.valueOf("1.0")).isTypeOfBasic(), is(true));
        assertThat(QueryFactory.of("dummy", Character.valueOf('B')).isTypeOfBasic(), is(true));
        assertThat(QueryFactory.of("dummy", Boolean.TRUE).isTypeOfBasic(), is(true));
        assertThat(QueryFactory.of("dummy", new BigDecimal("1.0")).isTypeOfBasic(), is(true));
        assertThat(QueryFactory.of("dummy", new BigInteger("1")).isTypeOfBasic(), is(true));
        assertThat(QueryFactory.of("dummy", new AtomicInteger(1)).isTypeOfBasic(), is(true));
        assertThat(QueryFactory.of("dummy", new AtomicLong(1L)).isTypeOfBasic(), is(true));
        //assertThat(QueryFactory.of("dummy", new AtomicBoolean(true)).isTypeOfBasic(), is(true));
        assertThat(QueryFactory.of("dummy", new AtomicLong(1L)).isTypeOfBasic(), is(true));
        // TODO new java 8 number types
        // "java.util.concurrent.atomic.DoubleAccumulator"
        // "java.util.concurrent.atomic.DoubleAdder"
        // "java.util.concurrent.atomic.LongAccumulator"
        // "java.util.concurrent.atomic.LongAdder"
        //assertThat(QueryFactory.of("dummy", new Date()).isTypeOfBasic(), is(true));
        //assertThat(QueryFactory.of("dummy", Calendar.getInstance()).isTypeOfBasic(), is(true));

        assertThat(QueryFactory.ofArray("dummy", "A", "B", "C").isTypeOfArray(), is(true));
        assertThat(QueryFactory.ofArray("dummy", "A", "B", "C").isTypeOfArrayBasicTypes(), is(true));
        
        
        Object[] paramAsArray = new Object[1];
        Map<String, Object> map = new HashMap<String, Object>();
        paramAsArray[0] = map;
        assertThat(QueryFactory.ofArray("dummy", paramAsArray).isTypeOfArrayMap(), is(true));
        
        paramAsArray[0] = new AuthorFlat();
        assertThat(QueryFactory.ofArray("dummy", paramAsArray).isTypeOfArrayPojo(), is(true));
        
        List<Object> paramsAsList = new ArrayList<Object>();
        paramsAsList.add(new String[]{"A", "B", "C"});
        assertThat(QueryFactory.of("dummy", Arrays.asList("A", "B", "C")).isTypeOfCollection(), is(true));
        assertThat(QueryFactory.of("dummy", Arrays.asList("A", "B", "C")).isTypeOfBulk(), is(true));
        assertThat(QueryFactory.of("dummy", Arrays.asList("A", "B", "C")).isTypeOfCollectionBasicTypes(), is(true));
        assertThat(QueryFactory.of("dummy", paramsAsList).isTypeOfCollectionArray(), is(true));
        assertThat(QueryFactory.of("dummy", paramsAsList).isTypeOfBulk(), is(true));
        
        paramsAsList.clear(); paramsAsList.add(map);
        assertThat(QueryFactory.of("dummy", paramsAsList).isTypeOfCollectionMap(), is(true));
        assertThat(QueryFactory.of("dummy", paramsAsList).isTypeOfBulk(), is(true));
        paramsAsList.clear(); paramsAsList.add(new AuthorFlat());
        assertThat(QueryFactory.of("dummy", paramsAsList).isTypeOfCollectionPojo(), is(true));
        assertThat(QueryFactory.of("dummy", paramsAsList).isTypeOfBulk(), is(true));

        assertThat(QueryFactory.of("dummy", "A", "B", "C").isTypeOfMap(), is(true));
        assertThat(QueryFactory.of("dummy").isTypeOfNull(), is(true));
    }

    @Test
    public void whenQueryIteratorOverArray()
    {
        Iterator<Param> it = QueryFactory.ofArray("dummy", "A", "B", "C").iterator();
        assertThat(it.hasNext(), is(true));
        assertThat(it.next().getValue().toString(), is("A"));
        assertThat(it.hasNext(), is(true));
        assertThat(it.next().getValue().toString(), is("B"));
        assertThat(it.hasNext(), is(true));
        assertThat(it.next().getValue().toString(), is("C"));
        assertThat(it.hasNext(), is(false));
    }

    @Test
    public void whenQueryIteratorOverCollection()
    {
        Iterator<Param> it = QueryFactory.of("dummy", Arrays.asList("A", "B", "C")).iterator();
        assertThat(it.hasNext(), is(true));
        assertThat(it.next().getValue().toString(), is("A"));
        assertThat(it.hasNext(), is(true));
        assertThat(it.next().getValue().toString(), is("B"));
        assertThat(it.hasNext(), is(true));
        assertThat(it.next().getValue().toString(), is("C"));
        assertThat(it.hasNext(), is(false));
    }

    @Test
    public void whenQueryIteratorOverNull()
    {
        catcher.expect(NullPointerException.class);
        catcher.expectMessage("Cannot iterate over null reference");
        QueryFactory.of("dummy").iterator();
    }   

    @Test
    public void whenQueryIteratorOverUnsupportedType()
    {
        catcher.expect(UnsupportedOperationException.class);
        catcher.expectMessage("Cannot iterate over another type of object, just Arrays or Collections");
        QueryFactory.of("dummy", "A").iterator();
    }

    @Test
    public void whenQueryIsEquals()
    {
        Queryable q1 = QueryFactory.of("dummy");
        Queryable q2 = QueryFactory.of("dummy");
        assertThat(q1, is(q2));

        q1 = QueryFactory.of("dummy", 5, 10);
        q2 = QueryFactory.of("dummy", 5, 10);
        assertThat(q1, is(q2));
        
        AuthorFlat authorFlat = new AuthorFlat("Bob", "Whinstone in Action");
        q1 = QueryFactory.of("dummy", authorFlat, 5, 10);
        q2 = QueryFactory.of("dummy", authorFlat, 5, 10);
        assertThat(q1, is(q2));

        q1 = QueryFactory.of("dummy", 5, 11);
        q2 = QueryFactory.of("dummy", 5, 10);
        assertThat(q1, not(is(q2)));
    }

    @Test
    public void whenEquals()
    {
        Queryable q1 = QueryFactory.of("dummy1");
        Queryable q2 = QueryFactory.of("dummy2");
        assertThat(q1.equals(q2), is(false));
    }

    @Test
    public void whenBindValuesWithNoParams()
    {
        Queryable query = QueryFactory.of("dummy");
        given(this.sql.isSelectable()).willReturn(true);
        given(this.sql.getSqlDialect()).willReturn(this.dialect);
        given(this.sql.getParamParser()).willReturn(this.paramParser);
        given(this.sql.getSql(anyObject())).willReturn("select id, name, description from author");
        given(this.paramParser.find(anyString())).willReturn(new String[]{});
        given(this.paramParser.replaceForQuestionMark(anyString(), anyObject())).willReturn("select id, name, description from author");
        given(this.dialect.supportsFeature(SqlFeatureSupport.BOOKMARK_QUERY)).willReturn(false);        
        query.bind(this.sql);
        query.bind(stmt);
        assertThat(query.isBoundSql(), is(true));
        assertThat(query.isBoundParams(), is(true));
        assertThat(query.getProperty("name"), nullValue());
        assertThat(query.getProperty("id"), nullValue());
        assertThat(query.isTypeOfNull(), is(true));
        assertThat(query.getParamsNames(), is(arrayWithSize(0)));
        assertThat(query.values(), is(arrayWithSize(0)));
        assertThat(query.query(), is("select id, name, description from author"));
    }

    @Test
    public void whenBindValuesWithBasicParam()
    {
        Queryable query = QueryFactory.of("dummy", "id", 10, "name", "john");
        given(this.sql.isSelectable()).willReturn(true);
        given(this.sql.getSqlDialect()).willReturn(this.dialect);
        given(this.sql.getParamParser()).willReturn(this.paramParser);
        given(this.sql.getSql(anyObject())).willReturn("select id, name, description from author where id = :id and name = :name");
        given(this.paramParser.find(anyString())).willReturn(new String[]{"id","name"});
        given(this.paramParser.replaceForQuestionMark(anyString(), anyObject())).willReturn("select id, name, description from author where id = ? and name = ?");
        given(this.dialect.supportsFeature(SqlFeatureSupport.BOOKMARK_QUERY)).willReturn(false);
        
        query.bind(this.sql);
        query.bind(stmt);
        assertThat(query.isBoundSql(), is(true));
        assertThat(query.isBoundParams(), is(true));
        assertThat(query.isTypeOfMap(), is(true));
        assertThat(query.query(), is("select id, name, description from author where id = ? and name = ?"));
        assertThat(query.getParamsNames(), is(arrayWithSize(2)));
        assertThat(query.getParamsNames(), arrayContaining("id","name"));
        assertThat(query.values(), is(arrayWithSize(2)));
        assertThat(query.values(), arrayContaining(new Param(10,0,"id"),new Param("john", 1, "name")));
        given(this.dialect.supportsFeature(SqlFeatureSupport.BOOKMARK_QUERY)).willReturn(false);
    }
    
    @Test
    public void whenBindValuesWithBasicParamDate()
    {
        Date d = new Date();
        Queryable query = QueryFactory.of("dummy", d);
        given(this.sql.isSelectable()).willReturn(true);
        given(this.sql.getSqlDialect()).willReturn(this.dialect);
        given(this.sql.getParamParser()).willReturn(this.paramParser);
        given(this.sql.getSql(anyObject())).willReturn("select id, name, description from author where born = ?");
        given(this.paramParser.find(anyString())).willReturn(new String[]{"?"});
        given(this.paramParser.replaceForQuestionMark(anyString(), anyObject())).willReturn("select id, name, description from author where born = ?");
        given(this.dialect.supportsFeature(SqlFeatureSupport.BOOKMARK_QUERY)).willReturn(false);
        
        query.bind(this.sql);
        query.bind(stmt);
        assertThat(query.isBoundSql(), is(true));
        assertThat(query.isBoundParams(), is(true));
        assertThat(query.isTypeOfBasic(), is(true));
        assertThat(query.getParams(), instanceOf(Date.class));
        assertThat(query.query(), is("select id, name, description from author where born = ?"));
        assertThat(query.getParamsNames(), is(arrayWithSize(1)));
        assertThat(query.getParamsNames(), arrayContaining("?"));
        assertThat(query.values(), is(arrayWithSize(1)));
        assertThat(query.values(), arrayContaining(new Param(d,0,"?")));
        given(this.dialect.supportsFeature(SqlFeatureSupport.BOOKMARK_QUERY)).willReturn(false);
    }

    @Test
    public void whenBindValuesWithBasicParamCalendar()
    {
        Calendar d = Calendar.getInstance();
        Queryable query = QueryFactory.of("dummy", d);
        given(this.sql.isSelectable()).willReturn(true);
        given(this.sql.getSqlDialect()).willReturn(this.dialect);
        given(this.sql.getParamParser()).willReturn(this.paramParser);
        given(this.sql.getSql(anyObject())).willReturn("select id, name, description from author where born = ?");
        given(this.paramParser.find(anyString())).willReturn(new String[]{"?"});
        given(this.paramParser.replaceForQuestionMark(anyString(), anyObject())).willReturn("select id, name, description from author where born = ?");
        given(this.dialect.supportsFeature(SqlFeatureSupport.BOOKMARK_QUERY)).willReturn(false);
        
        query.bind(this.sql);
        query.bind(stmt);
        assertThat(query.isBoundSql(), is(true));
        assertThat(query.isBoundParams(), is(true));
        assertThat(query.isTypeOfBasic(), is(true));
        assertThat(query.getParams(), instanceOf(Calendar.class));
        assertThat(query.query(), is("select id, name, description from author where born = ?"));
        assertThat(query.getParamsNames(), is(arrayWithSize(1)));
        assertThat(query.getParamsNames(), arrayContaining("?"));
        assertThat(query.values(), is(arrayWithSize(1)));
        assertThat(query.values(), arrayContaining(new Param(d,0,"?")));
        given(this.dialect.supportsFeature(SqlFeatureSupport.BOOKMARK_QUERY)).willReturn(false);
    }

    @Test
    public void whenBindValuesWithPositionalArrayParams()
    {
        Object[] params = new Object[]{10, "john"};
        Queryable query = QueryFactory.ofArray("dummy", params);
        given(this.sql.isSelectable()).willReturn(true);
        given(this.sql.getSqlDialect()).willReturn(this.dialect);
        given(this.sql.getParamParser()).willReturn(this.paramParser);
        given(this.paramParser.getType()).willReturn(ParamMarkType.QUESTION);
        given(this.sql.getSql(anyObject())).willReturn("select id, name, description from author where id = ? and name = ?");
        given(this.paramParser.find(anyString())).willReturn(new String[]{"?","?"});
        given(this.paramParser.replaceForQuestionMark(anyString(), anyObject())).willReturn("select id, name, description from author where id = ? and name = ?");
        given(this.dialect.supportsFeature(SqlFeatureSupport.BOOKMARK_QUERY)).willReturn(false);
        
        query.bind(this.sql);
        query.bind(stmt);
        assertThat(query.isBoundSql(), is(true));
        assertThat(query.isBoundParams(), is(true));
        assertThat(query.isTypeOfArrayBasicTypes(), is(true));
        assertThat(query.query(), is("select id, name, description from author where id = ? and name = ?"));
        assertThat(query.getParamsNames(), is(arrayWithSize(2)));
        assertThat(query.getParamsNames(), arrayContaining("?","?"));
        assertThat(query.values(), is(arrayWithSize(2)));
        assertThat(query.values(), arrayContaining(new Param(10,0,"?"),new Param("john", 1, "?")));
        given(this.dialect.supportsFeature(SqlFeatureSupport.BOOKMARK_QUERY)).willReturn(false);
    }

    @Test
    public void whenBindValuesWithPositionalArrayParamsWithClauseIn()
    {
        Integer[] status = new Integer[] {1,2,3};
        Object[] params = new Object[]{10, "john", status};
        Queryable query = QueryFactory.ofArray("dummy", params);
        given(this.sql.isSelectable()).willReturn(true);
        given(this.sql.getSqlDialect()).willReturn(this.dialect);
        given(this.sql.getParamParser()).willReturn(this.paramParser);
        given(this.paramParser.getType()).willReturn(ParamMarkType.QUESTION);
        given(this.sql.getSql(anyObject())).willReturn("select id, name, description from author where id = ? and name = ? and status in (:in:status)");
        given(this.paramParser.find(anyString())).willReturn(new String[]{"?","?","in:status"});
        given(this.paramParser.replaceForQuestionMark(anyString(), anyObject())).willReturn("select id, name, description from author where id = ? and name = ? and status in (?,?,?)");
        given(this.dialect.supportsFeature(SqlFeatureSupport.BOOKMARK_QUERY)).willReturn(false);
        
        query.bind(this.sql);
        query.bind(stmt);
        assertThat(query.isBoundSql(), is(true));
        assertThat(query.isBoundParams(), is(true));
        assertThat(query.isTypeOfArrayBasicTypes(), is(true));
        assertThat(query.query(), is("select id, name, description from author where id = ? and name = ? and status in (?,?,?)"));
        assertThat(query.getParamsNames(), is(arrayWithSize(3)));
        assertThat(query.getParamsNames(), arrayContaining("?","?","in:status"));
        assertThat(query.values(), is(arrayWithSize(5)));
        assertThat(query.values(), arrayContaining(new Param(10,0,"?"),new Param("john", 1, "?"),
                new Param(status[0], 2, "?"),new Param(status[1], 3, "?"),new Param(status[2], 4, "?")   ));
        given(this.dialect.supportsFeature(SqlFeatureSupport.BOOKMARK_QUERY)).willReturn(false);
    }
    
    @Test
    public void whenInvokeToString()
    {
        assertThat(QueryFactory.of("dummy").toString(), notNullValue());
    }

}