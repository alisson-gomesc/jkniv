package net.sf.jkniv.whinstone.couchbase.statement;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.ParameterizedN1qlQuery;
import com.couchbase.client.java.query.Statement;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.experimental.TimerKeeper;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.logger.DataMasking;
import net.sf.jkniv.whinstone.Param;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.ResultSetParser;
import net.sf.jkniv.whinstone.classification.Groupable;
import net.sf.jkniv.whinstone.classification.GroupingBy;
import net.sf.jkniv.whinstone.classification.NoGroupingBy;
import net.sf.jkniv.whinstone.classification.Transformable;
import net.sf.jkniv.whinstone.statement.AutoKey;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class CouchbaseStatementAdapter<T, R> implements StatementAdapter<T, N1qlQueryRow>
{
    private static final Logger        LOG     = LoggerFactory.getLogger(CouchbaseStatementAdapter.class);
    private static final Logger        SQLLOG  = net.sf.jkniv.whinstone.couchbase.LoggerFactory.getLogger();
    private static final DataMasking   MASKING = net.sf.jkniv.whinstone.couchbase.LoggerFactory.getDataMasking();
    private final HandlerException     handlerException;
    private int                        index;
    private List<Param>                params;
    private Bucket                     bucket;
    private Queryable                  queryable;
    private ResultRow<T, N1qlQueryRow> resultRow;
    
    public CouchbaseStatementAdapter(Bucket bucket, Queryable queryable)
    {
        this.bucket = bucket;
        this.queryable = queryable;
        this.params = new ArrayList<Param>();
        this.handlerException = new HandlerException(RepositoryException.class, "Cannot set parameter [%s] value [%s]");
        this.reset();
        configHanlerException();
    }
    
    private void configHanlerException()
    {
        // JsonParseException | JsonMappingException | IOException
        handlerException.config(JsonParseException.class, "Error to parser json non-well-formed content [%s]");
        handlerException.config(JsonMappingException.class, "Error to deserialization content [%s]");
        handlerException.config(IOException.class, "Error from I/O json content [%s]");
    }

    @Override
    public StatementAdapter<T, N1qlQueryRow> with(ResultRow<T, N1qlQueryRow> resultRow)
    {
        //this.resultRow = resultRow; TODO implements resultrow for couchbase 
        return this;
    }
    
    @Override
    public StatementAdapter<T, N1qlQueryRow> bind(String name, Object value)
    {
        int index = currentIndex();
        this.params.add(new Param(value, name, index));
        return this;
    }
    
    @Override
    public StatementAdapter<T, N1qlQueryRow> bind(Param param)
    {
        this.currentIndex();
        this.params.add(param);
        return this;
    }
    
    @Override
    public StatementAdapter<T, N1qlQueryRow> bind(Param... values)
    {
        for (Param v : values)
        {
            this.currentIndex();
            this.params.add(v);
        }
        return this;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List<T> rows()
    {
        N1qlQueryResult rs = null;
        ResultSetParser<T, N1qlQueryResult> rsParser = null;
        Groupable<T, ?> grouping = new NoGroupingBy<T, T>();
        List<T> list = Collections.emptyList();
        //JsonObject jsonParams = getJsonParams();
        JsonArray jsonArray = getJsonArrayParams();
        try
        {
            TimerKeeper.start();
            ParameterizedN1qlQuery query = N1qlQuery.parameterized(queryable.query(), jsonArray);
            rs = bucket.query(query); 
            // # If this line is removed, the latest 'random' field might not be present
            // query.consistency = CONSISTENCY_REQUEST
            // https://docs.couchbase.com/java-sdk/current/n1ql-query.html
            
            // rs-> N1qlQueryResult{status='fatal', finalSuccess=false, parseSuccess=false, allRows=[], signature={}, info=N1qlMetrics{resultCount=0, errorCount=1, warningCount=0, mutationCount=0, sortCount=0, resultSize=0, elapsedTime='1.0012ms', executionTime='1.0012ms'}, profileInfo={}, errors=[{"msg":"Primary index def_primary not online.","code":4000}], requestId='481da6e5-7e49-486b-b84f-981cc2e83fcb', clientContextId='dc45df28-92be-4a7c-969b-5eeede4b26cb'}
            //                     {"status": "Executing statement"}
            //                     {"No data to display": "Hit execute to rerun the query."}
            queryable.getDynamicSql().getStats().add(TimerKeeper.clear());
            // rs.errors() -> {"msg":"Primary index def_primary not online.","code":4000}
            if (rs.errors().isEmpty())
            {
                resultRow = new JsonResultRow(queryable.getReturnType());
                Transformable<T> transformable = resultRow.getTransformable();
                if (hasGroupingBy())
                {
                    grouping = new GroupingBy(getGroupingBy(), queryable.getReturnType(), transformable);
                }
                rsParser = new ObjectResultSetParser(resultRow, grouping);
                list = rsParser.parser(rs);
            }
            else
            {
                /*
  {
    "code": 5000,
    "msg": " Indexer In Warmup State. Please retry the request later. from [127.0.0.1:9101] - cause:  Indexer In Warmup State. Please retry the request later. from [127.0.0.1:9101]",
    "query_from_user": "SELECT * FROM `travel-sample` WHERE type = \"airline\";"
  }
                 */
                LOG.error("Error executing the query [{}] status was: {} -> {}", queryable.getName(), rs.status(), rs.errors().get(0));
            }
        }
        catch (SQLException e)
        {
            queryable.getDynamicSql().getStats().add(e);
            handlerException.handle(e, e.getMessage());
        }
        finally
        {
            TimerKeeper.clear();
        }
        return list;
    }
    
    @Override
    public void bindKey()
    {
        // FIXME UnsupportedOperationException
        throw new UnsupportedOperationException("Couchbase repository doesn't implement this method yet!");
    }
    
    @Override
    public StatementAdapter<T, N1qlQueryRow> with(AutoKey generateKey)
    {
        return this;
    }
    
    @Override
    public int execute()
    {
        // FIXME UnsupportedOperationException
        throw new UnsupportedOperationException("Couchbase repository  doesn't implement this method yet!");
    }
    
    @Override
    public int reset()
    {
        int before = index;
        index = 1;
        return before;
    }
    /*
    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    private void setResultRow(JdbcColumn<ResultSet>[] columns)
    {
        Class<?> returnType = queryable.getReturnType();
        if (resultRow != null)
            return;
        
        if (queryable.isScalar())
        {
            resultRow = new ScalarResultRow(columns);
        }
        else if (Map.class.isAssignableFrom(returnType))
        {
            resultRow = new MapResultRow(returnType, columns);
        }
        else if (Number.class.isAssignableFrom(returnType)) 
            // FIXME implements for date, calendar, boolean improve design
        {
            resultRow = new NumberResultRow(returnType, columns);
        }
        else if (String.class.isAssignableFrom(returnType))
        {
            resultRow = new StringResultRow(columns);
        }
        else if (Boolean.class.isAssignableFrom(returnType))
        {
            resultRow = new BooleanResultRow(columns);
        }
        else if (!hasOneToMany())
        {
            resultRow = new FlatObjectResultRow(returnType, columns);
        }
        else
        {
            resultRow = new PojoResultRow(returnType, columns, getOneToMany());
        }
    }
    */
    
    /**
     * return the index and increment the next value
     * <b>Note: take care with debug invoke, this method increment the index</b>
     * @return the current index
     */
    private int currentIndex()
    {
        return index++;
    }
    
    private JsonObject getJsonParams()
    {
        JsonObject jsonParams = JsonObject.create();
        if (SQLLOG.isDebugEnabled())
        {
            for (Param param : this.params)
            {
                jsonParams.put(""+param.getIndex()+1, param.getValue());
                SQLLOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]",
                        param.getIndex(), param.getName(), MASKING.mask(param.getName(), param.getValue()),
                        (param.getValue() == null ? "NULL" : param.getValue().getClass()));
            }
        }
        else
        {
            for (Param param : this.params)
                jsonParams.put(param.getName(), param.getValue());
        }
        return jsonParams;
    }

    private JsonArray getJsonArrayParams()
    {
        JsonArray jsonParams = JsonArray.create();
        if (SQLLOG.isDebugEnabled())
        {
            for (Param param : this.params)
            {
                jsonParams.add(param.getValue());
                SQLLOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]",
                        param.getIndex(), param.getName(), MASKING.mask(param.getName(), param.getValue()),
                        (param.getValue() == null ? "NULL" : param.getValue().getClass()));
            }
        }
        else
        {
            for (Param param : this.params)
                jsonParams.add(param.getValue());
        }
        return jsonParams;
    }
    
    @Override
    public void close()
    {
        // there isn't statement to close
    }
    
    @Override
    public void setFetchSize(int rows)
    {
        LOG.warn("Couchbase doesn't support fetch size!");
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
