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
package net.sf.jkniv.whinstone.couchdb;

import java.util.Properties;

import net.sf.jkniv.sqlegance.Repository;
import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.spi.RepositoryFactory;

public class RepositoryFactoryCouchDb implements RepositoryFactory
{
    
    @Override
    public Repository newInstance()
    {
        return new RepositoryCouchDb();
    }
    
    @Override
    public Repository newInstance(Properties props)
    {
        return new RepositoryCouchDb(props);
    }
    
    @Override
    public Repository newInstance(Properties props, SqlContext sqlContext)
    {
        return new RepositoryCouchDb(props, sqlContext);
    }
    
    @Override
    public Repository newInstance(String sqlContext)
    {
        return new RepositoryCouchDb(sqlContext);
    }
    
    @Override
    public Repository newInstance(SqlContext sqlContext)
    {
        return new RepositoryCouchDb(sqlContext);
    }
    
    @Override
    public RepositoryType getType()
    {
        return RepositoryType.COUCHDB;
    }
    
}
