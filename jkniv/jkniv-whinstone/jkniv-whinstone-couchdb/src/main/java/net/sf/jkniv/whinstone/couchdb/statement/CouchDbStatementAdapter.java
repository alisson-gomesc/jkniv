package net.sf.jkniv.whinstone.couchdb.statement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.sqlegance.KeyGeneratorType;
import net.sf.jkniv.sqlegance.OneToMany;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.logger.DataMasking;
import net.sf.jkniv.sqlegance.params.ParamParser;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.ResultSetParser;
import net.sf.jkniv.whinstone.couchdb.HttpBuilder;
import net.sf.jkniv.whinstone.couchdb.commands.JsonMapper;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.2
 */
public class CouchDbStatementAdapter<T, R> implements StatementAdapter<T, String>
{
    private static final Logger      LOG     = LoggerFactory.getLogger(CouchDbStatementAdapter.class);
    private static final Logger      SQLLOG  = net.sf.jkniv.whinstone.couchdb.LoggerFactory.getLogger();
    private static final DataMasking MASKING = net.sf.jkniv.whinstone.couchdb.LoggerFactory.getDataMasking();
    protected static final String  REGEX_QUESTION_MARK = "[\\?]+";    //"\\?";
    protected static final Pattern PATTERN_QUESTION = Pattern.compile(REGEX_QUESTION_MARK, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private final HandlerException   handlerException;
    //private final SqlDateConverter   dtConverter;
    private int                      index, indexIN;
    private Class<T>                 returnType;
    private ResultRow<T, String>     resultRow;
    private boolean                  scalar;
    private Set<OneToMany>           oneToManies;
    private List<String>             groupingBy;
    private KeyGeneratorType         keyGeneratorType;
    private String                   body;
    //private ParamParser              paramParser;
    //private HttpBuilder              httpBuilder;
    private List<Object>             params;
    private boolean                  boundParams;
    //private static BasicType         basicType = new BasicType();
    
    public CouchDbStatementAdapter(HttpBuilder httpBuilder, String body, ParamParser paramParser)//HttpRequestBase request)
    {
        //this.httpBuilder = httpBuilder;
        this.body = body;
        //this.paramParser = paramParser;
        this.params = new ArrayList<Object>();
        this.boundParams = false;
        //this.dtConverter = new SqlDateConverter();
        //this.oneToManies = Collections.emptySet();
        //this.groupingBy = Collections.emptyList();
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
        this.params.add(value);
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
        this.params.add(value);
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
            this.params.add(v);
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
        return Collections.emptyList();
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
        
    private void bindParams()
    {
        StringBuilder json = new StringBuilder();
        if (!boundParams)
        {
            Matcher matcherQuestion = PATTERN_QUESTION.matcher(this.body);
            int i = 0;
            int start = 0, endBody = this.body.length();
            while (matcherQuestion.find())
            {
                json.append(body.substring(start, matcherQuestion.start()));
                json.append(JsonMapper.mapper(params.get(i++)));
                //System.out.printf("group[%s] [%d,%d]\n", matcherQuestion.group(), matcherQuestion.start(), matcherQuestion.end());
                params.add(i++, body.subSequence(matcherQuestion.start(), matcherQuestion.end()).toString());
                start = matcherQuestion.end();
            }
            json.append(body.substring(start, endBody));
            //System.out.printf("%s\n", json);
            this.body = json.toString();
        }
        this.boundParams = true;
    }
    
//    private String quotesJson(Object value)
//    {
//        String ret  = String.valueOf("\"" + value + "\"");
//        if (value instanceof Number)
//            ret = String.valueOf(value);// FFIXME stament bind type like Date, Double, Calendar, Float
//        
//        return ret;
//    }
//    
    /*******************************************************************************/
    
    
    /**
     * return the index and increment the next value
     * <b>Note: take care with debug invoke, this method increment the index</b>
     * @return the current index
     */
    private int currentIndex()
    {
        return (index++ + indexIN);
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

    @Override
    public void close()
    {
        // there isn't statement to close
    }

    @Override
    public void setFetchSize(int rows)
    {
        LOG.warn("Couchdb doesn't support fetch size!");
    }

}
