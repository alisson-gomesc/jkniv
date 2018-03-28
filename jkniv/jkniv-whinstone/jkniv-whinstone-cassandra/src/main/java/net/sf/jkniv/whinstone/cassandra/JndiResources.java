package net.sf.jkniv.whinstone.cassandra;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.RepositoryException;

/**
 * Permite acessar recursos JNDI.
 *  
 * @author Alisson Gomes
 *
 */
class JndiResources
{
    private static final Logger   LOG = LoggerFactory.getLogger(JndiResources.class);
    private static InitialContext ctx;
    
    static
    {
        try
        {
            ctx = new InitialContext();
        }
        catch (NamingException e)
        {
            throw new RepositoryException("Cannot initiate JNDI InitialContext", e);//TODO exception design, must have ConfigurationException?
        }
    }
    
    public static Object lookup(String value)
    {
        Object o = null;
        try
        {
            o = ctx.lookup(value);
            LOG.debug("lookup successfully properties for jndi ["+value+"]");
        }
        catch (NamingException e)
        {
            LOG.info("Can not found the jndi name [" + value + "]: " + e.getMessage());
        }
        return o;
    }
}
