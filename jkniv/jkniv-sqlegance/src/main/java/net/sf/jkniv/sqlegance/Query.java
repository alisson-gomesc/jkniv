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
package net.sf.jkniv.sqlegance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.transaction.Isolation;

/**
 * The object used to name and parameterize a query.
 * 
 * @author Alisson Gomes
 * @since 0.0.28
 * @deprecated use {@code QueryFactory}, DON'T use this class WILL BE REMOVED at 1.0.0 version
 */
public class Query implements IQuery
{
    private static final Logger LOG = LoggerFactory.getLogger(Query.class);

    private String    name;
    private Integer   start;
    private Integer   max;
    private Long      total;
    private Isolation isolation;
    private String    hint;
    private int       timeout;
    private boolean   batch;
    private Object    params;
    
    /**
     * Creates a Query object parameterized with: isolation default, no timeout and online (no batch).
     * 
     * @param name a name for query
     * @param params parameters from query
     * @param start the first row
     * @param max row numbers
     */
    /**
     * Creates a Query object parameterized starting at first row and retrieve all rows, isolation default, no timeout and online (no batch).
     * 
     * @param name a name for query
     * @param params parameters from query
     */
    public Query(String name, Object params)
    {
        this(name, params, 0, Integer.MAX_VALUE);
    }
    
    /**
     * Creates a Query object parameterized starting at first row and retrieve all rows, isolation default, no timeout and online (no batch).
     * 
     * @param name a name for query
     */
    public Query(String name)
    {
        this(name, null, 0, Integer.MAX_VALUE);
    }
    
    public Query(String name, Object params, int start, int max)
    {
        this.name = name;
        this.params = params;
        this.start = start;
        this.max = max;
        this.total = -1L;
        this.isolation = Isolation.DEFAULT;
        this.timeout = -1;
        this.hint = "";
        this.batch = false;
        LOG.warn("DON'T use this class [{}] WILL BE REMOVED at 1.0.0 version", getClass().getCanonicalName());
    }
    
    public String getName()
    {
        return name;
    }
    
    public Object getParams()
    {
        return this.params;
    }
    
    public int getStart()
    {
        return this.start;
    }
    
    /**
     * @param value initial value of row number
     */
    public void setStart(int value)
    {
        this.start = value;
        
    }
    
    public int getMax()
    {
        return this.max;
    }
    
    public void setMax(int value)
    {
        this.max = value;
    }
    
    public long getTotal()
    {
        return this.total;
    }
    
    public void setTotal(Long total)
    {
        this.total = total;
    }
    
    public boolean hasMaxRows()
    {
        return (max < Integer.MAX_VALUE);
    }

    @Override
    public String toString()
    {
        return "name=" + this.name + ", start=" + this.start + ", max=" + this.max + ", total=" + this.total;
    }
    
}
