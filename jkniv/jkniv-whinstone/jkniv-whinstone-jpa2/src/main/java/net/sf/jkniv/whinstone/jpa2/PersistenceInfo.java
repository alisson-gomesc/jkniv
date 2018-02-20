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
package net.sf.jkniv.whinstone.jpa2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PersistenceInfo
{
    private String              provider;
    private String              unitName;
    private String              transactionType;
    private Map<String, String> properties;
    private Set<String>         classes;
    
    public PersistenceInfo()
    {
        this.provider = null;
        this.unitName = null;
        this.properties = new HashMap<String, String>();
        this.classes = new HashSet<String>();
    }
    
    public void addProperty(String key, String value)
    {
        properties.put(key, value);
    }
    
    public void addClass(String value)
    {
        classes.add(value);
    }
    
    public String getPropertyValue(String key)
    {
        return properties.get(key);
    }
    
    public Properties getProperties()
    {
        Properties p = new Properties();
        p.putAll(properties);
        return (Properties)p.clone();

    }
    
    public String getProvider()
    {
        return provider;
    }
    
    public void setProvider(String provider)
    {
        this.provider = provider;
    }
    
    public String getUnitName()
    {
        return unitName;
    }
    
    public void setUnitName(String unitName)
    {
        this.unitName = unitName;
    }
    
    public String getTransactionType()
    {
        return transactionType;
    }
    
    public void setTransactionType(String transactionType)
    {
        this.transactionType = transactionType;
    }
    
    @Override
    public String toString()
    {
        return "unitName=" + unitName + ", transactionType=" + transactionType + ", provider=" + provider
                + ", properties=" + properties + ", classes=" + classes;
    }
    
}
