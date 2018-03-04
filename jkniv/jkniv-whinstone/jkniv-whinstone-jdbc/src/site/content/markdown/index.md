Title: whinstone jdbc

jkniv whinstone jdbc
--------------------


The Repository implementation for direct access JDBC™ API. 

Version 0.6.0 will be release soon with:

- Supports local transaction ![x](check.png)
- Supports Spring transaction  ![x](check.png)
- Supports paginate query for Derby, Oracle, PostgreSQL ![x](check.png)
- Supports SQL parameters as Map, POJO ![x](check.png)
- Supports query result with POJO ![x](check.png)
- Supports query result with nested POJO objects (rustic supports, needs improve)

Roadmap to Version 0.7.0.

- Supports JTA transaction
- Supports batch SQL
- Supports one-to-many association
- Supports paginate query for SQL Server, MySQL, DB2/AS400... 
- Supports clause IN to SQL


A documentation will be prepared to 0.6.0 release.


### Relationships type supported

| Relationships  | Supports | Version|
| -------------- | -------- |--------|
|One-to-One      |   yes    | 0.6.0  |
|One-to-Many     |   yes    | 0.6.0  |
|Many-to-One     |   not    |   -    |


