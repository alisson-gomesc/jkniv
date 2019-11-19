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

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class PropertyAccess
{
    private static final Capitalize CAPITAL_SETTER = MethodNameFactory.getInstanceSetter();
    private static final Capitalize CAPITAL_GETTER = MethodNameFactory.getInstanceGetter();
    
    private String                  fieldName;
    private String                  writerMethod;
    private String                  readMethod;
    private PropertyAccess          inner;
    private Class                   paramType;
    
    public PropertyAccess(String fieldName)
    {
        this(fieldName, null);
    }

    public PropertyAccess(String fieldName, Object paramValue)
    {
//      if (fieldName.indexOf(".") > 0)
//      { 
//          this.inner = new PropertyAccess(fieldName.ssubstring("\\\\."));
//      }
//      else
//      {
          this.fieldName = fieldName;
          this.writerMethod = CAPITAL_SETTER.does(fieldName);
          this.readMethod = CAPITAL_GETTER.does(fieldName);
          this.setParamType(paramValue);
//      }
    }
    
    public String getFieldName()
    {
        if (inner == null)
            return fieldName;
        
        return getFieldName();
    }
    
    public String getWriterMethod()
    {
        if (inner == null)
            return writerMethod;
        return getWriterMethod();
    }
    
    public String getReadMethod()
    {
        if (inner == null)
            return readMethod;
        
        return getReadMethod();
    }
    
    public Class getParamType()
    {
        return paramType;
    }
    
    public void setParamType(Object paramValue)
    {
        
        this.paramType = (paramValue != null ? paramValue.getClass() : null);
    }
}
