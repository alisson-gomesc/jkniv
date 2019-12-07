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
package net.sf.jkniv.sqlegance.parser;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import net.sf.jkniv.sqlegance.params.ParamMarkType;
import net.sf.jkniv.sqlegance.params.ParamParser;
import net.sf.jkniv.sqlegance.params.ParamParserColonMark;
import net.sf.jkniv.sqlegance.params.ParamParserFactory;

public class ParamParserDollarMarkInClaseTest
{
//whenSqlIsNamedParameterReplaceNamesTwoDotsForQuestionMarkWithToDateFnAndINclause    
//    String sql         = "select id, name from Roles where id = :id and name in (:in:names) and dt = to_date(:dt,'YYYY-MM-DD HH24:MI:SS')";
//    String sqlExpected = "select id, name from Roles where id = ?   and name = ?            and dt = to_date(?  ,'YYYY-MM-DD HH24:MI:SS')";

    @Test
    public void whenReplaceWithDollar()
    {
        String s = "may all money k";
        String newS = s.replaceAll("k",  "\\$");
        assertThat(newS, is("may all money $"));
    }
    
    @Test
    public void whenParserParamsUseDollarMarkUsingSimpleINclause()
    {
        String[] params = {"A","B","C"};
        String sql                 = "select * from Roles where name IN (:in:names)";
        String sqlExpected         = "select * from Roles where name IN ($1,$2,$3)";
        ParamParser parser = ParamParserFactory.getInstance(ParamMarkType.DOLLAR);
        
        String[] names = parser.find(sql);
        assertThat(names.length, is(1));
        assertThat(names[0], is("in:names"));
        
        String newSql = parser.replaceForPlaceholder(sql);
        assertThat(newSql, is(sql));
        
        newSql = parser.replaceForPlaceholder(sql, params);
        assertThat(newSql, is(sqlExpected));
    }

    @Test
    public void whenParserParamsUseDollarMarkUsingDoubleINclause()
    {
        String[] params = {"A","B","C"};
        String sql                 = "select * from Roles where name IN (:in:names) or surname IN (:in:names)";
        String sqlExpected         = "select * from Roles where name IN ($1,$2,$3) or surname IN ($1,$2,$3)";
        ParamParser parser = ParamParserFactory.getInstance(ParamMarkType.DOLLAR);
        
        String[] names = parser.find(sql);
        assertThat(names.length, is(2));
        assertThat(names[0], is("in:names"));
        assertThat(names[1], is("in:names"));
        
        String newSql = parser.replaceForPlaceholder(sql, params);
        assertThat(newSql, is(sqlExpected));
    }
    
    @Test
    public void whenParserParamsUseDollarMarkINclauseWithQuestion()
    {
        String[] params = {"A","B","C"};
        String sql                 = "select * from Roles where name IN (:in:names) and id = $id";
        String sqlExpected         = "select * from Roles where name IN ($1,$2,$3) and id = $4 ";
        ParamParser parser = ParamParserFactory.getInstance(ParamMarkType.DOLLAR);
        
        String[] names = parser.find(sql);
        assertThat(names.length, is(2));
        assertThat(names[0], is("in:names"));
        assertThat(names[1], is("id"));
        
        String newSql = parser.replaceForPlaceholder(sql, params);
        assertThat(newSql, is(sqlExpected));
    }

    @Test
    public void whenParserParamsUseDollarMarkSingleParam()
    {
        String[] params = {"A"};
        String sql                 = "select * from Roles where name = $name";
        String sqlExpected         = "select * from Roles where name = $1   ";
        ParamParser parser = ParamParserFactory.getInstance(ParamMarkType.DOLLAR);
        
        String[] names = parser.find(sql);
        assertThat(names.length, is(1));
        assertThat(names[0], is("name"));
        
        String newSql = parser.replaceForPlaceholder(sql, params);
        assertThat(newSql, is(sqlExpected));
    }

    @Test
    public void whenParserParamsUseDollarMarkThreeParams()
    {
        String[] params = {"A","B","C"};
        String sql                 = "select * from Roles where name = $name and role = $role and access = $access";
        String sqlExpected         = "select * from Roles where name = $1    and role = $2    and access = $3     ";
        ParamParser parser = ParamParserFactory.getInstance(ParamMarkType.DOLLAR);
        
        String[] names = parser.find(sql);
        assertThat(names.length, is(3));
        assertThat(names[0], is("name"));
        assertThat(names[1], is("role"));
        assertThat(names[2], is("access"));
        
        String newSql = parser.replaceForPlaceholder(sql, params);
        assertThat(newSql, is(sqlExpected));
    }
    
    @Test
    public void whenParserParamsUseDollarMarkNumeralParams()
    {
        String[] params = {"A","B","C"};
        String sql                 = "select * from Roles where name = $1 and role = $2 and access = $3";
        String sqlExpected         = "select * from Roles where name = $1 and role = $2 and access = $3";
        ParamParser parser = ParamParserFactory.getInstance(ParamMarkType.DOLLAR);
        
        String[] names = parser.find(sql);
        assertThat(names.length, is(3));
        assertThat(names[0], is("1"));
        assertThat(names[1], is("2"));
        assertThat(names[2], is("3"));
        
        String newSql = parser.replaceForPlaceholder(sql, params);
        assertThat(newSql, is(sqlExpected));
    }

}
