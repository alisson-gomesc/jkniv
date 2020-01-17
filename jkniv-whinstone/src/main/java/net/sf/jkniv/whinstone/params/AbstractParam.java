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
}
