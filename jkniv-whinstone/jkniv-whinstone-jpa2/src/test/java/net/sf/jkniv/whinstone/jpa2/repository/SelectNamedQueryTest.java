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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.acme.domain.orm.Book;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jpa2.BaseTest;

public class SelectNamedQueryTest extends BaseTest
{
    @Test
    @Transactional
    public void selectORMQuery()
    {
        Repository repository = getRepository();

        String name = "Spoke Zarathustra", isbn = "978-0679601753";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        Book b1 = new Book(), b2 = null;
        b1.setName(name);
        b1.setIsbn(isbn);
        repository.add(b1);
        assertThat(b1.getId(), notNullValue());
        
        Queryable q1 = QueryFactory.of("jpql_getBookOrdered");
        Queryable q2 = QueryFactory.of("jpql_getBookByName", params);
        
        List<Book> list = repository.list(q1);
        assertThat(list, notNullValue());
        assertThat(list.isEmpty(), is(false));

        b2 = repository.get(q2);
        assertThat(b2, notNullValue());
        assertThat(b2.getName(), is(name));
        assertThat(b2.getIsbn(), is(isbn));
    }
}
