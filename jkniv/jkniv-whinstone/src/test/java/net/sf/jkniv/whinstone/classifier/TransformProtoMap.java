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
package net.sf.jkniv.whinstone.classifier;

import java.util.Map;

import net.sf.jkniv.reflect.Injectable;
import net.sf.jkniv.reflect.InjectableFactory;
import net.sf.jkniv.reflect.beans.MethodName;
import net.sf.jkniv.reflect.beans.MethodNameFactory;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;

public class TransformProtoMap<T>
{
    private static final MethodName SETTER = MethodNameFactory.getInstanceSetter();

    public T transform(Map<String, Object> row, Class<T> type)
    {
        ObjectProxy<T> proxy = ObjectProxyFactory.newProxy(type);
        Injectable<T> inject = InjectableFactory.newMethodInjection(proxy);
        for(String key : row.keySet())
        {
            Object v = row.get(key);
            inject.inject(SETTER.capitalize(key), v);
        }
        return proxy.getInstance();
    }
    
    public void transform(Map<String, Object> row, T instance)
    {
        ObjectProxy<T> proxy = ObjectProxyFactory.newProxy(instance);
        Injectable<T> inject = InjectableFactory.newMethodInjection(proxy);
        for(String key : row.keySet())
        {
            Object v = row.get(key);
            inject.inject(SETTER.capitalize(key), v);
        }
    }

}
