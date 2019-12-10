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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.couchdb.model.orm.Author;

public class CouchDbRepositoryUpdateTest extends BaseJdbc
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  

    @Test
    public void whenUpdateDocumentWithoutIdAndRevision()
    {
        catcher.expect(RepositoryException.class);
        catcher.expectMessage("HTTP/1.1 412 Precondition Failed, The database could not be created, the file already exists.");
        Repository repositoryDb = getRepository();
        Author author = new Author();
        author.setName("Alisson Gomes");
        author.setNationality("BR");
        Queryable q = QueryFactory.of("update", author);
        repositoryDb.update(q);
        
        Author authorCallbackCheck = repositoryDb.get(Author.class, author);
        assertThat(authorCallbackCheck.getAddAt(), is(notNullValue()));
        assertThat(authorCallbackCheck.getUpdateAt(), is(notNullValue()));
    }

    @Test
    public void whenUpdateDocumentWithoutRevision()
    {
        catcher.expect(RepositoryException.class);
        catcher.expectMessage("HTTP/1.1 409 Conflict, Document update conflict.");
        Repository repositoryDb = getRepository();
        Author author = new Author();
        author.setId("1");
        author.setName("Alisson Gomes");
        author.setNationality("BR");
        Queryable q = QueryFactory.of("update", author);
        repositoryDb.update(q);
     
        Author authorCallbackCheck = repositoryDb.get(Author.class, author);
        assertThat(authorCallbackCheck.getAddAt(), is(notNullValue()));
        assertThat(authorCallbackCheck.getUpdateAt(), is(notNullValue()));
    }

    @Test
    public void whenUpdateDocumentWithQueryable()
    {
        Repository repositoryDb = getRepository();
        Author author = repositoryDb.get(Author.class, "2");
        String revision = author.getRev();
        
        assertThat(revision, notNullValue());
        
        author.setName("Alisson Gomes");
        author.setNationality("BR");
        Queryable q = QueryFactory.of("update", author);
        int rows = repositoryDb.update(q);
        
        assertThat(rows, is(1));
        assertThat(author.getId(), notNullValue());
        assertThat(revision, not(is(author.getRev())));
        assertThat(author.getUpdateAt(), notNullValue());        
        
        Author authorCallbackCheck = repositoryDb.get(Author.class, author);
        assertThat(authorCallbackCheck.getAddAt(), is(nullValue()));
        assertThat(authorCallbackCheck.getUpdateAt(), is(notNullValue()));
    }

    @Test
    public void whenUpdateDocumentWithEntity()
    {
        Repository repositoryDb = getRepository();
        Author author = repositoryDb.get(Author.class, "2");
        String revision = author.getRev();
        
        assertThat(revision, notNullValue());
        
        author.setName("Alisson Gomes");
        author.setNationality("US");
        repositoryDb.update(author);
        
        assertThat(author.getId(), notNullValue());
        assertThat(revision, not(is(author.getRev())));
        assertThat(author.getUpdateAt(), notNullValue());   
        
        Author authorCallbackCheck = repositoryDb.get(Author.class, author);
        assertThat(authorCallbackCheck.getAddAt(), is(nullValue()));
        assertThat(authorCallbackCheck.getUpdateAt(), is(notNullValue()));
    }
    
}
