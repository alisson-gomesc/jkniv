/* 
 * JKNIV, SQLegance keeping queries maintainable.
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
package net.sf.jkniv.sqlegance.builder.xml;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.Deletable;
import net.sf.jkniv.sqlegance.Insertable;
import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.Statistical;
import net.sf.jkniv.sqlegance.Storable;
import net.sf.jkniv.sqlegance.Updateable;
import net.sf.jkniv.sqlegance.builder.xml.dynamic.ChooseTag;
import net.sf.jkniv.sqlegance.builder.xml.dynamic.ITextTag;
import net.sf.jkniv.sqlegance.builder.xml.dynamic.StaticText;
import net.sf.jkniv.sqlegance.builder.xml.dynamic.WhereTag;
import net.sf.jkniv.sqlegance.dialect.AnsiDialect;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;
import net.sf.jkniv.sqlegance.params.ParamMarkType;
import net.sf.jkniv.sqlegance.params.ParamParser;
import net.sf.jkniv.sqlegance.params.ParamParserFactory;
import net.sf.jkniv.sqlegance.params.ParamParserNoMark;
import net.sf.jkniv.sqlegance.statement.ResultSetConcurrency;
import net.sf.jkniv.sqlegance.statement.ResultSetHoldability;
import net.sf.jkniv.sqlegance.statement.ResultSetType;
import net.sf.jkniv.sqlegance.transaction.Isolation;
import net.sf.jkniv.sqlegance.validation.ValidateType;

/**
 * Generic tag to support the common functions from other tags.
 * 
 * @author Alisson Gomes
 * @since 0.0.2
 */
public abstract class AbstractSqlTag implements SqlTag
{
    protected static final Assertable NOT_NULL                        = AssertsFactory.getNotNull();
    private static final Logger       LOG                             = LoggerFactory.getLogger(AbstractSqlTag.class);
    // find the pattern #{id}
    private static final String       REGEX_HASH_MARK                 = "#\\{[\\w\\.?]+\\}";
    // find the pattern :id
    private static final String       REGEX_COLON_MARK                = ":[\\w\\.?]+";
    // find the pattern ?
    private static final String       REGEX_QUESTION_MARK             = "[\\?]+";
    // find the pattern :id
    private static final String       REGEX_DOLLAR_MARK               = "\\$[\\w\\.?]+";
    private static final Pattern      PATTERN_HASH                    = Pattern.compile(REGEX_HASH_MARK,
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private static final Pattern      PATTERN_COLON                   = Pattern.compile(REGEX_COLON_MARK,
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private static final Pattern      PATTERN_QUESTION                = Pattern.compile(REGEX_QUESTION_MARK,
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private static final Pattern      PATTERN_DOLLAR                  = Pattern.compile(REGEX_DOLLAR_MARK,
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    
    public static final String        ATTRIBUTE_NAME                  = "id";
    public static final String        ATTRIBUTE_TYPE                  = "type";
    public static final String        ATTRIBUTE_ISOLATION             = "isolation";
    public static final String        ATTRIBUTE_TIMEOUT               = "timeout";
    public static final String        ATTRIBUTE_HINT                  = "hint";
    public static final String        ATTRIBUTE_BATCH                 = "batch";
    public static final String        ATTRIBUTE_CACHE                 = "cache";
    public static final String        ATTRIBUTE_RETURN_TYPE           = "returnType";
    public static final String        ATTRIBUTE_GROUP_BY              = "groupBy";
    
    public static final String        ATTRIBUTE_RESULTSET_TYPE        = "resultSetType";
    public static final String        ATTRIBUTE_RESULTSET_CONCURRENCY = "resultSetConcurrency";
    public static final String        ATTRIBUTE_RESULTSET_HOLDABILITY = "resultSetHoldability";
    public static final String        ATTRIBUTE_VALIDATION            = "validation";
    
    protected String                  id;
    protected LanguageType            languageType;
    private Isolation                 isolation;
    private int                       timeout;
    private ResultSetType             resultSetType;
    private ResultSetConcurrency      resultSetConcurrency;
    private ResultSetHoldability      resultSetHoldability;
    private ValidateType              validateType;
    private List<ITextTag>            textTag;
    private String                    returnType;
    private Class<?>                  returnTypeClass;
    private String                    xpath;
    private String                    resourceName;
    private Date                      timestamp;
    private ParamParser               paramParser;
    private String                    paket;
    private SqlDialect                sqlDialect;
    private Statistical               stats;
    //private boolean                 returnTypeManaged;
    //private boolean                 batch;
    //private String                  hint;
    //private long                    timeToLive;    
    //private ResultRow<?, ?>         parserRow;
    
    /**
     * Build a new SQL tag from XML file.
     * 
     * @param id
     *            Name/Identify from tag.
     * @param languageType
     *            type of language from tag.
     */
    public AbstractSqlTag(String id, LanguageType languageType)
    {
        this(id, languageType, Isolation.DEFAULT, -1, ResultSetType.DEFAULT, ResultSetConcurrency.DEFAULT,
                ResultSetHoldability.DEFAULT, null, ValidateType.NONE, NoSqlStats.getInstance());
    }
    
    public AbstractSqlTag(String id, LanguageType languageType, SqlDialect sqlDialect)
    {
        this(id, languageType, Isolation.DEFAULT, -1, ResultSetType.DEFAULT, ResultSetConcurrency.DEFAULT,
                ResultSetHoldability.DEFAULT, "", ValidateType.NONE, NoSqlStats.getInstance());
        this.sqlDialect = sqlDialect;
    }
    
    /**
     * Build a new SQL tag from XML file.
     * 
     * @param id
     *            Name/Identify from tag.
     * @param languageType
     *            type of language from tag.
     * @param isolation
     *            Retrieves the current transaction isolation level for the
     *            query.
     * @param timeout
     *            Retrieves the number of seconds the repository will wait for a
     *            Query object to execute.
     * @param validateType validation to apply before execute SQL.
     * @param stats SQL statistical
     */
    public AbstractSqlTag(String id, LanguageType languageType, Isolation isolation, int timeout,
            ValidateType validateType, Statistical stats)
    {
        this(id, languageType, isolation, timeout, ResultSetType.DEFAULT, ResultSetConcurrency.DEFAULT,
                ResultSetHoldability.DEFAULT, "", validateType, stats);
    }
    
    /**
     * Build a new SQL tag from XML file.
     * 
     * @param id
     *            Name/Identify from tag.
     * @param languageType
     *            type of language from tag.
     * @param isolation
     *            Retrieves the current transaction isolation level for the
     *            query.
     * @param timeout
     *            Retrieves the number of seconds the repository will wait for a
     *            Query object to execute.
     * @param resultSetType TODO javadoc
     * @param resultSetConcurrency TODO javadoc
     * @param resultSetHoldability TODO javadoc
     * @param returnType v
     * @param validateType TODO javadoc
     * @param stats statistical for procedures
     */
    public AbstractSqlTag(String id, LanguageType languageType, Isolation isolation, int timeout,
            ResultSetType resultSetType, ResultSetConcurrency resultSetConcurrency,
            ResultSetHoldability resultSetHoldability, String returnType, ValidateType validateType, Statistical stats)
    {
        this.textTag = new ArrayList<ITextTag>();
        this.id = id;
        this.languageType = languageType;
        this.isolation = isolation;
        this.timeout = timeout;
        //this.batch = batch;
        //this.hint = hint;
        this.resultSetType = resultSetType;
        this.resultSetConcurrency = resultSetConcurrency;
        this.resultSetHoldability = resultSetHoldability;
        this.returnType = returnType;
        this.validateType = validateType;
        this.timestamp = new Date();
        this.paramParser = ParamParserNoMark.emptyParser();
        this.stats = stats;
        if (hasReturnType())
        {
            this.returnTypeClass = ObjectProxyFactory.of(returnType).mute(ClassNotFoundException.class)
                    .getTargetClass();
        }
        //this.timeToLive = -1;//testing 30*1000; 
        
    }
    
    /**
     * Retrieve static sql from a node, the dynamic parts is skipped.
     * 
     * @return Sql sentence
     */
    @Override
    public String getSql()
    {
        StringBuilder sb = new StringBuilder();
        for (ITextTag text : textTag)
        {
            if (!text.isDynamic())
            {
                if (sb.length() > 0)
                    sb.append(" " + text.getText());
                else
                    sb.append(text.getText());
            }
        }
        return sb.toString();
    }
    
    /**
     * Retrieve dynamic sql from a node, the dynamic parts is evaluated at time
     * to create the sql.
     * 
     * @param params
     *            parameters to evaluate dynamic sql, can be a object like
     *            Author, Book, etc or a java.util.Map where yours keys is like
     *            the properties.
     * @return Dynamic sql sentence
     */
    @Override
    public String getSql(Object params)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < textTag.size(); i++)
        {
            ITextTag tag = textTag.get(i);
            boolean result = tag.eval(params);
            //log.trace("eval [" + result + "] " + tag.getText());
            if (result)
            {
                String text = "";
                if (tag.isDynamicGroup())
                    text = tag.getText(params);
                else
                    text = tag.getText();
                
                if (sb.length() > 0)
                    sb.append(" " + text);
                else
                    sb.append(text);
            }
        }
        String q = sb.toString();
        LOG.trace("SQL [{}] [{}]:\n{}", id, languageType, q);
        return q;
    }
    //    
    //    protected void reload()
    //    {
    //        
    //    }
    
    /**
     * language from SQL sentence.
     * 
     * @return the type of language used to SQL sentence.
     */
    @Override
    public LanguageType getLanguageType()
    {
        return languageType;
    }
    
    /**
     * Retrieve the identifier name from tag.
     * 
     * @return name from tag
     */
    @Override
    public String getName()
    {
        return id;
    }
    
    /**
     * add a new text tag.
     */
    @Override
    public void addTag(ITextTag tag)
    {
        this.textTag.add(tag);
        if (hasParamParser())
            return;
        
        reBindParamParser();
    }
    
    private void reBindParamParser()
    {
        for (ITextTag firstTag : this.textTag)
        {
            if (!(firstTag instanceof WhereTag) && !firstTag.isDynamicGroup())
            {
                if (bindParamParser(firstTag.getText()))
                    return;
            }
            
            for (ITextTag tag : firstTag.getTags())
            {
                if ((tag instanceof WhereTag || tag instanceof ChooseTag) 
                        && tag.isDynamicGroup())//FIXME BUG implements params from set, choose tags
                {
                    for (ITextTag innerTag : tag.getTags())
                    {
                        if (bindParamParser(innerTag.getText()))
                            return;
                        
                        for (ITextTag t2 : innerTag.getTags())
                            if (bindParamParser(t2.getText()))
                                return;
                    }
                }
                else if (!tag.isDynamicGroup())
                    bindParamParser(tag.getText());
            }
        }
    }
    
    /**
     * add a set of static text from tag elements.
     */
    @Override
    public void addTag(String text)
    {
        this.textTag.add(new StaticText(text));
        if (!hasParamParser())
            this.bindParamParser(text);
    }
    
    /**
     * add a set of text tags (static or dynamic).
     */
    @Override
    public void addTag(List<ITextTag> tags)
    {
        for (ITextTag t : tags)
            addTag(t);
    }
    
    @Override
    public Isolation getIsolation()
    {
        return this.isolation;
    }
    
    public void setIsolation(Isolation isolation)
    {
        this.isolation = isolation;
    }
    
    @Override
    public int getTimeout()
    {
        return this.timeout;
    }
    
    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }
    
    //    @Override
    //    public boolean isBatch()
    //    {
    //        return this.batch;
    //    }
    //    
    //    public void setBatch(boolean batch)
    //    {
    //        this.batch = batch;
    //    }
    //    
    /*
    public String getCache()
    {
        return cache;
    }
    
    public void setCache(String cache)
    {
        this.cache = cache;
    }
    */
    
    @Override
    public boolean isSelectable()
    {
        return false;
    }
    
    @Override
    public boolean isInsertable()
    {
        return false;
    }
    
    @Override
    public boolean isUpdateable()
    {
        return false;
    }
    
    @Override
    public boolean isDeletable()
    {
        return false;
    }
    
    @Override
    public Selectable asSelectable()
    {
        throw new UnsupportedOperationException("Isn't Selectable object instance");
    }
    
    @Override
    public Insertable asInsertable()
    {
        throw new UnsupportedOperationException("Isn't Insertable object instance");
    }
    
    @Override
    public Updateable asUpdateable()
    {
        throw new UnsupportedOperationException("Isn't Updateable object instance");
    }
    
    @Override
    public Deletable asDeletable()
    {
        throw new UnsupportedOperationException("Isn't deletable object instance");
    }
    
    @Override
    public boolean isStorable()
    {
        return false;
    }
    
    @Override
    public Storable asStorable()
    {
        throw new UnsupportedOperationException("Isn't Storable object instance");
    }
    
    @Override
    public ResultSetType getResultSetType()
    {
        return resultSetType;
    }
    
    public void setResultSetType(ResultSetType resultSetType)
    {
        this.resultSetType = resultSetType;
    }
    
    @Override
    public ResultSetConcurrency getResultSetConcurrency()
    {
        return resultSetConcurrency;
    }
    
    public void setResultSetConcurrency(ResultSetConcurrency resultSetConcurrency)
    {
        this.resultSetConcurrency = resultSetConcurrency;
    }
    
    public ResultSetHoldability getResultSetHoldability()
    {
        return resultSetHoldability;
    }
    
    public void setResultSetHoldability(ResultSetHoldability resultSetHoldability)
    {
        this.resultSetHoldability = resultSetHoldability;
    }
    
    public void setResultType(String returnType)
    {
        this.returnType = returnType;
    }
    
    @Override
    public String getReturnType()
    {
        return this.returnType;
    }
    
    @Override
    public boolean hasReturnType()
    {
        return this.returnType != null && !"".equals(returnType.trim());
    }
    
    @Override
    public Class<?> getReturnTypeAsClass()
    {
        return this.returnTypeClass;
    }
    
    @Override
    public void setValidateType(ValidateType validateType)
    {
        this.validateType = validateType;
    }
    
    @Override
    public ValidateType getValidateType()
    {
        return validateType;
    }
    
    @Override
    public void setXpath(String xpath)
    {
        this.xpath = xpath;
    }
    
    @Override
    public String getXPath()
    {
        return xpath;
    }
    
    @Override
    public void setResourceName(String resourceName)
    {
        this.resourceName = resourceName;
    }
    
    @Override
    public String getResourceName()
    {
        return this.resourceName;
    }
    
    @Override
    public Date getTimestamp()
    {
        return this.timestamp;
    }
    
    @Override
    public ParamParser getParamParser()
    {
        return this.paramParser;
    }
    
    @Override
    public String[] extractNames(Object params)
    {
        String sql = getSql(params);
        return paramParser.find(sql);
    }
    
    @Override
    public String[] extractNames(String sql)
    {
        return paramParser.find(sql);
    }
    
    @Override
    public void bind(SqlDialect sqlDialect)
    {
        this.sqlDialect = sqlDialect;
        if (!sqlDialect.supportsParmMark(this.paramParser.getType()))
            this.paramParser = ParamParserNoMark.emptyParser();
        reBindParamParser();
    }
    
    @Override
    public SqlDialect getSqlDialect()
    {
        return this.sqlDialect;
    }
    
    @Override
    public String getPackage()
    {
        return this.paket;
    }
    
    @Override
    public void setPackage(String name)
    {
        this.paket = name;
    }
    
    @Override
    public Statistical getStats()
    {
        return this.stats;
    }
    
    protected void setStats(Statistical stats)
    {
        this.stats = stats;
    }
    
    private boolean bindParamParser(String text)
    {
        boolean assignedParamParser = false;
        SqlDialect sqlDialectToCheck = sqlDialect != null ? sqlDialect : new AnsiDialect();
        if (sqlDialectToCheck.supportsParmMark(ParamMarkType.COLON) && PATTERN_COLON.matcher(text).find())
        {
            this.paramParser = ParamParserFactory.getInstance(ParamMarkType.COLON);
            assignedParamParser = true;
        }
        else if (sqlDialectToCheck.supportsParmMark(ParamMarkType.HASH) && PATTERN_HASH.matcher(text).find())
        {
            this.paramParser = ParamParserFactory.getInstance(ParamMarkType.HASH);
            assignedParamParser = true;
        }
        else if (sqlDialectToCheck.supportsParmMark(ParamMarkType.QUESTION) && PATTERN_QUESTION.matcher(text).find())
        {
            this.paramParser = ParamParserFactory.getInstance(ParamMarkType.QUESTION);
            assignedParamParser = true;
        }
        else if (sqlDialectToCheck.supportsParmMark(ParamMarkType.DOLLAR) && PATTERN_DOLLAR.matcher(text).find())
        {
            this.paramParser = ParamParserFactory.getInstance(ParamMarkType.DOLLAR);
            assignedParamParser = true;
        }
        return assignedParamParser;
    }
    
    private boolean hasParamParser()
    {
        return !(this.paramParser instanceof ParamParserNoMark);
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((paket == null) ? 0 : paket.hashCode());
        result = prime * result + ((resourceName == null) ? 0 : resourceName.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj)// TODO test case for Tag equals method
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractSqlTag other = (AbstractSqlTag) obj;
        if (id == null)
        {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        if (paket == null)
        {
            if (other.paket != null)
                return false;
        }
        else if (!paket.equals(other.paket))
            return false;
        if (resourceName == null)
        {
            if (other.resourceName != null)
                return false;
        }
        else if (!resourceName.equals(other.resourceName))
            return false;
        return true;
    }
    
    //    /**
    //     * 
    //     * @param seconds Time to live in seconds
    //     */
    //    public void setTimeToLive(long seconds)
    //    {
    //        this.timeToLive = TimeUnit.SECONDS.toMillis(seconds);
    //    }
    
    //    /**
    //     * 
    //     * @return the time in seconds
    //     */
    //    public long getTimeToLive()
    //    {
    //        return TimeUnit.MILLISECONDS.toSeconds(timeToLive);
    //    }
    
    //    public boolean isExpired()
    //    {
    //        if (timeToLive < 0)
    //            return false;
    //        
    //        return (this.timestamp.getTime() + timeToLive < new Date().getTime());
    //    }
}
