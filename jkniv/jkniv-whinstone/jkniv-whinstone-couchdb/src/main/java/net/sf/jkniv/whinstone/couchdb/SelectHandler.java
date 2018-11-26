package net.sf.jkniv.whinstone.couchdb;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.cache.Cacheable;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.ResultRow;

public class SelectHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(SelectHandler.class);
    private HttpCookieConnectionAdapter       adapterConn;
    private Queryable queryable;
    private Class<?> overloadReturnType;
    private Selectable selectable;
    private ResultRow<?, ?> overloadResultRow;
    
    public SelectHandler(Queryable queryable, 
                         Selectable selectable, 
                         Class<?> overloadReturnType, 
                         ResultRow<?, ?> overloadResultRow,
                         HttpCookieConnectionAdapter adapterConn)
    {
        this.queryable = queryable;
        this.selectable = selectable;
        this.overloadReturnType = overloadReturnType;
        this.overloadResultRow = overloadResultRow;
        this.adapterConn = adapterConn;
    }
    
    @SuppressWarnings("unchecked")
    public <T> List<T> list()
    {
        if (LOG.isTraceEnabled())
            LOG.trace("Executing [{}] as select command", queryable);
        
        Queryable queryableClone = QueryFactory.clone(queryable, overloadReturnType);
        List<T> list = Collections.emptyList();
        //Selectable selectable = sqlContext.getQuery(queryable.getName()).asSelectable();
        selectable.getValidateType().assertValidate(queryableClone.getParams());
        
        if (!queryableClone.isBoundSql())
            queryableClone.bind(selectable);
        
        Cacheable.Entry entry = null;
        
        if (!queryableClone.isCacheIgnore())
            entry = selectable.getCache().getEntry(queryableClone);
        
        if (entry == null)
        {
            Command command = adapterConn.asSelectCommand(queryableClone, overloadResultRow);
            list = command.execute();
            if (selectable.hasCache() && !list.isEmpty())
                selectable.getCache().put(queryableClone, list);
        }
        else
        {
            queryable.cached();
            list = (List<T>) entry.getValue();
            if (LOG.isDebugEnabled())
                LOG.debug("{} object(s) was returned from [{}] cache using query [{}] since {} reach [{}] times",
                        list.size(), selectable.getCache().getName(), selectable.getName(), entry.getTimestamp(),
                        entry.hits());
        }
        queryable.setTotal(queryableClone.getTotal());
        if (LOG.isDebugEnabled())
            LOG.debug("Executed [{}] query as select command, {} rows fetched", queryableClone.getName(), list.size());
        
        return list;
        
    }
    
}
