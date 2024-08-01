CREATE TABLE alter_table (
	id INT,
	name VARCHAR(50),
	fKey INT
);
CREATE INDEX fkeyIndex ON alter_table(fKey);

CREATE TABLE table_fk (
	id INT unique
);
CREATE INDEX idIndex ON table_fk(id);