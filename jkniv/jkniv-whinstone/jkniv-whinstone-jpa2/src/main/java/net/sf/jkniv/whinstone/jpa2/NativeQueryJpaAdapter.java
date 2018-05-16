package net.sf.jkniv.whinstone.jpa2;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.whinstone.Queryable;

public class NativeQueryJpaAdapter extends AbstractQueryJpaAdapter
{
    private Query queryJpa;
    
    public NativeQueryJpaAdapter(EntityManager em, Queryable queryable, Sql isql)
    {
        super(em, queryable, isql);
    }

    @Override
    public <T> T getSingleResult()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public <T> List<T> getResultList()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
