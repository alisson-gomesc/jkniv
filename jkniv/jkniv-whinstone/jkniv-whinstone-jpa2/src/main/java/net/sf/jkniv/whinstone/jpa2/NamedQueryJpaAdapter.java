package net.sf.jkniv.whinstone.jpa2;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.QueryNotFoundException;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.logger.SqlLogger;

public class NamedQueryJpaAdapter extends AbstractQueryJpaAdapter
{
    private static final Logger    LOG        = LoggerFactory.getLogger(NamedQueryJpaAdapter.class);

    public NamedQueryJpaAdapter(EntityManager em, Queryable queryable, Class<?> mandatoryReturnType, SqlLogger sqlLogger)
    {
        super(em, queryable, null, sqlLogger);
        try
        {
            if (mandatoryReturnType == null)
                this.queryJpa = em.createNamedQuery(queryable.getName());
            else
                this.queryJpa = em.createNamedQuery(queryable.getName(), mandatoryReturnType);
            
            this.initParams(queryJpa);
        }
        catch (IllegalArgumentException notFound)
        {
            throw new QueryNotFoundException("Named Query not found [" + queryable.getName() + "] check if orm.xml have the query named or it's annotated");
        }
    }
    
    

    private void initParams(Query queryJpa)
    {
        if (!queryable.isTypeOfNull())
        {
            if (queryable.isTypeOfMap())
            {
                //PrepareParams<Query> prepareParams = PrepareParamsFactory.newPrepareParams(queryJpa, new ParamParserColonMark(), queryable);
                setMapParams(queryJpa, (Map) queryable.getParams());
            }
            else if (queryable.getParams().getClass().isArray())
            {
                //PrepareParams<Query> prepareParams = PrepareParamsFactory.newPrepareParams(queryJpa, new ParamParserQuestionMark(), queryable);
                setArrayParams(queryJpa, (Object[]) queryable.getParams());
            }
        }
        if (queryable.isPaging())
            queryJpa.setFirstResult(queryable.getOffset()).setMaxResults(queryable.getMax());
    }
    
    private void setMapParams(Query query, Map<String, Object> params)
    {
        int i = 0;
        for (String s : params.keySet())
        {
            Object o = params.get(s);
            if (LOG.isDebugEnabled())// TODO making data sqlLogger.mask
                LOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", i++,
                        s, o, (o == null ? "NULL" : o.getClass()));
            
            query.setParameter(s, o);
        }
    }
    
    private void setArrayParams(Query query, Object[] params)
    {
        int i = 1;
        for (Object o : params)
        {
            if (LOG.isDebugEnabled())// TODO making data sqlLogger.mask
                LOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", i,
                        "?", o, (o == null ? "NULL" : o.getClass()));
            query.setParameter(i++, o);
        }
    }

    
}
