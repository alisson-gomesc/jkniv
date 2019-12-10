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
package net.sf.jkniv.whinstone.couchdb;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import java.util.Map;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.RepositoryService;
import net.sf.jkniv.whinstone.couchdb.model.orm.Author;

@Ignore("delete me test from couchdb")
public class CouchDbRepositoryJndiTest extends BaseJdbc
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  

    @Test
    public void whenTryConnectToDatabaseWithoutUserAndPassword()
    {
        catcher.expect(RepositoryException.class);
        catcher.expectMessage("Access denied, unauthorized");
        
        Repository repository = RepositoryService.getInstance().lookup(RepositoryType.COUCHDB).newInstance("/repository-sql-security-nopass.xml");
        
        Queryable q = getQuery("get", "1");
        
        Author ret = repository.get(q,Author.class);
        assertThat(ret, notNullValue());
        assertThat(ret, instanceOf(Author.class));
        assertThat(ret.getName(), is("Friedrich Nietzsche"));
    }

    @Test
    public void whenTryConnectToDatabaseWithJndiResourceAtContextName()
    {
        Repository repository = RepositoryService.getInstance().lookup(RepositoryType.COUCHDB).newInstance("repository-sql-security-jndi.xml");
        
        Queryable q = getQuery("get", "1");
        
        Author ret = repository.get(q, Author.class);
        assertThat(ret, notNullValue());
        assertThat(ret, instanceOf(Author.class));
        assertThat(ret.getName(), is("Friedrich Nietzsche"));
    }


    @Test
    public void whenTryConnectToDatabaseWithJndiConfig()
    {
        Repository repository = RepositoryService.getInstance().lookup(RepositoryType.COUCHDB).newInstance("repository-sql.xml");
        
        Queryable q = getQuery("get", "1");
        
        Author ret = repository.get(q, Author.class);
        assertThat(ret, notNullValue());
        assertThat(ret, instanceOf(Author.class));
        assertThat(ret.getName(), is("Friedrich Nietzsche"));
    }

}
