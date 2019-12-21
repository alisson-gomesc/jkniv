package net.sf.jkniv.whinstone.couchdb.statement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.logger.DataMasking;
import net.sf.jkniv.sqlegance.params.ParamParser;
import net.sf.jkniv.whinstone.Param;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.couchdb.HttpBuilder;
import net.sf.jkniv.whinstone.couchdb.commands.JsonMapper;
import net.sf.jkniv.whinstone.statement.AutoKey;
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
    private int                      index;
    private String                   body;
    private boolean                  boundParams;
    private List<Param>              params;
    
    public CouchDbStatementAdapter(HttpBuilder httpBuilder, String body, ParamParser paramParser)//HttpRequestBase request)
    {
        this.body = body;
        this.params = new ArrayList<Param>();
        this.boundParams = false;
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
    public StatementAdapter<T, String> with(ResultRow<T, String> resultRow)
    {
        //this.resultRow = resultRow; TODO implements resultrow for couchbase
        return this;
    }
    
    @Override
    public StatementAdapter<T, String> bind(String name, Object value)
    {
        int index = currentIndex();//increment index
        this.params.add(new Param(value, name, index));
        return this;
    }
    
    @Override
    public StatementAdapter<T, String> bind(Param value)
    {
        this.params.add(value);
        return this;
    }
    
    @Override
    public StatementAdapter<T, String> bind(Param... values)
    {
        for (Param v : values)
        {
            this.params.add(v);
        }
        return this;
    }
    
    @Override
    public List<T> rows()
    {
        bindParams();
        return Collections.emptyList();
    }
    
    @Override
    public void bindKey()
    {
        // FIXME UnsupportedOperationException
        throw new UnsupportedOperationException("CouchDb repository doesn't implement this method yet!");
    }
    
    @Override
    public StatementAdapter<T, String> with(AutoKey generateKey)
    {
        return this;
    }
    
    @Override
    public int execute()
    {
        // FIXME UnsupportedOperationException
        throw new UnsupportedOperationException("CouchDb repository  doesn't implement this method yet!");
    }
    
    @Override
    public int reset()
    {
        int before = index;
        index = 1;
        return before;
    }
        
    private void bindParams()
    {
        StringBuilder json = new StringBuilder();
        logParams();
        if (!boundParams)
        {
            Matcher matcherQuestion = PATTERN_QUESTION.matcher(this.body);
            int i = 0;
            int start = 0, endBody = this.body.length();
            while (matcherQuestion.find())
            {
                json.append(body.substring(start, matcherQuestion.start()));
                json.append(JsonMapper.mapper(params.get(i++).getValue()));
                //System.out.printf("group[%s] [%d,%d]\n", matcherQuestion.group(), matcherQuestion.start(), matcherQuestion.end());
                params.add(i++, new Param(body.subSequence(matcherQuestion.start(), matcherQuestion.end()).toString(), i-1));
                start = matcherQuestion.end();
            }
            json.append(body.substring(start, endBody));
            //System.out.printf("%s\n", json);
            this.body = json.toString();
        }
        this.boundParams = true;
    }
    
    /**
     * return the index and increment the next value
     * <b>Note: take care with debug invoke, this method increment the index</b>
     * @return the current index
     */
    private int currentIndex()
    {
        return index++;
    }
    
    private void logParams()
    {
        
        if (SQLLOG.isDebugEnabled())
        {
            for(Param param : params)
            SQLLOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", param.getIndex(),
                    param.getName(), MASKING.mask(param.getName(), param.getValue()), (param.getValue() == null ? "NULL" : param.getValue().getClass()));
        }
    }
    
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
