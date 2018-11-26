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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.couchdb.model.orm.AuthorForDelete;

public class CouchDbRepositoryBulkUpdateTest extends BaseJdbc
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  

    
    @Test
    public void whenUpdateBulkDocumentsWithouId()
    {
        Repository repositoryDb = getRepository();
        List<AuthorForDelete> authors = loadData();
        
        AuthorForDelete a1 = authors.get(0);
        AuthorForDelete a2 = authors.get(1);
        AuthorForDelete a3 = authors.get(2);
        AuthorForDelete a4 = authors.get(3);
        AuthorForDelete a5 = authors.get(4);
        a1.setNationality("pt_BR");
        a2.setNationality("us_EN");
        a3.setNationality("mx_ES");
        
        a4.setDeleted(true);
        a5.setDeleted(true);
        Queryable q = QueryFactory.of("update", authors);
        
        int rows = repositoryDb.update(q);
        assertThat(rows, is(5));
        assertThat(a1.getId(), notNullValue());
        assertThat(a1.getRev(), notNullValue());
        assertThat(a1.getName(), notNullValue());
        assertThat(a1.getNationality(), notNullValue());
        
        assertThat(a2.getId(), notNullValue());
        assertThat(a2.getRev(), notNullValue());
        assertThat(a2.getName(), notNullValue());
        assertThat(a2.getNationality(), notNullValue());
        
        assertThat(a3.getId(), notNullValue());
        assertThat(a3.getRev(), notNullValue());
        assertThat(a3.getName(), notNullValue());
        assertThat(a3.getNationality(), notNullValue());
        
        AuthorForDelete checkA1 = repositoryDb.get(a1);
        assertThat(checkA1.getId(), is(a1.getId()));
        assertThat(checkA1.getName(), is(a1.getName()));
        assertThat(checkA1.getNationality(), is("pt_BR"));
        
        AuthorForDelete checkA2 = repositoryDb.get(a2);
        assertThat(checkA2.getId(), is(a2.getId()));
        assertThat(checkA2.getName(), is(a2.getName()));
        assertThat(checkA2.getNationality(), is("us_EN"));
        
        AuthorForDelete checkA3 = repositoryDb.get(a3);
        assertThat(checkA3.getId(), is(a3.getId()));
        assertThat(checkA3.getName(), is(a3.getName()));
        assertThat(checkA3.getNationality(), is("mx_ES"));

        
        a4.setRev(null);
        a4.setDeleted(false);
        a5.setRev(null);
        a5.setDeleted(false);

        AuthorForDelete checkA4 = repositoryDb.get(a4);
        AuthorForDelete checkA5 = repositoryDb.get(a5);

        assertThat(checkA4, nullValue());
        assertThat(checkA5, nullValue());
    }
    
    private List<AuthorForDelete> loadData()
    {
        Repository repositoryDb = getRepository();
        List<AuthorForDelete> authors = new ArrayList<AuthorForDelete>();
        AuthorForDelete a1 = new AuthorForDelete();
        a1.setId("Bulk-1");
        a1.setName("Bulk One");
        a1.setNationality("BR");
        AuthorForDelete a2 = new AuthorForDelete();
        a2.setId("Bulk-2");
        a2.setName("Bulk Two");
        a2.setNationality("PT");
        AuthorForDelete a3 = new AuthorForDelete();
        a3.setId("Bulk-3");
        a3.setName("Bulk Three");
        a3.setNationality("AU");

        AuthorForDelete a4 = new AuthorForDelete();
        a4.setId("Bulk-4");
        a4.setName("Bulk Four");
        a4.setNationality("AR");

        AuthorForDelete a5 = new AuthorForDelete();
        a5.setId("Bulk-5");
        a5.setName("Bulk Five");
        a5.setNationality("MX");

        authors.add(a1); authors.add(a2); authors.add(a3); authors.add(a4); authors.add(a5);
        Queryable q = QueryFactory.of("add", authors);
        repositoryDb.add(q);
        return authors;
    }
}
