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

import java.lang.reflect.Method;
import java.util.Map;

import net.sf.jkniv.exception.HandleableException;

class MapInvoker extends AbstractInvoker implements Invokable
{

    public MapInvoker(HandleableException handleException)
    {
        super(handleException);
    }

    @Override
    public Object invoke(Method method, Object theInstance, Object... values)
    {
        throw new UnsupportedOperationException("Collection Invoke cannot call directly java.lang.reflect.Method");
    }

    @Override
    public <T> T invoke(Class<T> targetClass, Object... values)
    {
        throw new UnsupportedOperationException("Collection Invoke cannot call anything without a instance object");
    }
   
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object invoke(String methodName, Object theInstance, Object... values)
    {
        Object oldValue = null;
        for(Object v : values)
            oldValue = ((Map)theInstance).put(methodName, v);
        
        return oldValue;
    }
    
}
