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
package net.sf.jkniv.whinstone.classification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoGroupingBy<T, R> implements Groupable<T, R>
{
    private final List<T>    grouped;
    
    public NoGroupingBy()
    {
        this.grouped = new ArrayList<T>();
    }
    
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.classifier.Groupable#classifier(R)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void classifier(R row)
    {
        this.grouped.add((T)row);
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.classifier.Groupable#asCollection()
     */
    @Override
    public Collection<T> asCollection()
    {
        return grouped;
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.classifier.Groupable#asList()
     */
    @Override
    public List<T> asList()
    {
        return grouped;
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.classifier.Groupable#asSet()
     */
    @Override
    public Set<T> asSet()
    {
        return new HashSet<T>(grouped);
    }
    
}
