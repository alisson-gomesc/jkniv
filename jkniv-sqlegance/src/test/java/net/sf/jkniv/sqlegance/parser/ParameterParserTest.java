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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("ParameterParser is deprecated and deleted")
public class ParameterParserTest
{
    private static final String SQL_WITH_HASH          = "select id, name from Roles where id = #{id} and name = #{name}";
    private static final String SQL_WITH_2_DOTS        = "select id, name from Roles where id = :id and name = :name and xyz = :xyz";
    private static final String SQL_WITH_QUESTION_MARK = "select id, name from Roles where id = ? and name = ? and x = ? and y = ?";
    private static final String SQL_WITH_HASH_DOUBLE   = "select id, name from Roles where name = #{name} or name like #{name}";
    private static final String SQL_WITH_IN_HASH       = "select id, name from Roles where id = #{id} and name in :in:names";
    private static final String SQL_WITH_IN_2DOTS      = "select id, name from Roles where id = :id and name in :in:names";
    private static final String SQL_WITH_IN_QUESTION   = "select id, name from Roles where id = ? and name in :in:names";
    
    @Test
    public void regexToGetParameters()
    {
        String regexHashSymbol = "#\\{[\\w]+\\}"; // find the pattern #{id}
        String regexTwoDotsSymbol = ":[\\w]+"; // find the pattern :id
        String regexQuestionSymbol = "\\?"; // find the pattern ?
        int countSqlHash = 0, countSql2Dots = 0, countSqlQuestionMark = 0, countSqlHashDoubleNamed = 0;
        
        Matcher matcherHash = Pattern.compile(regexHashSymbol, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE).matcher(SQL_WITH_HASH);
        Matcher matcher2Dots = Pattern.compile(regexTwoDotsSymbol, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE).matcher(SQL_WITH_2_DOTS);
        Matcher matcherQuestinMark = Pattern.compile(regexQuestionSymbol, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE)
                .matcher(SQL_WITH_QUESTION_MARK);
        Matcher matcherHashDoubleNamed = Pattern.compile(regexHashSymbol, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE)
                .matcher(SQL_WITH_HASH_DOUBLE);
        
        while (matcherHash.find())
            countSqlHash++;
        
        while (matcher2Dots.find())
            countSql2Dots++;
        
        while (matcherQuestinMark.find())
            countSqlQuestionMark++;
        
        while (matcherHashDoubleNamed.find())
            countSqlHashDoubleNamed++;
        
        Assert.assertEquals(2, countSqlHash);
        Assert.assertEquals(3, countSql2Dots);
        Assert.assertEquals(4, countSqlQuestionMark);
        Assert.assertEquals(2, countSqlHashDoubleNamed);
    }
    
    @Test
    public void parameterParserExtractTest()
    {
        String[] paramsSqlHash = null;// = ParameterParser.extract(SQL_WITH_HASH);
        String[] paramsSql2Dots=null;// = ParameterParser.extract(SQL_WITH_2_DOTS);
        String[] paramsSqlQuestionMark=null;// = ParameterParser.extract(SQL_WITH_QUESTION_MARK);
        String[] paramsSqlHashDoubledNamed=null;// = ParameterParser.extract(SQL_WITH_HASH_DOUBLE);
        String[] paramsSqlEmpty=null;// = ParameterParser.extract("");
        String[] paramsSqlNull=null;// = ParameterParser.extract(null);
        
        assertThat( paramsSqlHash.length, is(2));
        assertThat(paramsSqlHash[0], is("id"));
        assertThat(paramsSqlHash[1], is("name"));
        
        assertThat(paramsSql2Dots.length, is(3));
        assertThat(paramsSql2Dots[0], is("id"));
        assertThat(paramsSql2Dots[1], is("name"));
        assertThat(paramsSql2Dots[2], is("xyz"));
        
        assertThat(paramsSqlQuestionMark.length, is(4));
        assertThat(paramsSqlQuestionMark[0], is("?"));
        assertThat(paramsSqlEmpty.length, is(0));
        
        assertThat(paramsSqlHashDoubledNamed.length, is(2));
        assertThat(paramsSqlHashDoubledNamed[0], is("name"));
        assertThat(paramsSqlHashDoubledNamed[1], is("name"));

        assertThat(paramsSqlNull.length, is(0));
    }
    
    @Test @Ignore("ParameterParser is deprecated and deleted")
    public void parameterParserExtractInClauseTest()
    {
        String[] paramsSqlHash = null;// = ParameterParser.extract(SQL_WITH_IN_HASH);
        String[] paramsSql2Dots = null;// = ParameterParser.extract(SQL_WITH_IN_2DOTS);
        String[] paramsSqlQuestion = null;// = ParameterParser.extract(SQL_WITH_IN_QUESTION);
        
        
        assertThat(paramsSqlHash.length, is(2) );
        assertThat(paramsSqlHash[0], is("id"));
        assertThat(paramsSqlHash[1], is("in:names"));
        
        assertThat(paramsSql2Dots.length, is(2) );
        assertThat(paramsSql2Dots[0], is("id"));
        assertThat(paramsSql2Dots[1], is("in:names"));

        assertThat(paramsSqlQuestion.length, is(2) );
        assertThat(paramsSqlQuestion[0], is("?"));
        assertThat(paramsSqlQuestion[1], is("in:names"));
    }
    
    @Test(expected = RuntimeException.class)
    @Ignore("ParameterParser is deprecated and deleted")
    public void whenSqlMixNamedParameterExceptionsIsExpectedCase1()
    {
        String sql = "select id, name from Roles where id = #{id} and name = #{name} and xy = ?";
        //ParameterParser.extract(sql);
    }
    
    @Test(expected = RuntimeException.class)
    
    public void whenSqlMixNamedParameterExceptionsIsExpectedCase2()
    {
        String sql = "select id, name from Roles where id = #{id} and name = #{name} and xy = :xy";
        //ParameterParser.extract(sql);
    }
    
    @Test(expected = RuntimeException.class)
    public void whenSqlMixNamedParameterExceptionsIsExpectedCase3()
    {
        String sql = "select id, name from Roles where id = :id and name = #{name}";
        //ParameterParser.extract(sql);
    }
    
    @Test(expected = RuntimeException.class)
    public void whenSqlMixNamedParameterExceptionsIsExpectedCase4()
    {
        String sql = "select id, name from Roles where id = ? and name = #{name}";
        //ParameterParser.extract(sql);
    }
    
    @Test
    public void whenArrayIsEmptyOrHaveOneElementToArrayWorks()
    {
        String[] zeroArray, oneArray;
        List<String> zero = new ArrayList<String>();
        List<String> one = new ArrayList<String>();
        one.add("s");
        
        zeroArray = zero.toArray(new String[0]);
        oneArray = one.toArray(new String[0]);
        Assert.assertNotNull(zeroArray);
        Assert.assertNotNull(oneArray);
        Assert.assertEquals(0, zeroArray.length);
        Assert.assertEquals(1, oneArray.length);
    }
    
    @Test
    public void whenSqlIsNamedParameterReplaceNamesTwoDotsForQuestionMark()
    {
        String sql = "select id, name from Roles where id = :id and name = :name and status = :status";
        String sqlExpected = "select id, name from Roles where id = ?   and name = ?     and status = ?      ";
        String newSql = null;// = ParameterParser.replaceTwoDotsForQuestionMark(sql);
        assertThat(newSql, is(sqlExpected));
    }
    
    @Test
    public void whenSqlIsNamedParameterReplaceNamesTwoDotsForQuestionMarkWithToDateFn()
    {
        String sql         = "select id, name from Roles where id = :id and name = :name and status = :status and dt = to_date(:dt,'YYYY-MM-DD HH24:MI:SS')";
        String sqlExpected = "select id, name from Roles where id = ?   and name = ?     and status = ?       and dt = to_date(?  ,'YYYY-MM-DD HH24:MI:SS')";
        String newSql = null;// = ParameterParser.replaceTwoDotsForQuestionMark(sql);
        assertThat(newSql, is(sqlExpected));
    }
    
    @Test
    public void whenSqlIsNamedParameterReplaceNamesForQuestionMark()
    {
        String sql         = "select id, name from Roles where id = #{id} and name = #{name} and status = #{status}";
        String sqlExpected = "select id, name from Roles where id = ?     and name = ?       and status = ?        ";
        String newSql = null;// = ParameterParser.replaceHashForQuestionMark(sql);
        assertThat(newSql, is(sqlExpected));
    }
    
    @Test
    public void whenSqlIsNamedParameterReplaceNamesForQuestionMarkToDateFn()
    {
        String sql         = "select id, name from Roles where id = #{id} and name = #{name} and status = #{status} and dt = to_date(#{dt},'YYYY-MM-DD HH24:MI:SS')";
        String sqlExpected = "select id, name from Roles where id = ?     and name = ?       and status = ?         and dt = to_date(?    ,'YYYY-MM-DD HH24:MI:SS')";
        String newSql = null;// = ParameterParser.replaceHashForQuestionMark(sql);
        assertThat(newSql, is(sqlExpected));
    }
    
    @Test
    public void whenSqlIsNamedParameterReplaceNamesForQuestionMarkInMultipleLines()
    {
        String sql         = "select id, name \nfrom Roles where \nid = #{id} and \nname = #{name} and \nstatus = #{status}";
        String sqlExpected = "select id, name \nfrom Roles where \nid = ?     and \nname = ?       and \nstatus = ?        ";
        String newSql = null;// = ParameterParser.replaceHashForQuestionMark(sql);
        assertThat(newSql, is(sqlExpected));
    }
    
    @Test
    public void whenSqlHaveHashAnswerIsTrue()
    {
        String sql = "select id, name from Roles where id = #{id} and name = #{name} and status = #{status}";
        boolean answer = false;// = ParameterParser.hasHash(sql);
        String[] count = null;// = ParameterParser.extract(sql);
        Assert.assertTrue("Hash will be found", answer);
        assertThat("Two dots will be found 3 parameters", count.length, is(3));
    }
    
    @Test
    public void whenSqlHaveHashAnswerIsTrueNested()
    {
        String sql = "select * from Route(#{a.longitude.value},#{a.latitude.value},#{b.longitude.value},#{b.latitude.value})";
        boolean answer = false;//ParameterParser.hasHash(sql);
        String[] count = null;// = ParameterParser.extract(sql);
        Assert.assertTrue("Hash will be found", answer);
        assertThat("Two dots will be found 4 parameters", count.length, is(4));
    }
    
    @Test
    public void whenSqlHaveQuestionAnswerIsTrue()
    {
        String sql = "select id, name from Roles where id = ? and name = ? and status = ?";
        boolean answer = false;//ParameterParser.hasQuestion(sql);
        String[] count = null;// = ParameterParser.extract(sql);
        Assert.assertTrue("Question mark will be found", answer);
        assertThat("Two dots will be found 3 parameters", count.length, is(3));
    }
    
    @Test
    public void whenSqlHaveTwoDotsAnswerIsTrue()
    {
        String sql = "select id, name from Roles where id = :id and name = :name and status = :status";
        boolean answer = false;//ParameterParser.hasTwoDots(sql);
        String[] count = null;// = ParameterParser.extract(sql);
        Assert.assertTrue("Two dots will be found", answer);
        assertThat("Two dots will be found 3 parameters", count.length, is(3));
    }
    
    @Test
    public void whenSqlHaveTwoDotsAnswerIsTrueNested()
    {
        String sql = "select * from Route(:a.longitude.value,:a.latitude.value,:b.longitude.value,:b.latitude.value)";
        boolean answer = false;//ParameterParser.hasTwoDots(sql);
        String[] count = null;// = ParameterParser.extract(sql);
        Assert.assertTrue("Two dots will be found", answer);
        assertThat("Two dots will be found 4 parameters", count.length, is(4));
    }
    
    @Test
    public void whenSqlHaveTwoDotsWithDateTwoDots()
    {
        String sql = "select id, name from Roles where dt = to_date(:dt,'YYYY-MM-DD HH24:MI:SS') and name = :name and status = :status";
        String[] answer = null;// = ParameterParser.extract(sql);
        assertThat("Two dots will be found 3 parameters", answer.length, is(3));
    }
    
    @Test
    public void whenSqlHaveQuestionMarksDateTwoDots()
    {
        String sql = "select id, name from Roles where dt = to_date(?,'YYYY-MM-DD HH24:MI:SS') and name = ? and status = ?";
        String[] answer = null;// = ParameterParser.extract(sql);
        assertThat("Two dots will be found 3 parameters", answer.length, is(3));
    }
    
    @Test
    public void whenSqlHashMarksDateTwoDots()
    {
        String sql = "select id, name from Roles where dt = to_date(#{dt},'YYYY-MM-DD HH24:MI:SS') and name = #{name} and status = #{status}";
        String[] answer = null;// = ParameterParser.extract(sql);
        assertThat("Two dots will be found 3 parameters", answer.length, is(3));
    }
    
}
