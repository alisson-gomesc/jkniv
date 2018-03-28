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

import java.util.HashMap;
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
 * FIXME design CouchDb 
 * http://docs.couchdb.org/en/2.0.0/api/ddoc/common.html
 *  PUT /{db}/_design/{ddoc}
 *
 *  The PUT method creates a new named design document, or creates a new revision of the existing design document.
 *
 *  The design documents have some agreement upon their fields and structure. Currently it is the following:
 *
 *      language (string): Defines Query Server key to process design document functions
 *      options (object): View’s default options
 *      filters (object): Filter functions definition
 *      lists (object): List functions definition
 *      rewrites (array or string): Rewrite rules definition
 *      shows (object): Show functions definition
 *      updates (object): Update functions definition
 *      validate_doc_update (string): Validate document update function source
 *      views (object): View functions definition.
 *
 *  Note, that for filters, lists, shows and updates fields objects are mapping of function name to string function source code. 
 *  For views mapping is the same except that values are objects with map and reduce (optional) keys which also contains 
 *  functions source code.
 *
 *
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class CouchDbSynchViewDesign
{
    private final static Logger LOG = LoggerFactory.getLogger(CouchDbSynchViewDesign.class);
    private final SqlContext    sqlContext;
    private final HttpBuilder   httpBuilder;
    
    public CouchDbSynchViewDesign(final HttpBuilder httpBuilder, final SqlContext sqlContext)
    {
        this.sqlContext = sqlContext;
        this.httpBuilder = httpBuilder;
    }
    
    public void update()
    {
        Map<String, List<Sql>> queries = sqlContext.getPackageStartWith("_design");
        for (String packet : queries.keySet())
        {
            List<Sql> sqls = queries.get(packet);
            DesignCommand command = new DesignCommand(httpBuilder, packet);
            Map<String, ViewFunction> views = new HashMap<String, ViewFunction>();
            for (Sql sql : sqls)
            {
                String name = null;
                boolean map = true;
                if (sql.getName().startsWith("map#"))
                    name = sql.getName().substring(4);
                else if (sql.getName().startsWith("reduce#"))
                {
                    name = sql.getName().substring(7);
                    map = false;
                }
                else
                    throw new RepositoryException("Cannot build " + packet
                            + " view from docs. The queries must be start with 'map#' or 'reduce#' name");
                
                ViewFunction view = views.get(name);
                if (view == null)
                {
                    view = new ViewFunction(name);
                    views.put(name, view);
                }
                if (map)
                    view.setMapfun(sql.getSql());
                else
                    view.setRedfun(sql.getSql());
            }
            command.add(views.values());
            command.closeBody();
            command.execute();
            //LOG.info("Updating view _design with view [{}]", sql.getName());
        }
        LOG.info("Host [{}] had [{}] views design updated", httpBuilder.getHostContext(), queries.size());
    }
    
    //    private void update(Sql view)
    //    {
    //        LOG.debug("\n{}", view.getSql());
    //        try
    //        {
    //            new DesignCommand(httpBuilder, view).execute();
    //        }
    //        catch(RepositoryException e)
    //        {
    //            LOG.error("Cannot create or update the view [{}]", view.getName(), e);
    //        }
    //    }
    
}
