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
package net.sf.jkniv.reflect;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;

/**
 * 
 * FIXME Overload methods, Approximately number conversion
 * 
 * @author Alisson Gomes
 *
 * @param <T> Type of Class that have injected value
 */
class MethodInjection<T> implements Injectable<T>
{
    private final static Logger LOG = LoggerFactory.getLogger(MethodInjection.class);
    private final static Assertable notNull = AssertsFactory.getNotNull();
    private ObjectProxy<T> proxy;

    public MethodInjection(T instance)
    {
        this(ObjectProxyFactory.of(instance));
    }

    public MethodInjection(ObjectProxy<T> proxy)
    {
        //this(proxy, false);
        notNull.verify(proxy);
        this.proxy = proxy;
    }

    public void inject(String[] names, Object[] values) // TODO test me inject array of properties
    {
        Map<String, Object> returns = new HashMap<String, Object>();
        for (int i=0; i<names.length;i++)
        {
            Object ret = proxy.invoke(names[i], values[i]);
            if (ret != null)
                returns.put(names[i], ret);
        }
        if (!returns.isEmpty())
        {
            if (LOG.isWarnEnabled())
                LOG.warn("Use [Object inject] to get return value, the methods return values -> " + returns);
        }
        returns.clear();
    }

    public void inject(String[] names, Object[] values, Class<?>[] types)// TODO test me inject array of properties
    {
        Map<String, Object> returns = new HashMap<String, Object>();
        for (int i=0; i<names.length;i++)
        {
            Object ret = proxy.invoke(names[i], values[i], types);
            if (ret != null)
                returns.put(names[i], ret);
        }
        if (!returns.isEmpty())
        {
            if (LOG.isWarnEnabled())
                LOG.warn("Use [public Object inject(..)] to get return value, the methods return values -> " + returns);
        }
        returns.clear();
    }

    public Object inject(String name, Object value)
    {
        if (LOG.isTraceEnabled())
            LOG.trace("Injecting value={} into={} type={}", value, name, (value == null ? "null" : value.getClass().getName()));
        return proxy.invoke(name, value);
    }

    public Object inject(String name, Object value, Class<?> type)
    {
        if (LOG.isTraceEnabled())
            LOG.trace("Injecting value={} into={} type={}", value, name, (type== null ? "null" : type.getName()));
        Class<?> types[] = {type};
        Object[] values = {value};
        return proxy.invoke(name, values, types);
    }

    public Object inject(String name, Date value)
    {
        LOG.trace("Injecting value={} into={} type={}", value, name, "java.util.Date");
        return proxy.invoke(name, new Object[]{value});
        //return proxy.invoke(name, new Object[]{value}, new Class[]{Date.class});
    }
    
    public Object inject(String name, Calendar value)
    {
        LOG.trace("Injecting value={} into={} type={}", value, name, "java.util.Calendar");
        return proxy.invoke(name, new Object[]{value});
        //return proxy.invoke(name, new Object[]{value}, new Class[]{Calendar.class});
    }    

    public Object inject(String name, byte value)
    {
        if (LOG.isTraceEnabled())
            LOG.trace("Injecting value={} into={} type={}", value, name, "byte");
        return proxy.invoke(name, new Object[]{value});
        //return proxy.invoke(name, new Object[]{value}, new Class[]{byte.class});
    }

    public Object inject(String name, short value)
    {
        LOG.trace("Injecting value={} into={} type={}", value, name, "short");
        return proxy.invoke(name, new Object[]{value});
        //return proxy.invoke(name, new Object[]{value}, new Class[]{short.class});
    }

    public Object inject(String name, int value)
    {
        LOG.trace("Injecting value={} into={} type={}", value, name, "int");
        return proxy.invoke(name, new Object[]{value});
        //return proxy.invoke(name, new Object[]{value}, new Class[]{int.class});
    }

    public Object inject(String name, long value)
    {
        LOG.trace("Injecting value={} into={} type={}", value, name, "long");
        return proxy.invoke(name, new Object[]{value});
        //return proxy.invoke(name, new Object[]{value}, new Class[]{long.class});
    }

    public Object inject(String name, float value)
    {
        LOG.trace("Injecting value={} into={} type={}", value, name, "float");
        return proxy.invoke(name, new Object[]{value});
        //return proxy.invoke(name, new Object[]{value}, new Class[]{float.class});
    }

    public Object inject(String name, double value)
    {
        LOG.trace("Injecting value={} into={} type={}", value, name, "double");
        return proxy.invoke(name, new Object[]{value});
        //return proxy.invoke(name, new Object[]{value}, new Class[]{double.class});
    }

    public Object inject(String name, boolean value)
    {
        LOG.trace("Injecting value={} into={} type={}", value, name, "boolean");
        return proxy.invoke(name, new Object[]{value});
        //return proxy.invoke(name, new Object[]{value}, new Class[]{boolean.class});
    }

    public Object inject(String name, char value)
    {
        LOG.trace("Injecting value={} into={} type={}", value, name, "char");
        return proxy.invoke(name, new Object[]{value});
        //return proxy.invoke(name, new Object[]{value}, new Class[]{char.class});
    }    
}
