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
package net.sf.jkniv.whinstone.couchbase;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.RepositoryService;
import net.sf.jkniv.whinstone.couchdb.BaseSpringJUnit4;
import net.sf.jkniv.whinstone.spi.RepositoryFactory;

public class BaseCouchbase extends BaseSpringJUnit4
{
    private static final Logger    LOG                 = LoggerFactory.getLogger(BaseCouchbase.class);
    public static final Properties config, configDb3t;
    private static final String    URL                 = "192.168.99.100";
    private static final String    SCHEMA              = "travel-sample";
    private static final String    USER                = "admin";
    private static final String    PASSWD              = "123456";
    
    static
    {
        config = new Properties();
        configDb3t = new Properties();
        config.setProperty(RepositoryProperty.JDBC_URL.key(), URL);
        config.setProperty(RepositoryProperty.JDBC_SCHEMA.key(), SCHEMA);
        config.setProperty(RepositoryProperty.JDBC_USER.key(), USER);
        config.setProperty(RepositoryProperty.JDBC_PASSWORD.key(), PASSWD);
    }
    
    protected static Repository getRepository()
    {
        return RepositoryService.getInstance().lookup(RepositoryType.COUCHBASE).newInstance(config);
    }
}
