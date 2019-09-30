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

//import static org.hamcrest.Matchers.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.sqlegance.params.ParamMarkType;

public class XmlBuiderSqlAutGeneratedKeyTest
{
    private static SqlContext sqlContext;
    
    @BeforeClass
    public static void setUp()
    {
        sqlContext = SqlContextFactory.newInstance("/repository-sql.xml");
    }

    @Test
    public void whenBuildInsertWithDefaultAutoGeneratedKey()
    {
        Sql sql = sqlContext.getQuery("test-default-autokey-insert");
        assertThat(sql.getLanguageType(), is(LanguageType.NATIVE));
        assertThat(sql.getSqlType(), is(SqlType.INSERT));
        assertThat(sql, instanceOf(Insertable.class));
        Insertable tag  = (Insertable) sql;
        assertThat(tag.isAutoGenerateKey(), is(true));
        assertThat(tag.getAutoGeneratedKey(), notNullValue());
        assertThat(tag.getAutoGeneratedKey().getStrategy(), is(KeyGeneratorType.AUTO));
        assertThat(tag.getAutoGeneratedKey().getColumns(), is(""));
        assertThat("Properties cannot be null or empty when use auto generate key", 
                   tag.getAutoGeneratedKey().getProperties(), not(isEmptyOrNullString()));
        assertThat(tag.getAutoGeneratedKey().getText(), is(""));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.HASH));

    }

    @Test
    public void whenBuildInsertWithAutoGeneratedKey()
    {
        Sql sql = sqlContext.getQuery("test-auto-autokey-insert");
        assertThat(sql.getLanguageType(), is(LanguageType.NATIVE));
        assertThat(sql.getSqlType(), is(SqlType.INSERT));
        assertThat(sql, instanceOf(Insertable.class));
        Insertable tag  = (Insertable) sql;
        assertThat(tag.isAutoGenerateKey(), is(true));
        assertThat(tag.getAutoGeneratedKey(), notNullValue());
        assertThat(tag.getAutoGeneratedKey().getStrategy(), is(KeyGeneratorType.AUTO));
        assertThat(tag.getAutoGeneratedKey().getColumns(), is(""));
        assertThat(tag.getAutoGeneratedKey().getText(), is(""));
        assertThat("Properties cannot be null or empty when use auto generate key", 
                tag.getAutoGeneratedKey().getProperties(), not(isEmptyOrNullString()));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.HASH));
    }
    
    @Test
    public void whenBuildInsertWithSequenceGeneratedKey()
    {
        Sql sql = sqlContext.getQuery("test-sequence-autokey-insert");
        assertThat(sql.getLanguageType(), is(LanguageType.NATIVE));
        assertThat(sql.getSqlType(), is(SqlType.INSERT));
        assertThat(sql, instanceOf(Insertable.class));
        Insertable tag  = (Insertable) sql;
        assertThat(tag.isAutoGenerateKey(), is(true));
        assertThat(tag.getAutoGeneratedKey(), notNullValue());
        assertThat(tag.getAutoGeneratedKey().getStrategy(), is(KeyGeneratorType.SEQUENCE));
        assertThat(tag.getAutoGeneratedKey().getColumns(), is(""));
        assertThat(tag.getAutoGeneratedKey().getText(), is(""));
        assertThat("Properties cannot be null or empty when use auto generate key", 
                tag.getAutoGeneratedKey().getProperties(), not(isEmptyOrNullString()));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.HASH));
    }
    
    @Test @Ignore("KeyGeneratorType.TABLE disabled")
    public void whenBuildInsertTableGeneratedKey()
    {
        Sql sql = sqlContext.getQuery("test-table-autokey-insert");
        assertThat(sql.getLanguageType(), is(LanguageType.NATIVE));
        assertThat(sql.getSqlType(), is(SqlType.INSERT));
        assertThat(sql, instanceOf(Insertable.class));
        Insertable tag  = (Insertable) sql;
        assertThat(tag.isAutoGenerateKey(), is(true));
        assertThat(tag.getAutoGeneratedKey(), notNullValue());
        //assertThat(tag.getAutoGeneratedKeyTag().getStrategy(), is(KeyGeneratorType.TABLE));
        assertThat(tag.getAutoGeneratedKey().getColumns(), is(""));
        assertThat(tag.getAutoGeneratedKey().getText(), is(""));
        assertThat("Properties cannot be null or empty when use auto generate key", 
                tag.getAutoGeneratedKey().getProperties(), not(isEmptyOrNullString()));
    }

    @Test
    public void whenBuildInsertSequenceGeneratedKeyWithKey()
    {
        Sql sql = sqlContext.getQuery("test-sequence-autokey-insert-with-query");
        assertThat(sql.getLanguageType(), is(LanguageType.NATIVE));
        assertThat(sql.getSqlType(), is(SqlType.INSERT));
        assertThat(sql, instanceOf(Insertable.class));
        Insertable tag  = (Insertable) sql;
        assertThat(tag.isAutoGenerateKey(), is(true));
        assertThat(tag.getAutoGeneratedKey(), notNullValue());
        assertThat(tag.getAutoGeneratedKey().getStrategy(), is(KeyGeneratorType.SEQUENCE));
        assertThat(tag.getAutoGeneratedKey().getColumns(), is(""));
        assertThat(tag.getAutoGeneratedKey().getText(), is("select myschema.mysequence.nextval from dual"));
        assertThat("Properties cannot be null or empty when use auto generate key", 
                tag.getAutoGeneratedKey().getProperties(), not(isEmptyOrNullString()));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.HASH));
    }

    @Test
    public void whenBuildInsertWithoutNoGeneratedKey()
    {
        Sql sql = sqlContext.getQuery("test-no-autokey-insert");
        assertThat(sql.getLanguageType(), is(LanguageType.NATIVE));
        assertThat(sql.getSqlType(), is(SqlType.INSERT));
        assertThat(sql, instanceOf(Insertable.class));
        Insertable tag  = (Insertable) sql;
        assertThat(tag.isAutoGenerateKey(), is(false));
        assertThat(tag.getAutoGeneratedKey(), nullValue());
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.HASH));
    }

    private Map<String, Object> getParams()
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", "me");
        params.put("passowrd", "secret");
        params.put("email", "nobody@nohome.com");
        return params;
    }
}
