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
package net.sf.jkniv.whinstone.jdbc.regex;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Ignore;
import org.junit.Test;

public class RegexSqlTest
{
    // find the pattern start with 'select'
    //private static final String REGEX_START_SELECT          = "^\\s*(select)\\s";
    
    // find the pattern start with 'select distinct'
    private static final String REGEX_START_SELECT_DISTINCT = "^\\s*(select\\s+distinct|select)\\s";
    
    // find the pattern end with 'for update'
    private static final String REGEX_ENDS_FORUPDATE        = "\\s+(for\\s+update)\\s*$";
    
    // find the pattern end with 'order by'
    private static final String REGEX_ENDS_ORDERBY          = "\\s*(order\\s+by)\\s*[a-zA-Z0-9,_\\.\\)\\s]*$";
    
    //public static final Pattern SELECT                      = Pattern.compile(REGEX_START_SELECT, Pattern.CASE_INSENSITIVE);
    public static final Pattern SELECT_DISTINCT             = Pattern.compile(REGEX_START_SELECT_DISTINCT, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    public static final Pattern FOR_UPDATE                  = Pattern.compile(REGEX_ENDS_FORUPDATE, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    public static final Pattern ORDER_BY                    = Pattern.compile(REGEX_ENDS_ORDERBY, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    
//    private static final Pattern                      PATTERN_ORDER_BY       = Pattern
//            .compile("order\\s+by\\s+[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    private static final Pattern     PATTERN_ORDER_BY = Pattern.compile(REGEX_ENDS_ORDERBY, Pattern.CASE_INSENSITIVE);
    
    // "order\\s+?by\\s?([\\w\\.?]|[\\W\\.?])"
    //  Pattern.compile("order\\s+by\\s+[\\w|\\W|\\s|\\S]*",
    
    
    /*
    @Test
    public void whenRegexMatchSelect()
    {
        Matcher matcher = SELECT.matcher("select * from author");
        assertThat(matcher.find(), is(true));
        System.out.printf("select match-> %s (%d,%d)\n", matcher.group(), matcher.start(), matcher.end());
        
        matcher = SELECT.matcher("     select    * from author");
        assertThat(matcher.find(), is(true));
        
        matcher = SELECT.matcher("  select  (1) from author");
        assertThat(matcher.find(), is(true));
    }
    */
    @Test
    public void whenRegexMatchSelectOrDistinct()
    {
        Matcher matcher = SELECT_DISTINCT.matcher("select * from author");
        assertThat(matcher.find(), is(true));
        System.out.printf("select match-> %s (%d,%d)\n", matcher.group(), matcher.start(), matcher.end());

        matcher = SELECT_DISTINCT.matcher("     select    * from author");
        assertThat(matcher.find(), is(true));
        
        matcher = SELECT_DISTINCT.matcher("  select  (1) from author");
        assertThat(matcher.find(), is(true));

        matcher = SELECT_DISTINCT.matcher("select distinct * from author");
        assertThat(matcher.find(), is(true));
        System.out.printf("select distinct match-> %s (%d,%d)\n", matcher.group(), matcher.start(), matcher.end());
        
        matcher = SELECT_DISTINCT.matcher("   select    distinct   * from author");
        assertThat(matcher.find(), is(true));
        
        matcher = SELECT_DISTINCT.matcher("Select DIStincT id, name, code from author OrDer bY name");
        assertThat(matcher.find(), is(true));
    }

    @Test 
    public void whenRegexMatchOrderBy()
    {
        Matcher matcher = ORDER_BY.matcher("select * from author order by name");
        assertThat(matcher.find(), is(true));
        System.out.printf("select match-> %s (%d,%d)\n", matcher.group(), matcher.start(), matcher.end());

        matcher = ORDER_BY.matcher("select * from author order by name, code");
        assertThat(matcher.find(), is(true));
        
        matcher = ORDER_BY.matcher("SELECT name FROM Employees ORDER BY First Name OFFSET 10 ROWS");
        assertThat(matcher.find(), is(true));
        
        matcher = ORDER_BY.matcher("Select DIStincT id, name, code from author OrDer bY name");
        assertThat(matcher.find(), is(true));

        matcher = ORDER_BY.matcher("Select DIStincT id, name, code from author a OrDer bY a.name, a.code");
        assertThat(matcher.find(), is(true));

        matcher = ORDER_BY.matcher("Select DIStincT id, name, code from author a OrDer bY a.full_name, a.name, a.code");
        assertThat(matcher.find(), is(true));
}
    

    @Test
    public void whenRegexMatchForUpdate()
    {
        String sentence = "select * from author for update";
        Matcher matcher = FOR_UPDATE.matcher(sentence);
        assertThat(matcher.find(), is(true));
        
        System.out.printf("for update match-> %s (%d,%d)\n", matcher.group(), matcher.start(), matcher.end());
    }

    @Test
    public void whenRegexRemoveOrderBy()
    {
        Matcher matcher = PATTERN_ORDER_BY.matcher("select * from author order by name");
        StringBuffer sb = new StringBuffer();
        while (matcher.find())
            matcher.appendReplacement(sb, "");
        matcher.appendTail(sb);
        assertThat(sb.toString().trim(), is("select * from author"));

        matcher = PATTERN_ORDER_BY.matcher("select * from author order by name, code");
        sb = new StringBuffer();
        while (matcher.find())
            matcher.appendReplacement(sb, "");
        matcher.appendTail(sb);
        assertThat(sb.toString().trim(), is("select * from author"));

        matcher = PATTERN_ORDER_BY.matcher("SELECT name FROM Author ORDER BY First Name OFFSET 10 ROWS");
        sb = new StringBuffer();
        while (matcher.find())
            matcher.appendReplacement(sb, "");
        matcher.appendTail(sb);
        assertThat(sb.toString().trim(), is("SELECT name FROM Author"));

        matcher = PATTERN_ORDER_BY.matcher("Select DIStincT id, name, code from author OrDer bY name");
        sb = new StringBuffer();
        while (matcher.find())
            matcher.appendReplacement(sb, "");
        matcher.appendTail(sb);
        assertThat(sb.toString().trim(), is("Select DIStincT id, name, code from author"));

        matcher = PATTERN_ORDER_BY.matcher("Select DIStincT id, name, code from author a OrDer bY a.name, a.code");
        sb = new StringBuffer();
        while (matcher.find())
            matcher.appendReplacement(sb, "");
        matcher.appendTail(sb);
        assertThat(sb.toString().trim(), is("Select DIStincT id, name, code from author a"));
        
        matcher = PATTERN_ORDER_BY.matcher("Select DIStincT id, name, code from author a OrDer bY a.full_name, a.name, a.code");
        sb = new StringBuffer();
        while (matcher.find())
            matcher.appendReplacement(sb, "");
        matcher.appendTail(sb);
        assertThat(sb.toString().trim(), is("Select DIStincT id, name, code from author a"));

   
        /*
        matcher = PATTERN_ORDER_BY.matcher("select * from (select name, sum(score) as s from author group by name order by name) order by score");
        sb = new StringBuffer();
        while (matcher.find())
        {
            System.out.println("match 2 order by stating: " + matcher.start());
            
            //if (matcher.hitEnd())
                matcher.appendReplacement(sb, "");
        }
        matcher.appendTail(sb);
        assertThat(sb.toString().trim(), is("select * from (select name, sum(score) as s from author group by name order by name)"));
/*/
        // FIXME how to infer a count query to total of records?
        /*
        matcher = ORDER_BY.matcher("select * from (Select name, sum(score) as score from Student group by name order by name) order by score");
        sb = new StringBuffer();
        while (matcher.find())
            matcher.appendReplacement(sb, "");
        matcher.appendTail(sb);
        assertThat(sb.toString().trim(), is("Select DIStincT id, name, code from author a"));
         */
    }

}
