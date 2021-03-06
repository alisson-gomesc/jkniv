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
package net.sf.jkniv.whinstone.classification;

import java.util.Map;

import net.sf.jkniv.reflect.Injectable;
import net.sf.jkniv.reflect.InjectableFactory;
import net.sf.jkniv.reflect.beans.CapitalNameFactory;
import net.sf.jkniv.reflect.beans.Capitalize;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;

@SuppressWarnings("unchecked")
public class MapTransform implements Transformable<Map<String, Object>>
{
    private static final Capitalize CAPITAL_SETTER = CapitalNameFactory.getInstanceOfSetter();

    /* (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.classifier.Transformable#transform(java.lang.Object, java.lang.Class)
     */
    @Override
    public <T> T transform(Map<String, Object> row, Class<T> type)
    {
        ObjectProxy<?> proxy = ObjectProxyFactory.of(type);
        Injectable<?> inject = InjectableFactory.of(proxy);
        for(String key : row.keySet())
        {
            Object v = row.get(key);
            inject.inject(CAPITAL_SETTER.does(key), v);
        }
        return (T)proxy.getInstance();
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.classifier.Transformable#transform(java.lang.Object, T)
     */
    @Override
    public void transform(Map<String, Object> row, Object instance)
    {
        ObjectProxy<?> proxy = ObjectProxyFactory.of(instance);
        Injectable<?> inject = InjectableFactory.of(proxy);
        for(String key : row.keySet())
        {
            Object v = row.get(key);
            inject.inject(CAPITAL_SETTER.does(key), v);
        }
    }
}
