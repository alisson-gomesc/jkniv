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

import org.junit.Ignore;
import org.junit.Test;

import net.sf.jkniv.sqlegance.logger.LogLevel;
import net.sf.jkniv.sqlegance.logger.SimpleDataMasking;

@Ignore("SqlLogger was deprecated")
public class SqlLoggerTest
{

    @Test
    public void whenLoggerIsConfigAsALL()
    {
        //SqlLogger log = new SqlLogger(LogLevel.ALL, new SimpleDataMasking());
        //log.debug("This log level [{}] works", "DEFAULT ALL");
        //log.debugconn("This log level [{}] works", "Level Connection");
        //log.debugtx("This log level [{}] works", "Level Transaction");
        //log.debugstmt("This log level [{}] works", "Level Statement");
        //log.debugrs("This log level [{}] works", "Level ResultSet");
    }
    

    @Test
    public void whenLoggerIsConfigAsConn()
    {
        //SqlLogger log = new SqlLogger(LogLevel.ALL, new SimpleDataMasking());
        //log.debug("This log level [{}] works", "DEFAULT ALL");
        //log.debugconn("This log level [{}] works", "Level Connection");
        //log.debugtx("This log level [{}] works", "Level Transaction");
        //log.debugstmt("This log level [{}] works", "Level Statement");
        //log.debugrs("This log level [{}] works", "Level ResultSet");
    }

    @Test
    public void whenLoggerIsConfigAsStmt()
    {
        //SqlLogger log = new SqlLogger(LogLevel.STMT, new SimpleDataMasking());
        //log.debug("This log level [{}] works", "DEFAULT ALL");
        //log.debugconn("This log level [{}] works", "Level Connection");
        //log.debugtx("This log level [{}] works", "Level Transaction");
        //log.debugstmt("This log level [{}] works", "Level Statement");
        //log.debugrs("This log level [{}] works", "Level ResultSet");
    }

    @Test
    public void whenLoggerIsConfigAsResultSet()
    {
        //SqlLogger log = new SqlLogger(LogLevel.RESULTSET, new SimpleDataMasking());
        //log.debug("This log level [{}] works", "DEFAULT ALL");
        //log.debugconn("This log level [{}] works", "Level Connection");
        //log.debugtx("This log level [{}] works", "Level Transaction");
        //log.debugstmt("This log level [{}] works", "Level Statement");
        //log.debugrs("This log level [{}] works", "Level ResultSet");
    }

    @Test
    public void whenLoggerIsConfigAsNone()
    {
        //SqlLogger log = new SqlLogger(LogLevel.NONE, new SimpleDataMasking());
        //log.debug("This log level [{}] works", "DEFAULT ALL");
        //log.debugconn("This log level [{}] works", "Level Connection");
        //log.debugtx("This log level [{}] works", "Level Transaction");
        //log.debugstmt("This log level [{}] works", "Level Statement");
        //log.debugrs("This log level [{}] works", "Level ResultSet");
    }

}
