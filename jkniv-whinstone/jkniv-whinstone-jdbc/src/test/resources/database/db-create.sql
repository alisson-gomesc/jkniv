----CREATE SCHEMA derbwhinstone;
----SET SCHEMA derbwhinstone;

--CREATE SEQUENCE SEQ_AUTHOR_ID;

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
  VISUALIZATION INTEGER DEFAULT 0,
  IN_STOCK CHAR(1),
  PRIMARY KEY (ID)
);

CREATE TABLE COLOR
( 
  CODE BIGINT NOT NULL, 
  NAME VARCHAR(20),
  PRIORITY VARCHAR(20)
);

CREATE TABLE ACCOUNT
( 
  NAME VARCHAR(20),
  BALANCE INTEGER
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
  MY_FLOAT      FLOAT(23) ,
  MY_DECIMAL    DECIMAL(9,5) ,
  MY_VARCHAR    VARCHAR(30) ,
  MY_CHAR       CHAR(10) ,
  MY_BLOB       BLOB,
  MY_CLOB       CLOB,
  MY_DATE       DATE ,
  MY_TIME       TIME ,
  MY_TIMESTAMP  TIMESTAMP,
  MY_BOOL_CHAR  CHAR(1),
  MY_BOOL_CHAR_OVERRIDE  VARCHAR(3),
  MY_DATE_INT   INTEGER,
  TIME_UNIT_1   varchar(10),
  TIME_UNIT_2   INTEGER    
);

CREATE TABLE ITEMS
( 
  ID BIGINT NOT NULL, 
  NAME VARCHAR(255),
  CODE INTEGER,  
  PRICE FLOAT(23),
  PRIMARY KEY (ID)
);