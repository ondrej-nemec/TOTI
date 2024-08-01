CREATE TABLE table1 (
	id INT,
	name VARCHAR(50)
);

CREATE TABLE table2 (
	id INT
);

INSERT INTO table1 VALUES (1, 'name_1');
INSERT INTO table1 VALUES (2, 'name_2');
INSERT INTO table1 VALUES (3, 'name_3');
INSERT INTO table1 VALUES (4, 'name_4');
		
INSERT INTO table2 VALUES (1);
INSERT INTO table2 VALUES (2);
INSERT INTO table2 VALUES (3);
INSERT INTO table2 VALUES (4);
		
CREATE VIEW test_view AS SELECT * FROM table1