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
    private Object valueAs;// converted value
    private int index;
    private String name;

    public Param() 
    {
    }

    public Param(Object value) 
    {
        this(value, value, "?", 0);
    }

    public Param(Object value, int index)
    {
        this(value, value, "?", index);
    }

    public Param(Object value, String name)
    {
        this(value, value, name, 0);
    }

    public Param(Object value, Object valueAs, String name)
    {
        this(value, valueAs, name, 0);
    }

    public Param(Object value, String name, int index)
    {
        this(value, value, name, index);
    }

    public Param(Object value, Object valueAs, String name, int index)
    {
        super();
        this.value = value;
        this.valueAs = valueAs;
        this.index = index;
        this.name = name;
    }

    public Object getValue()
    {
        return value;
    }
    
    /**
     * get the value converted when there is one, otherwise the original value.
     * @return a converted value or original value.
     */
    public Object getValueAs()
    {
        return valueAs;
    }

    public int getIndex()
    {
        return index;
    }

    public String getName()
    {
        return name;
    }

    public Param[] asArray()
    {
        Param[] params = null;
        if (isArray())
            params = ofArray();
        else if (isCollection())
            params = ofCollection();
        else
            throw new IllegalStateException("Cannot retrieve the parameter as array because is not a collection or array");
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
            params[i] = new Param(o, this.name, i);
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
            params[i] = new Param(o, this.name, i);
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
        return "Param [value=" + value + ", index=" + index + ", name=" + name + "]";
    }
    
    
}
