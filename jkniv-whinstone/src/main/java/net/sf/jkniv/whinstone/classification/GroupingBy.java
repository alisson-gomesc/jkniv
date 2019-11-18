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

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jkniv.reflect.beans.Capitalize;
import net.sf.jkniv.reflect.beans.MethodNameFactory;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.whinstone.classification.Transformable.TransformableType;

/**
 * Create group of objects with a tabular result {@code <R>}.
 * 
 * @param <T> type of grouped objects
 * @param <R> The driver result of a query like {@link ResultSet}
 * 
 * @author alisson
 */
public class GroupingBy<T, R> implements Groupable<T, R>
{
    private static final Capitalize GETTER = MethodNameFactory.getInstanceGetter();
    private final Transformable<R>  transform;
    private final Map<String, T>    grouped;
    private final List<String>      keys;
    private final Class<T>          type;
    
    public GroupingBy(List<String> keys, Class<T> type, Transformable.TransformableType transformableType)
    {
        this.grouped = new LinkedHashMap<String, T>();
        this.keys = keys;
        this.type = type;
        if (transformableType == TransformableType.MAP)
            this.transform = (Transformable<R>) new MapTransform();
        //else if (transformableType == TransformableType.RESULT_SET)
        //    this.transform = (Transformable<R>) new ResultSetTransform();
        else
            this.transform = (Transformable<R>) new ObjectTransform();
    }
    
    public GroupingBy(List<String> keys, Class<T> type, Transformable<R> transform)
    {
        this.grouped = new LinkedHashMap<String, T>();
        this.keys = keys;
        this.type = type;
        this.transform = transform;
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.classifier.Groupable#classifier(R)
     */
    @Override
    @SuppressWarnings({ "rawtypes" })
    public void classifier(R row)
    {
        ObjectProxy<R> rowProxy = ObjectProxyFactory.of(row);
        StringBuilder keySb = new StringBuilder();
        for (String k : this.keys)
        {
            Object keyValue = null;
            if (row instanceof Map)
                keyValue = ((Map) row).get(k);
            else
                keyValue = rowProxy.invoke(GETTER.does(k));
            
            if (keyValue != null)
                keySb.append(keyValue.toString());
        }
        String key = keySb.toString();
        T object = grouped.get(key);
        if (object == null)
        {
            object = transform.transform(row, type);
            grouped.put(key, object);
        }
        else
        {
            //System.out.println("   ROW -> " + row);
            //System.out.println("OBJECT -> " + object);
            transform.transform(row, object);
        }
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.classifier.Groupable#asCollection()
     */
    @Override
    public Collection<T> asCollection()
    {
        return grouped.values();
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.classifier.Groupable#asList()
     */
    @Override
    public List<T> asList()
    {
        return new ArrayList<T>(grouped.values());
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.classifier.Groupable#asSet()
     */
    @Override
    public Set<T> asSet()
    {
        return new HashSet<T>(grouped.values());
    }
    
}
