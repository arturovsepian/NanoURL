CREATE KEYSPACE nanospace WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 3};

CREATE TABLE nanourl (
  shorturl TEXT,
  longurl TEXT,
  createtime TIMESTAMP,
  PRIMARY KEY((shorturl), createtime)
) WITH CLUSTERING ORDER BY (createtime DESC);
