package net.sf.jkniv.whinstone.rest;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.stream.JsonGenerationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.reflect.beans.MethodName;
import net.sf.jkniv.reflect.beans.MethodNameFactory;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.QueryFactory;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Repository;
import net.sf.jkniv.sqlegance.RepositoryService;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;

/**
 * 
 * @author Alisson Gomes
 *
 */
public class BaseResource
{
    private static final Logger LOG =LoggerFactory.getLogger(BaseResource.class);
    private static final MethodName SETTER = MethodNameFactory.getInstanceSetter();
    protected static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
    protected static String sqlContextName;
    //private static SqlContext sqlContext;// = SqlContextFactory.newInstance("/repository-sql.xml");
    private static int       MAX       = 10;
    protected static final SimpleNameRegistry modelTypes = new SimpleNameRegistry();
    protected static final SimpleNameRegistry reportJaspers = new SimpleNameRegistry();
    protected static final SimpleNameRegistry transformers = new SimpleNameRegistry();
    //private Repository repository;
    public static Map<String, Repository> repositories = new HashMap<>();
    
    /*
    static {
        if (sqlContextName == null)
            sqlContextName = "/repository-sql.xml";
        
        if(repositories.isEmpty())
            initRepositories();
    }
     */
    
    public BaseResource()
    {
        if (sqlContextName == null)
            sqlContextName = "/repository-sql.xml";
        
        if(repositories.isEmpty())
            initRepositories();
    }
        
    private static void initRepositories()
    {
        RepositoryService service = RepositoryService.getInstance();
        String[] contexts = sqlContextName.split(",");
        for(String ctxName : contexts)
        {
            ctxName = ctxName.trim();
            LOG.trace("Creating new repository for sql context ["+ctxName+"]");
            
            SqlContext context = SqlContextFactory.newInstance(ctxName);
            if(!repositories.containsKey(context.getName()))
            {
                Repository repository = service.lookup(context.getRepositoryConfig().getRepositoryType()).newInstance(context);
                repositories.put(context.getName(), repository);
                LOG.debug("Sql Context ["+ctxName+"] was created");
            }
        }
    }

    /*
     * Build response with status for error messages or OK otherwise for the content.
     * @param messages
     * @param status
     * @return
     *
    protected Response buildResponse(List<Message> messages, Status status)
    {
        return buildResponse(null, messages, status);
    }
    */

    /*
     * Build response with NOT_FOUND status for error messages or OK otherwise for the content.
     * @param messages
     * @return
     *
    protected Response buildResponse(List<Message> messages)
    {
        Status httpStatus = Status.NOT_FOUND;
        for (Message m : messages) {
            if (m.getType() == MessageType.ERROR)
                httpStatus = Status.CONFLICT;
        }
        return buildResponse(null, messages, httpStatus);
    }
    */
    /*
     * Build response with status for error messages or OK otherwise for the content.
     * @param content
     * @param status
     * @return
     *
    protected Response buildResponse(Object content, Status status)
    {
        return buildResponse(content, Collections.emptyList(), status);
    }
    */

    /*
     * Build response with NOT_FOUND status for error messages or OK otherwise for the content.
     * @param content
     * @return
     *
    protected Response buildResponse(Object content)
    {
        return buildResponse(content, Collections.emptyList(), Status.NOT_FOUND);
    }
    */

    /*
     * Build NOT_FOUND status response if has error messages or OK otherwise for the content. 
     * @param content
     * @param messages
     * @return
     *
    protected Response buildResponse(Object content, List<Message> messages)
    {
        return buildResponse(content, messages, Status.NOT_FOUND);
    }
    */
    /*
    protected  Response buildResponse(Object content, List<Message> messages, Status status)
    {
        ServiceResponse serviceResponse = new ServiceResponse(messages);
        if (content != null)
            serviceResponse.setContent(content);
        
        return buildResponse(serviceResponse, status);
    }

    protected  Response buildResponse(ServiceResponse serviceResponse)
    {
        return buildResponse(serviceResponse, Status.NOT_FOUND);
    }

    protected  Response buildResponse(ServiceResponse serviceResponse, Status status)
    {
        Response response = null;
        if (serviceResponse.hasError() || serviceResponse.hasWarn())
            response = Response.status(status).entity(serviceResponse).build();
        else
            response = Response.ok().entity(serviceResponse).build();
        return response;
    }
*/
    protected  Response buildSimpleResponse(Object resource)
    {
        Response response = null;
        if (resource != null)
            response = Response.ok().entity(jsonContent(resource)).build();
        else
            response = Response.status(Status.NO_CONTENT).build();
        return response;
    }

    protected Response buildSimpleResponse(List<?> resources)
    {
        //GenericEntity<List> list = new GenericEntity<List>(resources){};
        Response response = null;
        if (resources.isEmpty())
            response = Response.status(Status.NO_CONTENT).build();
        else
            response = Response.ok().entity(jsonContent(resources)).build();
        
        return response;
    }

    protected Response buildSimpleResponse(Number rowsAffected)
    {
        Map<String, Number> content = new HashMap<>(1);
        content.put("rows", rowsAffected);
        if (rowsAffected.intValue() > 0)
            return Response.ok().entity(jsonContent(content)).build();
        else if (rowsAffected.intValue() == 0)
            return Response.noContent().entity(jsonContent(content)).build();
        else
            return Response.status(Status.CONFLICT).entity(jsonContent(content)).build();
    }
    

    protected  Response ok()
    {
        return Response.ok().build();
    }
    /*
    protected  Response buildSimpleResponse(int rowsAffected, Queryable queryable)
    {
        Response response = null;
        if (rowsAffected < 1)
            response = Response.status(Status.CONFLICT).build();
        else
            response = Response.ok().entity(new ServiceResponse().setContent(queryable.getParams()).getContentJson()).build();
        
        return response;
    }

    protected Response buildServerError(Message message)
    {
        if (message == null || message.getType() != MessageType.ERROR)
            throw new IllegalArgumentException("Message must be error type ["+message+"]");
        
        List<Message> messages = new ArrayList<Message>();
        messages.add(message);
        return buildResponse(null, messages, Status.INTERNAL_SERVER_ERROR);
    }
*/

//    protected Queryable buildQueryable(UriInfo ui)
//    {
//        String q = ui.getPathParameters().get("q").get(0);
//        return newQuery(q, marshallToMap(ui.getPathParameters()), 0);
//    }

    protected Queryable buildQueryable(String q, UriInfo ui)
    {
        return newQuery(q, marshallToMap(ui.getQueryParameters()), 0);
    }

    protected Queryable buildQueryable(String q, Map<String,?> params)
    {
        return newQuery(q, params, 0);
    }

    protected Queryable buildQueryable(String q, Object params)
    {
        return newQuery(q, params, 0);
    }

//    protected Queryable buildQueryable(String q, Class<?> className, String id)
//    {
//        ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(className);
//        return newQuery(q, marshallToMap(proxy, ui.getQueryParameters()), 0);
//    }

    

    private Queryable _buildQueryable(String q, Class<?> className, MultivaluedMap<String, String> formParams)
    {
        ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(className);
        return newQuery(q, marshallToProxy(proxy, formParams), 0);
    }

    protected Queryable buildQueryable(String q, Class<?> className, UriInfo ui)
    {
        ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(className);
        return newQuery(q, marshallToProxy(proxy, ui.getQueryParameters()), 0);
    }

    protected Queryable buildQueryable(String q, Object entity, UriInfo ui)
    {
        ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(entity);
        return newQuery(q, marshallToProxy(proxy, ui.getQueryParameters()), 0);
    }

    public static Repository getRepository(String context)
    {
        return repositories.get(context);
    }


    protected Object marshallToProxy(ObjectProxy<?> proxy, MultivaluedMap<String, String> pathParams)
    {
        for (Map.Entry<String, List<String>> entry : pathParams.entrySet())
        {
            List<String> list = entry.getValue();
            if (list.size() == 1)
                proxy.invoke(SETTER.capitalize(entry.getKey()), list.get(0));
            else if(list.size() > 1)
                proxy.invoke(SETTER.capitalize(entry.getKey()), list);
        }
        return proxy.getInstance();
    }

    protected Map<String, Object> marshallToMap(MultivaluedMap<String, String> pathParams)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        for (Map.Entry<String, List<String>> entry : pathParams.entrySet())
        {
            List<String> list  =entry.getValue();
            if (list.size() == 1)
                params.put(entry.getKey(), list.get(0));
            else if(list.size() > 1)
                params.put(entry.getKey(), list);
        }
        return params;
    }
    
    private Queryable newQuery(String name, Object params, int page)
    {
        Queryable q = QueryFactory.newInstance(name, params);
        if (page > 1)
            q.setOffset((page - 1) * MAX);
        return q;
    }


    private String jsonContent(Object content)
    {
        String json = null;
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(Feature.WRITE_DATES_AS_TIMESTAMPS , false);
            json = new ObjectMapper().writeValueAsString(content);
        }
        catch (JsonGenerationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (JsonMappingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return json;
    }
}
