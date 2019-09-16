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
package net.sf.jkniv.whinstone;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.lang.reflect.Method;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.domain.flat.AuthorFlat;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.SqlType;

public class CallbackProcessorTest
{

    @Test
    public void whenCallbackProcessoFindAnnotatedMethods()  throws SecurityException, NoSuchMethodException
    {
        ObjectProxy<AuthorFlat> proxy = ObjectProxyFactory.newProxy(AuthorFlat.class); 
        CallbackProcessor processor = new CallbackProcessor(proxy);
        ObjectCallback callback = processor.loadCallbackEvents();
        assertThat(callback, notNullValue());
        
        Method methodPreInsert = AuthorFlat.class.getMethod("callMePreAdd");
        Method methodPreUpdate = AuthorFlat.class.getMethod("callMePreUpdate");
        Method methodPreSelect = AuthorFlat.class.getMethod("callMePreSelect");
        Method methodPreDelete = AuthorFlat.class.getMethod("callMePreRemove");
        Method methodPostInsert = AuthorFlat.class.getMethod("callMePostAdd");
        Method methodPostUpdate = AuthorFlat.class.getMethod("callMePostUpdate");
        Method methodPostSelect = AuthorFlat.class.getMethod("callMePostSelect");
        Method methodPostDelete = AuthorFlat.class.getMethod("callMePostRemove");

        assertThat(callback.getPreMethods(SqlType.INSERT), contains(methodPreInsert));
        assertThat(callback.getPreMethods(SqlType.UPDATE), contains(methodPreUpdate));
        assertThat(callback.getPreMethods(SqlType.SELECT), contains(methodPreSelect));
        assertThat(callback.getPreMethods(SqlType.DELETE), contains(methodPreDelete));

        assertThat(callback.getPostMethods(SqlType.INSERT), contains(methodPostInsert));
        assertThat(callback.getPostMethods(SqlType.UPDATE), contains(methodPostUpdate));
        assertThat(callback.getPostMethods(SqlType.SELECT), contains(methodPostSelect));
        assertThat(callback.getPostMethods(SqlType.DELETE), contains(methodPostDelete));
    }
   
    
    @Test
    public void whenToString()
    {
        ObjectProxy<AuthorFlat> proxy = ObjectProxyFactory.newProxy(AuthorFlat.class); 
        assertThat(new CallbackProcessor(proxy).toString(),notNullValue());
    }
}
