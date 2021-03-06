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
package net.sf.jkniv.whinstone.couchdb;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.builder.RepositoryConfig;
import net.sf.jkniv.sqlegance.transaction.Isolation;
import net.sf.jkniv.whinstone.commands.CommandAdapter;

public class HttpConnectionFactory //implements ConnectionFactory
{
    private static final Logger LOG         = LoggerFactory.getLogger(HttpConnectionFactory.class);
    //private static final String AUTH_COOKIE = "Set-Cookie";
    private final String        contextName;
    private String              url;
    private String              schema;
    private String              username;
    private String              password;
    private CouchDbAuthenticate auth;
    private HandleableException handlerException;
    
    public HttpConnectionFactory(RepositoryConfig config, String contextName)
    {
        this.url = config.getProperty(RepositoryProperty.JDBC_URL.key(), "http://127.0.0.1:5984"); //config.getProperty(RepositoryProperty.JDBC_URL.key(), "http://127.0.0.1:5984");
        this.schema = config.getProperty(RepositoryProperty.JDBC_SCHEMA.key());
        this.username = config.getProperty(RepositoryProperty.JDBC_USER.key());
        this.password = config.getProperty(RepositoryProperty.JDBC_PASSWORD.key());
        this.auth = new CouchDbAuthenticate(this.url, this.username, this.password);
        this.contextName = contextName;
    }
    
    //@Override
    //public ConnectionFactory with(HandleableException handlerException)
    public HttpConnectionFactory with(HandleableException handlerException)
    {
        this.handlerException = handlerException;
        return this;
    }
    
  //  @Override
    public CommandAdapter open()
    {
        //String token = auth.authenticate();
        HttpBuilder httpBuilder = new HttpBuilder(auth, this.url, this.schema, new RequestParams(this.schema));
        return new HttpCookieCommandAdapter(httpBuilder, contextName);
    }
    
    
    //@Override
    public CommandAdapter open(Isolation isolation)
    {
        LOG.warn("whinstone-cassandra doesn't support isolation attribute [{}]", isolation);
        return open();
    }
    /*
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
//        try
//        {
            conn.close();
//        }
//        catch (SQLException e)
//        {
//            LOG.warn("Error to try close Cassandra session/cluster [{}]", conn, e);
//        }
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
    
    public String getSchema()
    {
        return schema;
    }
*/
}
