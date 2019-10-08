Title: Dynamic SQL


Dynamic SQL
----------------------


This framework was inspired by iBatis, where it's possible put our queries at XML file and provide dynamic SQL capabilities.
Quote MyBatis team:

<blockquote><i>"How painful  it is to conditionally concatenate strings of SQL together, making  sure not to forget spaces or to omit a comma at the end of a list of columns. Dynamic SQL can be downright painful to deal with."</i></blockquote>

The dynamic SQL elements should be familiar to anyone who has used SQL and JSTL test. For each SQL sentence there is an XML element:

- insert
- delete
- update
- select
- procedure
        
There are four others specials tags:

- `if`: nested with any other tag as `insert`, `delete`, `update`, `select`, `where` and `set`.
- `choose`: nested with any other tag as `insert`, `delete`, `update`, `select`, `where` and `set`.
- `where`: used when an condition have many possibilities but we don't know which is true
- `set`: used when an update must be executed under some columns and/or conditions

## Parameter Name Format

SQLegance doesn't accept appended parameter to SQL, so the statements must use a `PreparedStatement` where an object represents a pre-compiled SQL statement. This way yours SQL will are protected against SQL injection attack.

There are three ways to define parameters names: 

 - `#{id}` a mybatis style
 - `:id` a JPA style
 - `?` a JDBC style
 
Note: They cannot be mixed, just one type for statement.

    <select id="users-hash">
      select id, name from Users where id = #{id} or name like #{name}
    </select>

    <select id="users-dot">
      select id, name from Users where id = :id or name like :name
    </select>

    <select id="users-question">
      select id, name from Users where id = ? or name like ?
    </select>

    
### Nested parameters evaluate

To evaluate the conditions a parameter can be check with nested values (`roles.size()`):

    <select id="users-dot">
      select u.id, u.name user_name, r.name role_name
      from Users u
      inner join Roles r on r.id = u.role_id 
      where u.status = 'ACTIVE'
      <if test="roles != null and roles.size() > 0">
       and r.name like :roleName
      </if>
    </select>


### Support IN clause for queries with dynamic values

Can use array or collections from wrapper types

    select 
     b.ID as id, b.ISBN as isbn, b.NAME as name, a.name as author
    from 
     BOOK b inner join AUTHOR a on a.ID = b.AUTHOR_ID
    where 
     a.name in (:in:authors)

`IN` clause doesn't accept `?` or `#{}` parameter format, must be strictly `:in:`&lt;my collection or array&gt;


##include Tag

To keep a project with a lot of SQL and reduce the possibility of conflict between developers edit the same XML file, the SQL sentences could be organized into different files with `<include href="..." />` element.


    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <statements 
     context="ctx-acme"
     xmlns="http://jkniv.sf.net/schema/sqlegance/statements" 
     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     xsi:schemaLocation="http://jkniv.sf.net/schema/sqlegance/statements
     http://jkniv.sf.net/schema/sqlegance/sqlegance-stmt.xsd">

      <include href="/sql-finance.xml" />
      <include href="/sql-reports.xml" />
      <include href="/sql-users.xml" />
    </statements>

## insert Tag

An `insert` tag can be dynamically with several `if` with distinct conditions. **If have over one condition it's true the final SQL will be wrong**.

    <insert id="insertAuthor" type="NATIVE">
      insert into Author
      <if
        test="username != null and password != null and email != null and bio != null">
        (username,password,email,bio) values (#{id},#{username},#{password},#{email},#{bio})
      </if>
      <if
        test="username != null and password != null and email != null and bio == null">
        (username,password,email) values (#{id},#{username},#{password},#{email})
      </if>
      <if
        test="username != null and password != null and email == null and bio == null">
        (username,password) values (#{id},#{username})
      </if>
    </insert>
        
## delete Tag

An `delete` tag can be dynamically like `insert`, but the most common is a fixed clause where.

    <delete id="deleteAuthor" type="NATIVE">
      delete from Author where id = #{id}
    </delete>
   
## udpate Tag

An `update` tag can be dynamically with several `if` with distinct conditions. When the columns to update it's conditional must be indent with `set` tag.

    <update id="updateAuthor" type="NATIVE">
      update Author
      <set>
        <if test="username != null">username = #{username}</if>
        <if test="password != null">password = #{password}</if>
        <if test="email != null">email = #{email}</if>
        <if test="bio != null">bio = #{bio}</if>
      </set>
      where id = #{id}
    </update>

## select Tag

The `select` element it's most common to use. This is a Hibernate query.

    <select id="getZonaArea" type="JPQL">
      select za
      from ZonaArea za 
      inner join fetch za.zona z
      inner join fetch z.district d
      inner join fetch d.division
      <where>
        <if test="division != null">za.zona.district.division.id = :division</if>
        <if test="district != null">and za.zona.district.id = :district</if>
        <if test="zona != null">and za.zona.id = :zona</if>
        <if test="zonaType != null">and za.zonaType = :zonaType</if>
      </where>         
    </select>

## where Tag

The `where` element can be used to dynamically include the word <b>where</b> when we don't know which is the fist `where` condition.

    <select id="selectGroups" type="JPQL">
      select id, name from Groups
      <where>
        <if test="state != null">
          and state = #{state}
        </if>
        <if test="title != null">
          and title like #{title}
        </if>
        <if test="author != null and author.name != null">
          or author_name like #{author.name}
        </if>
        <if test="age == 5">
          or (age >= 5 and age <= 15)
        </if>
      </where>
    </select>

## set Tag

The set element can be used to dynamically include columns to update, and leave out others.

    <update id="updateAuthor" type="NATIVE">
      update Author
      <set>
        <if test="username != null">username = #{username}</if>
        <if test="password != null">password = #{password}</if>
        <if test="email != null">email = #{email}</if>
        <if test="bio != null">bio = #{bio}</if>
      </set>
      where id = #{id}
    </update>
        
## if Tag

SQLegance employs powerful OGNL based expressions to verify if condition is true or false, when the condition it's true the SQL sentence is appended with main query. 

    <select id="selectUsers" type="JPQL">
      select id, name from Users
      <if test="name != null">
        where name like #{name}
      </if>
    </select>

When name is different from <b>null</b> the main query is:
 
    select id, name from Users where name like #{name}

## choose Tag

When it is need to choose only one case among many options must be use the `choose` tag.

    <select id="test-order2-where" type="JPQL">
      select id, name from Users
      <where>
        <if test="name != null">
          AND name = #{name}
        </if>
        <if test="cel != null">
          AND cel = #{cel}
        </if>
        <choose>
          <when test="doc != null">
            AND doc like #{doc}
          </when>
          <when test="phone != null">
            AND phone like #{phone}
          </when>
          <otherwise>
            AND status = 1
          </otherwise>
        </choose>
        <if test="age != null">
          AND age = #{age}
        </if>
      </where>
    </select>

It's very important that the order from evaluation from sentence it's sequential, SQLegance doesn't change the query order in XML file at result string mounted.

    @Test
    public void orderTestSelectWhere2() {
      Map<String, Object> p = new HashMap<String, Object>();
      Sql statement;
      p.put("name", "acme");
      p.put("cel", "99880066");
      p.put("age", "18");
        
      statement = XmlBuilderSql.getQuery("test-order2-where");
      System.out.println(statement.getSql(p));
    }


The result when run this test case is:
        
    select id, name 
    from Users 
    where 
     name = #{name} AND 
     cel = #{cel} AND 
     status = 1 AND 
     age = #{age}

## package Tag

The "package tag" permit to organize the queries in different namespaces.

    <package name="acme.com.one">
      <select id="test-pack-1" type="JPQL">
        select id, name from Users where id = #{id}
      </select>
    </package>
        
To get this query use:


    ISql sql = XmlBuilderSql.getQuery("acme.com.one.test-pack-1");  


## description Tag

Some queries is difficult to understand, so a optional `description` element can be used to clarify the intentions of the statements.

      <select id="all-users">
        <description>get all users with status equal active</description>
        select id, name from Users where status = 'A'
      </select>

Note: of course this is a easier statement, but document using `description` is a good practice



