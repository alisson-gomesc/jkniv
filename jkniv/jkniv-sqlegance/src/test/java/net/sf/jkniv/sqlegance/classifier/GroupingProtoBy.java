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
package net.sf.jkniv.sqlegance.classifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;

public class GroupingProtoBy<T, R>
{
  //  private static final MethodName GETTER = MethodNameFactory.newGetterMethod();
    private final Map<String, T>          grouped;
    private final List<String>            keys;
    private final TransformProtoMap<T>  transform;
    private final Class<T> type;
    
    public GroupingProtoBy(List<String> keys, Class<T> type)
    {
        this.transform = new TransformProtoMap<T>();
        this.grouped = new LinkedHashMap<String, T>();
        this.keys = keys;
        this.type = type;
    }
  
    @SuppressWarnings({"unchecked","rawtypes"})
    public void classifier(R row)
    {
        ObjectProxy<R> rowProxy = ObjectProxyFactory.newProxy(row);
        StringBuilder keySb = new StringBuilder();
        for (String k : this.keys)
        {
            Object keyValue = null;
            if (row instanceof Map)
                keyValue = ((Map)row).get(k);
            else
                keyValue = rowProxy.invoke(k);
            
            if (keyValue != null)
                keySb.append(keyValue.toString());
        }
        String key = keySb.toString();
        T object = grouped.get(key);
        if (object == null)
        {
            object  = (T) transform.transform((Map)row, type);
            grouped.put(key, object);
        }
        else
        {
            transform.transform((Map)row, object);
        }
    }


/*    
    public void collect(R row)
    {
        ObjectProxy<T> proxy = ObjectProxyFactory.newProxy(t);
        StringBuilder keySb = new StringBuilder();
        for (String k : this.keys)
        {
            Object keyValue = proxy.invoke(k);
            if (keyValue != null)
                keySb.append(keyValue.toString());
        }
        String key = keySb.toString();
        T object = grouped.get(keySb.toString());
        if (object == null)
        {
            grouped.put(key, t);
        }
        else
        {
            ObjectProxy<T> proxyOrigin = ObjectProxyFactory.newProxy(object);
            
            
        }
    }
*/
    public Collection<T> build() {
        return grouped.values();
    }
    
    public List<T> buildAsList() {
        return new ArrayList<T>(grouped.values());
    }

    public Set<T> buildAsSet() {
        return new HashSet<T>(grouped.values());
    }

}
