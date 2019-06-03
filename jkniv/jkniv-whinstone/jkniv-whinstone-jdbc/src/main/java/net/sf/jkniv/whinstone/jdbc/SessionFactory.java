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
import net.sf.jkniv.whinstone.jdbc.transaction.UnitOfWork;
import net.sf.jkniv.whinstone.jdbc.transaction.Work;
import net.sf.jkniv.whinstone.transaction.TransactionContext;
import net.sf.jkniv.whinstone.transaction.TransactionSessions;

class SessionFactory
{
    private static final Logger            LOG   = LoggerFactory.getLogger(RepositoryJdbc.class);
    private final static Map<String, Work> works = new HashMap<String, Work>();
    
    /**
     * Start an unit of work for this thread
     */
    public static synchronized Work currentWork(ConnectionFactory connectionFactory, RepositoryConfig config)
    {
        Work work = works.get(Thread.currentThread().getName() + "." + connectionFactory.getContextName());
        if (work == null)
            work = newWork(connectionFactory, config);
        
        return work;
    }
    
    private static Work newWork(ConnectionFactory connectionFactory, RepositoryConfig config)
    {
        Work work = new UnitOfWork(connectionFactory, config);
        if(LOG.isDebugEnabled())
            LOG.debug("Started new {}", work);
        TransactionContext ctx = TransactionSessions.get(config.getName());
        if (ctx != null && ctx.isActive())
            works.put(Thread.currentThread().getName() + "." + connectionFactory.getContextName(), work);
        
        return work;
    }
    
    public static synchronized void clear()
    {
        LOG.info("Cleanup {} unit of works", works.size());
        for (String key : works.keySet())
        {
            Work w  = works.get(key);
            LOG.info("Closing {}", w);
            // TODO implements a gracefully closeable transaction can be in progress
            w.close();
        }
        works.clear();
    }
    
}
