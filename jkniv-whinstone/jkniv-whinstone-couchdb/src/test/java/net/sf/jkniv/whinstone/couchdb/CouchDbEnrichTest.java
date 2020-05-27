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
import static org.hamcrest.Matchers.notNullValue;

import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.sqlegance.NonUniqueResultException;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.couchdb.model.orm.Author;

public class CouchDbEnrichTest extends BaseJdbc
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  

    @Test
    public void whenEnrichObject()
    {
        Repository repositoryDb = getRepository();
        Queryable q1 = QueryFactory.of("authorByName", "name", "Friedrich Nietzsche");
        
        Author nietzsche = repositoryDb.get(q1);
        assertThat(nietzsche.getName(), is("Friedrich Nietzsche"));
        assertThat(nietzsche.getNationality(), is("DE"));
        assertThat(nietzsche.getBooks().size(), is(1));
        assertThat(nietzsche.getBooks().get(0).getName(), is("Beyond Good and Evil"));
        assertThat(nietzsche.getBooks().get(0).getIsbn(), is("978-1503250888"));
        assertThat(nietzsche.getBooks().get(0).getPublished(), is(1886L));
     
        nietzsche.setName("Karl Marx");
        Queryable q2 = QueryFactory.of("authorByName", nietzsche);
        
        boolean enriched = repositoryDb.enrich(q2);
        assertThat(enriched, is(true));
        assertThat(nietzsche.getName(), is("Karl Marx"));
        assertThat(nietzsche.getBorn(), is(1818));
        assertThat(nietzsche.getNationality(), is("DE"));
        assertThat(nietzsche.getBooks().size(), is(2));
        assertThat(nietzsche.getBooks().get(0).getName(), is("Beyond Good and Evil"));
        assertThat(nietzsche.getBooks().get(1).getName(), is("Das Kapital"));
        assertThat(nietzsche.getBooks().get(1).getIsbn(), is("978-8520004678"));
        assertThat(nietzsche.getBooks().get(1).getPublished(), is(1867L));        
    }
    
    @Test
    public void whenScalarReturnNonUniqueResult()
    {
        catcher.expect(NonUniqueResultException.class);
        catcher.expectMessage("No unique result for query [authorName]");
        Repository repositoryDb = getRepository();
        Queryable q1 = QueryFactory.of("authorByName", "name", "Friedrich Nietzsche");
        Author nietzsche = repositoryDb.get(q1);

        nietzsche.setName("Karl Marx");
        Queryable q2 = QueryFactory.of("authorName", nietzsche);
        boolean enriched = repositoryDb.enrich(q2);
    }

}
