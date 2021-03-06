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
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import net.sf.jkniv.acme.domain.Book;
import net.sf.jkniv.acme.domain.FooColor;
import net.sf.jkniv.exception.HandlerException;

public class PojoInvokeTest
{
    @Test
    public void whenReflectionBuildEnum()
    {
        Invokable call = new PojoInvoker(new HandlerException());
        
        FooColor red = call.invoke(FooColor.class, "RED");
        FooColor blue = call.invoke(FooColor.class, "BLUE");
        FooColor white = call.invoke(FooColor.class, "WHITE");
        FooColor black = call.invoke(FooColor.class, "BLACK");
        
        assertThat(red, is(FooColor.RED));
        assertThat(blue, is(FooColor.BLUE));
        assertThat(white, is(FooColor.WHITE));
        assertThat(black, is(FooColor.BLACK));
    }

    @Test
    public void whenReflectionBuildPojo()
    {
        Invokable call = new PojoInvoker(new HandlerException());
        call.register(new NumberTranslateType(), Integer.class);
        Book book = new Book();
        call.invoke("setId", book, 101);
        call.invoke("setName", book, "Ernest Miller Hemingway");
        call.invoke("setIsbn", book, "ISBN-101202-2017-10");
        
        assertThat(book.getId(), is(101L));
        assertThat(book.getName(), is("Ernest Miller Hemingway"));
        assertThat(book.getIsbn(), is("ISBN-101202-2017-10"));
    }

}
