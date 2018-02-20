package net.sf.jkniv.whinstone.jpa2;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.transaction.Transactional;
import net.sf.jkniv.whinstone.jpa2.transaction.EmptyTransactionAdapter;

class JpaEmFactoryJndi implements JpaEmFactory
{
    private final static  Logger LOG = LoggerFactory.getLogger(JpaEmFactoryJndi.class);
    private Context             ctx;
    private String              unitName;
    private NamingException     nex;
    private String              jndiName;
    private Boolean             active;
    
    public JpaEmFactoryJndi(String unitName)
    {
        this.unitName = unitName;
        this.jndiName = "java:comp/env/persistence/" + unitName;
        try
        {
            this.ctx = new InitialContext();
        }
        catch (NamingException ex)
        {
            LOG.error("Error to start the InitialContext {}", ex.getMessage());
            this.nex = ex;
        }
    }
    
    @Override
    public EntityManager createEntityManager()
    {
        EntityManager em = null;
        if (this.nex != null)
            throw new RepositoryException("Cannot create InitialContext", nex);
        
        try
        {
            em = (EntityManager) ctx.lookup(jndiName);
            LOG.debug("EntityManager {} was lookup successufly", em);
        }
        catch (NamingException ex)
        {
            throw new RepositoryException("Cannot lookup [" + jndiName + "] for EntityManager", ex);
        }
        return em;
    }
    
    /*
    @Override
    public boolean isContainerManaged()
    {
        return true;
    }
    */
    /**
     * @throws IllegalStateException if the entity manager is container-managed
     */
    @Override
    public Transactional getTransaction()
    {
        LOG.warn("JPA is container manager cannot lookup EntityTransaction");
        return EmptyTransactionAdapter.empty();
        //throw new IllegalStateException("JTA Entity Manager cannot, resource-level EntityTransaction object is container managed");
    }
    
    @Override
    public boolean isActive()
    {
        if (active == null)
            checkIsActive();
        
        return active;
    }
    
    private void checkIsActive()
    {
        try
        {
            this.createEntityManager();
            this.active = true;
            //this.active = em.isOpen();
        }
        catch (RepositoryException rex)
        {
            LOG.error("Error at start EntityManagerFactory for JNDI [{}], cause: {}", jndiName, rex.getCause().getMessage());
            this.active = false;
        }
    }
    
    @Override
    public boolean close(EntityManager em)
    {
        LOG.warn("Entity Manager factory is container-managed Entity Manager cannot be closed");
        return false;
    }
    
    @Override
    public boolean close()
    {
        LOG.warn("Entity Manager factory is container-managed cannot be closed");
        return false;
    }
    
}
