package net.sf.jkniv.whinstone.jpa2;

import java.util.List;

import javax.persistence.EntityManager;

import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.whinstone.Queryable;

public class TypedQueryJpaAdapter extends AbstractQueryJpaAdapter
{
    
    public TypedQueryJpaAdapter(EntityManager em, Queryable queryable, Sql isql)
    {
        super(em, queryable, isql);
    }

    @Override
    public <T> T getSingleResult()
    {
        return null;
    }
    
    @Override
    public <T> List<T> getResultList()
    {
        return null;
    }
    
}
