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

import java.util.Calendar;
import java.util.Date;

import net.sf.jkniv.sqlegance.params.ParamMarkType;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

/**
 * Factory to create new instances of {@link AutoBindParams}.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class PrepareParamsFactory
{
    
    public static <T,R> AutoBindParams newPrepareParams(StatementAdapter<T,R> adapter, Queryable queryable)
    {
        AutoBindParams prepareParams = null;
        if (queryable.isTypeOfNull())
            prepareParams = new NoParams(adapter);
        else if (queryable.isTypeOfBasic() || queryable.getParams() instanceof Date || queryable.getParams() instanceof Calendar)
            prepareParams = new BasicParam(adapter, queryable);
        else if (queryable.isTypeOfArrayBasicTypes())
            prepareParams = new PositionalArrayParams(adapter, queryable);
        else if (queryable.isTypeOfCollectionBasicTypes())
            prepareParams = new PositionalCollectionParams(adapter, queryable);
        else if (queryable.getDynamicSql().getParamParser().getType() == ParamMarkType.QUESTION)
            prepareParams = new PositionalParams(adapter, queryable);
        else
            prepareParams = new NamedParams(adapter, queryable);
        
        return prepareParams;
    }
    
    public static <T,R> AutoBindParams newNoParams(StatementAdapter<T,R> adapter)
    {
        return new NoParams(adapter);
    }

    public static <T,R> AutoBindParams newBasicParam(StatementAdapter<T,R> adapter, Queryable queryable)
    {
        return new BasicParam(adapter, queryable);
    }
    
    public static <T,R> AutoBindParams newPositionalArrayParams(StatementAdapter<T,R> adapter, Queryable queryable)
    {
        return new PositionalArrayParams(adapter,  queryable);
    }
    
    public static <T,R> AutoBindParams newPositionalCollectionParams(StatementAdapter<T,R> adapter, Queryable queryable)
    {
        return new PositionalCollectionParams(adapter, queryable);
    }

    public static <T,R> AutoBindParams newPositionalCollectionMapParams(StatementAdapter<T,R> adapter, Queryable queryable)
    {
        return new PositionalCollectionMapParams(adapter, queryable);
    }


    public static <T,R> AutoBindParams newPositionalCollectionPojoParams(StatementAdapter<T,R> adapter, Queryable queryable)
    {
        return new PositionalCollectionPojoParams(adapter, queryable);
    }

    public static <T,R> AutoBindParams newPositionalCollectionArrayParams(StatementAdapter<T,R> adapter, Queryable queryable)
    {
        return new PositionalCollectionArrayParams(adapter, queryable);
    }

    public static <T,R> AutoBindParams newPositionalParams(StatementAdapter<T,R> adapter, Queryable queryable)
    {
        return new PositionalParams(adapter, queryable);
    }
    
    public static <T,R> AutoBindParams newNamedParams(StatementAdapter<T,R> adapter, Queryable queryable)
    {
        return new NamedParams(adapter, queryable);
    }

//    public static <T,R> AutoBindParams newPositionalArrayMapParams(StatementAdapter<T,R> adapter, Queryable queryable)
//    {
//        return new PositionalarCollectionMapParams(adapter, queryable);
//    }
}
