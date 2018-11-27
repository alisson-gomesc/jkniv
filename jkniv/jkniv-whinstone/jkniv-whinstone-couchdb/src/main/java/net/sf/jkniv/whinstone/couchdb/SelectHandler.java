package net.sf.jkniv.whinstone.couchdb;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.cache.Cacheable;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.DefaultCommandHandler;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.ResultRow;

public class SelectHandler extends DefaultCommandHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(SelectHandler.class);
    
    public SelectHandler(HttpCookieConnectionAdapter adapterConn)
    {
        super(adapterConn);
        with(this);
    }
    
    @Override @SuppressWarnings("unchecked")
    public <T> T run()
    {
        if (LOG.isTraceEnabled())
            LOG.trace("Executing [{}] as {} command", queryable, sql.getSqlType());
        
        List<?> list = Collections.emptyList();
        sql.getValidateType().assertValidate(queryable.getParams());
        Selectable selectable = sql.asSelectable();
        if (!queryable.isBoundSql())
            queryable.bind(selectable);
        
        Cacheable.Entry entry = null;
        
        if (!queryable.isCacheIgnore())
            entry = selectable.getCache().getEntry(queryable);
        
        if (entry == null)
        {
            preCallback();
            Command command = adapterConn.asSelectCommand(queryable, overloadResultRow);
            list = command.execute();
            postCallback();
            if (selectable.hasCache() && !list.isEmpty())
                selectable.getCache().put(queryable, list);
        }
        else
        {
            queryable.cached();
            list = (List<?>) entry.getValue();
            if (LOG.isDebugEnabled())
                LOG.debug("{} object(s) was returned from [{}] cache using query [{}] since {} reach [{}] times",
                        list.size(), selectable.getCache().getName(), sql.getName(), entry.getTimestamp(),
                        entry.hits());
        }
        queryable.setTotal(queryable.getTotal());
        if (LOG.isDebugEnabled())
            LOG.debug("Executed [{}] query as {} command, {} rows fetched", queryable.getName(), sql.getSqlType(), list.size());
        
        return (T)list;
    }

    @Override
    public Command asCommand()
    {
        return adapterConn.asSelectCommand(queryable, overloadResultRow);
    }
}
