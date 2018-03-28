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

import net.sf.jkniv.sqlegance.Deletable;
import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;
import net.sf.jkniv.sqlegance.transaction.Isolation;
import net.sf.jkniv.sqlegance.validation.ValidateType;

/**
 * Tag of delete sentence.
 * 
 * @author Alisson Gomes
 * @since 0.0.2
 */
class DeleteTag extends AbstractSqlTag implements Deletable 
{
    /**
     * Build a new <code>delete</code> tag from XML file.
     * 
     * @param id Name/Identify from tag
     * @param languageType type of language from tag
     */
    public DeleteTag(String id, LanguageType languageType)
    {
        super(id, languageType);
    }

    /**
     * Build a new <code>delete</code> tag from XML file.
     * 
     * @param id Name/Identify from tag
     * @param languageType type of language from tag
     * @param sqlDialect dialect from database
     */
    public DeleteTag(String id, LanguageType languageType, SqlDialect sqlDialect)
    {
        super(id, languageType, sqlDialect);
    }

    /**
     * Build a new <code>delete</code> tag from XML file.
     * 
     * @param id
     *            Name/Identify from tag.
     * @param languageType
     *            type of language from tag.
     * @param isolation Retrieves the current transaction isolation level for the query.
     * @param timeout Retrieves the number of seconds the repository will wait for a Query
     * object to execute.
     * @param batch Indicate if query is a batch of commands.
     * @param hint A SQL hint can be used on certain database platforms
     * @param validateType validation to apply before execute SQL.
     */
    public DeleteTag(String id, LanguageType languageType, Isolation isolation, int timeout, boolean batch, String hint,
            ValidateType validateType)
    {
        super(id, languageType, isolation, timeout, batch, false, hint, validateType);
    }
    
    /**
     * Retrieve the tag name.
     * 
     * @return name from tag <code>delete</code>.
     */
    @Override
    public String getTagName()
    {
        return TAG_NAME;
    }
    
    /**
     * Command type to execute.
     * 
     * @return the type of command used, <code>DELETE</code>.
     */
    @Override
    public SqlType getSqlType()
    {
        return SqlType.DELETE;
    }
    
    @Override
    public boolean isDeletable()
    {
        return true;
    }
    
    @Override
    public Deletable asDeletable()
    {
        return this;
    }

}
