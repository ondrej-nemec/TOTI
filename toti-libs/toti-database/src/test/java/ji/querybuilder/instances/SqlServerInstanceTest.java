package ji.querybuilder.instances;

import java.sql.Connection;
import java.sql.SQLException;

import ji.database.Connections;

public class SqlServerInstanceTest extends AbstractInstanceTest {

	public SqlServerInstanceTest() {
		super(new SqlServerQueryBuilder());
	}

	@Override
	protected String getCreateTable(boolean withRestrict) {
		if (withRestrict) {
			return null; // restrict not supported
		}
		return "CREATE TABLE create_table ("
			+ "Primary_column INT IDENTITY(1,1) NOT NULL,"
			+ " Unique_column INT UNIQUE,"
			+ " Nullable_column INT NULL DEFAULT 42,"
			+ " Bool_column BIT,"
			+ " Float_column FLOAT,"
			+ " Double_column FLOAT,"
			+ " Char_column CHAR(1),"
			+ " Text_column TEXT,"
			+ " String_column VARCHAR(10),"
			+ " Time_column TIME,"
			+ " Time2_column TIME(6),"
			+ " Date_column DATE,"
			+ " DateTime_column DATETIME2,"
			+ " DateTime2_column DATETIME2(6),"
			+ " DateTime_Zoned_column DATETIMEOFFSET,"
			+ " DateTime_Zoned2_column DATETIMEOFFSET(6),"
			+ " FK_column_1 INT,"
			+ " FK_column_2 INT,"
			+ " FK_column_3 INT,"
			+ " FK_column_4 INT,"
			+ " PRIMARY KEY (Primary_column),"
			+ " CONSTRAINT FK_FK_column_1 FOREIGN KEY (FK_column_1) REFERENCES table_for_index_1(id),"
			+ " CONSTRAINT FK_FK_column_2 FOREIGN KEY (FK_column_2) REFERENCES table_for_index_2(id) ON DELETE CASCADE ON UPDATE NO ACTION,"
			+ " CONSTRAINT FK_FK_column_3 FOREIGN KEY (FK_column_3) REFERENCES table_for_index_3(id) ON DELETE SET NULL ON UPDATE SET DEFAULT,"
			+ " CONSTRAINT FK_FK_column_4 FOREIGN KEY (FK_column_4) REFERENCES table_for_index_4(id) ON DELETE SET NULL"
		+ ")";
	}

	@Override
	protected String getCreateTableWithPrimary() {
		return "CREATE TABLE create_table ("
			+ "Primary_column_1 INT,"
			+ " Primary_column_2 INT,"
			+ " Primary_column_3 INT,"
			+ " PRIMARY KEY (Primary_column_1, Primary_column_2, Primary_column_3),"
			+ " CONSTRAINT FK_Primary_column_3 FOREIGN KEY (Primary_column_3) REFERENCES table_for_index_1(id)"
		+ ")";
	}

	@Override
	protected String getAlterTable(boolean full) {
		if (!full) {
			return "ALTER TABLE table_to_alter"
				+ " ADD Add_column_1 INT NOT NULL;"
				+ "ALTER TABLE table_to_alter"
				+ " DROP COLUMN Column_to_delete;"
				+ "EXEC sp_rename 'table_to_alter.Column_to_rename', 'Renamed_column', 'COLUMN'";
		}
		return "ALTER TABLE table_to_alter"
			+ " ADD Add_column_1 INT NOT NULL,"
			+ " Add_column_2 INT NULL UNIQUE DEFAULT 42;"

			+ "ALTER TABLE table_to_alter"
			+ " ALTER COLUMN Column_to_modify_1 FLOAT;"

			+ "ALTER TABLE table_to_alter"
			+ " ALTER COLUMN Column_to_modify_2 FLOAT;"
			
			// add default
			+ "ALTER TABLE table_to_alter"
			+ " ADD CONSTRAINT DF_table_to_alter_Column_to_modify_1 DEFAULT 5 FOR Column_to_modify_1;"
			
			// remove default
			+ "ALTER TABLE table_to_alter"
			+ " DROP CONSTRAINT DF_table_to_alter_Column_to_modify_2;"
			
			+ "ALTER TABLE table_to_alter"
			+ " ALTER COLUMN Column_to_modify_1 INT NOT NULL;"
			
			+ "ALTER TABLE table_to_alter"
			+ " ALTER COLUMN Column_to_modify_2 INT NOT NULL;"
			
			+ "ALTER TABLE table_to_alter"
			+ " ADD CONSTRAINT FK_Add_column_1 FOREIGN KEY (Add_column_1) REFERENCES table_for_index_1(id),"
			+ " CONSTRAINT FK_Add_column_2 FOREIGN KEY (Add_column_2)"
				+ " REFERENCES table_for_index_2(id) ON DELETE CASCADE ON UPDATE NO ACTION,"
			+ " CONSTRAINT table_to_alter_column_to_modify_2_key UNIQUE (Column_to_modify_2);"
			
			+ "ALTER TABLE table_to_alter"
			+ " DROP COLUMN Column_to_delete;"
			
			+ "ALTER TABLE table_to_alter DROP CONSTRAINT FK_to_delete;"

			+ "ALTER TABLE table_to_alter ADD CONSTRAINT table_to_alter_column_to_modify_1_key UNIQUE (Column_to_modify_1);"
		
			+ "EXEC sp_rename 'table_to_alter.Column_to_rename', 'Renamed_column', 'COLUMN'"
			;
	}

	@Override
	protected String getAlterTableRenameTable() {
		return "EXEC sp_rename 'table_to_rename', 'table_with_another_name'";
	}

	@Override
	protected String getDeleteTable() {
		return "DROP TABLE table_to_delete";
	}

	@Override
	protected String getCreateView_fromString(boolean create) {
		return "CREATE VIEW some_view_1 AS SELECT 1 as A";
	}

	@Override
	protected String getCreateView_fromSelect(boolean create) {
		return "CREATE VIEW some_view_2 AS SELECT A FROM (SELECT 1 AS A) AS a";
	}

	@Override
	protected String getCreateView_fromMultiSelect(boolean create) {
		return "CREATE VIEW some_view_3 AS"
			+ " SELECT A"
			+ " FROM ("
				+ "SELECT 1 AS A"
				+ " UNION"
				+ " SELECT 2 AS A"
			+ ") AS a";
	}

	@Override
	protected String getCreateView(boolean create) {
		return "CREATE VIEW some_view_4 AS"
			+ " SELECT t1.id, t1.name, MAX(t1.id) as max_id"
			+ " FROM table_1 AS t1"
			
			+ " JOIN table_2 ON t1.id = table_2.id"
			+ " LEFT JOIN table_3 AS t3 ON t1.id = t3.id"
			+ " RIGHT JOIN (SELECT * FROM table_4) AS st4 ON t3.id = st4.id"
			
			+ " JOIN table_5 ON t1.id = table_5.id"
			+ " LEFT JOIN table_6 AS t6 ON t1.id = t6.id"
			+ " RIGHT JOIN (SELECT * FROM table_7) AS st7 ON t1.id = st7.id"
			+ " WHERE (t1.id = 1) OR (t1.id != 1) AND (t1.id = table_2.id) OR (t1.id = t6.id)"
			+ " GROUP BY t1.id, t1.name"
			+ (
				create
					? " HAVING 'AAAA' != 'BBBB' AND MAX(t1.id) < 10"
					: " HAVING :a != :b AND MAX(t1.id) < :max_id"
			)
			+ " ORDER BY t1.id, MAX(t1.id)"
			+ " OFFSET 15 ROWS FETCH NEXT 10 ROWS ONLY";
	}

	@Override
	protected String getAlterView_fromString(boolean create) {
		return "ALTER VIEW view_to_alter AS SELECT id FROM table_1";
	}

	@Override
	protected String getAlterView_fromStringAlias(boolean create) {
		return "ALTER VIEW view_to_alter AS SELECT id FROM table_1 AS a";
	}

	@Override
	protected String getAlterView_fromSelect(boolean create) {
		return "ALTER VIEW view_to_alter AS SELECT A FROM (SELECT 1 AS A) AS a";
	}

	@Override
	protected String getAlterView_fromMultiSelect(boolean create) {
		return "ALTER VIEW view_to_alter AS"
			+ " SELECT A"
			+ " FROM ("
				+ "SELECT 1 AS A"
				+ " UNION"
				+ " SELECT 2 AS A"
			+ ") AS a";
	}

	@Override
	protected String getAlterView(boolean create) {
		return "ALTER VIEW view_to_alter AS"
			+ " SELECT t1.id, t1.name, MAX(t1.id) as max_id"
			+ " FROM table_1 AS t1"
			
			+ " JOIN table_2 ON t1.id = table_2.id"
			+ " LEFT JOIN table_3 AS t3 ON t1.id = t3.id"
			+ " RIGHT JOIN (SELECT * FROM table_4) AS st4 ON t3.id = st4.id"
			
			+ " JOIN table_5 ON t1.id = table_5.id"
			+ " LEFT JOIN table_6 AS t6 ON t1.id = t6.id"
			+ " RIGHT JOIN (SELECT * FROM table_7) AS st7 ON t1.id = st7.id"
			
			+ " WHERE (t1.id = 1) OR (t1.id != 1) AND (t1.id = table_2.id) OR (t1.id = t6.id)"
			+ " GROUP BY t1.id, t1.name"
			+ (
				create
					? " HAVING 'AAAA' != 'BBBB' AND MAX(t1.id) < 10"
					: " HAVING :a != :b AND MAX(t1.id) < :max_id"
			)
			+ " ORDER BY t1.id, MAX(t1.id)"
			+ " OFFSET 15 ROWS FETCH NEXT 10 ROWS ONLY";
	}

	@Override
	protected String getDeleteView() {
		return "DROP VIEW IF EXISTS view_to_delete";
	}

	@Override
	protected String getCreateIndex() {
		return "CREATE INDEX index_name ON table_for_index_1(id, name)";
	}

	@Override
	protected String getDeleteIndex() {
		return "DROP INDEX index_to_delete ON table_for_index_1";
	}

	@Override
	protected String getQueryInsert() {
		return "INSERT INTO table_1 (id, name, typ) VALUES (123, 'Item 123', 'X')";
	}

	@Override
	protected String getQueryInsertFromSelect(boolean create) {
		return "WITH cte AS (SELECT id, name FROM table_2 WHERE (id = 2)),"
			+ " cte2 AS (SELECT id, name FROM table_2 WHERE (id = 2))"
			+ " INSERT INTO table_1 (id, name, typ)"
			+ " SELECT id, name, " + (create ? "'X'" : ":type") + " FROM cte";
	}

	@Override
	protected String getQueryInsertOverrideAI() {
		return "SET IDENTITY_INSERT table_ai ON;"
			+ "INSERT INTO table_ai (id, name, typ) VALUES (123, 'Item 123', 'X');"
			+ "SET IDENTITY_INSERT table_ai OFF;"
			//+ "DBCC CHECKIDENT ('table_ai', RESEED, (SELECT ISNULL(MAX(id), 0) FROM table_ai))"
			+"DECLARE @nextId INT;"
			+ "SELECT @nextId = ISNULL(MAX(id), 0) FROM table_ai;"
			+ "DBCC CHECKIDENT ('table_ai', RESEED, @nextId)"
			;
	}

	@Override
	protected String getQueryUpdateBasic(boolean create) {
		String id = create ? "1" : ":id";
		return "UPDATE table_1"
			+ " SET name = " + (create ? "123" : ":value") + ", typ = UPPER('x')"
			+ " WHERE (id = " + id + ") OR (id = " + id + ") AND (id = " + id + ") OR (id = " + id + ")";
	}

	@Override
	protected boolean useQueryUpdateJoinsAlias() {
		return true;
	}

	@Override
	protected String getQueryUpdateJoins(boolean create) {
		return "UPDATE t1"
			+ " SET t1.name = " + (create ? "123" : ":value") + ", t1.typ = UPPER('x')"
			+ " FROM table_1 AS t1"
			+ " JOIN table_2 ON table_2.id = t1.id"
			+ " LEFT JOIN table_3 AS t3 ON table_2.id = t3.id"
			+ " RIGHT JOIN (SELECT * FROM table_4) AS st4 ON t3.id = st4.id"
			+ " JOIN table_5 ON table_2.id = table_5.id"
			+ " LEFT JOIN table_6 AS t6 ON table_2.id = t6.id"
			+ " RIGHT JOIN (SELECT * FROM table_7) AS st7 ON table_2.id = st7.id"
			+ " WHERE (t1.id = 1)";
	}

	@Override
	protected String getQueryUpdateWith(boolean create) {
		return "WITH cte AS (SELECT 1 as id),"
			+  " cte2 AS (SELECT 1 as id)"
			+ " UPDATE t1"
			+ " SET name = " + (create ? "123" : ":value") + ", typ = UPPER('x')"
			+ " FROM table_1 AS t1"
			+ " JOIN cte ON cte.id = t1.id";
	}

	@Override
	protected String getQueryDeleteBasic(boolean create) {
		String id = create ? "1" : ":id";
		return "DELETE table_1 FROM table_1"
			+ " WHERE (id = " + id + ") OR (id = " + id + ") AND (id = " + id + ") OR (id = " + id + ")";
	}

	@Override
	protected String getQueryDeleteJoins(boolean create) {
		return "DELETE t1"
			+ " FROM table_1 AS t1"
			+ " JOIN table_2 ON t1.id = table_2.id"
			+ " LEFT JOIN table_3 AS t3 ON t1.id = t3.id"
			+ " RIGHT JOIN (SELECT * FROM table_4) AS st4 ON t3.id = st4.id"
			+ " JOIN table_5 ON t1.id = table_5.id"
			+ " LEFT JOIN table_6 AS t6 ON t1.id = t6.id"
			+ " RIGHT JOIN (SELECT * FROM table_7) AS st7 ON t1.id = st7.id"
			+ " WHERE (st7.id = 1)";
	}

	@Override
	protected String getQueryDeleteWith(boolean create) {
		return "WITH cte AS (SELECT 1 as id),"
			+ " cte2 AS (SELECT 1 as id)"
			+ " DELETE t1"
			+ " FROM table_1 AS t1"
			+ " JOIN cte ON cte.id = t1.id";
	}

	@Override
	protected String getQuerySelect_fromString(boolean create) {
		return "SELECT id, name, typ FROM table_1";
	}

	@Override
	protected String getQuerySelect_fromStringAlias(boolean create) {
		return "SELECT id, name, typ FROM table_1 AS a";
	}

	@Override
	protected String getQuerySelect_fromSelect(boolean create) {
		return "SELECT A FROM (SELECT 1 AS A) AS a";
	}

	@Override
	protected String getQuerySelect_with(boolean create) {
		return "WITH cte AS (SELECT 42 as a),"
			+ " cte2 AS (SELECT 42 as a)"
			+ " SELECT a FROM cte";
	}
	
	@Override
	protected String getQuerySelect_withRecursive(boolean create) {
		return "WITH cte AS (SELECT 1 AS A UNION ALL SELECT 2 AS A FROM cte),"
			+ " cte2 AS (SELECT 42 as a)"
			+ " SELECT TOP 2 A FROM cte";
	}

	@Override
	protected String getQuerySelect_fromMultiSelect(boolean create) {
		return "SELECT A FROM (SELECT 1 AS A UNION SELECT 2 AS A) AS a";
	}

	@Override
	protected String getQuerySelect(boolean create) {
		return "SELECT t1.id, t1.name, MAX(t1.id) as max_id"
			+ " FROM table_1 AS t1"
			
			+ " JOIN table_2 ON t1.id = table_2.id"
			+ " LEFT JOIN table_3 AS t3 ON t1.id = t3.id"
			+ " RIGHT JOIN (SELECT * FROM table_4) AS st4 ON t3.id = st4.id"
			
			+ " JOIN table_5 ON t1.id = table_5.id"
			+ " LEFT JOIN table_6 AS t6 ON t1.id = t6.id"
			+ " RIGHT JOIN (SELECT * FROM table_7) AS st7 ON t1.id = st7.id"
			+ " WHERE (t1.id = 1) OR (t1.id != 1) AND (t1.id = table_2.id) OR (t1.id = t6.id)"
			+ " GROUP BY t1.id, t1.name"
			+ (
				create
					? " HAVING 'AAAA' != 'BBBB' AND MAX(t1.id) < 10"
					: " HAVING :a != :b AND MAX(t1.id) < :max_id"
			)
			+ " ORDER BY t1.id, MAX(t1.id)"
			+ " OFFSET 15 ROWS FETCH NEXT 10 ROWS ONLY";
	}

	@Override
    protected String getQuerySelect_limit(boolean create) {
        return "SELECT TOP 10 * FROM table_1";
    }

	@Override
	protected String getQuerySelect_limitOffset(boolean create) {
        return "SELECT * FROM table_1 ORDER BY (SELECT null) OFFSET 15 ROWS FETCH NEXT 10 ROWS ONLY";
	}

    @Override
    protected String getQuerySelect_limitOffsetOrderBy(boolean create) {
        return "SELECT * FROM table_1 ORDER BY id OFFSET 15 ROWS FETCH NEXT 10 ROWS ONLY";
    }

	@Override
	protected String getQueryMultipleSelect(boolean create) {
		return "SELECT " + (create ? "321" : ":id") + " as id, name FROM table_5"
			+ " UNION"
			+ " SELECT id, name FROM table_5"
			+ " INTERSECT"
			+ " SELECT id, name FROM table_5"
			+ " UNION ALL"
			+ " SELECT id, name FROM table_5"
			+ " EXCEPT"
			+ " SELECT id, name FROM table_5"
			+ " ORDER BY name, id";
	}

	@Override
	protected String getBatch(boolean create) {
		return "UPDATE table_1 SET name = ':x' WHERE (id > " + (create ? "4" : ":id") + ");"
			+ " UPDATE table_2 SET name = " + ( create ? "'NAME'" : ":x")
				+ " WHERE (id > " + (create ? "4" : ":id") + ");";
	}

	@Override
	protected String getCallProcedureInt() {
		return "{? = CALL procedure_int('some', ?, 123, ?, 0)}";
	}

	@Override
	protected String getCallProcedureVoid() {
		return "{CALL procedure_void('some', ?, 123, ?, 0)}";
	}

	/***********************/
	
	@Override
	protected String getFunctions_concat() {
		return "SELECT CONCAT('\"', name, '\"') FROM table_for_functions";
	}

	@Override
	protected String getFunctions_groupConcat() {
		return "SELECT STRING_AGG(name, ',') FROM table_for_functions GROUP BY name";
	}

	@Override
	protected String getFunctions_groupConcatOrderBy() {
		return "SELECT STRING_AGG(name, ',') WITHIN GROUP (ORDER BY id) FROM table_for_functions GROUP BY name";
	}

	@Override
	protected String getFunctions_cast() {
		return "SELECT CAST(id AS FLOAT) FROM table_for_functions";
	}

	@Override
	protected String getFunctions_max() {
		return "SELECT MAX(id) FROM table_for_functions";
	}

	@Override
	protected String getFunctions_min() {
		return "SELECT MIN(id) FROM table_for_functions";
	}

	@Override
	protected String getFunctions_avg() {
		return "SELECT AVG(id) FROM table_for_functions";
	}

	@Override
	protected String getFunctions_sum() {
		return "SELECT SUM(id) FROM table_for_functions";
	}

	@Override
	protected String getFunctions_count() {
		return "SELECT COUNT(id) FROM table_for_functions";
	}

	@Override
	protected String getFunctions_lower() {
		return "SELECT LOWER(name) FROM table_for_functions";
	}

	@Override
	protected String getFunctions_upper() {
		return "SELECT UPPER(name) FROM table_for_functions";
	}

	@Override
	protected Connection getConnection(Connections connections) throws SQLException {
		return connections.sqlserver();
	}

	@Override
	protected Connection getBaseConnection(Connections connections) throws SQLException {
		return connections.sqlserverBase();
	}

	@Override
	protected String getFilename() {
		return "sqlserver";
	}
}
