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
 * Factory to create new instances of {@link SqlFeature}.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class SqlFeatureFactory 
{

    private SqlFeatureFactory() {}

    /**
     * Create a new instance of {@link SqlFeature}
     * @param name of feature
     * @param supports
     * @return new instance of {@link SqlFeature}
     */
    public static SqlFeature newInstance(SqlFeatureSupport name, boolean supports) 
    {
        return new SqlFeatureBase(name, supports);
    }
    
    /**
     * Create a new instance of {@link SqlFeature}
     * @param name of feature
     * @return new instance of {@link SqlFeature} with supports equals a false
     */
    public static SqlFeature newInstance(SqlFeatureSupport name) 
    {
        return new SqlFeatureBase(name);
    }

}
