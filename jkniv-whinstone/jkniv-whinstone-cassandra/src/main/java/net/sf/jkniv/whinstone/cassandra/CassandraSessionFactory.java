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

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.TypeCodec;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.transaction.Isolation;
import net.sf.jkniv.whinstone.commands.CommandAdapter;
import net.sf.jkniv.whinstone.types.CalendarTimestampType;
import net.sf.jkniv.whinstone.types.Convertible;
import net.sf.jkniv.whinstone.types.ConvertibleFactory;
import net.sf.jkniv.whinstone.types.DateTimestampType;
import net.sf.jkniv.whinstone.types.DoubleBigDecimalType;
import net.sf.jkniv.whinstone.types.LongBigDecimalType;
import net.sf.jkniv.whinstone.types.LongNumericType;
import net.sf.jkniv.whinstone.types.ShortIntType;

public class CassandraSessionFactory //implements ConnectionFactory
{
    private static final Logger               LOG    = LoggerFactory.getLogger(CassandraSessionFactory.class);
    private final String                      contextName;
    private Cluster                           cluster;
    private CassandraCommandAdapter           conn;
    private final HandleableException         handlerException;
    public static final Map<String, CodecMap> CODECS = new HashMap<String, CodecMap>();
    
    static
    {
        CODECS.put("arrays.DoubleArrayCodec",
                new CodecMap("com.datastax.driver.extras.codecs.arrays.DoubleArrayCodec"));
        CODECS.put("arrays.FloatArrayCodec", new CodecMap("com.datastax.driver.extras.codecs.arrays.FloatArrayCodec"));
        CODECS.put("arrays.IntArrayCodec", new CodecMap("com.datastax.driver.extras.codecs.arrays.IntArrayCodec"));
        CODECS.put("arrays.LongArrayCodec", new CodecMap("com.datastax.driver.extras.codecs.arrays.LongArrayCodec"));
        CODECS.put("arrays.ObjectArrayCodec",
                new CodecMap("com.datastax.driver.extras.codecs.arrays.ObjectArrayCodec"));
        
        CODECS.put("date.SimpleDateCodec", new CodecMap("com.datastax.driver.extras.codecs.date.SimpleDateCodec"));
        CODECS.put("date.SimpleTimestampCodec", new CodecMap("com.datastax.driver.extras.codecs.date.SimpleTimestamp"));
        
        CODECS.put("enums.EnumNameCodec", new CodecMap("com.datastax.driver.extras.codecs.enums.EnumNameCodec"));
        CODECS.put("enums.EnumOrdinalCodec", new CodecMap("com.datastax.driver.extras.codecs.enums.EnumOrdinalCodec"));
        
        CODECS.put("guava.OptionalCodec", new CodecMap("com.datastax.driver.extras.codecs.guava.OptionalCodec"));
        
        CODECS.put("jdk8.InstantCodec", new CodecMap("com.datastax.driver.extras.codecs.jdk8.InstantCodec"));
        CODECS.put("jdk8.LocalDateCodec", new CodecMap("com.datastax.driver.extras.codecs.jdk8.LocalDateCodec"));
        CODECS.put("jdk8.LocalDateTimeCodec",
                new CodecMap("com.datastax.driver.extras.codecs.jdk8.LocalDateTimeCodec"));
        CODECS.put("jdk8.LocalTimeCodec", new CodecMap("com.datastax.driver.extras.codecs.jdk8.LocalTimeCodec"));
        CODECS.put("jdk8.OptinalCodec", new CodecMap("com.datastax.driver.extras.codecs.jdk8.OptionalCodec"));
        CODECS.put("jdk8.ZonedDateTimeCodec",
                new CodecMap("com.datastax.driver.extras.codecs.jdk8.ZonedDateTimeCodec"));
        CODECS.put("jdk8.ZonaIdCodec", new CodecMap("com.datastax.driver.extras.codecs.jdk8.ZoneIdCodec"));
        
        CODECS.put("joda.DateTimeCodec", new CodecMap("com.datastax.driver.extras.codecs.joda.DateTimeCodec"));
        CODECS.put("joda.InstantCodec", new CodecMap("com.datastax.driver.extras.codecs.joda.InstantCodec"));
        CODECS.put("joda.LocalDateCodec", new CodecMap("com.datastax.driver.extras.codecs.joda.LocalDateCodec"));
        CODECS.put("joda.LocalTimeCodec", new CodecMap("com.datastax.driver.extras.codecs.joda.LocalTimeCodec"));
        CODECS.put("json.JacksonJsonCodec", new CodecMap("com.datastax.driver.extras.codecs.json.JacksonJsonCodec"));
        
        CODECS.put("InstantCodec", new CodecMap("com.datastax.driver.extras.codecs.jdk8.InstantCodec"));
        CODECS.put("LocalDateCodec", new CodecMap("com.datastax.driver.extras.codecs.jdk8.LocalDateCodec"));
        CODECS.put("LocalDateTimeCodec", new CodecMap("com.datastax.driver.extras.codecs.jdk8.LocalDateTimeCodec"));
        CODECS.put("LocalTimeCodec", new CodecMap("com.datastax.driver.extras.codecs.jdk8.LocalTimeCodec"));
        CODECS.put("OptinalCodec", new CodecMap("com.datastax.driver.extras.codecs.jdk8.OptionalCodec"));
    }
    
    public CassandraSessionFactory(Properties props, String contextName, HandleableException handlerException)
    {
        this.handlerException = handlerException;
        String[] urls = props.getProperty(RepositoryProperty.JDBC_URL.key(), "127.0.0.1").split(",");
        String keyspace = props.getProperty(RepositoryProperty.JDBC_SCHEMA.key());
        String username = props.getProperty(RepositoryProperty.JDBC_USER.key());
        String password = props.getProperty(RepositoryProperty.JDBC_PASSWORD.key());
        String protocol = props.getProperty(RepositoryProperty.PROTOCOL_VERSION.key());
        ProtocolVersion version = getProtocolVersion(protocol);
        
        this.contextName = contextName;
        if (username != null)
            cluster = Cluster.builder().addContactPoints(urls).withCredentials(username, password)
                    .withProtocolVersion(version).build();
        else
            cluster = Cluster.builder().withProtocolVersion(version).addContactPoints(urls).build();
        
        settingProperties(props);
        
        final Metadata metadata = cluster.getMetadata();
        if (LOG.isInfoEnabled())
        {
            LOG.info("Connected to cluster: {}", metadata.getClusterName());
            LOG.info("List of hosts");
            for (final Host host : metadata.getAllHosts())
                LOG.info("Datacenter: {}; Host: {}; Rack: {}", host.getDatacenter(), host.getAddress(), host.getRack());
        }
        Session session = cluster.connect(keyspace);
        this.conn = new CassandraCommandAdapter(cluster, session, contextName, handlerException);
    }
    
    private void settingProperties(Properties props)
    {
        Enumeration<Object> keys = props.keys();
        while (keys.hasMoreElements())
        {
            String k = keys.nextElement().toString();
            if (k.startsWith("codec."))
                settingCodec(k, props);
            else if (k.startsWith("jkniv.repository.type."))
                settingConverters(k, props);                
        }
    }
    
    @SuppressWarnings("rawtypes")
    private void settingConverters(String k, Properties props)
    {
        String className = String.valueOf(props.get(k));// (22) -> "jkniv.repository.type."
        ObjectProxy<Convertible> proxy = ObjectProxyFactory.of(className);
        ConvertibleFactory.register(proxy.newInstance());
//        ConvertibleFactory.register(new DateTimestampType());
//        ConvertibleFactory.register(new CalendarTimestampType());
//        ConvertibleFactory.register(new LongNumericType());
//        ConvertibleFactory.register(new LongBigDecimalType());
//        ConvertibleFactory.register(new ShortIntType());
//        ConvertibleFactory.register(new DoubleBigDecimalType());
    }

    
    private void settingCodec(String k, Properties props)
    {
        boolean enable = Boolean.valueOf(props.getProperty(k));
        if (enable)
        {
            CodecMap codecMap = CODECS.get(k.substring(6));// (6) -> "codec."
            Field field = null;
            if (codecMap != null)
            {
                ObjectProxy<TypeCodec<?>> proxy = ObjectProxyFactory.of(codecMap.codecClassName);
                field = proxy.getDeclaredField("instance");
                if (field != null)
                {
                    TypeCodec<?> instance = null;
                    try
                    {
                        instance = (TypeCodec<?>) field.get(null);
                        cluster.getConfiguration().getCodecRegistry().register(instance);
                        codecMap.instance = instance;
                        if (k.endsWith("InstantCodec"))
                            CODECS.get("InstantCodec").instance = instance;
                        else if (k.endsWith("LocalDateCodec"))
                            CODECS.get("LocalDateCodec").instance = instance;
                        else if (k.endsWith("LocalTimeCodec"))
                            CODECS.get("LocalTimeCodec").instance = instance;
                        else if (k.endsWith("LocalDateTimeCodec"))
                            CODECS.get("LocalDateTimeCodec").instance = instance;
                        else if (k.endsWith("OptinalCodec"))
                            CODECS.get("OptinalCodec").instance = instance;
                        
                        LOG.info("The codec {} was registered with {}", k, codecMap.codecClassName);
                    }
                    catch (Exception e)//IllegalArgumentException, IllegalAccessException
                    {
                        LOG.error("Error registering codec {}", k, e);
                    }
                }
            }
            if (codecMap == null || field == null)
                LOG.error("Codec {} not found ", k);
        }        
    }
    
    private ProtocolVersion getProtocolVersion(String protocol)
    {
        ProtocolVersion version = null;
        for (ProtocolVersion v : ProtocolVersion.values())
        {
            if (v.name().equals(protocol))
                version = v;
        }
        if (version == null)
        {
            if (protocol != null)
                LOG.warn("Property {} has invalid value [{}] using protocol {}",
                        RepositoryProperty.PROTOCOL_VERSION.key(), protocol, ProtocolVersion.NEWEST_SUPPORTED);
            version = ProtocolVersion.NEWEST_SUPPORTED;
        }
        return version;
    }
    
    //    @Override
    //    public ConnectionFactory with(HandleableException handlerException)
    //    {
    //        //this.handlerException = handlerException;
    //        return this;
    //    }
    
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
    
    public static class CodecMap
    {
        public final String codecClassName;
        public TypeCodec    instance;
        
        CodecMap(String codecClassName)
        {
            this.codecClassName = codecClassName;
            this.instance = instance;
        }
    }
}
