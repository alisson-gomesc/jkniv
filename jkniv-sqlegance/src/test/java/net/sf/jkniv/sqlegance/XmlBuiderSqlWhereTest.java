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

public class XmlBuiderSqlWhereTest
{
    private static SqlContext sqlContext;
    
    @BeforeClass
    public static void setUp()
    {
        sqlContext = SqlContextFactory.newInstance("/repository-sql.xml");
    }

    @Test
    public void whenBuildTheQueryTheWhereClauseSuprimeFirstANDCase1()
    {
        Map<String, Object> p = new HashMap<String, Object>();
        Sql sql;
        p.put("name", "alanis");
        p.put("age", "18");
        
        sql = sqlContext.getQuery("test-where-suprime-and1");
        assertThat(sql.getSql(p).toLowerCase(),
                   is("select id, name from users where (age = #{age} or age >=18) and name = #{name}"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.HASH));
    }

    @Test
    public void whenQueryHasDynamicWhereClauseFistSentenceIsFalse()
    {
        Map<String, Object> p = new HashMap<String, Object>();
        Sql sql;
        p.put("name", "alanis");
        
        sql = sqlContext.getQuery("test-where-suprime-and1");
        assertThat(sql.getSql(p).toLowerCase(),
                   is("select id, name from users where name = #{name}"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.HASH));
    }

    @Test
    public void whenBuildTheQueryTheWhereClauseSuprimeFirstANDCase2()
    {
        Map<String, Object> p = new HashMap<String, Object>();
        Sql sql;
        p.put("name", "alanis");
        p.put("age", "18");
        
        sql = sqlContext.getQuery("test-where-suprime-and2");
        assertThat(sql.getSql(p).toLowerCase(),
                is("select id, name from users where (age = #{age} or age >=18) and name = #{name}"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.HASH));
    }

    @Test
    public void whenBuildTheQueryTheWhereClauseSuprimeFirstANDCase3()
    {
        Map<String, Object> p = new HashMap<String, Object>();
        Sql sql;
        p.put("name", "alanis");
        p.put("age", "18");
        sql = sqlContext.getQuery("test-where-suprime-and3");
        assertThat(sql.getSql(p).toLowerCase(),
                is("select id, name from users where age = #{age} and name = #{name}"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.HASH));
    }
    

    @Test
    public void whenBuildTheQueryTheWhereClauseWithChoose()
    {
        Map<String, Object> p = new HashMap<String, Object>();
        Sql sql;
        p.put("code", "123");
        p.put("codeShop", "18");
        sql = sqlContext.getQuery("test-where-with-choose-colon");
        assertThat(sql.getSql(p).replaceAll("\n", "").toLowerCase(), is("select * from customers c where trim(c.code) = trim(:code)          and trim(c.codeshop) = :codeshop"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.COLON));
    }
    
    
}
