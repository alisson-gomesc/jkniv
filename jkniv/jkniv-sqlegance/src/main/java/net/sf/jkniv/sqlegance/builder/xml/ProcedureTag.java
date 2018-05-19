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

import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.Storable;
import net.sf.jkniv.sqlegance.validation.ValidateType;
import net.sf.jkniv.sqlegance.transaction.Isolation;

/**
 * Tag of procedure sentence. <b>Stored procedures don't work yet!</b>
 * 
 * @author Alisson Gomes
 * @since 0.0.2
 */
public class ProcedureTag extends AbstractSqlTag implements SqlTag, Storable
{
    public static final String      TAG_NAME         = "procedure";
    public static final String      ATTRIBUTE_SPNAME = "spname";
    
    /**
     * Name of stored procedure at database
     */
    private String                  spName;
    
    /**
     * Parameters from stored procedure. The parameters are at the order from
     * signature of procedure.
     */
    private ProcedureParameterTag[] params;
    
    /**
     * Build a new <code>procedure</code> tag from XML file.
     * 
     * @param id Name/Identify from tag.
     * @param languageType type of language from tag.
     */
    public ProcedureTag(String id, LanguageType languageType)
    {
        super(id, languageType);
    }
    
    /**
     * Build a new <code>procedure</code> tag from XML file.
     * 
     * @param id
     *            Name/Identify from tag.
     * @param languageType
     *            type of language from tag.
     * @param isolation Retrieves the current transaction isolation level for the query.
     * @param timeout Retrieves the number of seconds the repository will wait for a Query
     * object to execute.
     * @param batch Indicate if query is a batch of commands.
     * @param hint A SQL hint can be used on certain database platforms.
     * @param validateType validation to apply before execute SQL.
     */
    public ProcedureTag(String id, LanguageType languageType, Isolation isolation, int timeout, boolean batch,
            String hint, ValidateType validateType)
    {
        super(id, languageType, isolation, timeout, batch, /*null,*/ hint, validateType);
    }
    
    /**
     * Retrieve the tag name.
     * 
     * @return name from tag <code>procedure</code>.
     */
    public String getTagName()
    {
        return TAG_NAME;
    }
    
    /**
     * Command type to execute.
     * 
     * @return the type of command used, <code>DELETE</code>.
     */
    public SqlType getSqlType()
    {
        return SqlType.PROCEDURE;
    }
    

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
    
    /**
     * Define parameters from stored procedure
     * 
     * @param params
     *            the parameters
     */
    public void setParams(ProcedureParameterTag[] params)
    {
        this.params = params;
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.builder.xml.Storable#getParams()
     */
    @Override
    public ProcedureParameterTag[] getParams()
    {
        return params;
    }
    
    /**
     * Define the name of stored procedure.
     * 
     * @param spName
     *            name of stored procedure.
     */
    public void setSpName(String spName)
    {
        this.spName = spName;
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.builder.xml.Storable#getSpName()
     */
    @Override
    public String getSpName()
    {
        return spName;
    }
    
    @Override
    public Storable asStorable()
    {
        return this;
    }
}
