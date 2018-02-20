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
package net.sf.jkniv.whinstone.jdbc.jndi;

import java.util.Properties;

import javax.naming.NamingException;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

public class JndiCreator
{
    public static void bind()
    {
        bind("java:comp/env/jdbc/whinstone");
    }
    
    public static void bind(String jndiName)
    {
        try
        {
            BasicDataSource datasource = new BasicDataSource();
            datasource.setUrl("jdbc:derby:memory:whinstone");
            datasource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
            final SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
            builder.bind(jndiName, datasource);
            builder.activate();
        }
        catch (NamingException ex)
        {
            ex.printStackTrace();
        }
    }
    
    /**
     * Properties: jndi, url, driver, username and password to create a BasicDataSource in JNDI.
     * 
     * @param props
     */
    public static void bind(Properties props)
    {
        try
        {
            BasicDataSource datasource = new BasicDataSource();
            datasource.setUrl(props.getProperty("url"));
            datasource.setDriverClassName(props.getProperty("driver"));
            datasource.setUsername(props.getProperty("username"));
            datasource.setPassword(props.getProperty("password"));
            final SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
            builder.bind(props.getProperty("jndi"),datasource);
            builder.activate();
        }
        catch (NamingException ex)
        {
            ex.printStackTrace();
        }
    }
}
