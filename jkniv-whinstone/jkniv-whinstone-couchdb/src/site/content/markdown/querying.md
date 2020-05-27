Title: Whinstone Querying


# Querying database

Whinstone use `jkniv-sqlegance` to handle the query language for your database. The philosophy behind `jkniv-whinstone` it's keeping the java code simple, without SQL manipulation, all [SQL is handler by XML files][1].


# CouchDB

Into `jkniv-whinstone-couchdb` there are queries that are built-in, or else, you don't need write them:

- `_all_docs`
- `get`
- `add`
- `update`
- `remove`


## _find


## _views


## _bulk



[1]: http://jkniv.sourceforge.net/jkniv-sqlegance/index.html "SQL is handler by XML files"