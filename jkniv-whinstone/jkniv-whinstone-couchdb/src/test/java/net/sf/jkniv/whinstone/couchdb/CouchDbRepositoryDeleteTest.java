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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.couchdb.model.orm.Author;

public class CouchDbRepositoryDeleteTest extends BaseJdbc
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  

    @BeforeClass
    public static void setUp()
    {
        Repository repository = getRepository();
        Author author1 = new Author();
        Author author2 = new Author();
        author1.setId("DELETEME-01");
        author2.setId("DELETEME-02");
        
        author1.setName("Paulo Coelho");
        author2.setName("Augusto Cury");
        repository.add(author1);
        repository.add(author2);
    }

    @Test
    public void whenDeleteDocumentWithQueryable()
    {
        Repository repository = getRepository();
        Author author = repository.get(Author.class, "DELETEME-01");
        String revision = author.getRev();
        
        assertThat(revision, notNullValue());
        assertThat(author.getName(), is("Paulo Coelho"));
        
        Queryable q = QueryFactory.of("remove", author);
        int rows = repository.remove(q);
        
        assertThat(rows, is(1));
        assertThat(author.getId(), notNullValue());
        assertThat(revision, not(is(author.getRev())));
        System.out.println(author);
    }

    @Test
    public void whenDeleteDocumentWithEntity()
    {
        Repository repository = getRepository();
        Author author = repository.get(Author.class, "DELETEME-02");
        String revision = author.getRev();
        
        assertThat(revision, notNullValue());
        assertThat(author.getName(), is("Augusto Cury"));
        
        repository.remove(author);
        
        assertThat(author.getId(), notNullValue());
        assertThat(revision, not(is(author.getRev())));
    }
    
}
