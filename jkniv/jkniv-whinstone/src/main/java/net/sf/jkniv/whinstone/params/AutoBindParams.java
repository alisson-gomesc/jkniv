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
package net.sf.jkniv.whinstone.params;

import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

/**
 * Realize the bind from parameter to {@link StatementAdapter} automatically.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 *
 */
public interface AutoBindParams
{
    /**
     * Bind all parameters from {@link Queryable} to {@link StatementAdapter} automatically
     */
    public void on();

    /**
     * Grouping multiple {@code insert} |{@code update} | {@code delete} statements
     * into a single Prepared Statement.
     * 
     * @return the number of rows affected.
     * <p><b>Note:</b>
     * <p><code>-2</code> no count of the number of rows it affected is available</p>
     * <p><code>-3</code> indicating that an error occurred while executing a bulk statement</p>
     */
    public int onBulk();

    /*
     * Grouping multiple {@code insert} |{@code update} | {@code delete} statements
     * into a single batch and having the whole batch sent to the database and 
     * processed in one trip 
     * @return the number of rows affected.
     * <p><b>Note:</b>
     * <p><code>-2</code> no count of the number of rows it affected is available</p>
     * <p><code>-3</code> indicating that an error occurred while executing a batch statement</p>
     */
    //public int onBatch();

    /**
     * @deprecated use auto bind 
     */
    public StatementAdapterOld parameterized(String[] paramsNames);
}
