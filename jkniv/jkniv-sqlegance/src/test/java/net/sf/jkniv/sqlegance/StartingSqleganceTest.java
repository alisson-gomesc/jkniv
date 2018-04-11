package net.sf.jkniv.sqlegance;

import net.sf.jkniv.sqlegance.builder.SqlContextFactory;

public class StartingSqleganceTest
{


    public static void main(String[] args)
    {
       SqlContext sqlContext = SqlContextFactory.newInstance("/repository-sql.xml");
       Sql sql = sqlContext.getQuery("sql2-attributes-default");
       
       String mysql = sql.getSql();
       System.out.println(mysql);
    }
}
