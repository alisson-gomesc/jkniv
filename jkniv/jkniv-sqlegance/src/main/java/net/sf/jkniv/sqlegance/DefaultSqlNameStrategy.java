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
package net.sf.jkniv.sqlegance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Alisson Gomes
 * 
 * @deprecated use DotQueryNameStrategy that is compatibility with this class
 */
public class DefaultSqlNameStrategy implements ISqlNameStrategy
{
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSqlNameStrategy.class);
    
    public DefaultSqlNameStrategy()
    {
        LOG.warn("DON'T use this class [{}] WILL BE REMOVED at 1.0.0 version", getClass().getCanonicalName());
    }
    
    public String toGetName(Object o)
    {
        return o.getClass().getSimpleName() + ".get";
    }
    
    public String toAddName(Object o)
    {
        return o.getClass().getSimpleName() + ".add";
    }
    
    public String toRemoveName(Object o)
    {
        return o.getClass().getSimpleName() + ".remove";
    }
    
    public String toUpdateName(Object o)
    {
        return o.getClass().getSimpleName() + ".update";
    }
    
    public String toListName(Object o)
    {
        return o.getClass().getSimpleName() + ".list";
    }
}
