package net.sf.jkniv.sqlegance.builder.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jkniv.sqlegance.Sql;

class SqlContext
{
    private String                  name;
    private List<String>            resources;
    private final Map<String, Sql> mapSql;
    
    public SqlContext(String name)
    {
        this.name = name;
        this.mapSql = new HashMap<String, Sql>();
        this.resources = new ArrayList<String>();
    }
    
}
