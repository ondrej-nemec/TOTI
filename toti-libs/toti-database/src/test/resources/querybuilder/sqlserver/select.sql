CREATE TABLE select_table (
	id INT,
	name VARCHAR(50)
);

CREATE TABLE joined_table (
	id INT,
	name VARCHAR(50),
	a_id INT
);

INSERT INTO select_table
	VALUES
		(1, 'name_a'),
		(2, 'name 2'),
		(3, 'name 3'),
		(4, 'name_a'),
		(5, 'name_5'),
		(6, 'name_6');
		
INSERT INTO joined_table
	VALUES
		(1, 'name 1', 1),
		(2, 'name_2', 1),
		(3, 'name_b', 2),
		(4, 'name 3', 3),
		(5, 'name 5', 4),
		(6, 'name 6', 4),
		(7, 'name 7', 4),
		(8, 'name 8', 5),
		(9, 'name 8', 6);