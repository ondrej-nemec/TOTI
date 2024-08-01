CREATE TABLE alter_table (
	primary_id INT primary key,
	id INT,
	name VARCHAR(50),
	fKey INT
	--Index myIndex (fKey)
);
CREATE INDEX myIndex ON alter_table (fKey);

CREATE TABLE table_fk (
	id INT primary key
	-- Index myIndex2 (id)
);
