CREATE TABLE table1 (
	id INT,
	name VARCHAR(50)
);

CREATE TABLE table2 (
	id INT
);

INSERT INTO table1
	VALUES
		(1, 'name_1'),
		(2, 'name_2'),
		(3, 'name_3'),
		(4, 'name_4');
		
INSERT INTO table2
	VALUES
		(1),
		(2),
		(3),
		(4);
		
CREATE VIEW test_view AS SELECT * FROM table1