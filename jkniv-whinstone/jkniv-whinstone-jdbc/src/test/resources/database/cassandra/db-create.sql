create keyspace dev_cas with replication = {'class':'SimpleStrategy','replication_factor':1};

CREATE TABLE dev_cas.TEST_DATA (
   my_key text
  ,evt_date TIMESTAMP
  ,object_id text
  ,lat FLOAT
  ,lng FLOAT
  ,warn int
 ,PRIMARY KEY( my_key,evt_date)
)
WITH CLUSTERING ORDER BY (evt_date desc);

INSERT INTO dev_cas.TEST_DATA (my_key,evt_date,object_id,lat, lng, warn)
values ('k001','2018-02-07 14:07:00', 'CAR001',20.683940,-88.567740,2);
