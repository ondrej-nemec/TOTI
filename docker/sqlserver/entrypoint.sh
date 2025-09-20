#!/bin/bash

# run SQL Server in background
/opt/mssql/bin/sqlservr &

echo "Waiting for SQL Server to start..."
# sleep 20
until /opt/mssql-tools/bin/sqlcmd -S localhost -U SA -P "$SA_PASSWORD" -Q "SELECT 1" &> /dev/null
do
    echo "SQL Server is starting..."
    sleep 2
done

# run all .sql scripts
for f in /init-db/*.sql
do
  echo "Running $f"
  /opt/mssql-tools/bin/sqlcmd -S localhost -U SA -P "$SA_PASSWORD" -i $f
done

# let container running
wait
