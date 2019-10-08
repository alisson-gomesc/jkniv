/* 
 * JKNIV, SQLegance keeping queries maintainable.
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
package net.sf.jkniv.whinstone.annotation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import net.sf.jkniv.whinstone.domain.orm.Cat;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.whinstone.CallbackScope;
import net.sf.jkniv.whinstone.PostCallBack;
import net.sf.jkniv.whinstone.PreCallBack;

public class AnnotationTest
{
    @Test
    public void whenReconizeAllCallbackMethods()
    {
        ObjectProxy<Cat> proxy = ObjectProxyFactory.of(Cat.class);
        
        List<Method> precallbacks = proxy.getAnnotationMethods(PreCallBack.class);
        List<Method> postcallbacks = proxy.getAnnotationMethods(PostCallBack.class);
        
        List<Method> preAdd = new ArrayList<Method>();
        List<Method> postAdd = new ArrayList<Method>();
        List<Method> preSelect = new ArrayList<Method>();
        List<Method> postSelect = new ArrayList<Method>();
        List<Method> preUpdate = new ArrayList<Method>();
        List<Method> postUpdate = new ArrayList<Method>();
        List<Method> preRemove = new ArrayList<Method>();
        List<Method> postRemove = new ArrayList<Method>();
        List<Method> postAddException = new ArrayList<Method>();
        List<Method> postAddCommit = new ArrayList<Method>();
        List<Method> postSelectException = new ArrayList<Method>();
        List<Method> postUpdateException = new ArrayList<Method>();
        List<Method> postUpdateCommit = new ArrayList<Method>();
        List<Method> postRemoveException = new ArrayList<Method>();
        List<Method> postRemoveCommit = new ArrayList<Method>();
        
        for (Method m : precallbacks)
        {
            PreCallBack precallback = m.getAnnotation(PreCallBack.class);
            for (CallbackScope scope : precallback.scope())
            {
                if (scope.isSelect())
                    preSelect.add(m);
                else if (scope.isAdd())
                    preAdd.add(m);
                else if (scope.isUpdate())
                    preUpdate.add(m);
                else if (scope.isRemove())
                    preRemove.add(m);
            }
        }
        for (Method m : postcallbacks)
        {
            PostCallBack postcallback = m.getAnnotation(PostCallBack.class);
            List<CallbackScope> scopes = Arrays.asList(postcallback.scope());
            boolean containsException = false;//scopes.contains(CallbackScope.EXCEPTION);
            boolean containsCommit = false;//scopes.contains(CallbackScope.COMMIT);
            for (CallbackScope scope : postcallback.scope())
            {
                if (containsCommit)
                {
                    if (scope.isAdd())
                        postAddCommit.add(m);
                    else if (scope.isUpdate())
                        postUpdateCommit.add(m);
                    else if (scope.isRemove())
                        postRemoveCommit.add(m);
                }
                else if (containsException)
                {
                    if (scope.isSelect())
                        postSelectException.add(m);
                    else if (scope.isAdd())
                        postAddException.add(m);
                    else if (scope.isUpdate())
                        postUpdateException.add(m);
                    else if (scope.isRemove())
                        postRemoveException.add(m);
                    
                }
                else if (scope.isSelect())
                    postSelect.add(m);
                else if (scope.isAdd())
                    postAdd.add(m);
                else if (scope.isUpdate())
                    postUpdate.add(m);
                else if (scope.isRemove())
                    postRemove.add(m);
            }
        }
        assertThat(preAdd.size(), is(1));
        assertThat(preSelect.size(), is(1));
        assertThat(preUpdate.size(), is(2));
        assertThat(preRemove.size(), is(1));
        assertThat(postAdd.size(), is(1));
        assertThat(postSelect.size(), is(1));
        assertThat(postUpdate.size(), is(1));
        assertThat(postRemove.size(), is(1));
        /*
        assertThat(postAddException.size(), is(1));
        assertThat(postAddCommit.size(), is(1));
        assertThat(postSelectException.size(), is(1));
        assertThat(postUpdateException.size(), is(1));
        assertThat(postUpdateCommit.size(), is(1));
        assertThat(postRemoveException.size(), is(1));
        assertThat(postRemoveCommit.size(), is(1));
        */
    }
}
