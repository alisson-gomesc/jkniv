package net.sf.jkniv.whinstone.jpa2;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.whinstone.jpa2.transaction.EmptyTransactionAdapter;
import net.sf.jkniv.whinstone.transaction.Transactional;

class JpaEmFactoryHard implements JpaEmFactory
{
    private static final Logger LOG     = LoggerFactory.getLogger(JpaEmFactoryHard.class);
    private static final String WARNING = "\n An entity manager must not be shared among multiple concurrently executing threads,"
            + "\n as the entity manager and persistence context are not required to be threadsafe."
            + "\n Entity managers must only be accessed in a single-threaded manner";
    private EntityManager       em;
    private Set<String>         threads;
    //private Boolean             containerManaged;
    
    public JpaEmFactoryHard(EntityManager em)
    {
        this.em = em;
        this.threads = new HashSet<String>();
        //isContainerManaged();
        LOG.warn("\nRepository instance using EntityManager provided with Constructor... ISN'T THREAD-SAFE! \nTake care, you need provider a new EntityManager for each thread!\n");
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.whinstone.jpa2.JpaEmFactory#createEntityManager()
     */
    @Override
    public EntityManager createEntityManager()
    {
        this.threads.add(Thread.currentThread().getName());
        
        if (threads.size() > 1)
        {
            StringBuilder sb = new StringBuilder();
            LOG.error(WARNING);
            for (String t : threads)
                sb.append(" [").append(t).append("]");
            
            LOG.error("\n\nThe EntityManager [{}] is being used concurrently with anothers threads: {}\n\n", em, sb.toString());
        }
        return em;
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.whinstone.jpa2.JpaEmFactory#isContainerManaged()
     *
    @Override
    public boolean isContainerManaged()
    {
        if (containerManaged == null)
        {
            try
            {
                em.getTransaction();
                containerManaged = false;
            }
            catch (IllegalStateException jta)
            {
                containerManaged = true;
            }
        }
        return containerManaged;
    }
    */
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.whinstone.jpa2.JpaEmFactory#getTransaction()
     */
    @Override
    public Transactional getTransaction()
    {
        //if (containerManaged)
            return EmptyTransactionAdapter.empty();
        
        //EntityTransaction tx = em.getTransaction();
        //Transactional transaction = new JpaTransactionAdapter(tx);
        //return transaction;
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.whinstone.jpa2.JpaEmFactory#isActive()
     */
    @Override
    public boolean isActive()
    {
        return (em != null);// && em.isOpen());
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.whinstone.jpa2.JpaEmFactory#close(javax.persistence.EntityManager)
     */
    @Override
    public boolean close(EntityManager em)
    {
        LOG.warn("Entity Manager cannot be closed, your origin is unknow");
        return false;
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.whinstone.jpa2.JpaEmFactory#close()
     */
    @Override
    public boolean close()
    {
        LOG.debug("Hard Factory the Entity Manager is not create here, cannot close");
        return false;
    }
    
}
