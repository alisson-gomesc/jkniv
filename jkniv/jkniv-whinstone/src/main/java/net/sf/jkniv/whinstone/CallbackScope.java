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

/**
 * Enumeration for possible scope of execution from callback methods.
 */
public enum CallbackScope
{
    /**
     * Value that means that a callback method is invoked before {@code add} {@link Repository#add(Queryable)}
     * or {@link Repository#add(Object)} methods
     */
    ADD,
    /**
     * Value that means that a callback method is invoked before {@code update} {@link Repository#update(Queryable)}
     * or {@link Repository#update(Object)} methods
     */
    UPDATE,
    /**
     * Value that means that a callback method is invoked before {@code remove} {@link Repository#remove(Queryable)}
     * or {@link Repository#remove(Object)} methods
     */
    REMOVE,
    /**
     * Value that means that a callback method is invoked before {@code get} and {@code list} methods
     * like: {@link Repository#get(Queryable)}, {@link Repository#get(Object)}, {@link Repository#list(Queryable)}
     * etc.
     */
    SELECT,
    /*
     * Value that means that a callback method is invoked after an {@code exception} is generate, this annotation
     * must be followed with another scope like {@code ADD}, {@code UPDATE}, {@code REMOVE} or {@code SELECT}.
     */
    //EXCEPTION,
    /*
     * Value that means that a callback method is invoked after an successful {@code COMMIT} is execute, this annotation
     * must be followed with another scope like {@code ADD}, {@code UPDATE}, {@code REMOVE} or {@code SELECT}.
     */
    //COMMIT,
    /**
     * Value that indicates that no callback method is invoked.
     */
    NONE;
    
    public boolean isAdd()
    {
        return (this == ADD ? true : false);
    }
    
    public boolean isUpdate()
    {
        return (this == UPDATE ? true : false);
    }
    
    public boolean isRemove()
    {
        return (this == REMOVE ? true : false);
    }
    
    public boolean isSelect()
    {
        return (this == SELECT ? true : false);
    }
    /*
    public boolean isException()
    {
        return (this == CallbackScope.EXCEPTION ? true : false);
    }
    
    public boolean isCommit()
    {
        return (this == COMMIT ? true : false);
    }
    */    
}
