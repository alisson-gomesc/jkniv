/* 
 * JKNIV, utils - Helper utilities for jdk code.
 * 
 * Copyright (C) 2017, the original author or authors.
 *
 * This library is free software; you can re of the GNU Lesser General Public
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
package net.sf.jkniv.reflect;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;


import java.lang.reflect.Method;

import net.sf.jkniv.acme.domain.Author;
import net.sf.jkniv.acme.domain.Book;

import org.junit.Test;

public class ReflectionUtilsTest
{
    
    @Test
    public void whenMakeClassFromStringWithReflection()
    {
        Object author = null, book = null;
        
        book = ReflectionUtils.forName(Book.class.getName());
        assertThat("Cannot define Class<Book>", Book.class.toString(), is(book.toString()));
        author = ReflectionUtils.forName(Author.class.getName());
        assertThat("Cannot define Class<Author>", Author.class.toString(), is(author.toString()));
    }
    
    @Test
    public void whenCreateNewInstanceWithReflection()
    {
        Object author = null, book = null;
        
        book = ReflectionUtils.newInstance(Book.class);
        assertThat("Cannot create a new instanceof Book", book, instanceOf(Book.class));
        author = ReflectionUtils.newInstance(Author.class);
        assertThat("Cannot create a new instanceof Author", author, instanceOf(Author.class));
    }

    @Test 
    public void whenStaticMethodIsGettedTest() throws SecurityException, NoSuchMethodException 
    {
        Method m = Double.class.getMethod("valueOf", String.class);
        assertThat(m.getName(), is("valueOf"));
    }
}
