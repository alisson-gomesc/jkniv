Title: Stored Procedure

<!--
https://docs.oracle.com/cd/B28359_01/appdev.111/b28843/tdddg_procedures.htm
https://blog.jooq.org/2017/12/15/how-to-fetch-oracle-dbms_output-from-jdbc/
-->

Whinstone JDBC Stored Procedure
--------------------


Stored procedures are the way to store programs in the database.

- Functions, which return a value
- Stored Procedure, which do not return a value


### Invoke Simple Stored Procedure
### Invoke Functions
### Recovering data from Cursors
### Enabling DBMS_OUTPUT


Simple stored procedure (oracle) to print out a string via DBMS_OUTPUT:


    CREATE OR REPLACE PROCEDURE STHELLOSTORED
    IS BEGIN
      DBMS_OUTPUT.PUT_LINE('Hello Oracle Stored');
    END;


Declare the call to stored in XML file:

    <statements>
      <update id="helloStored"> 
        begin
         STHELLOSTORED;
        END;
      </update>
    </statements>

    
Java code to call the stored `STHELLOSTORED`:

    Queryable q = QueryFactory.of("helloStored");
    repository.update(q);
    
    