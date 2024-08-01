CREATE TABLE alter_table (
	id INT,
	name VARCHAR(50),
	fKey INT(11),
	Index (fKey)
);

CREATE TABLE table_fk (
	id INT(11),
	Index (id)
);