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


import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Method;
import java.util.Set;

import org.junit.Test;

import net.sf.jkniv.sqlegance.SqlType;

public class ObjectCallbackTest
{
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
