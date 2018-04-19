 
 This is a sample properties file for the org.eclipse.jetty.security.JDBCLoginService
 implemtation of the UserRealm interface.  This allows Jetty users authentication 
 to work from a database.

+-------+      +------------+      +-------+
| users |      | user_roles |      | roles |
+-------+      +------------+      +-------+
| id    |     /| user_id    |\     | id    |
| user  -------| role_id    |------- role  |
| pwd   |     \|            |/     |       |
+-------+      +------------+      +-------+
   