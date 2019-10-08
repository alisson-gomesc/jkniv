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

public class XmlBuiderSqlOrderTest
{
    private static SqlContext sqlContext;
    
    @BeforeClass
    public static void setUp()
    {
        sqlContext = SqlContextFactory.newInstance("/repository-sql.xml");
    }
   
    @Test
    public void whenBuildTheQueryTheOrderOfElementsIsWarrantedSelectElements()
    {
        Map<String, Object> p = new HashMap<String, Object>();
        Sql sql;
        p.put("name", "acme");
        p.put("cel", "99880066");
        p.put("phone", "88885544");
        p.put("doc", "00000000");
        p.put("age", "18");
        
        sql = sqlContext.getQuery("test-order1-select");
        assertThat(sql.getSql(p).toLowerCase(),
                   is("select id, name from users where id > 0 and name = #{name} and cel = #{cel} and doc like #{doc} and age = #{age}"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.HASH));
    }
    
    @Test 
    public void whenBuildTheQueryTheOrderOfElementsIsWarrantedWhereElementsCase1()
    {
        Map<String, Object> p = new HashMap<String, Object>();
        Sql sql;
        p.put("name", "acme");
        p.put("cel", "99880066");
        p.put("phone", "88885544");
        p.put("age", "18");
        
        sql = sqlContext.getQuery("test-order2-where");
        assertThat(sql.getSql(p).toLowerCase(),
                  is("select id, name from users where name = #{name} and cel = #{cel} and phone like #{phone} and age = #{age}"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.HASH));
    }
    
    @Test 
    public void whenBuildTheQueryTheOrderOfElementsIsWarrantedSelectElementsCase2()
    {
        Map<String, Object> p = new HashMap<String, Object>();
        Sql sql;
        p.put("name", "acme");
        p.put("cel", "99880066");
        p.put("age", "18");
        
        sql = sqlContext.getQuery("test-order2-where");
        assertThat(sql.getSql(p).toLowerCase(),
                   is("select id, name from users where name = #{name} and cel = #{cel} and status = 1 and age = #{age}"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.HASH));
    }
    
    @Test
    public void whenBuildTheQueryTheOrderOfElementsIsWarrantedSelectElementsCase3()
    {
        Map<String, Object> p = new HashMap<String, Object>();
        Sql sql;
        p.put("doc", "99880066");
        p.put("age", "18");
        
        sql = sqlContext.getQuery("test-order2-where");
        assertThat(sql.getSql(p).toLowerCase(),
                is("select id, name from users where doc like #{doc} and age = #{age}"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.HASH));
    }
}
