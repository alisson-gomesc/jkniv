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
package net.sf.jkniv.whinstone.jpa2.repository;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jpa2.BaseTest;

public class SelectWithMapTest extends BaseTest
{
    
    @Test
    public void whenJpaReturnLisOfMaps()
    {
        Repository repository = getRepository();
        List<Map<String, Object>> books = repository.list(QueryFactory.of("BookAsMap"));
        assertThat(books, is(notNullValue()));
        assertThat(books.size(), is(15));
        assertThat(books.get(0), is(instanceOf(Map.class)));
        assertThat(books.get(0).containsKey("id"), is(true));
        assertThat(books.get(0).containsKey("name"), is(true));
        assertThat(books.get(0).containsKey("isbn"), is(true));
    }
    
    public void whenJpaReturnLisOfMapsNativeQuery()
    {
        Repository repository = getRepository();
        List<Map<String, Object>> books = repository.list(QueryFactory.of("BookAsMapNative"));
        assertThat(books, is(notNullValue()));
        assertThat(books.size(), is(15));
        assertThat(books.get(0), is(instanceOf(Map.class)));
        assertThat(books.get(0).containsKey("id"), is(true));
        assertThat(books.get(0).containsKey("name"), is(true));
        assertThat(books.get(0).containsKey("isbn"), is(true));
    }
    // TODO more test with select columns JPA
    
}
