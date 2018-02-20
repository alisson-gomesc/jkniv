Title: jkniv sqlegance

What is SQLegance?
--------------------

SQLegance is a sql sentence framework with support for dynamically generating SQL. SQLegance put all sql sentences at xml file, just it. Making the sql sentence more readable, an better maintenance, like iBatis framework, where the sql sentence can be dynamics. 

Maven users will need to add the following dependency to their pom.xml for this component:

    <dependency>
      <groupId>net.sf.jkniv</groupId>
      <artifactId>sqlegance</artifactId>
      <version>0.5.1-SNAPSHOT</version>
    </dependency>


Why SQLegance
--------------------

For a long time I have developed systems and often had to maintain  code developed by other programmers, only who confronted that knows how arduous the task of keeping it clean. This framework helps us to keep away SQL code from  java code, facilitating its maintenance.

The advantage to use SQLegance it's that can be use with other frameworks like JPA, Hibernate, Spring, JDBC native, because SQLegance don't need mapping, just keep your query, no more.

Simple XML files
--------------------

    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <statements xmlns="http://jkniv.sf.net/schema/sqlegance"
     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     xsi:schemaLocation="http://jkniv.sf.net/schema/sqlegance
     http://jkniv.sf.net/schema/sqlegance/sqlegance-0.5.xsd">
        
      <select id="selectCompanies" type="NATIVE">
        select id, name from Companies order by name
      </select>
          
      <update id="updateAuthor" type="NATIVE">
        update Author
        <set>
          <if test="username != null">username = #{username}</if>
          <if test="password != null">password = #{password}</if>
          <if test="email != null">email = #{email}</if>
        </set>
        where id = #{id}
      </update>
    </statements>


Road map
--------------------

We have a long way to make this framework better, just began.



 - Reload SQL files without restart web container to make development cycle more fast
 - Change the strategy from queries cache, now all queries are loaded in memory, maybe its not the better strategy to very big projects
 - Add RESTful support to expose the queries
 - Sign XML file to security reason
 - Add support to batch query
 - Add support to hint query
