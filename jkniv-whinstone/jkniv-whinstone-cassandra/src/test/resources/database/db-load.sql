DELETE FROM COLOR;
DELETE FROM VEHICLE;

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
