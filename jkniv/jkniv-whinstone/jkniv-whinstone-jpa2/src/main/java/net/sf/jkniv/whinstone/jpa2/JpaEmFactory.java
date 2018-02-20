package net.sf.jkniv.whinstone.jpa2;

import javax.persistence.EntityManager;

import net.sf.jkniv.sqlegance.transaction.Transactional;

public interface JpaEmFactory
{
    
    EntityManager createEntityManager();
    
    //boolean isContainerManaged();
    
    /**
     * Retrieve the transaction in progress from thread.
     * @throws IllegalStateException if the entity manager is container-managed
     * @return Transaction from thread 
     */
    Transactional getTransaction();
    
    boolean isActive();
    
    boolean close(EntityManager em);
    
    boolean close();
    
}
