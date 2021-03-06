CREATE TABLE JPA_AUTHOR
( 
  "ID" NUMBER(19) NOT NULL, 
  "NAME" VARCHAR(255), 
  PRIMARY KEY ("ID")
);

CREATE TABLE JPA_BOOK
(
  "ID" NUMBER(19) NOT NULL, 
  "ISBN" VARCHAR(255), 
  "NAME" VARCHAR(255), 
  "AUTHOR_ID" NUMBER(19), 
  PRIMARY KEY ("ID")
);

CREATE TABLE JPA_FOO
(
  ID NUMBER(19) NOT NULL, 
  NAME VARCHAR(50) ,
  PRIMARY KEY ("ID")
);

CREATE TABLE JPA_COLOR
( 
  "ID" NUMBER(19) NOT NULL, 
  "CODE" NUMBER(14) NOT NULL, 
  "NAME" VARCHAR(20),
  "PRIORITY" VARCHAR(20)
);


CREATE TABLE JPA_MY_DATES
(
  ID NUMBER(19) NOT NULL, 
  NAME VARCHAR(30) ,
  DURATION_DAY INTERVAL DAY TO SECOND DEFAULT INTERVAL '0' DAY NOT NULL,
  DURATION_YEAR INTERVAL YEAR TO MONTH DEFAULT INTERVAL '0' YEAR NOT NULL,
  INIT DATE DEFAULT CURRENT_TIMESTAMP NOT NULL,
  INIT_TS TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  INIT_TSZ TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
  INIT_TSL TIMESTAMP WITH LOCAL TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
  PRIMARY KEY ("ID")
);

CREATE SEQUENCE  "JPA_SEQ_FOO"  MINVALUE 1000 MAXVALUE 9000 INCREMENT BY 1 START WITH 1000 NOCACHE  NOORDER  NOCYCLE ;

