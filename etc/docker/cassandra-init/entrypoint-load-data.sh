#!/bin/bash

echo "Waiting for cassandra"
echo ""

# Create default keyspace for single node cluster
CQL="CREATE KEYSPACE IF NOT EXISTS whinstone WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1};"
until echo $CQL | cqlsh; do
  echo "cqlsh: Cassandra is unavailable - retry later"
  sleep 2
done

echo ""
echo "loading data into Cassandra"

cqlsh -f /docker-entrypoint-initdb.d/cassadra-init-data.cql


