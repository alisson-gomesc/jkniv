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

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.couchdb.model.orm.Author;

@Ignore("delete me test from couchdb")
public class CouchDbRepositoryBulkAddTest extends BaseJdbc
{
    @Test
    public void whenAddingBulkDocumentsWithouId()
    {
        Repository repository = getRepository();
        List<Author> authors = new ArrayList<Author>();
        
        Author a1 = new Author();
        a1.setName("Bulk One");
        a1.setNationality("BR");

        Author a2 = new Author();
        a2.setName("Bulk Two");
        a2.setNationality("PT");

        
        Author a3 = new Author();
        a3.setName("Bulk Three");
        a3.setNationality("AU");

        authors.add(a1); authors.add(a2); authors.add(a3);
        
        Queryable q = QueryFactory.of("add", authors);
        
        int rows = repository.add(q);
        assertThat(rows, is(3));
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
        
        Author checkA1 = repository.get(a1);
        assertThat(checkA1.getId(), is(a1.getId()));
        assertThat(checkA1.getName(), is(a1.getName()));
        assertThat(checkA1.getNationality(), is(a1.getNationality()));
        
        Author checkA2 = repository.get(a2);
        assertThat(checkA2.getId(), is(a2.getId()));
        assertThat(checkA2.getName(), is(a2.getName()));
        assertThat(checkA2.getNationality(), is(a2.getNationality()));
        
        Author checkA3 = repository.get(a3);
        assertThat(checkA3.getId(), is(a3.getId()));
        assertThat(checkA3.getName(), is(a3.getName()));
        assertThat(checkA3.getNationality(), is(a3.getNationality()));
    }
    
    @Test
    public void whenAddingBulkDocumentsWithId()
    {
        Repository repositoryDb = getRepository();
        List<Author> authors = new ArrayList<Author>();
        
        Author a1 = new Author();
        a1.setId("BULK-001");
        a1.setName("Bulk One");
        a1.setNationality("BR");

        Author a2 = new Author();
        a2.setId("BULK-002");
        a2.setName("Bulk Two");
        a2.setNationality("BR");

        
        Author a3 = new Author();
        a3.setId("BULK-003");
        a3.setName("Bulk Three");
        a3.setNationality("BR");

        authors.add(a1); authors.add(a2); authors.add(a3);
        
        Queryable q = QueryFactory.of("add", authors);
        
        int rows = repositoryDb.add(q);
        assertThat(rows, is(3));
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
        
        Author checkA1 = repositoryDb.get(a1);
        assertThat(checkA1.getId(), is(a1.getId()));
        assertThat(checkA1.getName(), is(a1.getName()));
        assertThat(checkA1.getNationality(), is(a1.getNationality()));
        
        Author checkA2 = repositoryDb.get(a2);
        assertThat(checkA2.getId(), is(a2.getId()));
        assertThat(checkA2.getName(), is(a2.getName()));
        assertThat(checkA2.getNationality(), is(a2.getNationality()));
        
        Author checkA3 = repositoryDb.get(a3);
        assertThat(checkA3.getId(), is(a3.getId()));
        assertThat(checkA3.getName(), is(a3.getName()));
        assertThat(checkA3.getNationality(), is(a3.getNationality()));
    }
}
