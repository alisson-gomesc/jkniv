package net.sf.jkniv.whinstone.couchdb.statement;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.experimental.converters.SqlDateConverter;
import net.sf.jkniv.sqlegance.JdbcColumn;
import net.sf.jkniv.sqlegance.KeyGeneratorType;
import net.sf.jkniv.sqlegance.OneToMany;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.ResultRow;
import net.sf.jkniv.sqlegance.ResultSetParser;
import net.sf.jkniv.sqlegance.logger.DataMasking;
import net.sf.jkniv.sqlegance.logger.LogLevel;
import net.sf.jkniv.sqlegance.logger.SimpleDataMasking;
import net.sf.jkniv.sqlegance.params.ParamParser;
import net.sf.jkniv.sqlegance.statement.StatementAdapter;
import net.sf.jkniv.whinstone.couchdb.HttpBuilder;
import net.sf.jkniv.whinstone.couchdb.result.PojoResultRow;
import net.sf.jkniv.whinstone.couchdb.result.ScalarResultRow;
import net.sf.jkniv.whinstone.couchdb.result.StringResultRow;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.2
 */
public class CouchDbStatementAdapter<T, R> implements StatementAdapter<T, String>
{
    private static final Logger      LOG     = LoggerFactory.getLogger(CouchDbStatementAdapter.class);
    private static final Logger      SQLLOG  = net.sf.jkniv.whinstone.LoggerFactory.getLogger();
    private static final DataMasking MASKING = net.sf.jkniv.whinstone.LoggerFactory.getDataMasking();
    private final HandlerException   handlerException;
    private final SqlDateConverter   dtConverter;
    private int                      index, indexIN;
    //private SqlLogger              sqlLogger;
    private Class<T>                 returnType;
    private ResultRow<T, String>     resultRow;
    private boolean                  scalar;
    private Set<OneToMany>           oneToManies;
    private List<String>             groupingBy;
    private KeyGeneratorType         keyGeneratorType;
    private String                   body;
    private ParamParser              paramParser;
    private HttpBuilder              httpBuilder;
    private Map<String, Object>      params;
    private boolean                  boundParams;
    
    public CouchDbStatementAdapter(HttpBuilder httpBuilder, String body, ParamParser paramParser)//HttpRequestBase request)
    {
        this.httpBuilder = httpBuilder;
        this.body = body;
        this.paramParser = paramParser;
        this.params = new LinkedHashMap<String, Object>();
        this.boundParams = false;
        this.dtConverter = new SqlDateConverter();
        this.oneToManies = Collections.emptySet();
        this.groupingBy = Collections.emptyList();
        this.handlerException = new HandlerException(RepositoryException.class, "Cannot set parameter [%s] value [%s]");
        this.reset();
        configHanlerException();
    }
    
    public String getBody()
    {
        return this.body;
    }
    
    private void configHanlerException()
    {
        // JsonParseException | JsonMappingException | IOException
        handlerException.config(JsonParseException.class, "Error to parser json non-well-formed content [%s]");
        handlerException.config(JsonMappingException.class, "Error to deserialization content [%s]");
        handlerException.config(IOException.class, "Error from I/O json content [%s]");
    }
    
    @Override
    public StatementAdapter<T, String> returnType(Class<T> returnType)
    {
        this.returnType = returnType;
        return this;
    }
    
    @Override
    public StatementAdapter<T, String> resultRow(ResultRow<T, String> resultRow)
    {
        this.resultRow = resultRow;
        return this;
    }
    
    @Override
    public StatementAdapter<T, String> scalar()
    {
        this.scalar = true;
        return this;
    }
    
    @Override
    public StatementAdapter<T, String> oneToManies(Set<OneToMany> oneToManies)
    {
        this.oneToManies = oneToManies;
        return this;
    }
    
    @Override
    public StatementAdapter<T, String> groupingBy(List<String> groupingBy)
    {
        this.groupingBy = groupingBy;
        return this;
    }
    
    @Override
    public StatementAdapter<T, String> keyGeneratorType(KeyGeneratorType keyGeneratorType)
    {
        this.keyGeneratorType = keyGeneratorType;
        return this;
    }
    
    @Override
    public KeyGeneratorType getKeyGeneratorType()
    {
        return this.keyGeneratorType;
    }
    
    @Override
    public StatementAdapter<T, String> bind(String name, Object value)
    {
        this.params.put(name, value);
        currentIndex();//increment index
        return this;
        /*
        //this.index++;
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
    public StatementAdapter<T, String> bind(Object value)
    {
        this.params.put(String.valueOf(currentIndex()), value);
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
                setInternalValue((Enum) value);
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
    public StatementAdapter<T, String> bind(Object... values)
    {
        for (Object v : values)
        {
            this.params.put(String.valueOf(currentIndex()), v);
        }
        return this;
        //this.bound = stmt.bind(values);
        //this.index = values.length - 1;
        //return this;
    }
    
    @Override
    public void batch()
    {
        // TODO implements batch https://docs.datastax.com/en/drivers/java/3.0/com/datastax/driver/core/BatchStatement.html
        // TODO implements batch https://docs.datastax.com/en/drivers/python/3.2/api/cassandra/query.html
        // TODO implements batch https://docs.datastax.com/en/cql/3.3/cql/cql_using/useBatch.html
        // TODO implements batch https://docs.datastax.com/en/cql/3.3/cql/cql_using/useBatchGoodExample.html
        
    }
    
    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    public List<T> rows()
    {
        bindParams();
        List<T> list = Collections.emptyList();
        /*
        //ResultSet rs = new StringResultRow<String>();
        ResultSetParser<T, String> rsParser = null;
        Groupable<T, ?> grouping = new NoGroupingBy<T, T>();
        FindAnswer answer = new FindAnswer();
        String json = new PostCommand(httpBuilder.newFind(), body).execute();
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            answer = mapper.readValue(json, FindAnswer.class);
            if (answer.getWarning() != null)
                LOG.warn(answer.getWarning());
            list = (List<T>) answer.getDocs();
        }
        catch (Exception e) // JsonParseException | JsonMappingException | IOException
        {
            handlerException.handle(e);
        }
        */
        //        try
        //        {
        //            CloseableHttpClient httpclient = HttpClients.createDefault();
        //            
        //            response = httpclient.execute(this.request);
        //            String json = EntityUtils.toString(response.getEntity());
        //            //LOG.debug(response.getStatusLine().toString());
        //            int statusCode = response.getStatusLine().getStatusCode();
        //            if (statusCode == DbCommand.HTTP_OK)
        //            {
        //                // FIXME
        //                ///rs = session.execute(bound);
        //                //JdbcColumn<Row>[] columns = getJdbcColumns(rs.getColumnDefinitions());
        //                //setResultRow(columns);
        //                //
        //                //Transformable<T> transformable = resultRow.getTransformable();
        ////                if (!groupingBy.isEmpty())
        ////                {
        ////                    grouping = new GroupingBy(groupingBy, returnType, transformable);
        ////                }
        //                rsParser = new ObjectResultSetParser(resultRow, grouping);
        //                list = rsParser.parser(json);
        //            }
        //            else if (statusCode == DbCommand.HTTP_NO_CONTENT || 
        //                     statusCode == DbCommand.HTTP_NOT_MODIFIED || 
        //                     statusCode == DbCommand.HTTP_RESET_CONTENT)
        //            {
        //                // 204 No Content, 304 Not Modified, 205 Reset Content
        //                LOG.info(response.getStatusLine().toString());
        //            }
        //            else
        //            {
        //                LOG.error("{} -> {} ",response.getStatusLine().toString(), json);
        //                throw new RepositoryException(response.getStatusLine().toString());
        //            }
        //        }
        //        catch (SQLException e)
        //        {
        //            handlerException.handle(e, e.getMessage());
        //        }
        //        catch (ClientProtocolException e)
        //        {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }
        //        catch (IOException e)
        //        {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }
        //        finally 
        //        {
        //            if(response != null)
        //            {
        //                try
        //                {
        //                    response.close();
        //                }
        //                catch (IOException e)
        //                {
        //                    // TODO Auto-generated catch block
        //                    e.printStackTrace();
        //                }
        //            }
        //        }        
        return list;
    }
    
    public ResultSetParser<T, String> generatedKeys()
    {
        // FIXME UnsupportedOperationException
        throw new UnsupportedOperationException("CouchDb repository  doesn't implement this method yet!");
    }
    
    public int execute()
    {
        // FIXME UnsupportedOperationException
        throw new UnsupportedOperationException("CouchDb repository  doesn't implement this method yet!");
        //session.execute(bound);
        //return 0;
    }
    
    @Override
    public int reset()
    {
        int before = (index + indexIN);
        index = 1;
        indexIN = 0;
        return before;
    }
    
    private void setValueIN(Object[] paramsIN) throws SQLException
    {
        int j = 0;
        for (; j < paramsIN.length; j++)
            __bindInternal__(paramsIN[j]);
        indexIN = indexIN + j;
    }
    
    private void bindParams()
    {
        if (!boundParams)
        {
            for (String k : params.keySet())
            {
                Object v = params.get(k);
                this.body = this.body.replaceFirst("\\?", String.valueOf("\"" + v + "\""));// FIXME stament bind type like Date, Double, Calendar, Float
            }
        }
        this.boundParams = true;
    }
    
    /*******************************************************************************/
    
    private StatementAdapter<T, String> __bindInternal__(Object value)
    {
        try
        {
            if (value == null)
                setToNull();
            else if (value instanceof String)
                setInternalValue((String) value);
            else if (value instanceof Integer)
                setInternalValue((Integer) value);
            else if (value instanceof Long)
                setInternalValue((Long) value);
            else if (value instanceof Double)
                setInternalValue((Double) value);
            else if (value instanceof Float)
                setInternalValue((Float) value);
            else if (value instanceof Boolean)
                setInternalValue((Boolean) value);
            else if (value instanceof BigDecimal)
                setInternalValue((BigDecimal) value);
            else if (value instanceof BigInteger)
                setInternalValue((BigInteger) value);
            else if (value instanceof Short)
                setInternalValue((Short) value);
            else if (value instanceof Date)
                setInternalValue((Date) value);
            else if (value instanceof java.util.Calendar)
                setInternalValue((Calendar) value);
            else if (Enum.class.isInstance(value))
                setInternalValue((Enum<?>) value);
            else if (value instanceof Byte)
                setInternalValue((Byte) value);
            else
            {
                //setValue(value);
            }
        }
        catch (SQLException e)
        {
            this.handlerException.handle(e);
        }
        return this;
    }
    
    private void setInternalValue(Calendar value)
    {
        //bound.setTimestamp(currentIndex(), value.getTime());
    }
    
    private void setInternalValue(Date value)
    {
        //bound.setTimestamp(currentIndex(), value);
    }
    
    private void setInternalValue(Integer value)
    {
        //bound.setInt(currentIndex(), value);
    }
    
    private void setInternalValue(Long value)
    {
        //bound.setLong(currentIndex(), value);
    }
    
    private void setInternalValue(Float value)
    {
        //bound.setFloat(currentIndex(), value);
    }
    
    private void setInternalValue(Double value)
    {
        //bound.setDouble(currentIndex(), value);
    }
    
    private void setInternalValue(Short value)
    {
        //bound.setShort(currentIndex(), value);
    }
    
    private void setInternalValue(Boolean value)
    {
        //bound.setBool(currentIndex(), value);
    }
    
    private void setInternalValue(Byte value)
    {
        //bound.setByte(currentIndex(), value);
    }
    
    private void setInternalValue(BigDecimal value)
    {
        //bound.setDecimal(currentIndex(), value);
    }
    
    private void setInternalValue(BigInteger value)
    {
        //bound.setVarint(currentIndex(), value);
    }
    
    private void setInternalValue(String value)
    {
        //bound.setString(currentIndex(), value);
    }
    
    private void setToNull()
    {
        //bound.setToNull(currentIndex());
    }
    
    private void setInternalValue(Enum<?> value) throws SQLException
    {
        // FIXME design converter to allow save ordinal value or other value from enum
        //bound.setString(currentIndex(), value.name());
    }
    
    /**
     * return the index and increment the next value
     * <b>Note: take care with debug invoke, this method increment the index</b>
     * @return the current index
     */
    private int currentIndex()
    {
        return (index++ + indexIN);
    }
    
    /*******************************************************************************/
    
    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    private void setResultRow(JdbcColumn<String>[] columns)
    {
        if (resultRow != null)
        {
            resultRow.setColumns(columns);
            return;
        }
        
        if (scalar)
        {
            resultRow = new ScalarResultRow(columns);
        }
        //        else if (Map.class.isAssignableFrom(returnType))
        //        {
        //            resultRow = new MapResultRow(returnType, columns, sqlLogger);
        //        }
        //        else if (Number.class.isAssignableFrom(returnType)) // FIXME implements for date, calendar, boolean improve design
        //        {
        //            resultRow = new NumberResultRow(returnType, columns, sqlLogger);
        //        }
        else if (String.class.isAssignableFrom(returnType))
        {
            resultRow = new StringResultRow(columns);
        }
        //        else if (oneToManies.isEmpty())
        //        {
        //            resultRow = new FlatObjectResultRow(returnType, columns, sqlLogger);
        //        }
        else
        {
            resultRow = new PojoResultRow(returnType, columns, oneToManies);
        }
    }
    
    private void log(String name, Object value)
    {
        if (SQLLOG.isDebugEnabled())
            SQLLOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", index,
                    name, MASKING.mask(name, value), (value == null ? "NULL" : value.getClass()));
    }
    
    private void log(Object value)
    {
        String name = String.valueOf(index + indexIN);
        if (SQLLOG.isDebugEnabled())
            SQLLOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", index, name,
                    MASKING.mask(name, value), (value == null ? "NULL" : value.getClass()));
    }
    
    //    /**
    //     * Summarize the columns from SQL result in binary data or not.
    //     * @param metadata  object that contains information about the types and properties of the columns in a <code>ResultSet</code> 
    //     * @return Array of columns with name and index
    //     */
    //    @SuppressWarnings("unchecked")
    //    private JdbcColumn<Row>[] getJdbcColumns(ColumnDefinitions metadata)
    //    {
    //        JdbcColumn<Row>[] columns = new JdbcColumn[metadata.size()];
    //        
    //        for (int i = 0; i < columns.length; i++)
    //        {
    //            String columnName = metadata.getName(i);//getColumnName(metadata, columnNumber);
    //            int columnType = metadata.getType(i).getName().ordinal(); //metadata.getColumnType(columnNumber);
    //            columns[i] = new CouchDbColumn(i, columnName, columnType);
    //        }
    //        return columns;
    //    }
    
}
