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
package net.sf.jkniv.whinstone.jpa2;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.whinstone.jpa2.transaction.EmptyTransactionAdapter;
import net.sf.jkniv.whinstone.jpa2.transaction.JpaTransactionAdapter;
import net.sf.jkniv.whinstone.transaction.Transactional;

class JpaEmFactorySEenv implements JpaEmFactory
{
    private static final Logger LOG = LoggerFactory.getLogger(JpaEmFactorySEenv.class);
    private EntityManagerFactory emf;
    private Boolean containerManaged;
    
    public JpaEmFactorySEenv(String unitName)
    {
        this.emf = Persistence.createEntityManagerFactory(unitName);
        //isContainerManaged();
        LOG.warn("Java SE environments factory {} was started successfully for unitName [{}]. No supports for transaction managed!", getClass().getName(), unitName);
    }
    
    @Override
    public EntityManager createEntityManager()
    {
        EntityManager em = emf.createEntityManager();
        LOG.debug("EntityManager {} was lookup successufly", em);
        return em;
    }

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
