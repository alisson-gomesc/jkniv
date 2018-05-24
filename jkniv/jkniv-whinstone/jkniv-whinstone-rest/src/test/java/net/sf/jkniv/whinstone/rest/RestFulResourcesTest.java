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

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.rest.RestFulResources;

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
        given(resources.list("rs",listOk, uri)).willReturn(OK_200);
        given(resources.list("rs",listOk, model, uri)).willReturn(OK_200);
        given(resources.list("rs",listOk, model, transform, uri)).willReturn(OK_200);

        // HTTP 204 NO_CONTENT
        given(resources.list("rs",listNoContent, uri)).willReturn(NO_CONTENT);
        given(resources.list("rs",listNoContent, model, uri)).willReturn(NO_CONTENT);
        given(resources.list("rs",listNoContent, model, transform, uri)).willReturn(NO_CONTENT);

        // HTTP 200 OK
        given(resources.get("rs",listOk, uri)).willReturn(OK_200);
        given(resources.get("rs",listOk, model, uri)).willReturn(OK_200);
        //given(resources.get(listOk, model, transform, uri)).willReturn(OK_200);

        // HTTP 204 NO_CONTENT
        given(resources.get("rs",listNoContent, uri)).willReturn(NO_CONTENT);
        given(resources.get("rs",listNoContent, model, uri)).willReturn(NO_CONTENT);
        //given(resources.get(listNoContent, model, transform, uri)).willReturn(NO_CONTENT);

    }

    @Test
    public void whenListResource_200_OK()
    {
        Response response1 = resources.list("rs",listOk, uri);
        Response response2 = resources.list("rs",listOk, model, uri);
        Response response3 = resources.list("rs",listOk, model, transform, uri);
        
        assertThat(response1.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(response2.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(response3.getStatus(), is(Response.Status.OK.getStatusCode()));
        
        verify(resources).list("rs",listOk, uri);
        verify(resources).list("rs",listOk, model, uri);
        verify(resources).list("rs",listOk, model, transform, uri);
    }

    @Test
    public void whenListResource_204_NO_CONTEXT()
    {
        Response response1 = resources.list("rs",listNoContent, uri);
        Response response2 = resources.list("rs",listNoContent, model, uri);
        Response response3 = resources.list("rs",listNoContent, model, transform, uri);
        
        assertThat(response1.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        assertThat(response2.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        assertThat(response3.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        
        verify(resources).list("rs",listNoContent, uri);
        verify(resources).list("rs",listNoContent, model, uri);
        verify(resources).list("rs",listNoContent, model, transform, uri);
    }
    @Test
    public void whenGetResource_200_OK()
    {
        Response response1 = resources.get("rs",listOk, uri);
        Response response2 = resources.get("rs",listOk, model, uri);
        //Response response3 = resources.get(listOk, model, transform, uri);
        
        assertThat(response1.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(response2.getStatus(), is(Response.Status.OK.getStatusCode()));
        //assertThat(response3.getStatus(), is(Response.Status.OK.getStatusCode()));
        
        verify(resources).get("rs",listOk, uri);
        verify(resources).get("rs",listOk, model, uri);
        //verify(resources).get(listOk, model, transform, uri);
    }

    @Test
    public void whenGetResource_204_NO_CONTEXT()
    {
        Response response1 = resources.get("rs",listNoContent, uri);
        Response response2 = resources.get("rs",listNoContent, model, uri);
        //Response response3 = resources.get(listNoContent, model, transform, uri);
        
        assertThat(response1.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        assertThat(response2.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        //assertThat(response3.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        
        verify(resources).get("rs",listNoContent, uri);
        verify(resources).get("rs",listNoContent, model, uri);
        //verify(resources).get(listNoContent, model, transform, uri);
    }


}
