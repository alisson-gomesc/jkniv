package net.sf.jkniv.whinstone.params;

import org.apache.commons.beanutils.PropertyUtils;

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

}
