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

/**
 * 
 * TODO properties to add
 *  add property to remove end line from queries, good for debug 
 *  add property to limit parameter at clause IN, default is 1000 for oracle
 *  
 *  
 * @author Alisson Gomes
 * @since 0.6.0
 */
public enum RepositoryProperty
{
    
    /** property: <code>user</code>, default value is: <code>null</code> */
    JDBC_USER {
        public String key() { return "user"; }
        
        public String defaultValue() { return null; }
    },
    
    /** property: <code>password</code>, default value is: <code>null</code> */
    JDBC_PASSWORD {
        public String key() { return "password"; }
        
        public String defaultValue() { return null; }
    },
    
    /** property: <code>url</code>, default value is: <code>null</code> */
    JDBC_URL {
        public String key() { return "url"; }
        
        public String defaultValue() { return null; }
    },
    
    /** property: <code>driver</code>, default value is: <code>null</code> */
    JDBC_DRIVER { 
        public String key() { return "driver"; }
        
        public String defaultValue() { return null; }
    },

    /** property: <code>schema</code>, default value is: <code>null</code> */
    JDBC_SCHEMA { 
        public String key() { return "schema"; }
        
        public String defaultValue() { return null; }
    },

    /*
    /** property: <code>jkniv.repository.jdbc.stmt_strategy</code>, default value is: <code>null</code>, must be implemented by whinstone-*technology* *
    PREPARED_STATEMENT_STRATEGY {
        public String key() { return "jkniv.repository.jdbc.stmt_strategy"; }
        
        public String defaultValue() { return null; }
    },
    */

    /** property: <code>jkniv.repository.query_namestrategy</code> */
    QUERY_NAME_STRATEGY {
        public String key() { return PREFIX+".query_namestrategy"; }
        
        public String defaultValue() { return "net.sf.jkniv.sqlegance.HashQueryNameStrategy"; }
    },

    /** property: <code>jkniv.repository.jdbc.dialect</code> */
    SQL_DIALECT {
        public String key() { return PREFIX+".jdbc.dialect"; }
        
        public String defaultValue() { return "net.sf.jkniv.sqlegance.dialect.AnsiDialect"; }
    },

    /** property: <code>jkniv.repository.stats</code> */
    SQL_STATS {
        public String key() { return PREFIX+".stats"; }
        
        public String defaultValue() { return "false"; }
    },

    /** property: <code>jkniv.repository.debug_sql</code> */
    DEBUG_SQL {
        public String key() { return PREFIX+".debug_sql"; }
        
        public String defaultValue() { return "NONE"; }
    },

    /** property: <code>jkniv.repository.data_masking</code> */
    DATA_MASKING {
        public String key() { return PREFIX+".data_masking"; }
        
        public String defaultValue() { return "net.sf.jkniv.sqlegance.logger.SimpleDataMasking"; }
    },

    /** 
     * Enabling (<b>true</b> value) a short name for query <code>id</code>, example: <code>com.acme.queries.<b>updateUser</b></code>,
     * where <code><b>updateUser</b></code> is a shortcut.
     *  Default is <b>false</b>.
     * property: <code>jkniv.repository.short_name_enable</code> */
    SHORT_NAME_ENABLE {
        public String key() { return PREFIX+".short_name_enable"; }
        
        public String defaultValue() { return "false"; }
    },

    /** 
     * Enabling (<b>true</b> value) reloadable xml file when change happens
     *  Default is <b>false</b>.
     * property: <code>jkniv.repository.reloadable_xml_enable</code> */
    RELOADABLE_XML_ENABLE {
        public String key() { return PREFIX+".reloadable_xml_enable"; }
        
        public String defaultValue() { return "false"; }
    },

    /*
     * Enabling (<b>true</b> value) the package from java project become make the function
     * from <code>package</code> tag. Default is <b>false</b>.
     * <code>package</code> tag override the project package.
     * <br>
     * property: <code>jkniv.repository.project_package_enable</code> *
    PROJECT_PACKAGE_ENABLE {
        public String key() { return "jkniv.repository.project_package_enable"; }
        
        public String defaultValue() { return "false"; }
    },
    */

    /** property: <code>jkniv.repository.jdbc_adapter_factory</code>, default value is: <code>null</code> */
    JDBC_ADAPTER_FACTORY {
        public String key() { return PREFIX+".jdbc_adapter_factory"; }
        
        public String defaultValue() { return null; }
    },
    
    /** property: <code>jkniv.repository.show_config</code> */
    SHOW_CONFIG {
        public String key() { return PREFIX+".show_config"; }
        
        public String defaultValue() { return "false"; }
    },


    /** property: <code>jkniv.repository.protocol_version</code> , default value is: <code>V4</code> */
    PROTOCOL_VERSION {
        public String key() { return PREFIX+".protocol_version"; }
        
        public String defaultValue() { return "V4"; }
    },

    /** property: <code>jkniv.repository.accessId</code> */
    ACCESS_ID {
        public String key() { return PREFIX+".accessId"; }
        
        public String defaultValue() { return "id,getId,setId"; }
    },
    
    /** property: <code>jkniv.repository.key_file</code> , default value is: <code>null</code> */
    KEY_FILE {
        public String key() { return PREFIX+".key_file"; }
        
        public String defaultValue() { return null; }
    },

    /** property: <code>jkniv.cassandra.local_datacenter</code> , default value is: <code>null</code> */
    LOCAL_DATACENTER {
        public String key() { return "jkniv.cassandra.local_datacenter"; }
        
        public String defaultValue() { return null; }
    },
//    /** <code>jkniv.repository.ttl_sql</code> */
//    TTL_SQL {
//        public String key() { return "jkniv.repository.ttl_sql"; }
//    }
    ;
//    /** <code>jkniv.repository.ttl_sql</code> */
//    TTL_SQL {
//        public String key() { return "jkniv.repository.ttl_sql"; }
//    }
    ;

    public abstract String key();
    
    public abstract String defaultValue();
    
    public static final String PREFIX = "jkniv.repository";
}
