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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;
import net.sf.jkniv.sqlegance.params.ParamParserColonMark;
import net.sf.jkniv.sqlegance.params.ParamParserQuestionMark;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.domain.orm.Animal;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

public class PrepareParamsFactoryTest
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
    public void whenCreateNamedParams()
    {
        Map<String, Object> params = new HashMap<String, Object>();
        List<Integer> values = Arrays.asList(1,2,3,4);
        params.put("id", values);
        given(this.selectableMock.getSql(any())).willReturn("select id, name from author where id IN (:in:id)");
        Queryable queryable = QueryFactory.of("dummy", params);
        queryable.bind(this.selectableMock);
        
        AutoBindParams auto = PrepareParamsFactory.newPrepareParams(stmtAdapter, queryable);
        assertThat(auto, instanceOf(NamedParams.class));
    }
    
    @Test
    public void whenCreateNamedParamsFromCollectionOfPojo()
    {
        List<Animal> params = new ArrayList<Animal>();
        params.add(new Animal());
        given(this.selectableMock.getSql(any())).willReturn("select id, name from author where id = ?");
        Queryable queryable = QueryFactory.of("dummy", params);
        queryable.bind(this.selectableMock);
        AutoBindParams auto = PrepareParamsFactory.newPrepareParams(stmtAdapter, queryable);
        assertThat(auto, instanceOf(NamedParams.class));
    }

    @Test
    public void whenCreatePositionalParamsFromQuestionMark()
    {
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("id", "1");
        given(this.selectableMock.getSql(any())).willReturn("select id, name from author where id = ?");
        given(this.selectableMock.getParamParser()).willReturn(ParamParserQuestionMark.getInstance());
        Queryable queryable = QueryFactory.of("dummy", params);
        queryable.bind(this.selectableMock);
        AutoBindParams auto = PrepareParamsFactory.newPrepareParams(stmtAdapter, queryable);
        assertThat(auto, instanceOf(PositionalParams.class));
    }

    @Test
    public void whenCreatePositionalParams()
    {
        List<Integer> values = Arrays.asList(1,2,3,4);
        given(this.selectableMock.getSql(any())).willReturn("select id, name from author where id IN (:in:id)");
        Queryable queryable = QueryFactory.of("dummy", values);
        queryable.bind(this.selectableMock);
        AutoBindParams auto = PrepareParamsFactory.newPrepareParams(stmtAdapter, queryable);
        assertThat(auto, instanceOf(PositionalCollectionParams.class));
    }
    
    @Test
    public void whenCreatePositionalArrayParams()
    {
        Object[] values = new Object[] {1,2,3,4};
        given(this.selectableMock.getSql(any())).willReturn("select id, name from author where id IN (:in:id)");
        Queryable queryable = QueryFactory.ofArray("dummy", values);
        queryable.bind(this.selectableMock);
        AutoBindParams auto = PrepareParamsFactory.newPrepareParams(stmtAdapter, queryable);
        assertThat(auto, instanceOf(PositionalArrayParams.class));
    }
    
    @Test
    public void whenCreatePositionalArrayUsingOfArray()
    {
        given(this.selectableMock.getSql(any())).willReturn("select id, name from author where id IN (:in:id)");
        Queryable queryable = QueryFactory.ofArray("dummy", 1,2,3,4);
        queryable.bind(this.selectableMock);
        AutoBindParams auto = PrepareParamsFactory.newPrepareParams(stmtAdapter, queryable);
        assertThat(auto, instanceOf(PositionalArrayParams.class));
    }
    
    @Test
    public void whenCreateBasicParam()
    {
        given(this.selectableMock.getSql(any())).willReturn("select id, name from author where id = ?");
        Queryable queryable = QueryFactory.of("dummy", "1");
        queryable.bind(this.selectableMock);
        AutoBindParams auto = PrepareParamsFactory.newPrepareParams(stmtAdapter, queryable);
        assertThat(auto, instanceOf(BasicParam.class));
    }
    
    
    @Test
    public void whenCreateNoParam()
    {
        given(this.selectableMock.getSql(any())).willReturn("select id, name from author where id = ?");
        Queryable queryable = QueryFactory.of("dummy");
        queryable.bind(this.selectableMock);
        AutoBindParams auto = PrepareParamsFactory.newPrepareParams(stmtAdapter, queryable);
        assertThat(auto, instanceOf(NoParams.class));
    }
    
    @Test
    public void whenCreateDateParam()
    {
        given(this.selectableMock.getSql(any())).willReturn("select id, name from author where id = ?");
        Queryable queryable = QueryFactory.of("dummy", new Date());
        queryable.bind(this.selectableMock);
        AutoBindParams auto = PrepareParamsFactory.newPrepareParams(stmtAdapter, queryable);
        assertThat(auto, instanceOf(BasicParam.class));
    }
    
    @Test
    public void whenCreateCalendarParam()
    {
        given(this.selectableMock.getSql(any())).willReturn("select id, name from author where id = ?");
        Queryable queryable = QueryFactory.of("dummy", Calendar.getInstance());
        queryable.bind(this.selectableMock);
        AutoBindParams auto = PrepareParamsFactory.newPrepareParams(stmtAdapter, queryable);
        assertThat(auto, instanceOf(BasicParam.class));
    }

    @Test
    public void whenCreateNewBasicParam()
    {
        given(this.selectableMock.getSql(any())).willReturn("select id, name from author where id = ?");
        Queryable queryable = QueryFactory.of("dummy", Calendar.getInstance());
        queryable.bind(this.selectableMock);
        AutoBindParams auto = PrepareParamsFactory.newBasicParam(stmtAdapter, queryable);
        assertThat(auto, instanceOf(BasicParam.class));
    }
    
    @Test
    public void whenCreateNewNamedParams()
    {
        given(this.selectableMock.getSql(any())).willReturn("select id, name from author where id = ?");
        Queryable queryable = QueryFactory.of("dummy", Calendar.getInstance());
        queryable.bind(this.selectableMock);
        AutoBindParams auto = PrepareParamsFactory.newNamedParams(stmtAdapter, queryable);
        assertThat(auto, instanceOf(NamedParams.class));
    }
    
    @Test
    public void whenCreateNewNoParams()
    {
        given(this.selectableMock.getSql(any())).willReturn("select id, name from author where id = ?");
        Queryable queryable = QueryFactory.of("dummy", Calendar.getInstance());
        queryable.bind(this.selectableMock);
        AutoBindParams auto = PrepareParamsFactory.newNoParams(stmtAdapter);
        assertThat(auto, instanceOf(NoParams.class));
    }
    
    @Test
    public void whenCreateNewPositionalArrayParams()
    {
        given(this.selectableMock.getSql(any())).willReturn("select id, name from author where id = ?");
        Queryable queryable = QueryFactory.of("dummy", Calendar.getInstance());
        queryable.bind(this.selectableMock);
        AutoBindParams auto = PrepareParamsFactory.newPositionalArrayParams(stmtAdapter, queryable);
        assertThat(auto, instanceOf(PositionalArrayParams.class));
    }
    
    @Test
    public void whenCreateNewPositionalCollectionArrayParams()
    {
        Object[] values = new Object[] {1,2,3,4};
        given(this.selectableMock.getSql(any())).willReturn("select id, name from author where id = ?");
        Queryable queryable = QueryFactory.of("dummy", values);
        queryable.bind(this.selectableMock);
        AutoBindParams auto = PrepareParamsFactory.newPositionalCollectionArrayParams(stmtAdapter, queryable);
        assertThat(auto, instanceOf(PositionalCollectionArrayParams.class));
    }

    @Test
    public void whenCreateNewPositionalCollectionMapParams()
    {
        Object[] values = new Object[] {1,2,3,4};
        given(this.selectableMock.getSql(any())).willReturn("select id, name from author where id = ?");
        Queryable queryable = QueryFactory.of("dummy", values);
        queryable.bind(this.selectableMock);
        AutoBindParams auto = PrepareParamsFactory.newPositionalCollectionMapParams(stmtAdapter, queryable);
        assertThat(auto, instanceOf(PositionalCollectionMapParams.class));
    }

    @Test
    public void whenCreateNewPositionalCollectionParams()
    {
        given(this.selectableMock.getSql(any())).willReturn("select id, name from author where id = ?");
        Queryable queryable = QueryFactory.of("dummy", Calendar.getInstance());
        queryable.bind(this.selectableMock);
        AutoBindParams auto = PrepareParamsFactory.newPositionalCollectionParams(stmtAdapter, queryable);
        assertThat(auto, instanceOf(PositionalCollectionParams.class));
    }

    @Test
    public void whenCreateNewPositionalCollectionPojoParams()
    {
        Object[] values = new Object[] {1,2,3,4};
        given(this.selectableMock.getSql(any())).willReturn("select id, name from author where id = ?");
        Queryable queryable = QueryFactory.of("dummy", values);
        queryable.bind(this.selectableMock);
        AutoBindParams auto = PrepareParamsFactory.newPositionalCollectionPojoParams(stmtAdapter, queryable);
        assertThat(auto, instanceOf(PositionalCollectionPojoParams.class));
    }

    @Test
    public void whenCreateNewPositionalParams()
    {
        given(this.selectableMock.getSql(any())).willReturn("select id, name from author where id = ?");
        Queryable queryable = QueryFactory.of("dummy", Calendar.getInstance());
        queryable.bind(this.selectableMock);
        AutoBindParams auto = PrepareParamsFactory.newPositionalParams(stmtAdapter, queryable);
        assertThat(auto, instanceOf(PositionalParams.class));
    }
}
