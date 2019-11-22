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

import java.util.Collection;

/**
 * Represents the parameter from {@link Queryable}.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class Param
{
    private Object value;
    private int index;
    private String name;
    private Class<?> ownerClass;

    public Param() 
    {
    }

    public Param(Object value) 
    {
        this(value, "?");
    }

    public Param(Object value, int index)
    {
        this(value, index, "?");
    }

    public Param(Object value, String name)
    {
        super();
        this.value = value;
        this.name = name;
    }

    public Param(Object value, int index, String name)
    {
        super();
        this.value = value;
        this.index = index;
        this.name = name;
    }

    public Param(Object value, int index, String name, Class<?> ownerClass)
    {
        super();
        this.value = value;
        this.index = index;
        this.name = name;
        this.ownerClass = ownerClass;
    }

    public Object getValue()
    {
        return value;
    }

    public int getIndex()
    {
        return index;
    }

    public String getName()
    {
        return name;
    }

    public Class<?> getOwnerClass()
    {
        return ownerClass;
    }
    
    public Param[] asArray()
    {
        Param[] params = null;
        if (isArray())
            params = ofArray();
        else if (isCollection())
            params = ofCollection();
        return params;
    }
    
    public boolean isArray()
    {
        return (value != null && value.getClass().isArray());
    }
    
    public boolean isCollection()
    {
        return (value instanceof Collection);
    }
    
    private Param[] ofCollection()
    {
        Collection<?> paramsAsCollection = (Collection<?>)this.value;
        Param[] params = new Param[paramsAsCollection.size()];
        int i = 0;
        for(Object o : paramsAsCollection)
        {
            params[i] = new Param(o, i, this.name);
            i++;
        }
        return params;
    }

    private Param[] ofArray()
    {
        Object[] paramsAsArray = (Object[])this.value;
        Param[] params = new Param[paramsAsArray.length];
        int i = 0;
        for(Object o : paramsAsArray)
        {
            params[i] = new Param(o, i, this.name);
            i++;
        }
        return params;
    }    
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((ownerClass == null) ? 0 : ownerClass.getName().hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Param other = (Param) obj;
        if (name == null)
        {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        if (ownerClass == null)
        {
            if (other.ownerClass != null)
                return false;
        }
        else if (other.ownerClass != null && !ownerClass.getName().equals(other.ownerClass.getName()))
            return false;
        if (value == null)
        {
            if (other.value != null)
                return false;
        }
        else if (!value.equals(other.value))
            return false;
        return true;
    }
    
    
    @Override
    public String toString()
    {
        return "Param [value=" + value + ", index=" + index + ", name=" + name + ", ownerClass=" + ownerClass + "]";
    }
    
    
}
