Title: Changelog


# Changelog


### Version 0.6.0 is not source compatible with 0.5.x  

- ![BUG Fix](images/bug_icon.png "BUG Fix") Load repository-sql.xml, SqlConfig.xml files from EJB classloader's works.

- ![BUG Fix](images/bug_icon.png "BUG Fix") Query evaluation with null parameter is null-safe now.

    IQuery q = new Query("cars");
    List&lt;Car&gt; list = repository.get(q);

if query had a dynamic clause an exception was thrown

- ![BUG Fix](images/bug_icon.png "BUG Fix") XSD schema build a wrong nested tags auto-complete and validation. Fixed version http://jkniv.sf.net/schema/sqlegance/sqlegance-0.5.1.xsd

- ![Update](images/update_icon.png "Update") Add hint attribute at `select`, `insert`, `update` and `delete` tags
- ![Update](images/update_icon.png "Update") Add timeout attribute at `select`, `insert`, `update` and `delete` tags.
- ![Update](images/update_icon.png "Update") Add batch attribute at `select`, `insert`, `update` and `delete` tags.
- ![Update](images/update_icon.png "Update") Add isolation level attribute at `select`, `insert`, `update` and `delete` tags.



### Changes between 0.5.0 and 0.5.0.1>

- ![BUG Fix](images/bug_icon.png "BUG Fix") Query using functions with colon `:` generate error at parser.

    select id, name from Roles where dt = to_date(:dt,'YYYY-MM-DD HH24:MI:SS') and name = :name and status = :status

    `'YYYY-MM-DD HH24:MI:SS'` error was fixed at parser.

### Changes between 0.0.29 and 0.5.0

- ![BUG Fix](images/bug_icon.png "BUG Fix") [xi:include] - The tag `xi:include` isn't supported. Gone out `xi` namespace, now it's just `&lt;include`. That's incompatible with before version.

Before:


    <statements ..
     xmlns:xi="http://www.w3.org/2001/XInclude"

    <xi:include href="/my-other-sql-file.xml" />
     ...

Now:


    <statements ..
    <include href="/my-other-sql-file.xml" />
    ...

    
- ![BUG Fix](images/bug_icon.png "BUG Fix") [where tag] - SQLException if clause start different from and/or in sensitive case, SQL isn't sensitive.


    <where>
      <if test="name != null">
       AND name = #{name}
      </if>
    ...

- ![Update](images/update_icon.png "Update") Add supports to [choose/when/otherwise] tags
- ![Update](images/update_icon.png "Update") Add supports to namespaces with [package] tag.

