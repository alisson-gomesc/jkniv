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
import net.sf.jkniv.sqlegance.Statistical;
import net.sf.jkniv.sqlegance.Updateable;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;
import net.sf.jkniv.sqlegance.validation.ValidateType;
import net.sf.jkniv.sqlegance.transaction.Isolation;

/**
 * Tag of update sentence.
 * 
 * @author Alisson Gomes
 * @since 0.0.2
 */
class UpdateTag extends AbstractSqlTag implements Updateable
{
    /**
     * Build a new <code>update</code> tag from XML file.
     * 
     * @param id
     *            Name/Identify from tag.
     * @param languageType
     *            type of language from tag.
     */
    public UpdateTag(String id, LanguageType languageType)
    {
        super(id, languageType);
    }
    
    /**
     * Build a new <code>update</code> tag from XML file.
     * 
     * @param id Name/Identify from tag
     * @param languageType type of language from tag
     * @param sqlDialect dialect from database
     */
    public UpdateTag(String id, LanguageType languageType, SqlDialect sqlDialect)
    {
        super(id, languageType, sqlDialect);
    }
    
    /**
     * Build a new <code>update</code> tag from XML file.
     * 
     * @param id
     *            Name/Identify from tag.
     * @param languageType
     *            type of language from tag.
     * @param isolation Retrieves the current transaction isolation level for the query.
     * @param timeout Retrieves the number of seconds the repository will wait for a Query
     * object to execute.
     * @param validateType validation to apply before execute SQL.
     * @param stats SQL statistical
     */
    public UpdateTag(String id, LanguageType languageType, Isolation isolation, int timeout, ValidateType validateType, Statistical stats)
    {
        super(id, languageType, isolation, timeout, validateType, stats);
    }

    /**
     * Retrieve the tag name.
     * 
     * @return name from tag <code>update</code>.
     */
    public String getTagName()
    {
        return TAG_NAME;
    }
    
    /**
     * Command type to execute.
     * 
     * @return the type of command used, <code>UPDATE</code>.
     */
    public SqlType getSqlType()
    {
        return SqlType.UPDATE;
    }
    
    @Override
    public boolean isUpdateable()
    {
        return true;
    }
    
    @Override
    public Updateable asUpdateable()
    {
        return this;
    }

}
