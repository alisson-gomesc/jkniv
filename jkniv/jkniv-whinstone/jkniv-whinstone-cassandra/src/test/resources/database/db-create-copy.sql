DROP TABLE BOOK;
DROP TABLE AUTHOR;
DROP TABLE COLOR;
DROP TABLE MY_ALL_TYPES;
DROP SEQUENCE SEQ_AUTHOR_ID;
DROP SCHEMA APP;
DROP SCHEMA ADMIN;

CREATE SCHEMA ADMIN;
SET SCHEMA ADMIN;


CREATE SEQUENCE SEQ_AUTHOR_ID;

CREATE TABLE AUTHOR 
( 
  ID BIGINT NOT NULL, 
  NAME VARCHAR(255), 
  PRIMARY KEY (ID)
);

CREATE TABLE BOOK 
(    
  ID BIGINT NOT NULL, 
  ISBN VARCHAR(255), 
  NAME VARCHAR(255), 
  AUTHOR_ID BIGINT, 
  PRIMARY KEY (ID)
);

CREATE TABLE COLOR
( 
  CODE BIGINT NOT NULL, 
  NAME VARCHAR(20),
  PRIORITY VARCHAR(20)
);


CREATE TABLE FOO 
(
  ID BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1000, INCREMENT BY 1), 
  NAME VARCHAR(255) 
);



CREATE TABLE MY_TYPES
( 
  ID BIGINT NOT NULL,
  MY_SMALLINT   SMALLINT ,
  MY_INTEGER    INTEGER ,
  MY_BIGINT     BIGINT ,
  MY_FLOAT      FLOAT ,
  MY_DECIMAL    DECIMAL ,
  MY_VARCHAR    VARCHAR(30) ,
  MY_CHAR       CHAR(10) ,
  MY_BLOB       BLOB,
  MY_CLOB       CLOB,
  MY_DATE       DATE ,
  MY_TIME       TIME ,
  MY_TIMESTAMP  TIMESTAMP 
);
