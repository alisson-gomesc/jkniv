title: Getting Started


Getting started
----------------------

Almost all enterprise applications need to database to persistence your data. Once you have configured your database, you will write SQL to manipulate your records at database. Here we began to use SQLegance, writing SQL.

## Sql Context

The all sentences queries are keep in XML file, to get access the queries a central interface SqlContext is provided.

    SqlContext sqlContext = SqlContextFactory.newInstance("/repository-sql.xml");

The file `/repository-sql.xml` is loaded from root classpath, maven project's should be in `src/main/resources` folder.

Below present a full sample with yours tags:
        
    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <statements 
      xmlns="http://jkniv.sf.net/schema/sqlegance/statements"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://jkniv.sf.net/schema/sqlegance/statements
       http://jkniv.sf.net/schema/sqlegance/sqlegance-stmt.xsd">

      <include href="/another-sql-file.xml" />
    
      <select id="selectUsers" type="JPQL">
        select id, name from Users where id = 1
      </select>
      
      <update id="updateAuthor" type="NATIVE">
        update Author set username = #{username}, password = #{password}, email = #{email}, bio = #{bio} where id = #{id}
      </update>
      
      <delete id="deleteAuthor" type="NATIVE">
        delete from Author where id = #{id}
      </delete>

      <procedure id="updateItens" spname="sprUpdateItens">
        <parameter property="categoryid" mode="IN" />
      </procedure>
      
    </statements>      


## SQL

Before to get a query it's necessary load the sentences in `SqlContext` instanceTo get a query call the methods:

    import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
    import net.sf.jkniv.sqlegance.SqlContext;
    import net.sf.jkniv.sqlegance.Sql;

    public static void main(String[] args)
    {
       SqlContext sqlContext = SqlContextFactory.newInstance("/repository-sql.xml");
       Sql sql = sqlContext.getQuery("selectUsers");
       
       String mysql = sql.getSql();
       
       System.out.println(mysql); // print: select id, name from Users where id = 1
    }

Just it!


`0.5.0 note:` In sqlegance-0.5.0 the sql context is static, from 0.6.0 version each instance it's a different context, a lot of things change from `0.5.0` to `0.6.0`.

