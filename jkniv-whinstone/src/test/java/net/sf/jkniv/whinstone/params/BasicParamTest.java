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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;
import net.sf.jkniv.sqlegance.params.ParamParserColonMark;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

public class BasicParamTest
{
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
                " int = :int and "+
                " long = :long and"+
                " float = :float and "+
                " double = :double and" +
                " short = :short and" +
                " char = :char and" +
                " booleanTrue = :booleanTrue and" +
                " booleanFalse = :booleanFalse and" +
                " date = :date and" +
                " calendar = :calendar and"+
                " string = :string");
        given(this.selectableMock.getParamParser()).willReturn(ParamParserColonMark.getInstance());
        given(this.selectableMock.getSqlType()).willReturn(SqlType.SELECT);
        given(this.selectableMock.getLanguageType()).willReturn(LanguageType.NATIVE);
        //given(this.selectableMock.getValidateType()).willReturn(validateTypeMock);
        given(this.selectableMock.getSqlDialect()).willReturn(this.sqlDialect);
        
        given(this.stmtAdapter.execute()).willReturn(1);
    }
    
    @Test
    public void whenInvokeOn()
    {
        Queryable queryable = QueryFactory.of("dummy", newParams());
        queryable.bind(this.selectableMock);
        BasicParam auto = new BasicParam(this.stmtAdapter, queryable);
        auto.on();
        verify(stmtAdapter).bind(queryable.getParams());
    }
    
    @Test
    public void whenInvokeOnBulk()
    {
        NoParams noParams = new NoParams(this.stmtAdapter);
        assertThat(noParams.onBulk(), is(1));
        verify(stmtAdapter).execute();
    }
    
    private Map<String, Object> newParams()
    {
        Map<String, Object> params = new HashMap<String, Object>();
        
        params.put("int", Integer.valueOf("1"));
        params.put("long", Long.valueOf("2"));
        params.put("float", Float.valueOf("3.1"));
        params.put("double", Double.valueOf("4.2"));
        params.put("short", Short.valueOf("5"));
        params.put("char", 'C');
        params.put("booleanTrue", Boolean.TRUE);
        params.put("booleanFalse", Boolean.FALSE);
        params.put("date", new Date());
        params.put("calendar", Calendar.getInstance());
        params.put("string", "hello");
        return params;
    }
    
}
