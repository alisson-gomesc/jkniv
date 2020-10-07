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

import java.io.File;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.Session;
*/
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.ProtocolVersion;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.sqlegance.DefaultClassLoader;
import net.sf.jkniv.sqlegance.RepositoryConfigException;
import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.transaction.Isolation;
import net.sf.jkniv.whinstone.commands.CommandAdapter;
import net.sf.jkniv.whinstone.types.RegisterType;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
@SuppressWarnings("rawtypes")
public class CassandraSessionFactory //implements ConnectionFactory
{
    private static final Logger       LOG = LoggerFactory.getLogger(CassandraSessionFactory.class);
    private final String              contextName;
    //private Cluster                   cluster;
    private CassandraCommandAdapter   conn;
    private final HandleableException handlerException;
    private final RegisterCodec      registerCodec;
    
    public CassandraSessionFactory(Properties props, String contextName, RegisterType registerType, HandleableException handlerException)
    {
        this.handlerException = handlerException;
        String[] urls = props.getProperty(RepositoryProperty.JDBC_URL.key(), "127.0.0.1").split(",");
        String keyspace = props.getProperty(RepositoryProperty.JDBC_SCHEMA.key());
        String username = props.getProperty(RepositoryProperty.JDBC_USER.key());
        String password = props.getProperty(RepositoryProperty.JDBC_PASSWORD.key());
        String protocol = props.getProperty(RepositoryProperty.PROTOCOL_VERSION.key());
        String datacenter= props.getProperty(RepositoryProperty.LOCAL_DATACENTER.key());
        ProtocolVersion version = getProtocolVersion(protocol);
        this.registerCodec = new RegisterCodec();
        this.contextName = contextName;
        CqlSession session = null;
        URL cloudSecureConnect = getCloudSecureConnect(props);
        
        CqlSessionBuilder builder = CqlSession.builder();
        settingProperties(builder, props);
        if (cloudSecureConnect != null)
        {
            session = builder
                        .withCloudSecureConnectBundle(cloudSecureConnect)
                        .withAuthCredentials(username, password)
                        .withLocalDatacenter(datacenter)
                        .withKeyspace(keyspace)
                        .build();
        }
        else if (username != null)
            session = builder
                        .addContactPoints(getContactPoints(urls))
                        .withAuthCredentials(username, password)
                        .withLocalDatacenter(datacenter)
                        .withKeyspace(keyspace)
                        .build();
        else
            session = builder
                        .addContactPoints(getContactPoints(urls))
                        .withAuthCredentials(username, password)
                        .withLocalDatacenter(datacenter)
                        .withKeyspace(keyspace)
                        .build();
        

//        if (LOG.isInfoEnabled())
//        {
//            final Metadata metadata = session.getMetadata();
//            LOG.info("Connected to cluster: {}", metadata.getClusterName());
//            LOG.info("List of hosts");
//            for (final HostAndPort host : metadata.getAllHosts())
//                LOG.info("Datacenter: {}; Host: {}; Rack: {}", host.getDatacenter(), host.getAddress(), host.getRack());
//        }
//        
        //Session session = cluster.connect(keyspace);
        this.conn = new CassandraCommandAdapter(contextName, session, registerType, registerCodec, handlerException);
    }
    
    private ProtocolVersion getProtocolVersion(String protocol)
    {
        ProtocolVersion[] values = {ProtocolVersion.DEFAULT, ProtocolVersion.DSE_V1, ProtocolVersion.DSE_V2, ProtocolVersion.V3, ProtocolVersion.V4, ProtocolVersion.V5};
        ProtocolVersion version = null;
        for (ProtocolVersion v : values)
        {
            if (v.name().equals(protocol))
                version = v;
        }
        if (version == null)
        {
            if (protocol != null)
                LOG.warn("Property {} has invalid value [{}] using protocol {}",
                        RepositoryProperty.PROTOCOL_VERSION.key(), protocol, ProtocolVersion.DEFAULT);
            version = ProtocolVersion.DEFAULT;
        }
        return version;
    }
    
    
    private void settingProperties(CqlSessionBuilder builder, Properties props)
    {
        Enumeration<Object> keys = props.keys();
        while (keys.hasMoreElements())
        {
            String k = keys.nextElement().toString();
            if (k.startsWith("codec."))
            {
                boolean enable = Boolean.valueOf(props.getProperty(k));
                String codecName = k.substring(6);// (6) -> "codec."
                registerCodec.register(builder, codecName, enable);
            }
        }
    }
    
    
    //@Override
    public CommandAdapter open()
    {
        return conn;
    }
    
    //@Override
    public CommandAdapter open(Isolation isolation)
    {
        LOG.warn("whinstone-cassandra doesn't support isolation attribute [{}]", isolation);
        return conn;
    }
    
    //    @Override
    //    public Transactional getTransactionManager()
    //    {
    //        // TODO Auto-generated method stub
    //        return null;
    //    }
    //    
    //    @Override
    //    public String getContextName()
    //    {
    //        return contextName;
    //    }
    //    
    //    @Override
    //    public void close(ConnectionAdapter conn)
    //    {
    ////        try
    ////        {
    //            conn.close();
    ////        }
    ////        catch (SQLException e)
    ////        {
    ////            LOG.warn("Error to try close Cassandra session/cluster [{}]", conn, e);
    ////        }
    //    }
    //    
    //    @Override
    //    public void close(PreparedStatement stmt)
    //    {
    //        // TODO Auto-generated method stub
    //        
    //    }
    //    
    //    @Override
    //    public void close(Statement stmt)
    //    {
    //        // TODO Auto-generated method stub
    //        
    //    }
    //    
    //    @Override
    //    public void close(ResultSet rs)
    //    {
    //        // TODO Auto-generated method stub
    //        
    //    }
    //    
    //    @Override
    //    public void close(CallableStatement call)
    //    {
    //        // TODO Auto-generated method stub
    //        
    //    }

    private URL getCloudSecureConnect(Properties props)
    {
        String keyFile = props.getProperty(RepositoryProperty.KEY_FILE.key());
        //keyFile = "file:///C:/dev/wks/wks-jkniv-git/jkniv-whinstone/jkniv-whinstone-cassandra/target/test-classes/database/astra-secure-connect-jkniv.zip";
        URL cloudSecureConnect = null;
        if(keyFile != null)
        {
            if (keyFile.startsWith("file:"))
            {
                try
                {
                    cloudSecureConnect = new URL(keyFile);
                }
                catch (MalformedURLException e)
                {
                    throw new RepositoryConfigException("Key file ["+keyFile+"]for Cassandra CloudSecureConnect is MalformedURLException [" + e.getMessage() + "]");
                }
                if (! new File(cloudSecureConnect.getFile()).exists())
                    throw new RepositoryConfigException("Key file ["+keyFile+"]for Cassandra CloudSecureConnect not exists");
            }
            else
                cloudSecureConnect = DefaultClassLoader.getResource(keyFile);
        }
        return cloudSecureConnect;
    }

    private Collection<InetSocketAddress> getContactPoints(String[] urls)
    {
        Collection<InetSocketAddress> endpoints = new ArrayList<>();
        for(String url : urls)
        {
            SocketAddressResolve resolver = SocketAddressResolve.of(url, 9042); 
            endpoints.add(new InetSocketAddress(resolver.getHost(), resolver.getPort()));
        }
        return endpoints;
    }
}
