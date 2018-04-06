package net.sf.jkniv.sqlegance;

import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.sqlegance.SqlContext;

import java.util.concurrent.SynchronousQueue;

import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Sql;

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
