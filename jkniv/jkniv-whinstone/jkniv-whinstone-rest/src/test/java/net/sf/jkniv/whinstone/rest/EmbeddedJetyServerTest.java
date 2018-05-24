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
package net.sf.jkniv.whinstone.rest;


import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.HttpURLConnection;
import java.net.URI;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class EmbeddedJetyServerTest
{
    private static Server server;
    private static URI serverUri;

    @BeforeClass
    public static void startJetty() throws Exception
    {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080); // auto-bind to available port
        server.addConnector(connector);

        ServletContextHandler context = configContext();
        configJersey(context);
        configWhinstoneRest(context);

        // Start Server
        server.start();

        // Determine Base URI for Server
        String host = connector.getHost();
        if (host == null)
        {
            host = "localhost";
        }
        int port = connector.getLocalPort();
        String uri = String.format("http://%s:%d/",host,port);
        serverUri = new URI(uri);
        System.out.println("server started "+uri);
    }

    private static ServletContextHandler configContext()
    {
        ServletContextHandler context = new ServletContextHandler();        
        ServletHolder defaultServ = new ServletHolder("default", DefaultServlet.class);
        defaultServ.setInitParameter("resourceBase",System.getProperty("user.dir"));
        defaultServ.setInitParameter("dirAllowed","true");
        context.addServlet(defaultServ,"/");
        context.setContextPath("/");
        server.setHandler(context);
        return context;
    }

    private static void configJersey(ServletContextHandler context)
    {
        ServletHolder servlet = context.addServlet(ServletContainer.class, "/api/*");
        servlet.setInitOrder(1);
        servlet.setInitParameter("jersey.config.server.provider.packages", "net.sf.jkniv.whinstone.rest");
    }

    private static void configWhinstoneRest(ServletContextHandler context)
    {
        // Scan package for model types
        context.setInitParameter("model.packages", "br.com.rwit.tecno3t.model");
        // Scan package for jasper file reports
        context.setInitParameter("report.packages", "br.com.rwit.tecno3t.reports");
        // Scan package for regiters transform result classes</description>
        context.setInitParameter("transform.packages", "br.com.rwit.tecno3t.transformers");
        // Sql context file</description>
        context.setInitParameter("fullname.sqlContext", "/repository-sql.xml");

        context.addEventListener(new net.sf.jkniv.whinstone.rest.RegistryLoaderListener());
    }

    @AfterClass
    public static void stopJetty()
    {
        try
        {
            server.stop();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void whenStartServer() throws Exception
    {
        // Test GET
        HttpURLConnection http = (HttpURLConnection) serverUri.resolve("/").toURL().openConnection();
        http.connect();
        assertThat("Response Code", http.getResponseCode(), is(HttpStatus.OK_200));
    }
    
    @Test
    public void whenWhinstonePing() throws Exception
    {
        // Test GET
        HttpURLConnection http = (HttpURLConnection) serverUri.resolve("/api/whinstone/ping").toURL().openConnection();
        http.connect();
        assertThat("Response Code", http.getResponseCode(), is(HttpStatus.OK_200));
    }

}
