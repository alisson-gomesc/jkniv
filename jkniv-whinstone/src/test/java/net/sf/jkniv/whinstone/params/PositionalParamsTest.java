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
package net.sf.jkniv.whinstone.params;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;
import net.sf.jkniv.sqlegance.params.ParamParserQuestionMark;
import net.sf.jkniv.whinstone.Param;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

public class PositionalParamsTest
{
    @Rule
    public ExpectedException  catcher = ExpectedException.none();

    private StatementAdapter<?, ?> stmtAdapter;
    private Selectable             selectableMock;
    private SqlDialect             sqlDialect;
    
    @Before
    public void setUp()
    {
        this.stmtAdapter = mock(StatementAdapter.class);
        this.selectableMock = mock(Selectable.class);
        this.sqlDialect = mock(SqlDialect.class);
        given(this.selectableMock.isSelectable()).willReturn(true);
        given(this.selectableMock.hasCache()).willReturn(false);
        given(this.selectableMock.getCache()).willReturn(null);
        given(this.selectableMock.asSelectable()).willReturn(selectableMock);
        given(this.selectableMock.getSql(any())).willReturn(
                "select id, name from author where "+
                " int = ? and "+
                " long = ? and"+
                " float = ? and "+
                " double = ? and" +
                " short = ? and" +
                " char = ? and" +
                " booleanTrue = ? and" +
                " booleanFalse = ? and" +
                " date = ? and" +
                " calendar = ? and"+
                " string = ?");
        given(this.selectableMock.getParamParser()).willReturn(ParamParserQuestionMark.getInstance());
        given(this.selectableMock.getSqlType()).willReturn(SqlType.SELECT);
        given(this.selectableMock.getLanguageType()).willReturn(LanguageType.NATIVE);
        //given(this.selectableMock.getValidateType()).willReturn(validateTypeMock);
        given(this.selectableMock.getSqlDialect()).willReturn(this.sqlDialect);
        given(this.stmtAdapter.execute()).willReturn(1);
    }
    
    @Test
    public void whenInvokeOnWithClauseInArray()
    {
        Integer[] values = {1,2,3,4};
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", values);
        given(this.selectableMock.getSql(any())).willReturn("select id, name from author where id IN (:in:id)");
        Queryable queryable = QueryFactory.of("dummy", params);
        queryable.bind(this.selectableMock);
        PositionalParams auto = new PositionalParams(this.stmtAdapter, queryable);
        auto.on();
        verify(stmtAdapter, never()).execute();
        verify(stmtAdapter, times(4)).bind(any(Param.class));
    }
    
    @Test
    public void whenInvokeOn()
    {
        Queryable queryable = QueryFactory.ofArray("dummy", newParams());
        queryable.bind(this.selectableMock);
        PositionalParams auto = new PositionalParams(this.stmtAdapter, queryable);
        auto.on();
        verify(stmtAdapter, never()).execute();
        verify(stmtAdapter, times(11)).bind(any(Param.class));
    }
    
    @Test
    public void whenInvokeOnWithOneParameter()
    {
        Integer[] values = new Integer[] {Integer.valueOf("1")};
        given(this.selectableMock.getSql(any())).willReturn("select id, name from author where id = ?");
        Queryable queryable = QueryFactory.ofArray("dummy", values);
        queryable.bind(this.selectableMock);
        PositionalParams auto = new PositionalParams(this.stmtAdapter, queryable);
        auto.on();
        verify(stmtAdapter, never()).execute();
        verify(stmtAdapter).bind(new Param(values[0]));
    }

    @Test
    public void whenInvokeOnBulk()
    {
        Queryable queryable = QueryFactory.ofArray("dummy", newParams());
        queryable.bind(this.selectableMock);
        PositionalParams auto = new PositionalParams(this.stmtAdapter, queryable);
        assertThat(auto.onBulk(), is(1));
        verify(stmtAdapter).execute();
    }

    /*
    @Test
    public void whenInvokeOnWithoutPositionalParams()
    {
        catcher.expect(ParameterException.class);
        catcher.expectMessage("The parameters of sql [dummy] is based at array but the parameters is not array");
        Queryable queryable = QueryFactory.of("dummy", newParams());
        queryable.bind(this.selectableMock);
        PositionalParams auto = new PositionalParams(this.stmtAdapter, queryable);
        auto.on();
    }
    */
    
    @Test
    public void whenInvokeOnMissingPositionalParams()
    {
        catcher.expect(ParameterException.class);
        catcher.expectMessage("A query [dummy] with positional parameters needs an array exactly have the same number of parameters from query.");
        Queryable queryable = QueryFactory.ofArray("dummy", new Object[] {Long.valueOf("2"),Float.valueOf("3.1")});
        queryable.bind(this.selectableMock);
        PositionalParams auto = new PositionalParams(this.stmtAdapter, queryable);
        auto.on();
    }

    private Object[] newParams()
    {
        return new Object[] {Integer.valueOf("1"),
                Long.valueOf("2"),
                Float.valueOf("3.1"),
                Double.valueOf("4.2"),
                Short.valueOf("5"),
                'C',
                Boolean.TRUE,
                Boolean.FALSE,
                new Date(),
                Calendar.getInstance(),
                "hello"};

        /*
        return Arrays.asList(Integer.valueOf("1"),
                Long.valueOf("2"),
                Float.valueOf("3.1"),
                Double.valueOf("4.2"),
                Short.valueOf("5"),
                'C',
                Boolean.TRUE,
                Boolean.FALSE,
                new Date(),
                Calendar.getInstance(),
                "hello");
        */
    }
    
}
