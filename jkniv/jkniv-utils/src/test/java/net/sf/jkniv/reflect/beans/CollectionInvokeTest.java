/* 
 * JKNIV, utils - Helper utilities for jdk code.
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
package net.sf.jkniv.reflect.beans;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.acme.domain.Author;
import net.sf.jkniv.exception.HandlerException;

public class CollectionInvokeTest
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  

    @Test
    public void whenReflectionBuilBasicCollection()
    {
        Invokable call = new CollectionInvoker(new HandlerException());
        Collection<String> col = new ArrayList<String>();
        
        Object ret = call.invoke("add", col, "RED");
        assertThat(ret.toString(), is("true"));
        assertThat(col.size(), is(1));
        String ref = col.iterator().next();
        assertThat(ref, is(notNullValue()));
        assertThat(ref, instanceOf(String.class));
        assertThat(ref, is("RED"));
    }

    @Test
    public void whenReflectionBuilPojoCollection()
    {
        Invokable call = new CollectionInvoker(new HandlerException());
        Collection<Author> col = new ArrayList<Author>();
        Author author = new Author("john");
        
        Object ret = call.invoke("add", col, author);
        assertThat(ret.toString(), is("true"));
        assertThat(col.size(), is(1));
        Author ref = col.iterator().next();
        assertThat(author, is(notNullValue()));
        assertThat(ref, instanceOf(Author.class));;
        assertThat(author.getName(), is("john"));
    }

    @Test
    public void whenReflectionCollectionInvokeBasicMethodUnsupportedOperation()
    {
        Invokable call = new CollectionInvoker(new HandlerException());
        catcher.expect(UnsupportedOperationException.class);
        call.invoke(String.class, "hello Exception");
    }
    
    @Test
    public void whenReflectionCollectionInvokeMethodUnsupportedOperation() throws SecurityException, NoSuchMethodException
    {
        Invokable call = new CollectionInvoker(new HandlerException());
        catcher.expect(UnsupportedOperationException.class);
        call.invoke(Map.class.getMethod("size"), "hello Exception");
    }

}
