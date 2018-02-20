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
package net.sf.jkniv.sqlegance.builder.xml;

import net.sf.jkniv.sqlegance.OneToMany;

/**
 * 
 * @author Alisson Gomes
 *
 */
class OneToManyTag implements OneToMany
{
    private String             property;
    private String             typeOf;
    /** implementation class from One-To-Many collections, default is {@code java.util.ArrayList} */
    private String             impl;
    
    public OneToManyTag(String property, String typeOf, String impl)
    {
        super();
        this.property = property;
        this.typeOf = typeOf;
        this.impl = ( ("".equals(impl) || impl == null) ? "java.util.ArrayList" : impl);
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.builder.xml.OneToMany#getProperty()
     */
    @Override
    public String getProperty()
    {
        return property;
    }
    
    public void setProperty(String property)
    {
        this.property = property;
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.builder.xml.OneToMany#getTypeOf()
     */
    @Override
    public String getTypeOf()
    {
        return typeOf;
    }
    
    public void setTypeOf(String typeOf)
    {
        this.typeOf = typeOf;
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.builder.xml.OneToMany#getImpl()
     */
    @Override
    public String getImpl()
    {
        return impl;
    }
    
    public void setImpl(String impl)
    {
        this.impl = impl;
    }
}
