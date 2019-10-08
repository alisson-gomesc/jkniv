package net.sf.jkniv.sqlegance.statement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SimpleColumnParser implements ColumnParser
{
    private static final Pattern PATTERN = Pattern.compile("(?<=select)(.*)(?=from)");

    @Override
    public String[] extract(String sql)
    {
        Matcher matcher = PATTERN.matcher(sql);
        String[] columnsOrAliasName = new String[0];
        if (matcher.find() && matcher.groupCount() == 1)
        {
            String[] columns = matcher.group().trim().split(",");
            columnsOrAliasName = new String[columns.length];
            for(int i=0; i<columns.length; i++)
                columnsOrAliasName[i] = getColumnNameOrAlias(columns[i]);
            
        }
        else
            throw new IllegalStateException("Cannot extract columns or alias name with ["+matcher.groupCount()+"] matcher select/from");
        
        return columnsOrAliasName;
    }

    private String getColumnNameOrAlias(String value)
    {
        StringBuilder columnName = new StringBuilder();
        for(int i=value.length()-1; i>=0; i--)
        {
            if (' ' == value.charAt(i))
                break;
            columnName.insert(0,value.charAt(i));
        }
        return columnName.toString();
    }
}
