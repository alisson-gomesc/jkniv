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

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.sqlegance.builder.xml.ProcedureTag;
import net.sf.jkniv.sqlegance.domain.orm.Author;
import net.sf.jkniv.sqlegance.domain.orm.FooDomain;
import net.sf.jkniv.sqlegance.params.ParamMarkType;

public class XmlBuilderSqlTest
{    
    private static SqlContext sqlContext;
    
    @BeforeClass
    public static void setUp()
    {
        sqlContext = SqlContextFactory.newInstance("/repository-sql.xml");
    }

    @Test @Ignore("getFiles method is deprecated and removed")
    public void whenReadXmlFilesYourStatusesAreFlagged()
    {
        /*
        SqlFile f1 = sqlContext.getFiles().get(0);
        SqlFile f2 = sqlContext.getFiles().get(1);
        SqlFile f3 = sqlContext.getFiles().get(2);
        
        Assert.assertEquals("/SQL1Test.xml", f1.getName());
        Assert.assertEquals("/SQL2Test.xml", f2.getName());
        Assert.assertEquals("/SQL3Test.xml", f3.getName());
        Assert.assertEquals(XMLFileStatus.PROCESSED, f1.getStatus());
        Assert.assertEquals(XMLFileStatus.PROCESSED, f2.getStatus());
        Assert.assertEquals(XMLFileStatus.PROCESSED, f3.getStatus());
        */
    }
    
    @Test
    public void selectClauseTest()
    {
        Map<String, Object> params1 = new HashMap<String, Object>();
        Sql sql;
        params1.put("name", "acme");
        params1.put("age", 6);
        
        sql = sqlContext.getQuery("selectUsers");
        Assert.assertEquals("select id, name from Users".toLowerCase(), sql.getSql().toLowerCase());
        Assert.assertEquals("select id, name from Users where id = 1".toLowerCase(), sql.getSql(params1).toLowerCase());
        Assert.assertEquals(LanguageType.JPQL, sql.getLanguageType());
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.NO_MARK));
        
        sql = sqlContext.getQuery("selectGroups");
        Assert.assertEquals("select id, name from Groups".toLowerCase(), sql.getSql().toLowerCase());
        Assert.assertEquals(LanguageType.JPQL, sql.getLanguageType());
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.HASH));
        
        sql = sqlContext.getQuery("selectCompanies");
        assertThat(sql.getSql().toLowerCase(), is("select id, name from companies"));
        assertThat(sql.getLanguageType(),is(LanguageType.NATIVE));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.NO_MARK));
        
        sql = sqlContext.getQuery("selectRoles");
        assertThat(sql.getSql().toLowerCase(), is("select id, name from roles"));
        assertThat(sql.getLanguageType(), is(LanguageType.NATIVE));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.NO_MARK));
        
        sql = sqlContext.getQuery("selectUsersHaving");
        assertThat(sql.getSql(params1).toLowerCase(), 
                    is("select id, name from users where name = 1 having age > 5"));
        assertThat(sql.getLanguageType(),is(LanguageType.NATIVE));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.NO_MARK));
    }
    
    @Test
    public void whereClauseTest()
    {
        Author author = new Author();
        author.setName("Bob");
        FooDomain d1 = new FooDomain("name", 0, "state", "title", author), d2 = new FooDomain("name", 0, null, "title", author), d3 = new FooDomain("name", 0,
                "state", null, author), d4 = new FooDomain("name", 0, "state", "title", null), d5 = new FooDomain(null, 5, null, null, author);
        
        Sql sql = sqlContext.getQuery("selectGroups");
        assertThat(sql.getSql().toLowerCase(), is("select id, name from groups"));
        assertThat(sql.getLanguageType(), is(LanguageType.JPQL));
        assertThat(sql.getSql(d1).toLowerCase(), 
                    is("select id, name from groups where state = #{state} and title like #{title} or author_name like #{author.name}"));
        assertThat(sql.getSql(d2).toLowerCase(),
                    is("select id, name from groups where title like #{title} or author_name like #{author.name}"));
        assertThat(sql.getSql(d3).toLowerCase(), 
                    is("select id, name from groups where state = #{state} or author_name like #{author.name}"));
        assertThat(sql.getSql(d4).toLowerCase(), 
                    is("select id, name from groups where state = #{state} and title like #{title}"));
        assertThat(sql.getSql(d5).toLowerCase(),
                    is("select id, name from groups where author_name like #{author.name} or (age >= 5 and age <= 15)"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.HASH));
    }
    
    @Test
    public void insertClauseTest()
    {
        Author author1 = new Author(), author2 = new Author(), author3 = new Author();
        author1.setName("Bob");
        author1.setUsername("Bob");
        author1.setPassword("pass");
        author1.setEmail("mail");
        author1.setBio("bio");
        author2.setName("Bob");
        author2.setUsername("Bob");
        author2.setPassword("pass");
        author2.setEmail("mail");
        author3.setName("Bob");
        author3.setUsername("Bob");
        author3.setPassword("pass");
        
        Sql sql1 = sqlContext.getQuery("insertAuthor1");
        Sql sql2 = sqlContext.getQuery("insertAuthor2");
        
        assertThat(sql1.getSql().toLowerCase(),
                    is("insert into author (username,password,email,bio) values (#{username},#{password},#{email},#{bio})"));
        assertThat(sql1.getLanguageType(), is(LanguageType.NATIVE));
        assertThat(sql2.getSql(author1).toLowerCase(),
                    is("insert into author (username,password,email,bio) values (#{id},#{username},#{password},#{email},#{bio})"));
        assertThat(sql2.getSql(author2).toLowerCase(), 
                    is("insert into author (username,password,email) values (#{id},#{username},#{password},#{email})"));
        assertThat(sql2.getSql(author3).toLowerCase(), 
                    is("insert into author (username,password) values (#{id},#{username})"));
        assertThat(sql1.getParamParser().getType(), is(ParamMarkType.HASH));
        assertThat(sql2.getParamParser().getType(), is(ParamMarkType.HASH));
    }
    
    @Test
    public void updateClauseTest()
    {
        Author author = new Author();
        author.setName("Bob");
        author.setUsername("Bob");
        author.setPassword("pass");
        author.setBio("bio");
        Sql sql1 = sqlContext.getQuery("updateAuthor1");
        Sql sql2 = sqlContext.getQuery("updateAuthor2");
        assertThat(sql1.getSql(), 
                    is("update Author set username = #{username}, password = #{password}, email = #{email}, bio = #{bio} where id = #{id}"));
        assertThat(sql2.getSql(author),
                    is("update Author set username = #{username}, password = #{password}, bio = #{bio} where id = #{id}"));
        assertThat(sql1.getParamParser().getType(), is(ParamMarkType.HASH));
        assertThat(sql2.getParamParser().getType(), is(ParamMarkType.HASH));
    }

    @Test
    public void whenReadDeleteSqlWithoutDynamicElements()
    {
        Sql sql = sqlContext.getQuery("deleteAuthor");
        assertThat(sql.getSql(), is("delete from Author where id = #{id}"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.HASH));
    }
    
    @Test
    public void whenReadXmlProcedureNodeAttributesElementMustMatch() throws InterruptedException
    {
        ProcedureTag sql = (ProcedureTag) sqlContext.getQuery("daInsertAutoItemUpc");
        Thread.sleep(5000);
        sql = (ProcedureTag) sqlContext.getQuery("daInsertAutoItemUpc");
        assertThat(sql.getLanguageType(), is(LanguageType.STORED));
        assertThat(sql.getSpName(), is("daInsertAutoITEM_UPC"));
        assertThat(sql.getParams().length, is(13));
        assertThat(sql.getParams()[4].getMode(), is(ParameterMode.IN));
        assertThat(sql.getParams()[5].getMode(), is(ParameterMode.OUT));
        assertThat(sql.getParams()[6].getMode(),is(ParameterMode.INOUT));
        
        assertThat(sql.getParams()[4].getProperty(), is("curDayMove"));
        assertThat(sql.getParams()[5].getProperty(), is("prevDayMove"));
        assertThat(sql.getParams()[6].getProperty(), is("curWkMove"));
        
        Assert.assertNull(sql.getParams()[4].getTypeName());
        Assert.assertNull(sql.getParams()[5].getTypeName());
        Assert.assertNull(sql.getParams()[6].getTypeName());
        
        assertThat(sql.getParams()[4].getSqlType(), is(0));
        assertThat(sql.getParams()[5].getSqlType(), is(0));
        assertThat(sql.getParams()[6].getSqlType(), is(0));
        
        assertThat(sql.getParams()[12].getSqlType(), is(Types.CHAR));
        assertThat(sql.getParams()[12].getTypeName(), is("com.acme.Model"));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.NO_MARK));
    }
}
