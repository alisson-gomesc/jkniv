package net.sf.jkniv.whinstone.jpa2.params;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.logger.LogLevel;
import net.sf.jkniv.sqlegance.logger.SqlLogger;
import net.sf.jkniv.sqlegance.params.StatementAdapterOld;

public class JpaStatementAdapter implements StatementAdapterOld
{
    private static final Logger LOG = LoggerFactory.getLogger(JpaStatementAdapter.class);
    private final Query query;
    private SqlLogger sqlLogger;
    private int index;
    private int indexIN;
    
    public JpaStatementAdapter(Query query, SqlLogger sqlLogger)
    {
        this.query = query;
        this.sqlLogger = sqlLogger;
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
        if (sqlLogger.isEnabled(LogLevel.STMT))
            sqlLogger.log(LogLevel.STMT,
                    "Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", index,
                    name, sqlLogger.mask(name, value), (value == null ? "NULL" : value.getClass()));
    }

    private void log(int position, Object value)
    {
        String name = String.valueOf(position);
        if (sqlLogger.isEnabled(LogLevel.STMT))
            sqlLogger.log(LogLevel.STMT,
                    "Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", index,
                    name, sqlLogger.mask(name, value), (value == null ? "NULL" : value.getClass()));
    }

}
