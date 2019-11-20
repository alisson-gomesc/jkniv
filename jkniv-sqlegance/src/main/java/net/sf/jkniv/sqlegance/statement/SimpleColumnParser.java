package net.sf.jkniv.sqlegance.statement;

class SimpleColumnParser implements ColumnParser
{
//    private static final Pattern PATTERN = Pattern.compile("(?<=select).*(?=from)", 
//            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

//    private static final Pattern PATTERN = Pattern.compile("(?is)SELECT(.*?)(?<!\\w*\")FROM(?!\\w*?\")(.*?)(?=WHERE|ORDER|$)", 
//            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    @Override
    public String[] extract(String sql)
    {
        int selects = 0;
        int froms = 0;
        int openedParenthesis = 0, closedParenthesis = 0; 
        int lengthOfParenthesis = 0; // to remove values between parenthesis -> cast(COALESCE(ca.channel_code,99) as varchar)
        StringBuilder columns = new StringBuilder();
        StringBuilder evaluating = new StringBuilder();
        String sqlTrim = sql.trim().replaceAll("\n", " ");
        String[] columnsOrAliasName = new String[0];
        char lastCurrent = ' ';// to remove sequential spaces
        for(int i=0; i<sqlTrim.length(); i++)
        {
            char current = sqlTrim.charAt(i);
            if (current == '(' || current == ')' || current == ' ')
            {
                if (current == '(' )
                    openedParenthesis++;
                else if (current == ')' )
                    closedParenthesis++;
                
                if (lengthOfParenthesis > 0 && openedParenthesis == closedParenthesis)
                {
                    columns.delete(columns.length()-lengthOfParenthesis, columns.length());// deleting content between (...)
                    lengthOfParenthesis = 0;   
                    openedParenthesis = 0;
                    closedParenthesis = 0;
                }
                if ("SELECT".equalsIgnoreCase(evaluating.toString()))
                    selects++;
                else if("FROM".equalsIgnoreCase(evaluating.toString()))
                {
                    froms++;
                    if (froms == selects)
                    {
                        columns.delete(columns.length()-"from".length(), columns.length());// deleting "from"
                        break;
                    }
                }
                evaluating = new StringBuilder();
                if (lastCurrent != ' ')
                    columns.append(current);
            }
            else
            {
                evaluating.append(current);
                columns.append(current);
            }
            if (openedParenthesis > 0)
                lengthOfParenthesis++;
            
            lastCurrent = current;
        }
        String columsAsStr = columns.toString(); 
        if (columsAsStr.indexOf("*") > 0)
            throw new IllegalArgumentException("Cannot resolve column or alias names from select with asterisk (*)");
        
        String[] columnsSeparated = columsAsStr.split(",");
        columnsOrAliasName = new String[columnsSeparated.length];
        for(int i=0; i<columnsSeparated.length; i++)
            columnsOrAliasName[i] = getColumnNameOrAlias(columnsSeparated[i]);
        
        return columnsOrAliasName;
    }
    private String getColumnNameOrAlias(String value)
    {
        StringBuilder columnName = new StringBuilder();
        String valueTrim = value.trim();
        for(int i=valueTrim.length()-1; i>=0; i--)
        {
            if (' ' == valueTrim.charAt(i))
                break;
            columnName.insert(0,valueTrim.charAt(i));
        }
        String name = columnName.toString();
        int dotPosition =  name.indexOf("."); // select u.name from users u
        if ( dotPosition > 0)
        {
            if (name.endsWith("\"")) // select u."employe.fullName", "address.id" from users u
            {
                int quotePosition = name.indexOf("\"");  
                if (quotePosition > dotPosition) // u."employe.fullName" -> "employe.fullName"
                {
                    name = name.substring(dotPosition+1);
                }
                name = name.replaceAll("\"", "");
            }
            else
            {
                name = name.substring(dotPosition+1); // u.name -> name
            }
        }
        return name;
    }
}
