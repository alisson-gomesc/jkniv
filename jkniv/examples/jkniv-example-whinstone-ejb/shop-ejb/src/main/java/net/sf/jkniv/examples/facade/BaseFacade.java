package net.sf.jkniv.examples.facade;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.Repository;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.whinstone.jpa2.RepositoryJpa;

public class BaseFacade
{
    protected final Logger LOG = LoggerFactory.getLogger(getClass());
    protected static SqlContext sqlContext = SqlContextFactory.newInstance("/repository-sql.xml");
    
    private Repository repository;
    
    //@PersistenceContext(unitName="shop-repo")
    //protected EntityManager em;
    
    
    @PostConstruct
    protected void initRepo()
    {
        //this.repository = new RepositoryJpa(em, sqlContext);
        this.repository = new RepositoryJpa("shop-repo", sqlContext);
        //LOG.info("init new repository with EM [{}] and repository [{}]", em, sqlContext);
        //repository = new RepositoryJpa("clsiv-repo");
    }

    protected Repository getRepository() {
        //return RepositoryFactory.getInstance();
        return repository;
    }
    protected Date newDateTime(Date date, int h, int m, int s)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, h);
        c.set(Calendar.MINUTE, m);
        c.set(Calendar.SECOND, s);
        c.set(Calendar.MILLISECOND, 999);;
        return c.getTime();
    }
    
    protected Map<String, Object> newParams()
    {
        return new HashMap<String, Object>();
    }
    protected Map<String, Object> newParams(String k, Object v)
    {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put(k, v);
        return m;
    }
}
