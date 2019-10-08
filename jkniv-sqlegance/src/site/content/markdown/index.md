Title: SQLegance

What is SQLegance?
--------------------

SQLegance is a library for SQL sentence that supports for dynamically append SQL. SQLegance put all SQL sentences at XML file, just it. Making the SQL sentence more readable, an better maintenance, like iBatis framework, where the SQL sentence can be dynamics. 


Why SQLegance
--------------------

For a long time I have developed systems and often had to maintain code developed by other programmers, only who confronted that knows how arduous the task of keeping it clean. This framework helps us to keep the code clean kicking the SQL code to XML files and keeping business logic to java, facilitating your maintenance.

The advantage to use SQLegance it's that can be adopted to your current project using frameworks like Hibernate, EclipseLink, Spring or JDBC easily, because SQLegance is not ORM or handle `java.sql.*` classes. SQLegance just keep your queries (native SQL, JPQL, HQL etc) in XML files, independently from framework.

Simple XML files
--------------------

    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <statements xmlns="http://jkniv.sf.net/schema/sqlegance"
     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     xsi:schemaLocation="http://jkniv.sf.net/schema/sqlegance
     http://jkniv.sf.net/schema/sqlegance/sqlegance.xsd">
        
      <select id="selectCompanies" type="NATIVE">
        select id, name from Companies where id = :id
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

