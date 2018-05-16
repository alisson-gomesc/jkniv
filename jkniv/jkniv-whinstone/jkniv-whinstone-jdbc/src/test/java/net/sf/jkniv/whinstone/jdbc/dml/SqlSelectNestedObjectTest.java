/* 
 * JKNIV, whinstone one contract to access your database.
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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jdbc.BaseJdbc;
import net.sf.jkniv.whinstone.jdbc.acme.domain.Book;

public class SqlSelectNestedObjectTest extends BaseJdbc
{
    @Autowired
    Repository repositoryDerby;

    @Test
    public void whenSelectNestedObjectsWorks()
    {
        Queryable q = QueryFactory.newInstance("listNestedBooks");
        List<Book> list = repositoryDerby.list(q);
        for(Book b : list)
        {
            System.out.println(b);
            assertThat(b, notNullValue());
            assertThat(b.getAuthor(), notNullValue());
            assertThat(b.getAuthor().getId(), notNullValue());
            assertThat(b.getAuthor().getName(), notNullValue());
            assertThat(b.getAuthor().getBooks(), notNullValue());
            assertThat(b.getAuthor().getBooks().size(), is(0));
        }
        
    }

    
}
