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
package net.sf.jkniv.whinstone.jpa2;

import java.util.List;

import javax.persistence.Query;

/**
 * Adapter for different Queries from JPA, like: 
 * <ul>
 *  <li>{@code Query}</li>
 *  <li>{@code NativeQuery}</li>
 *  <li>{@code NamedQuery}</li>
 *  <li>{@code TypedQuery}</li>
 * </ul>
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public interface QueryableJpaAdapter
{
    <T> T getSingleResult();
    
    <T> List<T> getResultList();
    
    void setQueryJpaForPaging(Query queryPaging);
    
    Query getQueryJpaForPaging();
    
    int executeUpdate();
    
//    void setTotalPaging(Queryable queryable);
//
//    QueryableJpaAdapter setFirstResult(int offset);
//    
//    QueryableJpaAdapter setOffsetResult(int max);
//    
//    QueryableJpaAdapter setMaxResult(int max);
}
