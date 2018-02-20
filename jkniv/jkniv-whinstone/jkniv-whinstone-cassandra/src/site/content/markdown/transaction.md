Title: Transaction

Whinstone JDBC Transaction
--------------------

`jkniv-whinstone-jdbc 0.6.0` just support local transaction. This means that a single connection performs all the work of the transaction and that the connection can only work on one transaction at a time. When all the work for that transaction has been completed or has failed, commit or rollback must be called to make the work permanent, and a new transaction can begin.
