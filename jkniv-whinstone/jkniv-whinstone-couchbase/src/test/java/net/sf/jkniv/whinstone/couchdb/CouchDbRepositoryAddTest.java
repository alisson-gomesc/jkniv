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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.couchdb.model.orm.Author;
import net.sf.jkniv.whinstone.couchdb.result.CustomResultRow;

public class CouchDbRepositoryAddTest extends BaseJdbc
{
    
    @Test
    public void whenAddDocumentWithouId()
    {
        Repository repositoryDb = getRepository();
        Author author = new Author();
        author.setName("Alisson Gomes");
        author.setNationality("BR");
        Queryable q = getQuery("add", author);
        int rows = repositoryDb.add(q);
        
        assertThat(rows, is(1));
        assertThat(author.getId(), notNullValue());
        assertThat(author.getAddAt(), notNullValue());
        assertThat(author.getUpdateAt(), nullValue());

        Author authorCallbackCheck = repositoryDb.get(Author.class, author);
        assertThat(authorCallbackCheck.getAddAt(), is(notNullValue()));
        assertThat(authorCallbackCheck.getUpdateAt(), is(nullValue()));
    }
    
    @Test
    public void whenAddDocumentWithId()
    {
        Repository repositoryDb = getRepository();
        Author author = new Author();
        author.setId("001");
        author.setName("Alisson Gomes");
        author.setNationality("BR");
        Queryable q = getQuery("add", author);
        int rows = repositoryDb.add(q);
        
        assertThat(rows, is(1));
        assertThat(author.getId(), is(repositoryDb.get(Author.class,"001").getId()));        
        assertThat(author.getAddAt(), notNullValue());
        assertThat(author.getUpdateAt(), nullValue());
        
        Author authorCallbackCheck = repositoryDb.get(Author.class, author);
        assertThat(authorCallbackCheck.getAddAt(), is(notNullValue()));
        assertThat(authorCallbackCheck.getUpdateAt(), is(nullValue()));
    }

    @Test
    public void whenAddEntityDocument()
    {
        Repository repositoryDb = getRepository();
        Author author = new Author();
        author.setId("002");
        author.setName("Nostradamus");
        author.setNationality("FR");
        repositoryDb.add(author);
        
        assertThat(author.getId(), is(repositoryDb.get(Author.class,"002").getId()));
        assertThat(author.getAddAt(), notNullValue());
        assertThat(author.getUpdateAt(), nullValue());
        
        Author authorCallbackCheck = repositoryDb.get(Author.class, author);
        assertThat(authorCallbackCheck.getAddAt(), is(notNullValue()));
        assertThat(authorCallbackCheck.getUpdateAt(), is(nullValue()));
    }
}
