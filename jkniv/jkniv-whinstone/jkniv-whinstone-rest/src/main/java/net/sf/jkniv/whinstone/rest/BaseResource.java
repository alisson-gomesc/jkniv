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
import net.sf.jkniv.sqlegance.QueryNotFoundException;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.RepositoryService;

/**
 * 
 * @author Alisson Gomes
 *
 */
public class BaseResource
{
    private static final Logger               LOG           = LoggerFactory.getLogger(BaseResource.class);
    private static final MethodName           SETTER        = MethodNameFactory.getInstanceSetter();
    protected static final SimpleDateFormat   SDF           = new SimpleDateFormat("yyyy-MM-dd");
    protected static String                   sqlContextName;
    private static int                        MAX           = 10;
    protected static final SimpleNameRegistry modelTypes    = new SimpleNameRegistry();
    protected static final SimpleNameRegistry reportJaspers = new SimpleNameRegistry();
    protected static final SimpleNameRegistry transformers  = new SimpleNameRegistry();
    public static Map<String, Repository>     repositories  = new HashMap<>();
    
    public BaseResource()
    {
        if (sqlContextName == null)
            sqlContextName = "/repository-sql.xml";
        
        if (repositories.isEmpty())
            initRepositories();
    }
    
    static void initRepositories()
    {
        RepositoryService service = RepositoryService.getInstance();
        String[] contexts = sqlContextName.split(",");
        for (String ctxName : contexts)
        {
            ctxName = ctxName.trim();
            LOG.trace("Creating new repository for sql context [" + ctxName + "]");
            
            SqlContext context = SqlContextFactory.newInstance(ctxName);
            if (!repositories.containsKey(context.getName()))
            {
                Repository repository = service.lookup(context.getRepositoryConfig().getRepositoryType())
                        .newInstance(context);
                repositories.put(context.getName(), repository);
                LOG.debug("Sql Context [" + ctxName + "] was created");
            }
        }
    }
    
    protected Response buildSimpleResponse(Object resource)
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
    
    protected Response ok()
    {
        return Response.ok().build();
    }
    
    Queryable buildQueryable(String q, UriInfo ui)
    {
        return newQuery(q, marshallToMap(ui.getQueryParameters()), 0);
    }
    
    Queryable buildQueryable(String q, Map<String, ?> params)
    {
        return newQuery(q, params, 0);
    }
    
    Queryable buildQueryable(String q, Object params)
    {
        return newQuery(q, params, 0);
    }
    
    private Queryable _buildQueryable(String q, Class<?> className, MultivaluedMap<String, String> formParams)
    {
        ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(className);
        return newQuery(q, marshallToProxy(proxy, formParams), 0);
    }
    
    Queryable buildQueryable(String q, Class<?> className, UriInfo ui)
    {
        ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(className);
        return newQuery(q, marshallToProxy(proxy, ui.getQueryParameters()), 0);
    }
    
    Queryable buildQueryable(String q, Object entity, UriInfo ui)
    {
        ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(entity);
        return newQuery(q, marshallToProxy(proxy, ui.getQueryParameters()), 0);
    }
    
    public static Repository getRepository(String context)
    {
        if (repositories.isEmpty())
            initRepositories();
        return repositories.get(context);
    }
    
    Object marshallToProxy(ObjectProxy<?> proxy, MultivaluedMap<String, String> pathParams)
    {
        for (Map.Entry<String, List<String>> entry : pathParams.entrySet())
        {
            List<String> list = entry.getValue();
            if (list.size() == 1)
            {
                proxy.invoke(SETTER.capitalize(entry.getKey()), list.get(0));
            }
            else if (list.size() > 1)
            {
                proxy.invoke(SETTER.capitalize(entry.getKey()), list);
            }
        }
        return proxy.getInstance();
    }
    
    Map<String, Object> marshallToMap(MultivaluedMap<String, String> pathParams)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        for (Map.Entry<String, List<String>> entry : pathParams.entrySet())
        {
            List<String> list = entry.getValue();
            if (list.size() == 1)
                params.put(entry.getKey(), list.get(0));
            else if (list.size() > 1)
                params.put(entry.getKey(), list);
        }
        return params;
    }
    
    private Queryable newQuery(String name, Object params, int page)
    {
        Queryable q = QueryFactory.of(name, params);
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
            //objectMapper.configure(Feature.WRITE_DATES_AS_TIMESTAMPS , false);
            objectMapper.disable(Feature.WRITE_DATES_AS_TIMESTAMPS);// FIXME design to pass jackson properties
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
