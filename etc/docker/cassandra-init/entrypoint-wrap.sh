#!/bin/bash

echo ""
echo "Starting docker entry point for Cassandra"
echo ""

sh /docker-entrypoint-initdb.d/entrypoint-load-data.sh &

exec /docker-entrypoint.sh "$@"
