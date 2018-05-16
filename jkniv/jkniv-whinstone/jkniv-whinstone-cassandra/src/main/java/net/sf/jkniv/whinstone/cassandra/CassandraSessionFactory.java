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
package net.sf.jkniv.whinstone.cassandra;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;

import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.transaction.Isolation;
import net.sf.jkniv.whinstone.ConnectionAdapter;
import net.sf.jkniv.whinstone.ConnectionFactory;
import net.sf.jkniv.whinstone.transaction.Transactional;

public class CassandraSessionFactory implements ConnectionFactory
{
    private static final Logger LOG = LoggerFactory.getLogger(CassandraSessionFactory.class);
    private String contextName;
    // Application connection objects
    private Cluster             cluster;
    private CassandraConnectionAdapter conn;
    
    public CassandraSessionFactory(Properties props)
    {
        String[] urls = props.getProperty(RepositoryProperty.JDBC_URL.key(),"127.0.0.1").split(",");
        String keyspace =  props.getProperty(RepositoryProperty.JDBC_SCHEMA.key());
        String username =  props.getProperty(RepositoryProperty.JDBC_USER.key());
        String password =  props.getProperty(RepositoryProperty.JDBC_PASSWORD.key());
        
        if (username != null)
            cluster = Cluster.builder().addContactPoints(urls).withCredentials(username, password).build();
        else
            cluster = Cluster.builder().addContactPoints(urls).build();
            
        final Metadata metadata = cluster.getMetadata();
        if (LOG.isInfoEnabled())
        {
            LOG.info("Connected to cluster: {}", metadata.getClusterName());
            LOG.info("List of hosts");
            for (final Host host : metadata.getAllHosts())
                LOG.info("Datacenter: {}; Host: {}; Rack: {}", host.getDatacenter(), host.getAddress(), host.getRack());
        }
        Session session = cluster.connect(keyspace);    
        this.conn = new CassandraConnectionAdapter(cluster, session);
    }
    
    @Override
    public ConnectionAdapter open()
    {
        return conn;
    }
    
    @Override
    public ConnectionAdapter open(Isolation isolation)
    {
        LOG.warn("whinstone-cassandra doesn't support isolation attribute [{}]", isolation);
        return conn;
    }
    
    @Override
    public Transactional getTransactionManager()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getContextName()
    {
        return contextName;
    }
    
    @Override
    public void close(ConnectionAdapter conn)
    {
        try
        {
            conn.close();
        }
        catch (SQLException e)
        {
            LOG.warn("Error to try close Cassandra session/cluster [{}]", conn, e);
        }
    }
    
    @Override
    public void close(PreparedStatement stmt)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void close(Statement stmt)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void close(ResultSet rs)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void close(CallableStatement call)
    {
        // TODO Auto-generated method stub
        
    }
    
}
