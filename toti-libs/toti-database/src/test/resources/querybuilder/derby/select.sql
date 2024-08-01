CREATE TABLE select_table (
	id INT,
	name VARCHAR(50)
);

CREATE TABLE joined_table (
	id INT,
	name VARCHAR(50),
	a_id INT
);

INSERT INTO select_table VALUES (1, 'name_a');
INSERT INTO select_table VALUES (2, 'name 2');
INSERT INTO select_table VALUES (3, 'name 3');
INSERT INTO select_table VALUES (4, 'name_a');
INSERT INTO select_table VALUES (5, 'name_5');
INSERT INTO select_table VALUES (6, 'name_6');
		
INSERT INTO joined_table VALUES (1, 'name 1', 1);
INSERT INTO joined_table VALUES (2, 'name_2', 1);
INSERT INTO joined_table VALUES (3, 'name_b', 2);
INSERT INTO joined_table VALUES (4, 'name 3', 3);
INSERT INTO joined_table VALUES (5, 'name 5', 4);
INSERT INTO joined_table VALUES (6, 'name 6', 4);
INSERT INTO joined_table VALUES (7, 'name 7', 4);
INSERT INTO joined_table VALUES (8, 'name 8', 5);
INSERT INTO joined_table VALUES (9, 'name 8', 6);
