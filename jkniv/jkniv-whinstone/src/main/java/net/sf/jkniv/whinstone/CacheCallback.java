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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jkniv.sqlegance.SqlType;

class CacheCallback
{
    private static Map<String, CallbackMethods> cachePreMethods = new HashMap<String, CallbackMethods>();
    private static Map<String, CallbackMethods> cachePostMethods = new HashMap<String, CallbackMethods>();
    //private static Map<String, CallbackMethods> cachePostCommitMethods = new HashMap<String, CallbackMethods>();
    //sprivate static Map<String, CallbackMethods> cachePostRollbackMethods = new HashMap<String, CallbackMethods>();

    static CallbackMethods getPost(Class<?> clazz, SqlType sqlType)
    {
        return handleGet(clazz, sqlType, cachePostMethods);
    }

    static CallbackMethods getPre(Class<?> clazz, SqlType sqlType)
    {
        return handleGet(clazz, sqlType, cachePreMethods);
    }

    private static CallbackMethods handleGet(
            Class<?> clazz, 
            SqlType sqlType, 
            final Map<String, CallbackMethods> cache)
    {
        String key = buildKey(clazz, sqlType);
        CallbackMethods callbacks = cache.get(key);
        if (callbacks == null)
            return CallbackMethods.EMPTY;
        return callbacks;
    }

    static CallbackMethods putPost(Class<?> clazz, SqlType sqlType, List<Method> methods)
    {
        return handlePut(clazz, sqlType, methods, cachePostMethods);
    }

    static CallbackMethods putPostCommit(Class<?> clazz, SqlType sqlType, List<Method> methods)
    {
        return handlePut(clazz, sqlType, methods, cachePostMethods);
    }

    static CallbackMethods putPre(Class<?> clazz, SqlType sqlType, List<Method> methods)
    {
        return handlePut(clazz, sqlType, methods, cachePreMethods);
    }

    private static CallbackMethods handlePut(
            Class<?> clazz, 
            SqlType sqlType, 
            List<Method> methods,
            final Map<String, CallbackMethods> cache)
    {
        String key = buildKey(clazz, sqlType);
        CallbackMethods callbacks = new CallbackMethods(sqlType, methods);
        CallbackMethods old = cache.put(key, callbacks);
        if (old != null)
            callbacks.getCallbacks().addAll(old.getCallbacks());
        return old;
    }
    

    private static String buildKey(Class<?> clazz, SqlType sqlType)
    {
        return (clazz != null ? clazz.getName() + "." + sqlType : sqlType.name());
    }
}
