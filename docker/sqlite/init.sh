#!/bin/sh
set -e

for f in /docker-entrypoint-initdb.d/*.sql; do
    filename=$(basename "$f" .sql)
    dbfile="/root/db/${filename}.db"

    if [ ! -f "$dbfile" ]; then
        echo "Creating database $dbfile from $f"
        sqlite3 "$dbfile" < "$f"
    else
        echo "Database $dbfile already exists, skipping..."
    fi
#    echo "Running $f"
#    filename=$(basename "$f" .sql)
#    sqlite3 "/root/db/${filename}.db" < "$f"
done

tail -f /dev/null
