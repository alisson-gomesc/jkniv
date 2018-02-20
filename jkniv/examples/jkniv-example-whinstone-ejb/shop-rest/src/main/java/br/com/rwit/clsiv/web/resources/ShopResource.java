package br.com.rwit.clsiv.web.resources;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.examples.client.BusinessFacade;
import net.sf.jkniv.examples.entities.Book;

/**
 * 
 * @author Alisson Gomes
 *
 */
@Path("rs")
@PermitAll
public class ShopResource
{
    private static final Logger LOG = LoggerFactory.getLogger(ShopResource.class);
    
    @EJB
    private BusinessFacade businessFacade;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("save")
    public Response save(Book book) {
        
        businessFacade.save(book);
        return Response.ok().build();
    }

}
