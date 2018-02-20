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
package net.sf.jkniv.sqlegance.builder;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.DefaultClassLoader;

class ReloadableXmlResource
{
    private static final Logger            LOG                 = LoggerFactory.getLogger(ReloadableXmlResource.class);
    private static final int               WAIT_TIME_TO_CANCEL = 3;
    private static final TimeUnit          TIME                = TimeUnit.HOURS;
    private final ScheduledExecutorService scheduler           = Executors.newScheduledThreadPool(1);
    
    public void pooling(ClassPathSqlContext context)
    {
        final Runnable watching = new WatchResources(context);
        
        final ScheduledFuture<?> poolScheduler = scheduler.scheduleAtFixedRate(watching, 5, 5, TimeUnit.SECONDS);
        LOG.info("Watching XML resources for next {} {} to load changes", WAIT_TIME_TO_CANCEL, TIME);
        scheduler.schedule(new Runnable()
        {
            public void run()
            {
                LOG.info("Cancel watching XML resources after {} {} execution.", WAIT_TIME_TO_CANCEL, TIME);
                poolScheduler.cancel(true);
            }
        }, WAIT_TIME_TO_CANCEL, TIME);
    }
    
    class WatchResources implements Runnable
    {
        private final ClassPathSqlContext context;
        
        public WatchResources(ClassPathSqlContext context)
        {
            this.context = context;
        }
        
        @Override
        public void run()
        {
            final Map<String, List<XmlStatement>> resources = context.getResources();
            Set<String> parents = resources.keySet();
            
            for (String parent : parents)
            {
                List<XmlStatement> xmlStatements = resources.get(parent);
                boolean mustReload = false;
                for (XmlStatement xmlStatement : xmlStatements)
                {
                    URL url = DefaultClassLoader.getResource(xmlStatement.getResourceName());
                    File file = new File(url.getFile());
                    Date lastModified = new Date(file.lastModified());
                    if (xmlStatement.getTimestamp().before(lastModified))
                    {
                        mustReload = true;
                        break;
                    }
                }
                if (mustReload)
                {
                    LOG.info("Auto reloading xml [{}] resource building", xmlStatements);
                    XmlStatement xmlStatement = xmlStatements.get(0);
                    xmlStatement.build(context.getSqlDialect());
                }
            }
        }
    }
}
