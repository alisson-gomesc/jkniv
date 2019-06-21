DROP TABLE COLOR;
DROP TABLE VEHICLE;


CREATE TABLE COLOR
( 
  NAME text,
  PRIORITY text,
  CODE integer, 
 ,PRIMARY KEY( NAME, PRIORITY )
);


CREATE TABLE VEHICLE
( 
  plate text,
  name text,
  color text,
  alarms list<text>
 ,PRIMARY KEY( plate )
);
