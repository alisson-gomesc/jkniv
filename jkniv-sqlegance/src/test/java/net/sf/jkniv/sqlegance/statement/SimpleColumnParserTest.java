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

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;

public class SimpleColumnParserTest
{
    private final Pattern PATTERN = Pattern.compile("(?<=select)(.*)(?=from)");
    private static SqlContext sqlContext;
    
    @Rule
    public ExpectedException catcher = ExpectedException.none();

    
    @BeforeClass
    public static void setUp()
    {
        sqlContext = SqlContextFactory.newInstance("/sql-column-parser.xml");
    }
    
    @Test
    public void whenTryParserColumnOrAliasNamesWithAsterisk()
    {
        catcher.expect(IllegalArgumentException.class);
        catcher.expectMessage("Cannot resolve column or alias names from select with asterisk (*)");

        Sql statement = sqlContext.getQuery("select-asterisk");
        ColumnParserFactory.getInstance().extract(statement.getSql());
    }

    
    @Test
    public void whenParseSubselectIntoSelect()
    {
        Sql statement = sqlContext.getQuery("subselect-into-select");
        String[] columns = ColumnParserFactory.getInstance().extract(statement.getSql());
        assertThat(columns.length, is(4));
        assertThat(columns[0], is("id"));
        assertThat(columns[1], is("name"));
        assertThat(columns[2], is("status"));
        assertThat(columns[3], is("role"));
    }
    
    @Test
    public void whenParseSubselectIntoJoin()
    {
        Sql statement = sqlContext.getQuery("subselect-into-join");
        String[] columns = ColumnParserFactory.getInstance().extract(statement.getSql());
        assertThat(columns.length, is(3));
        assertThat(columns[0], is("id"));
        assertThat(columns[1], is("name"));
        assertThat(columns[2], is("status"));
    }
    
    @Test
    public void complexQuery()
    {
        Sql statement = sqlContext.getQuery("join-and-subselect-double-quote-etc");
        String[] columns = ColumnParserFactory.getInstance().extract(statement.getSql());
        
        assertThat(columns.length, is(30));
        assertThat(columns[0], is("fds_code"));
        assertThat(columns[1], is("reference"));
        assertThat(columns[2], is("survey_dt"));
        assertThat(columns[3], is("survey_sk"));
        assertThat(columns[4], is("date_in"));
        assertThat(columns[5], is("sync_date"));
        assertThat(columns[6], is("survey_route"));
        assertThat(columns[7], is("survey_presale_route"));
        assertThat(columns[8], is("cust_sk"));
        assertThat(columns[9], is("cust_name"));
        assertThat(columns[10], is("cust_code"));
        assertThat(columns[11], is("cust_route"));
        assertThat(columns[12], is("cust_presale_route"));
        assertThat(columns[13], is("unit_code"));
        assertThat(columns[14], is("cac_code"));
        assertThat(columns[15], is("channel_code"));
        assertThat(columns[16], is("subchannel_code"));
        assertThat(columns[17], is("presale_route_code"));
        assertThat(columns[18], is("range_ini"));
        assertThat(columns[19], is("range_end"));
        assertThat(columns[20], is("valid_date_ini"));
        assertThat(columns[21], is("valid_date_end"));
        assertThat(columns[22], is("fds.id"));
        assertThat(columns[23], is("fds.status"));
        assertThat(columns[24], is("fds.descricao"));
        assertThat(columns[25], is("fds.codigoSegmento"));
        assertThat(columns[26], is("fds.peso"));
        assertThat(columns[27], is("fds.tipo"));
        assertThat(columns[28], is("fds.conformidadePreco"));
        assertThat(columns[29], is("id_fds_vigente"));
    }

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
