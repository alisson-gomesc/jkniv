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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jkniv.whinstone.Queryable;

/**
 * 
 * 
 * @author Alisson Gomes
 *
 */
@Path("report")
@PermitAll
public class RestFulReportResources extends BaseResource
{
    private static final Logger LOG = LoggerFactory.getLogger(RestFulReportResources.class);

    
    /**
     * Execute query <code>q</code> against the database with all parameters from URL string <code>ui</code>
     * @param q query name
     * @param j jasper simple file name
     * @param t type of media type to generate pdf and excel are accepted
     * @param ui query parameters
     * @return A set of JSON objects that matches with query and HTTP status <code>200 OK</code>, if
     * the query no match anyone object HTTP 404 <code>Not Found</code> is returned. 
     * @throws JRException 
     */
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("view/{q}/{j}/{t}")
    public Response view(@PathParam("context") String ctx, @PathParam("q") String q, @PathParam("t") String t, @PathParam("j") String j, @Context UriInfo ui) throws JRException {
        Queryable queryable = buildQueryable(q, ui);
        List<?>  resources = getRepository(ctx).list(queryable);
        InputStream is = this.reportJaspers.getResourceAsStream(j);
        String mediaType = "application/pdf";
        String fileName = "report.pdf";
        if ("xls".equalsIgnoreCase(t)) {
            mediaType = "application/vnd.ms-excel";
            fileName = "report.xls";
        }
        else if ("xlsx".equalsIgnoreCase(t)) {
            mediaType = "application/vnd.ms-excel";
            fileName = "report.xlsx";
        }
        Map<String, Object> params = new HashMap<String, Object>(ui.getQueryParameters());
        ByteArrayOutputStream output = exportToPdf(is, params, resources);        
        ResponseBuilder response = Response.ok((Object) output.toByteArray(), mediaType);
        response.type(mediaType);
        response.header("Content-Disposition", "filename=" + fileName);
        return response.build();
    }

    /**
     * Execute query <code>q</code> against the database with all parameters from URL string <code>ui</code>
     * converted for model type <code>m</code>
     * @param q query name
     * @param t type of media type to generate pdf and excel are accepted
     * @param m Model name (could be simple name or canonical name from class), for simple name use Model Type registered
     * @param j jasper simple file name
     * @param ui query parameters
     * @return A set of JSON objects that matches with query and HTTP status <code>200 OK</code>, if
     * the query no match anyone object HTTP 404 <code>Not Found</code> is returned. 
     * @throws JRException 
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("view/{q}/{m}/{j}/{t}")
    public Response view(@PathParam("context") String ctx, @PathParam("q") String q, @PathParam("t") String t, @PathParam("j") String j, @PathParam("m") String m, @Context UriInfo ui) throws JRException {
        Class<?> clazz = modelTypes.getType(m);
        Queryable queryable = buildQueryable(q, clazz, ui);
        List<?>  resources = getRepository(ctx).list(queryable);
        
        InputStream is = null;
        String mediaType = "application/pdf";
        String fileName = "report.pdf";
        if ("xls".equalsIgnoreCase(t)) {
            mediaType = "application/vnd.ms-excel";
            fileName = "report.xls";
        }
        else if ("xlsx".equalsIgnoreCase(t)) {
            mediaType = "application/vnd.ms-excel";
            fileName = "report.xlsx";
        }
        
        Map<String, Object> params = new HashMap<String, Object>(ui.getQueryParameters());
        ByteArrayOutputStream output = exportToPdf(is, params, resources);        
        ResponseBuilder response = Response.ok((Object) output.toByteArray(), mediaType);
        response.type(mediaType);
        response.header("Content-Disposition", "filename=" + fileName);
        return response.build();
    }
    
    
    /**
     * Execute query <code>q</code> against the database with all parameters from URL string <code>ui</code>
     * @param q query name
     * @param t type of media type to generate pdf and excel are accepted
     * @param j jasper simple file name
     * @param ui query parameters
     * @return A set of JSON objects that matches with query and HTTP status <code>200 OK</code>, if
     * the query no match anyone object HTTP 404 <code>Not Found</code> is returned. 
     */
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("show/{q}/{t}")
    public Response show(@PathParam("context") String ctx,@PathParam("q") String q, @PathParam("t") String t, @PathParam("j") String j, @Context UriInfo ui) {
        Queryable queryable = buildQueryable(q, ui);
        List<?>  resources = getRepository(ctx).list(queryable);
        return buildSimpleResponse(resources);
    }

    /**
     * Execute query <code>q</code> against the database with all parameters from URL string <code>ui</code>
     * converted for model type <code>m</code>
     * @param q query name
     * @param t type of media type to generate pdf and excel are accepted
     * @param j jasper simple file name
     * @param m Model name (could be simple name or canonical name from class), for simple name use Model Type registered
     * @param ui query parameters
     * @return A set of JSON objects that matches with query and HTTP status <code>200 OK</code>, if
     * the query no match anyone object HTTP 404 <code>Not Found</code> is returned. 
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("show/{q}/{m}/{t}")
    public Response show(@PathParam("context") String ctx,@PathParam("q") String q, @PathParam("t") String t, @PathParam("j") String j, @PathParam("m") String m, @Context UriInfo ui) {
        Class<?> clazz = modelTypes.getType(m);
        Queryable queryable = buildQueryable(q, clazz, ui);
        List<?>  resources = getRepository(ctx).list(queryable);
        return buildSimpleResponse(resources);
    }
    
    private ByteArrayOutputStream exportToPdf(InputStream is,  Map params, List<?> ds) throws JRException
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        JasperPrint jasperPrint = JasperFillManager.fillReport(is, params, new JRBeanCollectionDataSource(ds));

        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(output));
        SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
        exporter.setConfiguration(configuration);
        exporter.exportReport();

        return output;

    }
}
