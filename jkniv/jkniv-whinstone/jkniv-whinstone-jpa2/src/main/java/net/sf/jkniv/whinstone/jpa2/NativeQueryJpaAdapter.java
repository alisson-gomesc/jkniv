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

import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.whinstone.Queryable;

class NativeQueryJpaAdapter extends AbstractQueryJpaAdapter
{
    private Query queryJpa;
    
    public NativeQueryJpaAdapter(EntityManager em, Queryable queryable, Sql isql)
    {
        super(em, queryable, isql);
    }

    @Override
    public <T> T getSingleResult()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public <T> List<T> getResultList()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
