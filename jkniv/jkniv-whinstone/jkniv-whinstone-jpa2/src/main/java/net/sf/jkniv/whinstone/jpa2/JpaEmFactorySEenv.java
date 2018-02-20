package net.sf.jkniv.whinstone.jpa2;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.transaction.Transactional;
import net.sf.jkniv.whinstone.jpa2.transaction.EmptyTransactionAdapter;
import net.sf.jkniv.whinstone.jpa2.transaction.JpaTransactionAdapter;

class JpaEmFactorySEenv implements JpaEmFactory
{
    private static final Logger LOG = LoggerFactory.getLogger(JpaEmFactorySEenv.class);
    private EntityManagerFactory emf;
    private Boolean containerManaged;
    
    public JpaEmFactorySEenv(String unitName)
    {
        this.emf = Persistence.createEntityManagerFactory(unitName);
        //isContainerManaged();
        LOG.info("Factory {} was started successfully for unitName [{}]", getClass().getName(), unitName);
    }
    
    @Override
    public EntityManager createEntityManager()
    {
        EntityManager em = emf.createEntityManager();
        LOG.debug("EntityManager {} was lookup successufly", em);
        return em;
    }

    /*
    @Override
    public boolean isContainerManaged()
    {
        if (containerManaged == null) 
        {
            EntityManager em = null;
            try
            {
                em = createEntityManager();
                em.getTransaction();
                
                containerManaged = false;
            }
            catch(IllegalStateException ex) {
                containerManaged = true;
            } finally {
                if (em!=null && em.isOpen())
                    em.close();
            }
        }
        return containerManaged;
    }
    */
    
    /**
     * @throws IllegalStateException if the entity manager is container-managed
     */
    @Override
    public Transactional getTransaction()
    {
        if (containerManaged) 
            return EmptyTransactionAdapter.empty();
            
        
        EntityTransaction tx = createEntityManager().getTransaction();
        Transactional transactional = new JpaTransactionAdapter(tx);
        
        return transactional;
    }
    

    @Override
    public boolean isActive()
    {
        return (emf != null && emf.isOpen());
    }

    @Override
    public boolean close(EntityManager em)
    {
        LOG.debug("Closing Entity Manager");
        em.close();
        return true;
    }

    @Override
    public boolean close()
    {
        LOG.debug("Closing JPA Entity Manager Facotry");
        if (isActive())
            emf.close();
        
        return true;
    }
    
}
