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
import java.util.List;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.domain.flat.AuthorFlat;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.SqlType;

public class ObjectCallbackTest
{
    @Rule
    public ExpectedException  catcher = ExpectedException.none();

    @Test
    public void whenSetTagertClass() 
    {
        ObjectCallback callback = new ObjectCallback(getClass());
        assertThat(callback.getTargetClass().getName(), is(ObjectCallbackTest.class.getName()));
        callback.toString();
        //assertThat(callback.getGoalClass(), instanceOf(Class.forName(ObjectCallbackTest.class.getName())));
    }

    @Test
    public void whenSetNullTargetClass() 
    {
        catcher.expect(IllegalArgumentException.class);
        catcher.expectMessage("[Assertion failed] - this argument is required; it must not be null");
        new ObjectCallback(null);
    }

    @Test
    public void whenCheckMethodPreInvoke() throws SecurityException, NoSuchMethodException 
    {
        ObjectCallback callback = new ObjectCallback(AuthorFlat.class);
        Method methodPreInsert = AuthorFlat.class.getMethod("callMePreAdd");
        Method methodPreUpdate = AuthorFlat.class.getMethod("callMePreUpdate");
        Method methodPreSelect = AuthorFlat.class.getMethod("callMePreSelect");
        Method methodPreDelete = AuthorFlat.class.getMethod("callMePreRemove");
        
        callback.addPreMethod(SqlType.INSERT, methodPreInsert);
        callback.addPreMethod(SqlType.UPDATE, methodPreUpdate);
        callback.addPreMethod(SqlType.SELECT, methodPreSelect);
        callback.addPreMethod(SqlType.DELETE, methodPreDelete);
        
        assertThat(callback.getPreMethods(SqlType.INSERT), contains(methodPreInsert));
        assertThat(callback.getPreMethods(SqlType.UPDATE), contains(methodPreUpdate));
        assertThat(callback.getPreMethods(SqlType.SELECT), contains(methodPreSelect));
        assertThat(callback.getPreMethods(SqlType.DELETE), contains(methodPreDelete));
        
        assertThat(callback.getPreMethods(SqlType.DELETE), contains(methodPreDelete));
    }

    @Test
    public void whenCheckMethodPostInvoke() throws SecurityException, NoSuchMethodException 
    {
        ObjectCallback callback = new ObjectCallback(AuthorFlat.class);
        Method methodPostInsert = AuthorFlat.class.getMethod("callMePostAdd");
        Method methodPostUpdate = AuthorFlat.class.getMethod("callMePostUpdate");
        Method methodPostSelect = AuthorFlat.class.getMethod("callMePostSelect");
        Method methodPostDelete = AuthorFlat.class.getMethod("callMePostRemove");
        
        callback.addPostMethod(SqlType.INSERT, methodPostInsert);
        callback.addPostMethod(SqlType.UPDATE, methodPostUpdate);
        callback.addPostMethod(SqlType.SELECT, methodPostSelect);
        callback.addPostMethod(SqlType.DELETE, methodPostDelete);
        
        assertThat(callback.getPostMethods(SqlType.INSERT), contains(methodPostInsert));
        assertThat(callback.getPostMethods(SqlType.UPDATE), contains(methodPostUpdate));
        assertThat(callback.getPostMethods(SqlType.SELECT), contains(methodPostSelect));
        assertThat(callback.getPostMethods(SqlType.DELETE), contains(methodPostDelete));
    }
    
    @Test
    public void whenReplaceCommitMethod() throws SecurityException, NoSuchMethodException 
    {
        ObjectCallback callback = new ObjectCallback(AuthorFlat.class);
        Method methodPostInsert = AuthorFlat.class.getMethod("callMePostAdd");
        Method methodPostUpdate = AuthorFlat.class.getMethod("callMePostUpdate");
        callback.addCommitMethod(SqlType.INSERT, methodPostInsert);
        callback.addCommitMethod(SqlType.INSERT, methodPostUpdate);

        assertThat(callback.getCommitMethod(SqlType.INSERT), is(methodPostUpdate));
    }

    @Test
    public void whenReplaceExceptionMethod() throws SecurityException, NoSuchMethodException 
    {
        ObjectCallback callback = new ObjectCallback(AuthorFlat.class);
        Method methodPostInsert = AuthorFlat.class.getMethod("callMePostAdd");
        Method methodPostUpdate = AuthorFlat.class.getMethod("callMePostUpdate");
        callback.addExceptionMethod(SqlType.INSERT, methodPostInsert);
        callback.addExceptionMethod(SqlType.INSERT, methodPostUpdate);

        assertThat(callback.getExceptionMethod(SqlType.INSERT), is(methodPostUpdate));
    }

    @Test
    public void whenObjectCallbackReturnEmptyData()
    {
        ObjectCallback callback = new ObjectCallback(getClass());
        Set<Method> methods = null;
        Method method = null;
        methods = callback.getPreMethods(SqlType.SELECT);
        assertThat(methods, notNullValue());
        assertThat(methods.isEmpty(), is(true));
        
        methods = callback.getPreMethods(SqlType.INSERT);
        assertThat(methods, notNullValue());
        assertThat(methods.isEmpty(), is(true));
        
        methods = callback.getPreMethods(SqlType.UPDATE);
        assertThat(methods, notNullValue());
        assertThat(methods.isEmpty(), is(true));
        
        methods = callback.getPreMethods(SqlType.DELETE);
        assertThat(methods, notNullValue());
        assertThat(methods.isEmpty(), is(true));

        methods = callback.getPostMethods(SqlType.SELECT);
        assertThat(methods, notNullValue());
        assertThat(methods.isEmpty(), is(true));
        
        methods = callback.getPostMethods(SqlType.INSERT);
        assertThat(methods, notNullValue());
        assertThat(methods.isEmpty(), is(true));
        
        methods = callback.getPostMethods(SqlType.UPDATE);
        assertThat(methods, notNullValue());
        assertThat(methods.isEmpty(), is(true));
        
        methods = callback.getPostMethods(SqlType.DELETE);
        assertThat(methods, notNullValue());
        assertThat(methods.isEmpty(), is(true));

        method = callback.getCommitMethod(SqlType.SELECT);
        assertThat(method, nullValue());
        method = callback.getCommitMethod(SqlType.INSERT);
        assertThat(method, nullValue());
        method = callback.getCommitMethod(SqlType.UPDATE);
        assertThat(method, nullValue());
        method = callback.getCommitMethod(SqlType.DELETE);
        assertThat(method, nullValue());
        
        method = callback.getExceptionMethod(SqlType.SELECT);
        assertThat(method, nullValue());
        method = callback.getExceptionMethod(SqlType.INSERT);
        assertThat(method, nullValue());
        method = callback.getExceptionMethod(SqlType.UPDATE);
        assertThat(method, nullValue());
        method = callback.getExceptionMethod(SqlType.DELETE);
        assertThat(method, nullValue());
    }
}
