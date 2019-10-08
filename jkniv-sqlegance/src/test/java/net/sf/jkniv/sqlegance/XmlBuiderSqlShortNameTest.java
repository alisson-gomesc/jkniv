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

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.sqlegance.builder.SqlContextFactory;

public class XmlBuiderSqlShortNameTest
{
    private static SqlContext sqlContext;
    
    @BeforeClass
    public static void setUp()
    {
        sqlContext = SqlContextFactory.newInstance("/repository-sql.xml");
    }

    @Rule
    public ExpectedException catcher = ExpectedException.none();
    
	@Test
	public void whenGetDuplicateShortNames() {
        
	    catcher.expect(QueryNotFoundException.class);
	    catcher.expectMessage("There are duplicate short name [getUsers] for this statement, use fully name to recover it");
	    Sql sql1, sql2;
        sql1 = sqlContext.getQuery("com.acme.sample.getUsers");
        sql2 = sqlContext.getQuery("com.acme.sample.security.getUsers");
        assertThat(sql1.getSql().toLowerCase(), is("select id, name from users"));
        assertThat(sql2.getSql().toLowerCase(), is("select id, name from users_sec"));
        assertThat((sql1 != sql2), is(true));
        sqlContext.getQuery("getUsers");
	}
	
	
}
