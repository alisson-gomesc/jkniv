#!/bin/bash

echo ""
echo "STARTING DOCKER ENTRY POINT"
echo ""
# Create default keyspace for single node cluster
CQL="CREATE KEYSPACE IF NOT EXISTS whinstone WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1};"
until echo $CQL | cqlsh; do
  echo "cqlsh: Cassandra is unavailable - retry later"
  sleep 2
done &

exec /docker-entrypoint.sh "$@"