CREATE KEYSPACE dev_whin WITH REPLICATION = {'class':'SimpleStrategy','replication_factor':1};

DROP TABLE COLOR;
DROP TABLE VEHICLE;


CREATE TABLE COLOR 
( 
  NAME text, 
  PRIORITY text, 
  CODE smallint, 
  PRIMARY KEY( NAME, PRIORITY ) 
);


CREATE TABLE VEHICLE
( 
  plate text,
  name text,
  color text,
  frequency text,
  alarms list<text>
 ,PRIMARY KEY( plate )
);

CREATE TABLE test_data 
(
   my_key text,
   evt_date timestamp,
   object_id text, 
   lat float, 
   lng float, 
   warn int
   ,PRIMARY KEY( my_key, evt_date ) 
);



CREATE TABLE sample_data
( 
  id uuid,
  a text,
  b text,
  c text,
  PRIMARY KEY(id)
);
