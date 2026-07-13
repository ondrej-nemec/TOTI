-- postgresql
-- databases
SELECT datname FROM pg_database
-- database schemas - musi by vybrana databaze
SET search_path TO your_db_name;
SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA
-- tables
SELECT table_name, table_type FROM INFORMATION_SCHEMA.TABLES where table_schema = ''
-- columns 
SELECT column_name, data_type, column_default, is_generated, is_nullable FROM INFORMATION_SCHEMA.COLUMNS 

-- mysql
-- schemas
SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA
-- tables
SELECT table_name, table_type FROM INFORMATION_SCHEMA.TABLES
--columns
SELECT column_name, data_type, column_default, extra LiKE '%auto_increment%' is_generated, is_nullable FROM INFORMATION_SCHEMA.COLUMNS 

-- derby
