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
package net.sf.jkniv.whinstone.commands;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.whinstone.CallbackScope;
import net.sf.jkniv.whinstone.PostCallBack;
import net.sf.jkniv.whinstone.PreCallBack;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
class CallbackProcessor
{
    private ObjectProxy<?> proxyParams;
    
    public CallbackProcessor(ObjectProxy<?> proxyParams)
    {
        this.proxyParams = proxyParams;
    }
    
    public ObjectCallback loadCallbackEvents()
    {
        //////ObjectCallback objectCallback = OBJECTS_CALLBACKS.get(proxyParams.getTargetClass().getName());
        //////if (objectCallback == null)
        //////    return;
        ObjectCallback objectCallback = new ObjectCallback(proxyParams.getTargetClass());
        List<Method> precallbacks = proxyParams.getAnnotationMethods(PreCallBack.class);
        List<Method> postcallbacks = proxyParams.getAnnotationMethods(PostCallBack.class);
        for (Method m : precallbacks)
        {
            PreCallBack precallback = m.getAnnotation(PreCallBack.class);
            for (CallbackScope scope : precallback.scope())
            {
                if (scope.isSelect())
                    objectCallback.addPreMethod(SqlType.SELECT, m);
                else if (scope.isAdd())
                    objectCallback.addPreMethod(SqlType.INSERT, m);//preAdd.add(m);
                else if (scope.isUpdate())
                    objectCallback.addPreMethod(SqlType.UPDATE, m);//preUpdate.add(m);
                else if (scope.isRemove())
                    objectCallback.addPreMethod(SqlType.DELETE, m);//preRemove.add(m);
            }
        }
        for (Method m : postcallbacks)
        {
            PostCallBack postcallback = m.getAnnotation(PostCallBack.class);
            List<CallbackScope> scopes = Arrays.asList(postcallback.scope());
            //boolean containsException = scopes.contains(CallbackScope.EXCEPTION);
            //boolean containsCommit = scopes.contains(CallbackScope.COMMIT);
            for (CallbackScope scope : postcallback.scope())
            {
                /*
                if (containsCommit)
                {
                if (scope.isAdd())
                    objectCallback.addCommitMethod(SqlType.INSERT, m);
                else if (scope.isUpdate())
                    objectCallback.addCommitMethod(SqlType.UPDATE, m);
                else if (scope.isRemove())
                    objectCallback.addCommitMethod(SqlType.DELETE, m);
                }
                else if (containsException)
                {
                if (scope.isSelect())
                    objectCallback.addExceptionMethod(SqlType.SELECT, m);
                else if (scope.isAdd())
                    objectCallback.addExceptionMethod(SqlType.INSERT, m);
                else if (scope.isUpdate())
                    objectCallback.addExceptionMethod(SqlType.UPDATE, m);
                else if (scope.isRemove())
                    objectCallback.addExceptionMethod(SqlType.DELETE, m);
                }
                else */if (scope.isSelect())
                    objectCallback.addPostMethod(SqlType.SELECT, m);
                else if (scope.isAdd())
                    objectCallback.addPostMethod(SqlType.INSERT, m);
                else if (scope.isUpdate())
                    objectCallback.addPostMethod(SqlType.UPDATE, m);
                else if (scope.isRemove())
                    objectCallback.addPostMethod(SqlType.DELETE, m);//postRemove.add(m);
            }
        }
        //////OBJECTS_CALLBACKS.put(proxyParams.getTargetClass().getName(), objectCallback);
        return objectCallback;
    }
    
}
