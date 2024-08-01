create table table_to_delete (
	id int
);

create table table_for_index (
	id int PRIMARY KEY,
	name varchar(10)
);
create index index_to_delete ON table_for_index(id);

create table table_to_alter (
	id int,
	Column_to_modify_type int,
	Column_to_rename int,
	Column_to_delete int,
	CONSTRAINT FK_to_delete FOREIGN KEY (Column_to_delete) REFERENCES table_for_index(id)
);

create view view_to_delete AS select 1 as a;
create view view_to_alter AS select 1 as a;


create table table_for_functions (
	id int,
	name varchar(10)
);

insert into table_for_functions (id, name) VALUES
(1, 'Item 1'),
(2, 'Item 2'),
(3, 'Item 3'),
(4, 'Item 4'),
(5, 'Item 5');

/***************************/


create table table_1 (
	id int,
	name varchar(10),
	typ char(1)
);
create table table_2 (
	id int,
	name varchar(10),
	typ char(1)
);
create table table_3 (
	id int,
	name varchar(10),
	typ char(1)
);
create table table_4 (
	id int,
	name varchar(10),
	typ char(1)
);
create table table_5 (
	id int,
	name varchar(10),
	typ char(1)
);
create table table_6 (
	id int,
	name varchar(10),
	typ char(1)
);
create table table_7 (
	id int,
	name varchar(10),
	typ char(1)
);


insert into table_1 (id, name, typ) VALUES
(1, 'Item 1', 'A'),
(2, 'Item 2', 'A'),
(3, 'Item 3', 'A'),
(4, 'Item 4', 'A'),
(5, 'Item 5', 'A');
insert into table_2 (id, name, typ) VALUES
(1, 'Item 1', 'B'),
(2, 'Item 2', 'B'),
(3, 'Item 3', 'B'),
(4, 'Item 4', 'B'),
(5, 'Item 5', 'B');
insert into table_3 (id, name, typ) VALUES
(1, 'Item 1', 'C'),
(2, 'Item 2', 'C'),
(3, 'Item 3', 'C'),
(4, 'Item 4', 'C'),
(5, 'Item 5', 'C');
insert into table_4 (id, name, typ) VALUES
(1, 'Item 1', 'D'),
(2, 'Item 2', 'D'),
(3, 'Item 3', 'D'),
(4, 'Item 4', 'D'),
(5, 'Item 5', 'D');
insert into table_5 (id, name, typ) VALUES
(1, 'Item 1', 'E'),
(2, 'Item 2', 'E'),
(3, 'Item 3', 'E'),
(4, 'Item 4', 'E'),
(5, 'Item 5', 'E');
insert into table_6 (id, name, typ) VALUES
(1, 'Item 1', 'F'),
(2, 'Item 2', 'F'),
(3, 'Item 3', 'F'),
(4, 'Item 4', 'F'),
(5, 'Item 5', 'F');
insert into table_7 (id, name, typ) VALUES
(1, 'Item 1', 'F'),
(2, 'Item 2', 'F'),
(3, 'Item 3', 'F'),
(4, 'Item 4', 'F'),
(5, 'Item 5', 'F');