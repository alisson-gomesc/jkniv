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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.acme.domain.Author;
import net.sf.jkniv.exception.HandlerException;

public class MapInvokeTest
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  

    @Test
    public void whenReflectionBuilBasicMap()
    {
        Invokable call = new MapInvoker(new HandlerException());
        Map<String, String> map = new HashMap<String, String>();
        
        Object ret = call.invoke("color", map, "RED");
        
        Entry<String, String> ref = map.entrySet().iterator().next();
        
        assertThat(ret, is(nullValue()));
        assertThat(map.size(), is(1));
        assertThat(ref, is(notNullValue()));
        assertThat(ref, instanceOf(Entry.class));
        assertThat(ref.getKey(), instanceOf(String.class));
        assertThat(ref.getValue(), instanceOf(String.class));
        assertThat(ref.getKey(), is("color"));
        assertThat(ref.getValue(), is("RED"));        
    }

    @Test
    public void whenReflectionBuilPojoMap()
    {
        Invokable call = new MapInvoker(new HandlerException());
        Map<String, Author> map = new HashMap<String, Author>();
        Author author = new Author("john");
        
        Object ret = call.invoke("author", map, author);

        Entry<String, Author> ref = map.entrySet().iterator().next();

        assertThat(ret, is(nullValue()));
        assertThat(map.size(), is(1));
        assertThat(ref, is(notNullValue()));
        assertThat(ref, instanceOf(Entry.class));
        assertThat(ref.getKey(), instanceOf(String.class));
        assertThat(ref.getValue(), instanceOf(Author.class));

        assertThat(ref.getKey(), is("author"));
        assertThat(ref.getValue().getName(), is("john"));        

        Author yoko = new Author("yoko");
        ret = call.invoke("author", map, yoko);

        ref = map.entrySet().iterator().next();

        assertThat(map.size(), is(1));
        assertThat(ret, is(notNullValue()));
        assertThat(ret, instanceOf(Author.class));
        assertThat(((Author)ret).getName(), is("john"));        

        assertThat(ref.getKey(), is("author"));
        assertThat(ref.getValue().getName(), is("yoko"));        
    }
    
    @Test
    public void whenReflectionMapInvokeBasicMethodUnsupportedOperation()
    {
        Invokable call = new MapInvoker(new HandlerException());
        catcher.expect(UnsupportedOperationException.class);
        call.invoke(String.class, "hello Exception");
    }
    
    @Test
    public void whenReflectionMapInvokeMethodUnsupportedOperation() throws SecurityException, NoSuchMethodException
    {
        Invokable call = new MapInvoker(new HandlerException());
        catcher.expect(UnsupportedOperationException.class);
        call.invoke(Map.class.getMethod("size"), "hello Exception");
    }


}
