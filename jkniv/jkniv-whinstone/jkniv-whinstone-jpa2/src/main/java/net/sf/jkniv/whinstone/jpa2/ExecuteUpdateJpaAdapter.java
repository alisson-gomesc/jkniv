package net.sf.jkniv.whinstone.jpa2;

import java.util.List;

import javax.persistence.EntityManager;

import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.Queryable;

public class ExecuteUpdateJpaAdapter extends AbstractQueryJpaAdapter
{

    public ExecuteUpdateJpaAdapter(EntityManager em, Queryable queryable, Sql isql)
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
