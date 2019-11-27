package net.sf.jkniv.whinstone.params;

abstract class AbstractParam
{
    protected boolean hasInClause(String[] paramsNames)
    {
        for (String p : paramsNames)
        {
            if (hasInClause(p))
                return true;
        }
        return false;
    }
    
    protected boolean hasInClause(String paramName)
    {
        return (paramName.toLowerCase().startsWith("in:"));
    }
    /*
    private void setValue(Date value) throws SQLException
    {
        java.sql.Timestamp timestamp = dtConverter.convert(java.sql.Timestamp.class, value);
        stmt.setObject(currentIndex(), timestamp);
    }
    
    private void setValue(Calendar value) throws SQLException
    {
        java.sql.Timestamp timestamp = dtConverter.convert(java.sql.Timestamp.class, value.getTime());
        stmt.setObject(currentIndex(), timestamp);
    }
    
    private void setValue(Enum<?> value) throws SQLException
    {
        //ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(value);
        //stmt.setObject(currentIndex(), proxy.invoke("name"));
        stmt.setObject(currentIndex(), value.name());
    }
    
    private void setValueOfKey(ObjectProxy<?> proxy, String property, Object value)
    {
        Object parsedValue = value;
        
        if(value instanceof java.sql.Time)
            parsedValue = new Date(((java.sql.Time)value).getTime());
        else if (value instanceof java.sql.Date)
            parsedValue = new Date(((java.sql.Date)value).getTime());
        else if (value instanceof java.sql.Timestamp)
            parsedValue = new Date(((java.sql.Timestamp)value).getTime());
        
        proxy.invoke(SETTER.capitalize(property), parsedValue);
    }
    

     */
/*
    protected Object getProperty(String name, Object params)
    {
        Object o = null;
        try
        {
            o = PropertyUtils.getProperty(params, name);
        }
        catch (Exception e)
        {
            throw new ParameterNotFoundException("Cannot find the property [" + name + "] at param object ["
                    + (params != null ? params.getClass().getName() : "null") + "] ");
        }
        return o;
    }
*/
}
