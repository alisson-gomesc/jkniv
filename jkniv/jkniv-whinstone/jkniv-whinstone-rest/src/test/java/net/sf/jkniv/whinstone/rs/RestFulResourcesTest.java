package net.sf.jkniv.whinstone.rs;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import net.sf.jkniv.sqlegance.QueryFactory;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Repository;

import static org.mockito.BDDMockito.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;


public class RestFulResourcesTest
{
    RestFulResources resources;
    //private Repository repository;
    //private Queryable queryable = QueryFactory.newInstance("mycars");
    UriInfo uri = null;
    Response OK_200 = Response.status(Status.OK).build();
    Response NO_CONTENT = Response.status(Status.NO_CONTENT).build();
    String listOk = "OK";
    String listNoContent = "NO_CONTENT";
    String model = "model";
    String transform = "transform";
    
    @Before
    public void setup() 
    {
        //List cars = Arrays.asList(new Car("147", "Fiat", 2), new Car("fusca", "volks", 2)); 
        this.resources = mock(RestFulResources.class);

        // HTTP 200 OK
        given(resources.list(listOk, uri)).willReturn(OK_200);
        given(resources.list(listOk, model, uri)).willReturn(OK_200);
        given(resources.list(listOk, model, transform, uri)).willReturn(OK_200);

        // HTTP 204 NO_CONTENT
        given(resources.list(listNoContent, uri)).willReturn(NO_CONTENT);
        given(resources.list(listNoContent, model, uri)).willReturn(NO_CONTENT);
        given(resources.list(listNoContent, model, transform, uri)).willReturn(NO_CONTENT);

        // HTTP 200 OK
        given(resources.get(listOk, uri)).willReturn(OK_200);
        given(resources.get(listOk, model, uri)).willReturn(OK_200);
        //given(resources.get(listOk, model, transform, uri)).willReturn(OK_200);

        // HTTP 204 NO_CONTENT
        given(resources.get(listNoContent, uri)).willReturn(NO_CONTENT);
        given(resources.get(listNoContent, model, uri)).willReturn(NO_CONTENT);
        //given(resources.get(listNoContent, model, transform, uri)).willReturn(NO_CONTENT);

    }

    @Test
    public void whenListResource_200_OK()
    {
        Response response1 = resources.list(listOk, uri);
        Response response2 = resources.list(listOk, model, uri);
        Response response3 = resources.list(listOk, model, transform, uri);
        
        assertThat(response1.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(response2.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(response3.getStatus(), is(Response.Status.OK.getStatusCode()));
        
        verify(resources).list(listOk, uri);
        verify(resources).list(listOk, model, uri);
        verify(resources).list(listOk, model, transform, uri);
    }

    @Test
    public void whenListResource_204_NO_CONTEXT()
    {
        Response response1 = resources.list(listNoContent, uri);
        Response response2 = resources.list(listNoContent, model, uri);
        Response response3 = resources.list(listNoContent, model, transform, uri);
        
        assertThat(response1.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        assertThat(response2.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        assertThat(response3.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        
        verify(resources).list(listNoContent, uri);
        verify(resources).list(listNoContent, model, uri);
        verify(resources).list(listNoContent, model, transform, uri);
    }
    @Test
    public void whenGetResource_200_OK()
    {
        Response response1 = resources.get(listOk, uri);
        Response response2 = resources.get(listOk, model, uri);
        //Response response3 = resources.get(listOk, model, transform, uri);
        
        assertThat(response1.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(response2.getStatus(), is(Response.Status.OK.getStatusCode()));
        //assertThat(response3.getStatus(), is(Response.Status.OK.getStatusCode()));
        
        verify(resources).get(listOk, uri);
        verify(resources).get(listOk, model, uri);
        //verify(resources).get(listOk, model, transform, uri);
    }

    @Test
    public void whenGetResource_204_NO_CONTEXT()
    {
        Response response1 = resources.get(listNoContent, uri);
        Response response2 = resources.get(listNoContent, model, uri);
        //Response response3 = resources.get(listNoContent, model, transform, uri);
        
        assertThat(response1.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        assertThat(response2.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        //assertThat(response3.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        
        verify(resources).get(listNoContent, uri);
        verify(resources).get(listNoContent, model, uri);
        //verify(resources).get(listNoContent, model, transform, uri);
    }


}
