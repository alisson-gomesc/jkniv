package net.sf.jkniv.sqlegance;

public enum RepositoryType
{
    JDBC, JPA
    ;
    
    public static RepositoryType get(String s)
    {
        RepositoryType answer = RepositoryType.JDBC;
        for(RepositoryType type : RepositoryType.values())
        {
            if (type.name().equalsIgnoreCase(s))
                answer = type;
        }
        return answer;
    }

}
