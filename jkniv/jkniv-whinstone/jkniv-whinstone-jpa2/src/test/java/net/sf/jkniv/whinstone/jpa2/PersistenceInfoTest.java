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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;

import org.junit.Ignore;
import org.junit.Test;

import com.acme.domain.orm.Book;

import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;

//@Ignore("Needs resolve classloader dependency where persistence.xml is search at web classpath")
public class PersistenceInfoTest
{
    private String UNIT_NAME = "whinstone";
    
    @Test
    public void providerNotNullTest()
    {
        String providerClass = PersistenceReader.getPersistenceInfo(UNIT_NAME).getProvider();
        assertThat(providerClass, notNullValue());
        //Assert.assertEquals("org.hibernate.ejb.HibernatePersistence", providerClass);
        assertThat("org.hibernate.jpa.HibernatePersistenceProvider", is(providerClass));
    }
    
    @Test
    public void providerEntityManagerFactoryTest()
    {
        String providerClass = PersistenceReader.getPersistenceInfo(UNIT_NAME).getProvider();
        ObjectProxy<PersistenceProvider> proxy = ObjectProxyFactory.of(providerClass);
        proxy.mute(InstantiationException.class).mute(IllegalAccessException.class).mute(ClassNotFoundException.class);
        PersistenceProvider provider = proxy.newInstance();
        EntityManagerFactory emf = provider.createEntityManagerFactory(UNIT_NAME, null);
        assertThat(emf, notNullValue());
    }
    
    @Test @Ignore("java.sql.SQLSyntaxErrorException: ORA-00942: table or view does not exist")
    public void providerEntityManagerTest()
    {
        String providerClass = PersistenceReader.getPersistenceInfo(UNIT_NAME).getProvider();
        ObjectProxy<PersistenceProvider> proxy = ObjectProxyFactory.of(providerClass);
        proxy.mute(InstantiationException.class).mute(IllegalAccessException.class).mute(ClassNotFoundException.class);
        
        //        PersistenceProvider provider = (PersistenceProvider) proxy.invoke("createEntityManagerFactory", UNIT_NAME);
        EntityManagerFactory emf = (EntityManagerFactory) proxy.invoke("createEntityManagerFactory", UNIT_NAME,
                new Properties());
        assertThat(emf, notNullValue());
        EntityManager em = emf.createEntityManager();
        assertThat(em, notNullValue());
        List<Book> list = em.createNativeQuery("select * from Book b").getResultList();
        assertThat(list, notNullValue());
    }
}
