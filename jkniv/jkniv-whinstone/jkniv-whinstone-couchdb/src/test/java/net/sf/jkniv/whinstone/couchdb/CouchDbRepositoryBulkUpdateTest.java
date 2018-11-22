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
import net.sf.jkniv.whinstone.couchdb.model.orm.Author;

public class CouchDbRepositoryBulkUpdateTest extends BaseJdbc
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  

    
    @Test
    public void whenUpdateBulkDocumentsWithouId()
    {
        Repository repositoryDb = getRepository();
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
        loadDatad(authors);
        
        a1.setNationality("pt_BR");
        a2.setNationality("us_EN");
        a3.setNationality("mx_ES");
        
        Queryable q = QueryFactory.of("update", authors);
        
        int rows = repositoryDb.update(q);
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
        assertThat(checkA1.getNationality(), is("pt_BR"));
        
        Author checkA2 = repositoryDb.get(a2);
        assertThat(checkA2.getId(), is(a2.getId()));
        assertThat(checkA2.getName(), is(a2.getName()));
        assertThat(checkA2.getNationality(), is("us_EN"));
        
        Author checkA3 = repositoryDb.get(a3);
        assertThat(checkA3.getId(), is(a3.getId()));
        assertThat(checkA3.getName(), is(a3.getName()));
        assertThat(checkA3.getNationality(), is("mx_ES"));
    }
    
    private void loadDatad(List<Author> authors)
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("add", authors);
        repositoryDb.add(q);
    }
}
