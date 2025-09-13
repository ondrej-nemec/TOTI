#!/bin/bash
# run SQL Server in background
/opt/mssql/bin/sqlservr &

echo "Waiting for SQL Server to start..."
sleep 20

# run all .sql scripts
for f in /init-db/*.sql
do
  echo "Running $f"
  /opt/mssql-tools/bin/sqlcmd -S localhost -U SA -P "$SA_PASSWORD" -i $f
done

# let container running
wait
