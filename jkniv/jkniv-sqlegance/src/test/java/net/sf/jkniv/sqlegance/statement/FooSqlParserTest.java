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
package net.sf.jkniv.sqlegance.statement;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class FooSqlParserTest
{
    private Pattern PATTERN = Pattern.compile("(?<=select)(.*)(?=from)");
    
    @Test
    public void whenExtractColumnsNameFromSelectUntilFrom()
    {
        String sql = "select id, name from users";
        Matcher matcher = PATTERN.matcher(sql);
        assertThat(matcher.groupCount(), is(1));
        assertThat(matcher.find(), is(true));
        assertThat(matcher.group().trim(), is("id, name"));
        
        String[] columns = matcher.group().trim().split(",");
        assertThat(getColumnNameOrAlias(columns[0]), is("id"));
        assertThat(getColumnNameOrAlias(columns[1]), is("name"));
        
        columns = ColumnParserFactory.getInstance().extract(sql);
        assertThat(columns[0], is("id"));
        assertThat(columns[1], is("name"));
    }
    
    @Test
    public void whenExtractColumnsNameFromSelectUntilFromWithAlias()
    {
        String sql = "select id as myId, name as fullName from users";
        Matcher matcher = PATTERN.matcher(sql);
        assertThat(matcher.groupCount(), is(1));
        assertThat(matcher.find(), is(true));
        assertThat(matcher.group().trim(), is("id as myId, name as fullName"));
        
        String[] columns = matcher.group().trim().split(",");
        assertThat(getColumnNameOrAlias(columns[0]), is("myId"));
        assertThat(getColumnNameOrAlias(columns[1]), is("fullName"));
        
        columns = ColumnParserFactory.getInstance().extract(sql);
        assertThat(columns[0], is("myId"));
        assertThat(columns[1], is("fullName"));
    }
    
    @Test
    public void whenExtractColumnsNameWithTwoSelects()
    {
        String sql = "select id, name, (select count(1) from roles) roles from users";
        Matcher matcher = PATTERN.matcher(sql);
        assertThat(matcher.groupCount(), is(1));
        assertThat(matcher.find(), is(true));
        assertThat(matcher.group().trim(), is("id, name, (select count(1) from roles) roles"));
        
        String[] columns = matcher.group().trim().split(",");
        assertThat(getColumnNameOrAlias(columns[0]), is("id"));
        assertThat(getColumnNameOrAlias(columns[1]), is("name"));
        assertThat(getColumnNameOrAlias(columns[2]), is("roles"));

        columns = ColumnParserFactory.getInstance().extract(sql);
        assertThat(columns[0], is("id"));
        assertThat(columns[1], is("name"));
        assertThat(columns[2], is("roles"));
    }
    
    private String getColumnNameOrAlias(String value)
    {
        StringBuilder columnName = new StringBuilder();
        for (int i = value.length() - 1; i >= 0; i--)
        {
            if (' ' == value.charAt(i))
                break;
            columnName.insert(0, value.charAt(i));
        }
        return columnName.toString();
    }
    
}
