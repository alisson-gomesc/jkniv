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
package net.sf.jkniv.whinstone.couchdb.commands;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.whinstone.couchdb.HttpBuilder;

/**
 * 
 * https://docs.couchdb.org/en/stable/api/database/find.html#db-index
 * 
 * Mango is a declarative JSON querying language for CouchDB databases. 
 * Mango wraps several index types, starting with the Primary Index out-of-the-box. 
 * Mango indexes, with index type json, are built using MapReduce Views.
 *
 * @author Alisson Gomes
 * @since 0.6.6
 */
public class CouchDbSynchIndexDesign
{
    private final static Logger LOG = LoggerFactory.getLogger(CouchDbSynchIndexDesign.class);
    private final SqlContext    sqlContext;
    private final HttpBuilder   httpBuilder;
    
    public CouchDbSynchIndexDesign(final HttpBuilder httpBuilder, final SqlContext sqlContext)
    {
        this.sqlContext = sqlContext;
        this.httpBuilder = httpBuilder;
    }
    
    public void update()
    {
        Map<String, List<Sql>> queries = sqlContext.getPackageStartWith("_design");
        int indexes = 0;
        for (String packet : queries.keySet())
        {
            List<Sql> sqls = queries.get(packet);
            for (Sql sql : sqls)
            {
                String name = null;
                boolean map = true;
                if (sql.getName().startsWith("map#") || sql.getName().startsWith("reduce#"))
                    continue;

                else if(!sql.getName().startsWith("index#"))
                    throw new RepositoryException("Cannot build " + packet
                            + " view from docs. The queries must be start with 'map#' | 'reduce#' | 'index#' name");                
                
                
                name = sql.getName().substring(6);// index#

                DropIndexCommand drop = new DropIndexCommand(httpBuilder, "_design", name);
                drop.execute();
                CreateIndexCommand create = new CreateIndexCommand(httpBuilder, sql.getSql());
                create.execute();
                indexes++;
            }
        }
        if (indexes > 0)
            LOG.info("Host [{}] had created {} indexes", httpBuilder.getHostContext(), indexes);
    }
}
