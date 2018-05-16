package net.sf.jkniv.whinstone.jpa2.params;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import antlr.MakeGrammar;
import net.sf.jkniv.sqlegance.logger.DataMasking;
import net.sf.jkniv.sqlegance.logger.LogLevel;
import net.sf.jkniv.whinstone.params.StatementAdapterOld;

public class JpaStatementAdapter implements StatementAdapterOld
{
    private static final Logger LOG = LoggerFactory.getLogger(JpaStatementAdapter.class);
    private static final Logger SQLLOG = net.sf.jkniv.whinstone.jpa2.LoggerFactory.getLogger();
    private static final DataMasking MASKING = net.sf.jkniv.whinstone.jpa2.LoggerFactory.getDataMasking();
    private final Query query;
    private int index;
    private int indexIN;
    
    public JpaStatementAdapter(Query query)
    {
        this.query = query;
        this.index = 0;
        this.indexIN = 0;
        this.reset();
    }
    
    @Override
    public StatementAdapterOld setParameter(String name, Object value)
    {
        log(name, value);
        query.setParameter(name, value);
        return this;
    }
    
    @Override
    public StatementAdapterOld setParameter(int position, Object value)
    {
        this.index = position;
        log(position, value);
        query.setParameter(index+indexIN, value);
        return this;
    }

    @Override
    public StatementAdapterOld setParameters(Object... values)
    {
        for(;index<values.length;index++)
        {
            Object v = values[index];
            log(index, v);
            query.setParameter(index, v);
        }
        return this;
    }

    @Override
    public int reset()
    {
        int before = index;
        index = 0;
        indexIN = 0;
        return before;
    }
    
    private void log(String name, Object value)
    {
        if (SQLLOG.isDebugEnabled())
            SQLLOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", index,
                    name, MASKING.mask(name, value), (value == null ? "NULL" : value.getClass()));
    }

    private void log(int position, Object value)
    {
        String name = String.valueOf(position);
        if (SQLLOG.isDebugEnabled())
            SQLLOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", index,
                    name, MASKING.mask(name, value), (value == null ? "NULL" : value.getClass()));
    }

}
