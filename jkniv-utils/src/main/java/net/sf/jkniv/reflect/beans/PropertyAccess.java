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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.reflect.ReflectionException;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class PropertyAccess
{
    private static final Capitalize CAPITAL_SETTER = CapitalNameFactory.getInstanceOfSetter();
    private static final Capitalize CAPITAL_GETTER = CapitalNameFactory.getInstanceOfGetter();
    
    private String                  fieldName;
    private String                  writerMethodName;
    private String                  readMethodName;
    private Class<?>                targetClass;
    private boolean                 nestedField;
    private Field                   field;
    private Method                  writerMethod;
    private Method                  readMethod;
    
    public PropertyAccess(String fieldName)
    {
        this(fieldName, null);
    }
    
    public PropertyAccess(String fieldName, Class<?> targetClass)
    {
        this.fieldName = fieldName;
        int index = fieldName.lastIndexOf(".");
        if (index > 0)
        {
            this.nestedField = true;
            this.writerMethodName = CAPITAL_SETTER.does(fieldName.substring(index+1));
            this.readMethodName = CAPITAL_GETTER.does(fieldName.substring(index+1));
        }
        else
        {
            this.writerMethodName = CAPITAL_SETTER.does(fieldName);
            this.readMethodName = CAPITAL_GETTER.does(fieldName);
        }
        resolve(targetClass);
    }
    
    public String getFieldName()
    {
        return this.fieldName;
    }
    
    public Field getField()
    {
        return this.field;
    }
    
    public Method getWriterMethod()
    {
        return this.writerMethod;
    }
    
    public Method getReadMethod()
    {
        return this.readMethod;
    }
    
    public String getWriterMethodName()
    {
        return writerMethodName;
    }
    
    public String getReadMethodName()
    {
        return readMethodName;
    }
    
    public Class<?> getTargetClass()
    {
        return this.targetClass;
    }
    
    private void setTargetClass(Class<?> targetClass)
    {
        if (this.targetClass != null)
            throw new ReflectionException("Cannot re-define a new target class for PropertyAccess");
        
        this.targetClass = targetClass;
    }
    
    public boolean isNestedField()
    {
        return nestedField;
    }
    
    public void resolve(Class<?> targetClass)
    {
        setTargetClass(targetClass);
        if (targetClass != null &&
            !Map.class.isAssignableFrom(targetClass))
        {
            HandleableException handle = new HandlerException();
            handle.mute(NoSuchMethodException.class)
                   .mute(NoSuchFieldException.class);
            
            ObjectProxy<?> proxy = ObjectProxyFactory.of(this.targetClass);
            proxy.with(handle);
            this.field = proxy.getDeclaredField(this.fieldName);
            this.readMethod = new MethodReflect(handle).getMethod(this.fieldName, this.targetClass);
            this.writerMethod = new MethodReflect(handle).getMethod(this.fieldName, this.targetClass, CapitalNameFactory.getInstanceOfSetter());
        }
    }
    
    @Override
    public String toString()
    {
        return "PropertyAccess [fieldName=" + fieldName + ", readMethod=" + readMethod + ", writerMethod="
                + writerMethod + ", targetClass=" + targetClass + "]";
    }
    
}
