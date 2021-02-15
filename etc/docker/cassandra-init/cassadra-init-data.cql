
use whinstone;

DROP TABLE IF EXISTS COLOR;
DROP TABLE IF EXISTS VEHICLE;
DROP TABLE IF EXISTS TEST_DATA;
DROP TABLE IF EXISTS SAMPLE_DATA;

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
  alarms list<text>,
  PRIMARY KEY( plate )
);

CREATE TABLE TEST_DATA 
(
   my_key text,
   evt_date timestamp,
   object_id text, 
   lat float, 
   lng float, 
   warn int,
   PRIMARY KEY( my_key, evt_date ) 
);



CREATE TABLE SAMPLE_DATA
( 
  id uuid,
  a text,
  b text,
  c text,
  PRIMARY KEY(id)
);


INSERT INTO COLOR (NAME, PRIORITY, CODE) VALUES ('blue', 'HIGH', 100);
INSERT INTO COLOR (NAME, PRIORITY, CODE) VALUES ('blue', 'NORMAL', 101);
INSERT INTO COLOR (NAME, PRIORITY, CODE) VALUES ('blue', 'LOW', 102);
INSERT INTO COLOR (NAME, PRIORITY, CODE) VALUES ('black', 'HIGH', 200);
INSERT INTO COLOR (NAME, PRIORITY, CODE) VALUES ('black', 'NORMAL', 201);
INSERT INTO COLOR (NAME, PRIORITY, CODE) VALUES ('black', 'LOW', 202);
INSERT INTO COLOR (NAME, PRIORITY, CODE) VALUES ('white', 'HIGH', 300);
INSERT INTO COLOR (NAME, PRIORITY, CODE) VALUES ('white', 'NORMAL', 301);
INSERT INTO COLOR (NAME, PRIORITY, CODE) VALUES ('white', 'LOW', 302);


INSERT INTO VEHICLE(plate, name, color, alarms, frequency) VALUES ('OMN7176', 'bugatti', 'white', ['anchor','over_speed'], 'DAILY');
INSERT INTO VEHICLE(plate, name, color, alarms, frequency) VALUES ('OMN7000', 'fusca', 'white', ['anchor'], 'DAILY');
INSERT INTO VEHICLE(plate, name, color, frequency) VALUES ('OMN7001', 'mustang', 'blue', 'DAILY');
INSERT INTO VEHICLE(plate, name, color, frequency) VALUES ('OMN7002', 'mustang', 'black', 'DAILY');
