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
package net.sf.jkniv.sqlegance;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.sqlegance.params.ParamMarkType;

public class XmlBuiderSqlChooseTest
{
    private static SqlContext sqlContext;
    
    @BeforeClass
    public static void setUp()
    {
        sqlContext = SqlContextFactory.newInstance("/repository-sql.xml");
    }

	@Test
	public void whenNameParameterHasValueHeIsPartOfQuery() {
        Map<String, Object> p = new HashMap<String, Object>();
        Sql sql;
        p.put("name", "acme");
        sql = sqlContext.getQuery("test-choose-hashmark1");
        assertThat(sql.getSql(p).toLowerCase(), is("select id, name from users where id > 0 and name = #{name} and status = 1"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.HASH));
	}
	
	@Test
	public void whenDocParameterHasValueHeIsPartOfQuery() {
        Map<String, Object> p = new HashMap<String, Object>();
        Sql sql;
        p.put("doc", "acme");
        sql = sqlContext.getQuery("test-choose-hashmark1");
        assertThat(sql.getSql(p).toLowerCase(), is("select id, name from users where id > 0 and doc like #{doc}"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.HASH));
	}

    @Test
	public void whenPhoneParameterHasValueHeIsPartOfQuery() {
        Map<String, Object> p = new HashMap<String, Object>();
        Sql sql;
        p.put("phone", "acme");
        sql = sqlContext.getQuery("test-choose-hashmark1");
        assertThat(sql.getSql(p).toLowerCase(), is("select id, name from users where id > 0 and phone like #{phone}"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.HASH));
	}

	@Test
	public void whenHaventParameterValueOtherwiseNodeIsPartOfQuery() {
        Map<String, Object> p = new HashMap<String, Object>();
        Sql sql;
        sql = sqlContext.getQuery("test-choose-hashmark1");
        assertThat(sql.getSql(p).toLowerCase(), is("select id, name from users where id > 0 and status = 1"));		
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.HASH));
	}

	
}
