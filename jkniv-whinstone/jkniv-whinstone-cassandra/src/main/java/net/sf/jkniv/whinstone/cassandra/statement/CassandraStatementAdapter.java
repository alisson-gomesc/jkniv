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
package net.sf.jkniv.whinstone.cassandra.statement;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;

import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.experimental.TimerKeeper;
import net.sf.jkniv.sqlegance.OneToMany;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.logger.DataMasking;
import net.sf.jkniv.whinstone.JdbcColumn;
import net.sf.jkniv.whinstone.Param;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.ResultSetParser;
import net.sf.jkniv.whinstone.cassandra.CassandraColumn;
import net.sf.jkniv.whinstone.cassandra.LoggerFactory;
import net.sf.jkniv.whinstone.classification.Groupable;
import net.sf.jkniv.whinstone.classification.GroupingBy;
import net.sf.jkniv.whinstone.classification.NoGroupingBy;
import net.sf.jkniv.whinstone.classification.Transformable;
import net.sf.jkniv.whinstone.statement.AutoKey;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

/**
 * https://docs.datastax.com/en/developer/java-driver/3.1/manual/statements/prepared/
 * 
 * //FIXME unsupported method bound.setInet(...)
 * //FIXME unsupported method bound.setConsistencyLevel(ConsistencyLevel)
 * //FIXME unsupported method bound.setIdempotent(boolean)
 * //FIXME unsupported method bound.setBytes(...ByteBuffer)
 * //FIXME unsupported method bound.setInet(...InetAddress)
 * //FIXME unsupported method bound.setPartitionKeyToken(Token)
 * //FIXME unsupported method bound.setRoutingKey(ByteBuffer) 
 * //FIXME unsupported method bound.setToken(...Token)
 * //FIXME unsupported method bound.setUUID(...UUID)
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class CassandraStatementAdapter<T, R> implements StatementAdapter<T, Row>
{
    private static final Logger SQLLOG = net.sf.jkniv.whinstone.cassandra.LoggerFactory.getLogger();
    private static final DataMasking MASKING = LoggerFactory.getDataMasking();
    
    private final HandlerException   handlerException;
    private final Statement          stmt;
    private int                      index, indexIN;
    private Class<T>                 returnType;
    private ResultRow<T, Row>        resultRow;
    private boolean                  scalar;
    private Session                  session;
    private Queryable                queryable;
    
    @SuppressWarnings("unchecked")
    public CassandraStatementAdapter(Session session, Statement stmt, Queryable queryable)
    {
        this.stmt = stmt;
        this.session = session;
        this.handlerException = new HandlerException(RepositoryException.class, "Cannot set parameter [%s] value [%s]");
        this.queryable = queryable;
        this.returnType = (Class<T>) queryable.getReturnType();
        this.reset();
    }
    
    @Override
    public StatementAdapter<T, Row> with(ResultRow<T, Row> resultRow)
    {
        this.resultRow = resultRow;
        return this;
    }
    
    @Override
    public StatementAdapter<T, Row> bind(String name, Object value)
    {
        return this;
        /*
        log(name, value);
        if (name.toLowerCase().startsWith("in:"))
        {
            try
            {
                setValueIN((Object[]) value);
                return this;
            }
            catch (SQLException e)
            {
                this.handlerException.handle(e);// FIXME handler default message with custom params
            }
        }
        return bindInternal(value);
        */
    }
    
    @Override
    public StatementAdapter<T, Row> bind(Param value)
    {
        return this;
        /*
        log(value);
        try
        {
            if (value instanceof java.util.Date)
            {
                setInternalValue((java.util.Date) value);
            }
            else if (Enum.class.isInstance(value))
            {
                setInternalValue((Enum<?>) value);
            }
            else if (value instanceof java.util.Calendar)
            {
                setInternalValue((Calendar) value);
            }
            else
            {
                bindInternal(value);
            }
        }
        catch (SQLException e)
        {
            this.handlerException.handle(e);// FIXME handler default message with custom params
        }
        return this;
        */
    }
    
    @Override
    public StatementAdapter<T, Row> bind(Param... values)
    {
        return this;
    }
    /*
    @Override
    public void batch()
    {
        // TODO implements batch https://docs.datastax.com/en/drivers/java/3.0/com/datastax/driver/core/BatchStatement.html
        // TODO implements batch https://docs.datastax.com/en/drivers/python/3.2/api/cassandra/query.html
        // TODO implements batch https://docs.datastax.com/en/cql/3.3/cql/cql_using/useBatch.html
        // TODO implements batch https://docs.datastax.com/en/cql/3.3/cql/cql_using/useBatchGoodExample.html
        // TODO https://www.datastax.com/dev/blog/client-side-improvements-in-cassandra-2-0
    }
    */
    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    public List<T> rows()
    {
        ResultSet rs = null;
        ResultSetParser<T, ResultSet> rsParser = null;
        Groupable<T, ?> grouping = new NoGroupingBy<T, T>();
        List<T> list = Collections.emptyList();
        try
        {
            if (queryable.getBookmark() != null)
            {
                PagingState pagingState = PagingState.fromString(queryable.getBookmark());
                stmt.setPagingState(pagingState);
            }
            TimerKeeper.start();
            rs = session.execute(stmt);
            queryable.getDynamicSql().getStats().add(TimerKeeper.clear());
            
            JdbcColumn<Row>[] columns = getJdbcColumns(rs.getColumnDefinitions());
            setResultRow(columns);
            
            Transformable<T> transformable = resultRow.getTransformable();
            if (hasGroupingBy())
            {
                grouping = new GroupingBy(getGroupingBy(), returnType, transformable);
            }
            rsParser = new ObjectResultSetParser(resultRow, grouping);
            list = rsParser.parser(rs);//rs.getExecutionInfo().getPagingStateUnsafe();
            PagingState pagingState = rs.getExecutionInfo().getPagingState();
            //LOG.info("AvailableWithoutFetching={}, FullyFetched={}, Exhausted={}", rs.getAvailableWithoutFetching(), rs.isFullyFetched(), rs.isExhausted());
            if (pagingState != null)
                queryable.setBookmark(pagingState.toString());
        }
        catch (SQLException e)
        {
            queryable.getDynamicSql().getStats().add(e);
            
            handlerException.handle(e, e.getMessage());
        }
        finally {
            TimerKeeper.clear();            
        }
        return list;
    }
    
    @Override
    public void bindKey()
    {
        throw new UnsupportedOperationException("No implemented operation generatedKeys for RepositoryCassandra!");
    }
    
    @Override
    public StatementAdapter<T, Row> with(AutoKey generateKey)
    {
        return this;
    }
    
    public int execute()
    {
        session.execute(stmt);
        return java.sql.Statement.SUCCESS_NO_INFO; // FIXME design Statement.SUCCESS_NO_INFO
    }
    
    @Override
    public int reset()
    {
        int before = (index + indexIN);
        index = 0;
        indexIN = 0;
        return before;
    }
    
    /*******************************************************************************/
    
    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    private void setResultRow(JdbcColumn<Row>[] columns)
    {
        if (resultRow != null)
        {
            resultRow.setColumns(columns);
            return;
        }
        
        if (scalar)
            resultRow = new ScalarResultRow(columns);
        else if (Map.class.isAssignableFrom(returnType))
            resultRow = new MapResultRow(returnType, columns);
        else if (Number.class.isAssignableFrom(returnType)) // FIXME implements for date, calendar, boolean improve design
            resultRow = new NumberResultRow(returnType, columns);
        else if (String.class.isAssignableFrom(returnType))
            resultRow = new StringResultRow(columns);
        else if (!hasOneToMany())
            resultRow = new FlatObjectResultRow(returnType, columns);
        else
            resultRow = new PojoResultRow(returnType, columns, getOneToMany());
    }
    
    private void log(String name, Object value)
    {
        if (SQLLOG.isDebugEnabled())
            SQLLOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]",
                    (index + indexIN), name, MASKING.mask(name, value), (value == null ? "NULL" : value.getClass()));
    }
    
    private void log(Object value)
    {
        String name = String.valueOf(index + indexIN);
        if (SQLLOG.isDebugEnabled())
            SQLLOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]",
                    (index + indexIN), name, MASKING.mask(name, value), (value == null ? "NULL" : value.getClass()));
    }
    
    /**
     * Summarize the columns from SQL result in binary data or not.
     * @param metadata  object that contains information about the types and properties of the columns in a <code>ResultSet</code> 
     * @return Array of columns with name and index
     */
    @SuppressWarnings("unchecked")
    private JdbcColumn<Row>[] getJdbcColumns(ColumnDefinitions metadata)
    {
        JdbcColumn<Row>[] columns = new JdbcColumn[metadata.size()];
        
        for (int i = 0; i < columns.length; i++)
        {
            String columnName = metadata.getName(i);//getColumnName(metadata, columnNumber);
            //int columnType = metadata.getType(i).getName().ordinal(); //metadata.getColumnType(columnNumber);
            columns[i] = new CassandraColumn(i, columnName, metadata.getType(i).getName(), queryable.getReturnType());
        }
        return columns;
    }
    
    @Override
    public void close()
    {
        // TODO how to close cassandra statement ?
    }
    
    @Override
    public void setFetchSize(int rows)
    {
        stmt.setFetchSize(rows);
    }

    private boolean hasOneToMany()
    {
        return !queryable.getDynamicSql().asSelectable().getOneToMany().isEmpty();
    }

    private Set<OneToMany> getOneToMany()
    {
        return queryable.getDynamicSql().asSelectable().getOneToMany();
    }

    private boolean hasGroupingBy()
    {
        return !queryable.getDynamicSql().asSelectable().getGroupByAsList().isEmpty();
    }

    private List<String> getGroupingBy()
    {
        return queryable.getDynamicSql().asSelectable().getGroupByAsList();        
    }
}
