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
package net.sf.jkniv.sqlegance.logger;

/**
 * Level of logging 
 *
 * @author Alisson Gomes
 *
 */
public enum LogLevel
{
    /** non logging */
    NONE,
    /** logging Connection level */
    //CONN,
    //** logging Transaction level */
    //TX,
    /** logging Prepared Statement level */
    STMT,
    /** logging ResultSet level */
    RESULTSET,
    /** logging all levels */
    ALL
    ;
    
    
    public static LogLevel get(String l)
    {
        LogLevel answer = LogLevel.NONE;
        
        for(LogLevel level : LogLevel.values())
        {
            if (level.name().equalsIgnoreCase(l))
                answer = level;
        }
        return answer;
    }
}
