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

import org.junit.Test;

import com.acme.domain.orm.Book;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jpa2.BaseTest;

public class SelectScalarOperationTest extends BaseTest
{

    @Test
    public void whenGetUseStringAsParameterFromPositionalQueryable()
    {
        Repository repository = getRepository();
        String bookName = "Sentimento do Mundo";
        Book book = repository.get(QueryFactory.of("BookByName", bookName));
        assertThat(book, is(notNullValue()));
        assertThat(book.getName(), is(bookName));
    }
    
    @Test
    public void whenScalarUseStringAsParameterFromPositionalQueryable()
    {
        Repository repository = getRepository();
        String bookName = "Sentimento do Mundo";
        String name = repository.scalar(QueryFactory.of("GetNameBookByName", bookName));
        assertThat(name, is(notNullValue()));
        assertThat(name, instanceOf(String.class));
        assertThat(name, is(bookName));
    }
    
    @Test
    public void whenScalarUseStringAsParameterFromNativePositionalQueryable()
    {
        Repository repository = getRepository();
        String bookName = "Sentimento do Mundo";
        String name = repository.scalar(QueryFactory.of("GetNameBookByNameNative", bookName));
        assertThat(name, is(notNullValue()));
        assertThat(name, instanceOf(String.class));
        assertThat(name, is(bookName));
    }

    @Test
    public void whenScalarUseStringAsParameterFromNameQueryable()
    {
        Repository repository = getRepository();
        String bookName = "Sentimento do Mundo";
        String name = repository.scalar(QueryFactory.of("GetNameBookByName2", bookName));
        assertThat(name, is(notNullValue()));
        assertThat(name, instanceOf(String.class));
        assertThat(name, is(bookName));
    }

}
