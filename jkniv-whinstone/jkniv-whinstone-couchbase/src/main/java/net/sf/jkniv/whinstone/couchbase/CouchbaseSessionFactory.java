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
package net.sf.jkniv.whinstone.couchbase;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.cluster.BucketSettings;
import com.couchbase.client.java.cluster.ClusterManager;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.transaction.Isolation;
import net.sf.jkniv.whinstone.commands.CommandAdapter;

class CouchbaseSessionFactory
{
    private static final Logger       LOG = LoggerFactory.getLogger(CouchbaseSessionFactory.class);
    private CouchbaseCommandAdapter   conn;
    
    public CouchbaseSessionFactory(Properties props, String contextName, HandleableException handlerException)
    {
        String[] urls = props.getProperty(RepositoryProperty.JDBC_URL.key(), "127.0.0.1").split(",");
        String bucketName = props.getProperty(RepositoryProperty.JDBC_SCHEMA.key());
        String username = props.getProperty(RepositoryProperty.JDBC_USER.key());
        String password = props.getProperty(RepositoryProperty.JDBC_PASSWORD.key());
        
        Cluster cluster = CouchbaseCluster.create(Arrays.asList(urls));
        cluster.authenticate(username, password);
        Bucket bucket = cluster.openBucket(bucketName);
        
        ClusterManager clusterMgr = cluster.clusterManager();
        
        
        if (LOG.isInfoEnabled())
        {
            LOG.info("List of hosts context: {}", contextName);
            for (final String host : urls)
                LOG.info("Host: {}", host);
            List<BucketSettings> settings = clusterMgr.getBuckets();
            for(BucketSettings bs : settings)
            {
                LOG.info("Bucket name: {}, type of: {}, ", bs.name(),bs.type().name());
                LOG.info(" -> {}, ", bs.raw());
            }
        }
        this.conn = new CouchbaseCommandAdapter(cluster, bucket, contextName, handlerException);
    }
    
    public CommandAdapter open()
    {
        return conn;
    }
    
    public CommandAdapter open(Isolation isolation)
    {
        LOG.warn("whinstone-couchbase doesn't support isolation attribute [{}]", isolation);
        return conn;
    }
    
}
