package net.sf.jkniv.whinstone.rest;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import javax.annotation.security.PermitAll;
//import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;

/**
 * 
 * @author Alisson Gomes
 */
//@PermitAll
@Path("{context}")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
@Produces(MediaType.APPLICATION_JSON)
public class RestFulResources extends BaseResource
{
    private static final Logger LOG = LoggerFactory.getLogger(RestFulResources.class);
    protected static final SimpleNameRegistry modelTypes = new SimpleNameRegistry();
    protected static final SimpleNameRegistry reportJaspers = new SimpleNameRegistry();
    private final static Assertable notNull = AssertsFactory.getNotNull(); 
    private final static Assertable isNull = AssertsFactory.getIsNull();
    
    /**
     * Provider a way to reference a class type with a shortname
     * @param className entity represented
     */
    public static void registryModel(String className) 
    {
        notNull.verify(className);   
        modelTypes.registry(className);
        LOG.debug("Class {} was registered", className);
    }

    /**
     * Provider a way to reference a class type with a shortname
     * @param className entity represented
     */
    public static void registryReport(String className) 
    {
        notNull.verify(className);
        reportJaspers.registry(className);
        LOG.debug("Jasper file {} was registered", className);
    }
    
    public static void registryTransformers(String className) 
    {
        notNull.verify(className);
        transformers.registry(className);
        LOG.debug("Transform class {} was registered", className);
    }

    public static void registrySqlContext(String name)
    {
        notNull.verify(name);
        isNull.verify(RestFulResources.sqlContextName); // cannot change sql context file
        RestFulResources.sqlContextName = name;
    }
    
    public static void cleanup()
    {
        BaseResource.modelTypes.cleanup();
        BaseResource.reportJaspers.cleanup();
        BaseResource.transformers.cleanup();
    }
    
    public RestFulResources()
    {
        super();
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("ping")
    public Response ping() 
    {
        return Response.ok().entity("pong").build();
    }
    
    
    /**
     * Execute query <code>q</code> against the database with all parameters from URL string <code>ui</code>
     * @param q query name
     * @param ui query parameters
     * @return A set of JSON objects that matches with query and HTTP status <code>200 OK</code>, if
     * the query no match anyone object HTTP 204 <code>No Content</code> is returned. 
     */
    @GET
    @Path("list/{q}")
    public Response list(@PathParam("context") String ctx, @PathParam("q") String q, @Context UriInfo ui) {
        Queryable queryable = buildQueryable(q, ui);
        List<?>  resources = getRepository(ctx).list(queryable);
        return buildSimpleResponse(resources);
    }
    
    /**
     * Execute query <code>q</code> against the database with all parameters from URL string <code>ui</code>
     * converted for model type <code>m</code>
     * @param q query name
     * @param m Model or Transform name (could be simple name or canonical name from class)
     * @param ui query parameters
     * @return A set of JSON objects that matches with query and HTTP status <code>200 OK</code>, if
     * the query no match anyone object HTTP 204 <code>No Content</code> is returned. 
     */
    @GET
    @Path("list/{q}/{m}")
    public Response list(@PathParam("context") String ctx, @PathParam("q") String q, @PathParam("m") String m, @Context UriInfo ui) {
        Class<?> clazz = modelTypes.getType(m);
        Class<?> clazzTransform = transformers.getType(m);
        Queryable queryable = null;
        TransformReturn<?> transform = null;
        if (clazzTransform != null && TransformReturn.class.isAssignableFrom(clazzTransform))
        {
            queryable = buildQueryable(q, ui);
            transform = (TransformReturn<?>) ObjectProxyFactory.newProxy(clazzTransform).newInstance();
        }
        else
            queryable = buildQueryable(q, clazz, ui);
        
        List<?>  resources = getRepository(ctx).list(queryable);
        Response response = null;
        
        if (transform != null)
            response = buildSimpleResponse(transform.transform(resources));
        else
            response = buildSimpleResponse(resources);
        
        return response;
    }

    /**
     * Execute query <code>q</code> against the database with all parameters from URL string <code>ui</code>
     * converted for model type <code>m</code>
     * @param q query name
     * @param m Model name (could be simple name or canonical name from class)
     * @param t transform name (could be simple name or canonical name from class)
     * @param ui query parameters
     * @return A set of JSON objects that matches with query and HTTP status <code>200 OK</code>, if
     * the query no match anyone object HTTP 204 <code>No Content</code> is returned. 
     */
    @GET
    @Path("list/{q}/{m}/{t}")
    public Response list(@PathParam("context") String ctx, @PathParam("q") String q, @PathParam("m") String m, @PathParam("t") String t, @Context UriInfo ui) {
        Class<?> clazz = modelTypes.getType(m);
        Class<?> clazzTransform = transformers.getType(t);
        Queryable queryable = buildQueryable(q, clazz, ui);
        List<?>  resources = getRepository(ctx).list(queryable);
        TransformReturn<?> transform = (TransformReturn<?>) ObjectProxyFactory.newProxy(clazzTransform).newInstance();
        List<?>  newResources = transform.transform(resources);
        return buildSimpleResponse(newResources);
    }


    /**
     * Execute query <code>q</code> against the database with all parameters from URL string <code>ui</code>
     * @param q query name
     * @param ui query parameters
     * @return An JSON object that matches with query and HTTP status <code>200 OK</code>, if
     * the query no match anyone object HTTP 204 <code>No Content</code> is returned. 
     */
    @GET
    @Path("get/{q}")
    public Response get(@PathParam("context") String ctx, @PathParam("q") String q, @Context UriInfo ui) {
        Queryable queryable = buildQueryable(q, ui);
        Object resource = getRepository(ctx).get(queryable);
        return buildSimpleResponse(resource);
    }

    /**
     * Execute query <code>q</code> against the database with all parameters from URL string <code>ui</code>
     * converted for model type <code>m</code>.
     * @param q query name
     * @param m Model or Transform name (could be simple name or canonical name from class)
     * @param ui query parameters
     * @return An JSON object that matches with query and HTTP status <code>200 OK</code>, if
     * the query no match anyone object HTTP 204 <code>No Content</code> is returned. 
     */
    @GET
    @Path("get/{q}/{m}")
    public Response get(@PathParam("context") String ctx, @PathParam("q") String q, @PathParam("m") String m, @Context UriInfo ui) {
        Class<?> clazz = modelTypes.getType(m);
        Class<?> clazzTransform = transformers.getType(m);
        Queryable queryable = null;
        TransformReturn<?> transform = null;
        if (clazzTransform != null && TransformReturn.class.isAssignableFrom(clazzTransform))
        {
            queryable = buildQueryable(q, ui);
            transform = (TransformReturn<?>) ObjectProxyFactory.newProxy(clazzTransform).newInstance();
        }
        else
        {
            queryable = buildQueryable(q, clazz, ui);
        }
        Object resource = getRepository(ctx).get(queryable);
        Response response = null;
        if (transform != null)
            response = buildSimpleResponse(transform.transform(resource));
        else
            response = buildSimpleResponse(resource);
        
        return response;
    }

    //HTTP 201 Created
    //HTTP 409 Conflict
    @POST
    @Path("add/{q}")
    public Response add(@PathParam("context") String ctx, @PathParam("q") String q, @Context UriInfo ui) {
        Queryable queryable = buildQueryable(q, ui);
        Integer rowsAffected = getRepository(ctx).add(queryable);
        return buildSimpleResponse(rowsAffected);
        //return buildSimpleResponse(rowsAffected, queryable);
    }

    //HTTP 201 Created
    //HTTP 409 Conflict
    @POST
    @Path("add/{q}/{m}")
    public Response add(@PathParam("context") String ctx, @PathParam("q") String q, @PathParam("m") String m, @Context UriInfo ui) {
        Class<?> clazz = modelTypes.getType(m);
        Queryable queryable = buildQueryable(q, clazz, ui);
        Object object = getRepository(ctx).add(queryable.getParams());
        return buildSimpleResponse(object);// FIXME HTTP status
    }

    /**
     * {@code PUT} HTTP verb to process the update requests. 
     * <ul>
     *  <li> <p><code>HTTP 200 OK</code>: content updated, one or more rows affected</p> </li>
     *  <li> <p><code>HTTP 204 No Content</code>: Update change nothing, zero rows affected</p> </li>
     *  <li> <p><code>HTTP 409 Conflict</code>: Conflict to update rows, zero rows affected</p> </li>
     * </ul>
     * @param q queryable name
     * @param ui request URI information
     * @param body request information using json
     * @return <code>HTTP 200 OK</code>, <code>HTTP 204 No Content</code> or <code>HTTP 409 Conflict</code> with
     * number of rows affected by update command.
     */
    @PUT
    @Path("update/{q}")
    public Response update(@PathParam("context") String ctx, @PathParam("q") String q, @Context UriInfo ui, String body) throws JsonParseException, JsonMappingException, IOException 
    {
        Map<String,Object> params = marshallToMap(ui.getQueryParameters());
        if (isNotEmpty(body))
        {
            Map<String,Object> bodyParams = new ObjectMapper().readValue(body, HashMap.class);
            params.putAll(bodyParams);
        }
        Queryable queryable = buildQueryable(q, params);
        Integer rowsAffected = getRepository(ctx).update(queryable);
        return buildSimpleResponse(rowsAffected);
    }

    /**
     * {@code PUT} HTTP verb to process the update requests. 
     * <ul>
     *  <li> <p><code>HTTP 200 OK</code>: content updated, one or more rows affected</p> </li>
     *  <li> <p><code>HTTP 204 No Content</code>: Update change nothing, zero rows affected</p> </li>
     *  <li> <p><code>HTTP 409 Conflict</code>: Conflict to update rows, zero rows affected</p> </li>
     * </ul>
     * @param q queryable name
     * @param m model name (qualified or short name)
     * @param ui request URI information
     * @param body request information using json
     * @return <code>HTTP 200 OK</code>, <code>HTTP 204 No Content</code> or <code>HTTP 409 Conflict</code> with
     * number of rows affected by update command.
     */
    @PUT
    @Path("update/{q}/{m}")
    public Response update(@PathParam("context") String ctx, @PathParam("q") String q, @PathParam("m") String m, @Context UriInfo ui, String body)  
            throws JsonParseException, JsonMappingException, IOException
    {
        Class<?> clazz = modelTypes.getType(m);
        ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(clazz);
        marshallToProxy(proxy, ui.getQueryParameters());
        if (isNotEmpty(body))
        {
            Map<String,Object> bodyParams = new ObjectMapper().readValue(body, HashMap.class);
            proxy.merge(bodyParams);
        }
        Queryable queryable = buildQueryable(q, proxy.getInstance());
        Integer rowsAffected = getRepository(ctx).update(queryable);
        return buildSimpleResponse(rowsAffected);
    }


    /*
     * {@code PUT} HTTP verb to process the update requests. 
     * <ul>
     *  <li> <p><code>HTTP 200 OK</code>: content updated, one or more rows affected</p> </li>
     *  <li> <p><code>HTTP 204 No Content</code>: Update change nothing, zero rows affected</p> </li>
     *  <li> <p><code>HTTP 409 Conflict</code>: Conflict to update rows, zero rows affected</p> </li>
     * </ul>
     * @param q queryable name
     * @param m model name (qualified or short name)
     * @param formParams request form information
     * @return <code>HTTP 200 OK</code>, <code>HTTP 204 No Content</code> or <code>HTTP 409 Conflict</code> with
     * number of rows affected by update command.
     *
    @PUT
    @Path("update/{q}/{m}")
    public Response update(@PathParam("q") String q, 
                           @PathParam("m") String m,
                           MultivaluedMap<String, String> formParams) 
    {
        Class<?> clazz = modelTypes.getType(m);
        Queryable queryable = buildQueryable(q, clazz, formParams);
        Integer rowsAffected = getRepository().update(queryable);
        return buildSimpleResponse(rowsAffected);
    }
    */

    /*
     * {@code PUT} HTTP verb to process the update requests. 
     * <ul>
     *  <li> <p><code>HTTP 200 OK</code>: content updated, one or more rows affected</p> </li>
     *  <li> <p><code>HTTP 204 No Content</code>: Update change nothing, zero rows affected</p> </li>
     *  <li> <p><code>HTTP 409 Conflict</code>: Conflict to update rows, zero rows affected</p> </li>
     * </ul>
     * @param q queryable name
     * @param body request information using json
     * @return <code>HTTP 200 OK</code>, <code>HTTP 204 No Content</code> or <code>HTTP 409 Conflict</code> with
     * number of rows affected by update command.
     * @throws JsonParseException  for parsing problems
     * @throws JsonMappingException for mapping of content problems
     * @throws IOException for  interrupted I/O operations problems
     *
    @PUT
    @Path("update/{q}")
    public Response update(@PathParam("q") String q, 
                           String body) throws JsonParseException, JsonMappingException, IOException 
    {
        Map<String,Object> params = new ObjectMapper().readValue(body, HashMap.class);
        Queryable queryable = QueryFactory.newInstance(q, params);
        Integer rowsAffected = getRepository().update(queryable);
        return buildSimpleResponse(rowsAffected);
    }
    */
    //HTTP 201 Created
    //HTTP 409 Conflict
    @PUT
    @Path("update/{q}/{m}/{id}")
    public Response update(@PathParam("q") String q, 
                           @PathParam("m") String m, 
                           @PathParam("id") String id, 
                           @Context UriInfo ui) 
    {
        throw new UnsupportedOperationException("update/{q}/{m}/{id} NOT implemented YET!");
        /*
        Class<?> clazz = modelTypes.getType(m);
        Queryable queryable = null;//buildQueryable(q, clazz, id);
        Object entity = getRepository().get(id);
        //Queryable queryable = buildQueryable(q, id, ui);
        Integer rowsAffected = getRepository().update(queryable);
        return buildSimpleResponse(rowsAffected);
        //return buildSimpleResponse(rowsAffected, queryable);// FIXME HTTP status
         */
    }


    /**
     * {@code PUT} HTTP verb to process the update requests. 
     * <ul>
     *  <li> <p><code>HTTP 200 OK</code>: content updated, one or more rows affected</p> </li>
     *  <li> <p><code>HTTP 204 No Content</code>: Update change nothing, zero rows affected</p> </li>
     *  <li> <p><code>HTTP 409 Conflict</code>: Conflict to update rows, zero rows affected</p> </li>
     * </ul>
     * @param q queryable name
     * @param body request information using json
     * @return <code>HTTP 200 OK</code>, <code>HTTP 204 No Content</code> or <code>HTTP 409 Conflict</code> with
     * number of rows affected by update command.
     * @throws JsonParseException  for parsing problems
     * @throws JsonMappingException for mapping of content problems
     * @throws IOException for  interrupted I/O operations problems
     */
    @PUT
    @Path("updateAll/{q}")
    public Response updateAll(@PathParam("context") String ctx, @PathParam("q") String q, 
                              @Context UriInfo ui, 
                              String body) throws JsonParseException, JsonMappingException, IOException 
    {
        List<Map<String, Object>> params = null;
        if (isNotEmpty(body))
        {
            params = new ObjectMapper().readValue(body, new TypeReference<List<Map<String, Object>>>(){});
        }
        if (params == null || params.isEmpty())
            throw new UnsupportedOperationException("List of data is mandatory to HTTP updateAll request");
        Queryable queryable = QueryFactory.newInstance(q, params);
        Integer rowsAffected = getRepository(ctx).update(queryable);
        return buildSimpleResponse(rowsAffected);
    }
    
    /*
     * {@code PUT} HTTP verb to process the update requests. 
     * <ul>
     *  <li> <p><code>HTTP 200 OK</code>: content updated, one or more rows affected</p> </li>
     *  <li> <p><code>HTTP 204 No Content</code>: Update change nothing, zero rows affected</p> </li>
     *  <li> <p><code>HTTP 409 Conflict</code>: Conflict to update rows, zero rows affected</p> </li>
     * </ul>
     * @param q queryable name
     * @param m
     * @param formParams
     * @return <code>HTTP 200 OK</code>, <code>HTTP 204 No Content</code> or <code>HTTP 409 Conflict</code> with
     * number of rows affected by update command.
     *
    @PUT
    @Path("updateAll/{q}/{m}")
    public Response updateAll(@PathParam("q") String q, 
                           @PathParam("m") String m,
                           MultivaluedMap<String, String> formParams) 
    {
        Class<?> clazz = modelTypes.getType(m);
        Queryable queryable = buildQueryable(q, clazz, formParams);
        Integer rowsAffected = getRepository().update(queryable);
        return buildSimpleResponse(rowsAffected);
    }
    */
    
    // HTTP 204 No Content
    // HTTP 403 Forbidden (Resource has constraint to delete)
    // HTTP 409 Conflict
    // HTTP 410 Gone (Resource already deleted )
    // HTTP 404 Not Found (Resource not exists)
    @DELETE
    @Path("remove/{q}")
    public Response remove(@PathParam("context") String ctx, @PathParam("q") String q, @Context UriInfo ui) 
    {
        Queryable queryable = buildQueryable(q, ui);
        Integer rowsAffected = getRepository(ctx).remove(queryable);
        return buildSimpleResponse(rowsAffected);
        //return buildSimpleResponse(rowsAffected, queryable);// FIXME HTTP status
    }

    // HTTP 204 No Content
    // HTTP 403 Forbidden (Resource has constraint to delete)
    // HTTP 409 Conflict
    // HTTP 410 Gone (Resource already deleted )
    // HTTP 404 Not Found (Resource not exists)
    @DELETE
    @Path("remove/{q}/{m}")
    public Response remove(@PathParam("context") String ctx, @PathParam("q") String q, @PathParam("m") String m, @Context UriInfo ui) 
    {
        Class<?> clazz = modelTypes.getType(m);
        Queryable queryable = buildQueryable(q, clazz, ui);
        int rowsAffected = getRepository(ctx).remove(queryable);
        return buildSimpleResponse(rowsAffected);
        //return buildSimpleResponse(rowsAffected, queryable);// FIXME HTTP status
    }
/*
    // HTTP 204 No Content
    // HTTP 403 Forbidden (Resource has constraint to delete)
    // HTTP 409 Conflict
    // HTTP 410 Gone (Resource already deleted )
    // HTTP 404 Not Found (Resource not exists)
    @DELETE
    @Path("remove/{q}/{id}")
    public Response remove(@PathParam("q") String q, @PathParam("id") Long id, @Context UriInfo ui) {
        Object entity = getRepository().get(id);
        Queryable queryable = buildQueryable(q, entity, ui);
        int rowsAffected = getRepository().remove(queryable);
        return buildSimpleResponse(rowsAffected, queryable);// FIXME HTTP status
    }
    */

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////    

    private boolean isNotEmpty(String s)
    {
        return s != null && !"".equals(s);
    }
}
