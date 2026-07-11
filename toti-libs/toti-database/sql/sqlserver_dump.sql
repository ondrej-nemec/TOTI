create table escape_table (
	col_bool bit null,
	col_int int null,
	col_float float null,
	col_char char(3) null,
	col_string varchar(5) null,
	col_text text null,
	col_time time(6) null,
	col_date date null,
	col_datetime datetime2(6) null,
	col_datetime_zoned datetimeoffset(6) null
);

-- separator --

create table table_to_delete (
	id int
);

-- separator --

create table table_to_rename (
	id int
);

-- separator --

create table table_for_index_1 (
	id int PRIMARY KEY,
	name varchar(10)
);

-- separator --

create table table_for_index_2 (
	id int PRIMARY KEY,
	name varchar(10)
);

-- separator --

create table table_for_index_3 (
	id int PRIMARY KEY,
	name varchar(10)
);

-- separator --

create table table_for_index_4 (
	id int PRIMARY KEY,
	name varchar(10)
);

-- separator --

create index index_to_delete ON table_for_index_1(id);

-- separator --

create table table_to_alter (
	id int,
	Column_to_modify_1 int NOT NULL
		CONSTRAINT UQ_table_to_alter_Column_to_modify_1 UNIQUE
		CONSTRAINT DF_table_to_alter_Column_to_modify_1 DEFAULT 0,
	Column_to_modify_2 int NULL CONSTRAINT DF_table_to_alter_Column_to_modify_2 DEFAULT 0,
	Column_to_modify_3 char,
	Column_to_rename int,
	Column_to_delete int,
	CONSTRAINT FK_id FOREIGN KEY (id) REFERENCES table_for_index_1(id),
	CONSTRAINT FK_to_delete FOREIGN KEY (id) REFERENCES table_for_index_1(id)
);

-- separator --

create table table_to_alter_2 (
	id int,
	Column_to_rename int,
	Column_to_delete int
);

-- separator --

create view view_to_delete AS select 1 as a;

-- separator --

create view view_to_alter AS select 1 as a;

-- separator --

create table table_for_functions (
	id int,
	name varchar(10)
);

-- separator --

insert into table_for_functions (id, name) VALUES
(1, 'Item 1'),
(2, 'Item 2'),
(3, 'Item 3'),
(4, 'Item 4'),
(5, 'Item 5');

-- separator --

create table table_1 (
	id int,
	name varchar(10),
	typ char(1)
);

-- separator --

create table table_2 (
	id int,
	name varchar(10),
	typ char(1)
);

-- separator --

create table table_3 (
	id int,
	name varchar(10),
	typ char(1)
);

-- separator --

create table table_4 (
	id int,
	name varchar(10),
	typ char(1)
);

-- separator --

create table table_5 (
	id int,
	name varchar(10),
	typ char(1)
);

-- separator --

create table table_6 (
	id int,
	name varchar(10),
	typ char(1)
);

-- separator --

create table table_7 (
	id int,
	name varchar(10),
	typ char(1)
);

-- separator --

create table table_ai (
	id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,
	name varchar(10),
	typ char(1)
);

-- separator --


insert into table_1 (id, name, typ) VALUES
(1, 'Item 1', 'A'),
(2, 'Item 2', 'A'),
(3, 'Item 3', 'A'),
(4, 'Item 4', 'A'),
(5, 'Item 5', 'A');

-- separator --

insert into table_2 (id, name, typ) VALUES
(1, 'Item 1', 'B'),
(2, 'Item 2', 'B'),
(3, 'Item 3', 'B'),
(4, 'Item 4', 'B'),
(5, 'Item 5', 'B');

-- separator --

insert into table_3 (id, name, typ) VALUES
(1, 'Item 1', 'C'),
(2, 'Item 2', 'C'),
(3, 'Item 3', 'C'),
(4, 'Item 4', 'C'),
(5, 'Item 5', 'C');

-- separator --

insert into table_4 (id, name, typ) VALUES
(1, 'Item 1', 'D'),
(2, 'Item 2', 'D'),
(3, 'Item 3', 'D'),
(4, 'Item 4', 'D'),
(5, 'Item 5', 'D');

-- separator --

insert into table_5 (id, name, typ) VALUES
(1, 'Item 1', 'E'),
(2, 'Item 2', 'E'),
(3, 'Item 3', 'E'),
(4, 'Item 4', 'E'),
(5, 'Item 5', 'E');

-- separator --

insert into table_6 (id, name, typ) VALUES
(1, 'Item 1', 'F'),
(2, 'Item 2', 'F'),
(3, 'Item 3', 'F'),
(4, 'Item 4', 'F'),
(5, 'Item 5', 'F');

-- separator --

insert into table_7 (id, name, typ) VALUES
(1, 'Item 1', 'F'),
(2, 'Item 2', 'F'),
(3, 'Item 3', 'F'),
(4, 'Item 4', 'F'),
(5, 'Item 5', 'F');

-- separator --

CREATE PROCEDURE procedure_int(
	@i_string varchar(20),
	@o_string varchar(20) OUTPUT,
	@i_integer int,
	@o_integer int OUTPUT,
	@i_bool bit
)
AS
BEGIN
  set @o_string = 'something'
  set @o_integer = 42
  return 1
END;

-- separator --

CREATE PROCEDURE procedure_void(
	@i_string varchar(20),
	@o_string varchar(20) OUTPUT,
	@i_integer int,
	@o_integer int OUTPUT,
	@i_bool bit
)
AS
BEGIN
  set @o_string = 'something'
  set @o_integer = 42
  -- return
END;