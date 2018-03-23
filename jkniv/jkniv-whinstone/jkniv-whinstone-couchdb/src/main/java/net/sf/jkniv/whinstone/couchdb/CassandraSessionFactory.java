package net.sf.jkniv.whinstone.couchdb;

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

import net.sf.jkniv.sqlegance.ConnectionAdapter;
import net.sf.jkniv.sqlegance.ConnectionFactory;
import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.transaction.Isolation;
import net.sf.jkniv.sqlegance.transaction.Transactional;

public class CassandraSessionFactory implements ConnectionFactory
{
    private static final Logger LOG = LoggerFactory.getLogger(CassandraSessionFactory.class);
    private String contextName;
    // Application connection objects
    private Cluster             cluster;
    private CassandraConnectionAdapter conn;
    
    public CassandraSessionFactory(Properties props)
    {
        String server_ip = props.getProperty(RepositoryProperty.JDBC_URL.key(),"127.0.0.1");
        String keyspace =  props.getProperty(RepositoryProperty.JDBC_USER.key(),"dev_data_3t");
        cluster = Cluster.builder().addContactPoints(server_ip).build();
        
        final Metadata metadata = cluster.getMetadata();
        String msg = String.format("Connected to cluster: %s", metadata.getClusterName());
        System.out.println(msg);
        System.out.println("List of hosts");
        for (final Host host : metadata.getAllHosts())
        {
            msg = String.format("Datacenter: %s; Host: %s; Rack: %s", host.getDatacenter(), host.getAddress(),
                    host.getRack());
            System.out.println(msg);
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
