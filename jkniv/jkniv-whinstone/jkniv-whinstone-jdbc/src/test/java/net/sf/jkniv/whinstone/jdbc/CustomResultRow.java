/* 
 * JKNIV ,
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

package net.sf.jkniv.whinstone.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


import net.sf.jkniv.sqlegance.JdbcColumn;
import net.sf.jkniv.sqlegance.ResultRow;
import net.sf.jkniv.sqlegance.classification.ObjectTransform;
import net.sf.jkniv.sqlegance.classification.Transformable;

public class CustomResultRow<T> implements ResultRow<T, ResultSet>
{
    private Transformable<T> transformable;
    private JdbcColumn<ResultSet>[] columns;
    
    public CustomResultRow()
    {
        this.transformable = (Transformable<T>) new ObjectTransform();
    }
    
    public T row(ResultSet rs, int rownum) throws SQLException
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("JUNIT", Boolean.TRUE);
        map.put(String.valueOf(rownum), rs.getString(1));
        return (T) map;
    }
    
    private void next(JdbcColumn<ResultSet> column, ResultSet rs, Map<String, Object> map) throws SQLException
    {
        Object jdbcObject = null;
        if (column.isBinary())
            jdbcObject = column.getBytes(rs);
        else
            jdbcObject = column.getValue(rs);
        
        map.put(column.getAttributeName(), jdbcObject);
    }

    @Override
    public Transformable<T> getTransformable()
    {
        return transformable;
    }
    
    @Override
    public void setColumns(JdbcColumn<ResultSet>[] columns)
    {
        this.columns = columns;
    }

}
