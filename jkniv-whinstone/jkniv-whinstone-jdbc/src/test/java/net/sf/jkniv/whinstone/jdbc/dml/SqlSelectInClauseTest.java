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
package net.sf.jkniv.whinstone.jdbc.dml;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jdbc.BaseJdbc;
import net.sf.jkniv.whinstone.jdbc.domain.acme.FlatAuthor;
import net.sf.jkniv.whinstone.jdbc.domain.acme.FlatBook;

public class SqlSelectInClauseTest extends BaseJdbc
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  

    @Autowired
    Repository repositoryDerby;
    
    @Test
    public void whenSelectRecordsUsingInClauseWithArray()
    {
        String[] params =
        { "Albert Camus", "Franz Kafka", "Martin Fowler" };
        Queryable q = QueryFactory.ofArray("getBooksFromAuthorUsingIN", params);
        List<FlatAuthor> list = repositoryDerby.list(q);
        assertThat(list.size(), is(9));
    }

    @Test
    public void whenSelectRecordsUsingINAnotherParam()
    {
        Queryable q = QueryFactory.of("getBooksUsingINAnotherParam", 
                "authors", new String[]{ "Albert Camus", "Franz Kafka", "Martin Fowler" },
                "name", "%The%");
        
        List<FlatAuthor> list = repositoryDerby.list(q);
        assertThat(list.size(), is(4));
    }

    @Test
    public void whenSelectRecordsUsingINAnotherParamAfterIN()
    {
        Queryable q = QueryFactory.of("getBooksUsingINAnotherParamAfterIN", 
                "authors", new String[]{ "Albert Camus", "Franz Kafka", "Martin Fowler" },
                "name", "%The%", "isbn", "978-1612931036");
        
        List<FlatAuthor> list = repositoryDerby.list(q);
        assertThat(list.size(), is(1));
    }

    
    
    @Test
    public void whenSelectRecordsUsingInClauseWithCollections()
    {
        String a = "Albert Camus", b = "Franz Kafka", c = "Martin Fowler";
        List<FlatAuthor> result = null;
        Queryable q = null;
        
        String[] array =
        { a, b, c };
        
        q = QueryFactory.ofArray("getBooksFromAuthorUsingIN", array);
        result = repositoryDerby.list(q);
        assertThat(result.size(), is(9));
        
        List<String> list = new ArrayList<String>();
        list.add(a);
        list.add(b);
        list.add(c);
        q = QueryFactory.of("getBooksFromAuthorUsingIN", list);
        result = repositoryDerby.list(q);
        assertThat(result.size(), is(9));
        
        LinkedList<String> linkedList = new LinkedList<String>();
        linkedList.add(a);
        linkedList.add(b);
        linkedList.add(c);
        q = QueryFactory.of("getBooksFromAuthorUsingIN", linkedList);
        result = repositoryDerby.list(q);
        assertThat(result.size(), is(9));
        
        Vector<String> vector = new Vector<String>();
        vector.add(a);
        vector.add(b);
        vector.add(c);
        q = QueryFactory.of("getBooksFromAuthorUsingIN", vector);
        result = repositoryDerby.list(q);
        assertThat(result.size(), is(9));
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("a", "A");
        map.put("authors", array);
        map.put("c", "A");
        q = QueryFactory.of("getBooksFromAuthorUsingIN", map);
        result = repositoryDerby.list(q);
        assertThat(result.size(), is(9));
        
    }
    
    @Test
    public void whenSelectRecordsUsingInClauseWithWrongParameterType()
    {
        catcher.expect(RepositoryException.class);
        catcher.expectMessage("Cannot prepare statement [Syntax error: Encountered \":\" at line 4, column 22.]");
        
        Map<String, String> map = new HashMap<String, String>();
        map.put("a", "A");
        map.put("b", "A");
        map.put("c", "A");
        Queryable q = QueryFactory.of("getBooksFromAuthorUsingIN", map);
        List<FlatBook> result = repositoryDerby.list(q);
    }
    
}
