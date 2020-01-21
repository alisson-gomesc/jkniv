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
package net.sf.jkniv.whinstone.convertible;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;

import java.sql.ResultSet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.domain.flat.AuthorFlat;
import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.dialect.AnsiDialect;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureSupport;
import net.sf.jkniv.sqlegance.params.ParamParser;
import net.sf.jkniv.whinstone.Param;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.statement.StatementAdapter;
import net.sf.jkniv.whinstone.types.RegisterType;

public class ConvertibleTest
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
    public void whenBindValuesWithCollectionOfPojo()
    {
        Foo param1 = new Foo(Boolean.TRUE, LanguageType.CRITERIA),
            param2 = new Foo(Boolean.TRUE, LanguageType.HQL),
            param3 = new Foo(Boolean.TRUE, LanguageType.JPQL),
            param4 = new Foo(Boolean.FALSE, LanguageType.NATIVE),
            param5 = new Foo(Boolean.FALSE, LanguageType.STORED);
        Queryable query = QueryFactory.of("dummy", param1);
        given(this.sql.isSelectable()).willReturn(true);
        given(this.sql.getSqlDialect()).willReturn(this.dialect);
        given(this.sql.getParamParser()).willReturn(this.paramParser);
        given(this.sql.getSql(anyObject())).willReturn("select id, name, description from author where active = :active and language = :languageType");
        given(this.paramParser.find(anyString())).willReturn(new String[]{"active","languageType"});
        given(this.paramParser.replaceForPlaceholder(anyString(), anyObject())).willReturn("select id, name, description from author where active = ? and language = ?");
        given(this.dialect.supportsFeature(SqlFeatureSupport.BOOKMARK_QUERY)).willReturn(false);
        
        query.bind(this.sql);
        query.bind(stmt);
        assertThat(query.isBoundSql(), is(true));
        assertThat(query.isBoundParams(), is(true));
        assertThat(query.isTypeOfPojo(), is(true));
        assertThat(query.query(), is("select id, name, description from author where active = ? and language = ?"));
        assertThat(query.getParamsNames(), is(arrayWithSize(2)));
        assertThat(query.getParamsNames(), arrayContaining("active","languageType"));
        assertThat(query.values(), is(arrayWithSize(2)));
        assertThat(query.values(), arrayContaining(new Param(param1.getActive(),"T", "active",0),
                                                   new Param(param1.getLanguageType(),
                                                             param1.getLanguageType().ordinal(), "languageType",1)));
        Param[] values = query.values();
        assertThat(values[0].getValueAs().toString(), is("T"));
        assertThat(values[1].getValueAs().toString(), is(String.valueOf(LanguageType.CRITERIA.ordinal())));
        
        query = QueryFactory.of("dummy", param2);
        query.bind(this.sql);
        query.bind(stmt);
        values = query.values();
        assertThat(values[0].getValueAs().toString(), is("T"));
        assertThat(values[1].getValueAs().toString(), is(String.valueOf(LanguageType.HQL.ordinal())));

        query = QueryFactory.of("dummy", param3);
        query.bind(this.sql);
        query.bind(stmt);
        values = query.values();
        assertThat(values[0].getValueAs().toString(), is("T"));
        assertThat(values[1].getValueAs().toString(), is(String.valueOf(LanguageType.JPQL.ordinal())));

        query = QueryFactory.of("dummy", param4);
        query.bind(this.sql);
        query.bind(stmt);
        values = query.values();
        assertThat(values[0].getValueAs().toString(), is("F"));
        assertThat(values[1].getValueAs().toString(), is(String.valueOf(LanguageType.NATIVE.ordinal())));

        query = QueryFactory.of("dummy", param5);
        query.bind(this.sql);
        query.bind(stmt);
        values = query.values();
        assertThat(values[0].getValueAs().toString(), is("F"));
        assertThat(values[1].getValueAs().toString(), is(String.valueOf(LanguageType.STORED.ordinal())));

    }
    


}