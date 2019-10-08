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
package net.sf.jkniv.sqlegance.dialect;

/**
 * Base class to build {@link SqlFeature}.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
class SqlFeatureBase implements SqlFeature
{
    private final SqlFeatureSupport sqlFeatureSupport;
    private final boolean supports;
    
    public SqlFeatureBase()
    {
        this(SqlFeatureSupport.UNKNOW, false);
    }
    
    public SqlFeatureBase(SqlFeatureSupport sqlFeatureSupport)
    {
        this(sqlFeatureSupport, false);
    }
    
    
    public SqlFeatureBase(SqlFeatureSupport sqlFeatureSupport, boolean supports)
    {
        this.supports = supports;
        this.sqlFeatureSupport = sqlFeatureSupport;
    }

    @Override
    public boolean supports()
    {
        return supports;
    }
    
    @Override
    public String name()
    {
        return this.sqlFeatureSupport.name();
    }

    @Override
    public SqlFeatureSupport getSqlFeature()
    {
        return this.sqlFeatureSupport;
    }
    
    @Override
    public String toString()
    {
        return getClass() + " [name=" + this.sqlFeatureSupport + ", supports=" + supports + "]";
    }
}
