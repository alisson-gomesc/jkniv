title: Getting Started


Getting started
----------------------

Almost all enterprise applications need to database to persistence your data. Once you have configured your database, you will write SQL to manipulate your records at database. Here we began to use SQLegance, writing SQL.

## SqlConfig.xml

By default all SQL are at SqlConfig.xml file or your derivative, by "include" tag. This file will be searched in the default classpath (root).
        
Below present a full sample with your tags:
        
    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <statements
      xmlns="http://jkniv.sf.net/schema/sqlegance"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xmlns:xi="http://www.w3.org/2001/XInclude"
      xsi:schemaLocation="http://jkniv.sf.net/schema/sqlegance
       http://jkniv.sf.net/schema/sqlegance/sqlegance-0.5.xsd">

      <include href="/my-other-sql-file.xml" />
    
      <select id="selectUsers" type="JPQL">
        select id, name from Users
        <if test="name != null">
            where id = 1
        </if>
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


If you have some constraint to use `"SqlConfig.xml"` as file name, it's possible change it, but to load the queries you must call the method `configFile</code> from `XmlBuilderSql`:


    XmlBuilderSql.configFile("my_custom_filename.xml");


## XmlBuilderSql

This class is responsable for read XML file and load all SQLs. The first time when we try get an query the XML are loaded.
        
To get a query call the method:  

    import net.sf.jkniv.sqlegance.builder.XmlBuilderSql;
    import net.sf.jkniv.sqlegance.ISql;
    ...
    public void getMyQuery() {        
      ISql isql = XmlBuilderSql.getQuery("myQueryName");
      ...
    }

This is it!
