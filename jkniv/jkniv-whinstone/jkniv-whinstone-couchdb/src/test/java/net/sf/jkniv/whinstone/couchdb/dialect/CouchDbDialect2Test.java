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
package net.sf.jkniv.whinstone.couchdb.dialect;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.whinstone.params.ParameterException;

public class CouchDbDialect2Test
{
    private static SqlContext sqlContext;
    @Rule
    public ExpectedException  catcher = ExpectedException.none();
    
    @BeforeClass
    public static void setUp()
    {
        sqlContext = SqlContextFactory.newInstance("/repository-sql-find-page.xml");
    }
    
    @Test
    public void whenCreateQueryAlrightHasLimit()
    {
        catcher.expect(ParameterException.class);
        //catcher.expectMessage(".");
        Sql sql = sqlContext.getQuery("authors-page-override");
        String sqlText = sql.getSql();
        CouchDbDialect20 dialect = new CouchDbDialect20();
        dialect.buildQueryPaging(sqlText, 0, 10);
    }

    @Test
    public void whenBuildQueryPage()
    {
        Sql sql = sqlContext.getQuery("authors-page");
        String sqlText = sql.getSql();
        CouchDbDialect20 dialect = new CouchDbDialect20();
        String sqlPage = dialect.buildQueryPaging(sqlText, 0, 10);
        
        assertThat(sqlPage, is("{\n     \"selector\": {\"nationality\": {\"$in\": :nations}}\n    \n,\"limit\": 10, \"skip\": 0 }") );
    }
}
