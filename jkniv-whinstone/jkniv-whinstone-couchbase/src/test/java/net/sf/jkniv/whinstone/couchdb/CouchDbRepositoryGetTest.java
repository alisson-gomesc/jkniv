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

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.couchdb.model.orm.Author;

@Ignore("delete me test from couchdb")
public class CouchDbRepositoryGetTest extends BaseJdbc
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  

    @Test
    public void whenGetWithBasicType()
    {
        Repository repositoryDb = getRepository();
        Queryable q = getQuery("get", "1");
        
        Map ret = repositoryDb.get(q);
        assertThat(ret, notNullValue());
        assertThat(ret, instanceOf(Map.class));
        assertThat(ret.get("name").toString(), is("Friedrich Nietzsche"));
        System.out.println(ret);
    }

    @Test
    public void whenGetWithIdName()
    {
        Repository repositoryDb = getRepository();
        Queryable q = getQuery("get", asParams("id","1"));
        
        Map ret = repositoryDb.get(q);
        assertThat(ret, notNullValue());
        assertThat(ret, instanceOf(Map.class));
        assertThat(ret.get("name").toString(), is("Friedrich Nietzsche"));
    }

    @Test
    public void whenGetWith_IdName()
    {
        Repository repositoryDb = getRepository();
        Queryable q = getQuery("get", asParams("_id","1"));
        
        Map ret = repositoryDb.get(q);
        assertThat(ret, notNullValue());
        assertThat(ret, instanceOf(Map.class));
        assertThat(ret.get("name").toString(), is("Friedrich Nietzsche"));
    }

    
    @Test
    public void whenGetWithDocidName()
    {
        Repository repositoryDb = getRepository();
        Queryable q = getQuery("get", asParams("docid","1"));
        
        Map ret = repositoryDb.get(q);
        assertThat(ret, notNullValue());
        assertThat(ret, instanceOf(Map.class));
        assertThat(ret.get("name").toString(), is("Friedrich Nietzsche"));
    }

    
    @Test
    public void whenGetWithAnotherIdName()
    {
        catcher.expect(RepositoryException.class);
        catcher.expectMessage("Cannot lookup [ id | _id | docid ] from [QueryName [name=get, offset=0, max=2147483647, timeout=-1, batch=false, scalar=false, paramType=MAP]]");

        Repository repositoryDb = getRepository();
        Queryable q = getQuery("get", asParams("anotherId","1"));
        repositoryDb.get(q);
    }

    
    @Test
    public void whenGetWithReturnType()
    {
        Repository repositoryDb = getRepository();
        Queryable q = getQuery("get", asParams("id","1"));
        
        Author ret = repositoryDb.get(q, Author.class);
        assertThat(ret, notNullValue());
        assertThat(ret, instanceOf(Author.class));
        assertThat(ret.getName(), is("Friedrich Nietzsche"));
    }

    @Test
    public void whenGetWithReturnTypeInternalXml()
    {
        Author author = getRepository().get(QueryFactory.of("author-by-fixedname"));
        
        assertThat(author, notNullValue());
        assertThat(author, instanceOf(Author.class));
        assertThat(author.getName(), is("Friedrich Nietzsche"));
    }

    @Test
    public void whenGetWithEntity()
    {
        Repository repositoryDb = getRepository();
        Author author = new Author();
        author.setId("1");
        Author ret = repositoryDb.get(author);
        assertThat(ret, notNullValue());
        assertThat(ret, instanceOf(Author.class));
        assertThat(ret.getName(), is("Friedrich Nietzsche"));
    }
    
    @Test
    public void whenGetWithParamOfMapReturnEntity()
    {
        Repository repositoryDb = getRepository();
        Object map = asParams("id","1");
        Author ret = repositoryDb.get(Author.class, map);
        assertThat(ret, notNullValue());
        assertThat(ret, instanceOf(Author.class));
        assertThat(ret.getName(), is("Friedrich Nietzsche"));
    }
    
    @Test
    public void whenGetNullReturn()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("authorName", "nationality", "NO_EXIST");
        Map map= repositoryDb.get(q);
        assertThat(map, nullValue());        
    }

}
