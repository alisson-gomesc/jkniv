/* 
 * JKNIV ,
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

package net.sf.jkniv.whinstone.jdbc;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.builder.RepositoryConfig;
import net.sf.jkniv.whinstone.ConnectionFactory;
import net.sf.jkniv.whinstone.jdbc.transaction.UnitOfWorkJdbc;
<<<<<<< Upstream, based on origin/0.6.0.M47
=======
import net.sf.jkniv.whinstone.jdbc.transaction.Work;
>>>>>>> 3a27083 whinstone-jdbc move code REMOVE to work with Command and CommandHandler
import net.sf.jkniv.whinstone.jdbc.transaction.WorkJdbc;
import net.sf.jkniv.whinstone.transaction.TransactionContext;
import net.sf.jkniv.whinstone.transaction.TransactionSessions;

class SessionFactory
{
    private static final Logger            LOG   = LoggerFactory.getLogger(RepositoryJdbc.class);
<<<<<<< Upstream, based on origin/0.6.0.M47
    private final static Map<String, WorkJdbc> works = new HashMap<String, WorkJdbc>();
=======
    private final static Map<String, WorkJdbc> WORKS_JDBC = new HashMap<String, WorkJdbc>();
    
    private final static Map<String, Work> WORKS = new HashMap<String, Work>();
>>>>>>> 3a27083 whinstone-jdbc move code REMOVE to work with Command and CommandHandler
    
    /**
     * Start an unit of work for this thread
     */
    public static synchronized WorkJdbc currentWork(ConnectionFactory connectionFactory, RepositoryConfig config)
    {
<<<<<<< Upstream, based on origin/0.6.0.M47
        WorkJdbc work = works.get(Thread.currentThread().getName() + "." + connectionFactory.getContextName());
=======
        WorkJdbc work = WORKS_JDBC.get(Thread.currentThread().getName() + "." + connectionFactory.getContextName());
>>>>>>> 3a27083 whinstone-jdbc move code REMOVE to work with Command and CommandHandler
        if (work == null)
            work = newWork(connectionFactory, config);
        
        return work;
    }
    
    private static WorkJdbc newWork(ConnectionFactory connectionFactory, RepositoryConfig config)
    {
        WorkJdbc work = new UnitOfWorkJdbc(connectionFactory, config);
        if(LOG.isDebugEnabled())
            LOG.debug("Started new {}", work);
        TransactionContext ctx = TransactionSessions.get(config.getName());
        if (ctx != null && ctx.isActive())
            WORKS_JDBC.put(Thread.currentThread().getName() + "." + connectionFactory.getContextName(), work);
        
        return work;
    }
    
    public static synchronized void clear()
    {
        LOG.info("Cleanup {} unit of works", WORKS_JDBC.size());
        for (String key : WORKS_JDBC.keySet())
        {
<<<<<<< Upstream, based on origin/0.6.0.M47
            WorkJdbc w  = works.get(key);
=======
            WorkJdbc w  = WORKS_JDBC.get(key);
>>>>>>> 3a27083 whinstone-jdbc move code REMOVE to work with Command and CommandHandler
            LOG.info("Closing {}", w);
            // TODO implements a gracefully closeable transaction can be in progress
            w.close();
        }
        WORKS_JDBC.clear();
    }
    
}
