package ji.querybuilder.instances;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgresSqlInstanceTest extends AbstractInstanceTest {

	public PostgresSqlInstanceTest() {
		super(new PostgreSqlQueryBuilder());
	}

	@Override
	protected String getCreateTable() {
		return "CREATE TABLE create_table ("
			+ "Primary_column SERIAL NOT NULL,"
			+ " Unique_column INT UNIQUE,"
			+ " Nullable_column INT DEFAULT 42 NULL,"
			+ " Bool_column BOOLEAN,"
			+ " Float_column FLOAT,"
			+ " Double_column FLOAT,"
			+ " Char_column CHAR(1),"
			+ " Text_column TEXT,"
			+ " String_column VARCHAR(10),"
			+ " DateTime_column TIMESTAMP,"
			+ " FK_column_1 INT,"
			+ " FK_column_2 INT,"
			+ " FK_column_3 INT,"
			+ " FK_column_4 INT,"
			+ " PRIMARY KEY (Primary_column),"
			+ " CONSTRAINT FK_FK_column_1 FOREIGN KEY (FK_column_1) REFERENCES table_for_index(id),"
			+ " CONSTRAINT FK_FK_column_2 FOREIGN KEY (FK_column_2) REFERENCES table_for_index(id) ON DELETE CASCADE ON UPDATE NO ACTION,"
			+ " CONSTRAINT FK_FK_column_3 FOREIGN KEY (FK_column_3) REFERENCES table_for_index(id) ON DELETE RESTRICT ON UPDATE SET DEFAULT,"
			+ " CONSTRAINT FK_FK_column_4 FOREIGN KEY (FK_column_4) REFERENCES table_for_index(id) ON DELETE SET NULL"
		+ ")";
	}

	@Override
	protected String getCreateTableWithPrimary() {
		return "CREATE TABLE create_table ("
			+ "Primary_column_1 INT,"
			+ " Primary_column_2 INT,"
			+ " Primary_column_3 INT,"
			+ " PRIMARY KEY (Primary_column_1, Primary_column_2, Primary_column_3),"
			+ " CONSTRAINT FK_Primary_column_3 FOREIGN KEY (Primary_column_3) REFERENCES table_for_index(id)"
		+ ")";
	}

	@Override
	protected String getAlterTable() {
		return "ALTER TABLE table_to_alter"
			+ " ADD Add_column_1 INT NOT NULL,"
			+ " ADD Add_column_2 INT DEFAULT 42 UNIQUE NULL,"
			+ " ADD CONSTRAINT FK_Add_column_1 FOREIGN KEY (Add_column_1) REFERENCES table_for_index(id),"
			+ " ADD CONSTRAINT FK_Add_column_2 FOREIGN KEY (Add_column_2) REFERENCES table_for_index(id) ON DELETE CASCADE ON UPDATE NO ACTION,"
			+ " DROP CONSTRAINT FK_to_delete,"
			+ " DROP COLUMN Column_to_delete,"
			+ " ALTER COLUMN Column_to_modify_type TYPE FLOAT";
	}

	@Override
	protected String getAlterTableRename() {
		return "ALTER TABLE table_to_alter"
			+ " RENAME COLUMN Column_to_rename TO Renamed_column";
	}

	@Override
	protected String getDeleteTable() {
		return "DROP TABLE table_to_delete";
	}

	@Override
	protected String getCreateView_fromString(boolean create) {
		return "CREATE VIEW some_view AS SELECT 1 as A";
	}

	@Override
	protected String getCreateView_fromSelect(boolean create) {
		return "CREATE VIEW some_view AS SELECT A FROM (SELECT 1 AS A) AS a";
	}

	@Override
	protected String getCreateView_fromMultiSelect(boolean create) {
		return "CREATE VIEW some_view AS"
			+ " SELECT A"
			+ " FROM ("
				+ "SELECT 1 AS A"
				+ " UNION"
				+ " SELECT 2 AS A"
			+ ") AS a";
	}

	@Override
	protected String getCreateView(boolean create) {
		return "CREATE VIEW some_view AS"
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
			+ " LIMIT 10 OFFSET 15";
	}

	@Override
	protected String getAlterView_fromString(boolean create) {
		return "DROP VIEW view_to_alter; CREATE VIEW view_to_alter AS SELECT id FROM table_1";
	}

	@Override
	protected String getAlterView_fromStringAlias(boolean create) {
		return "DROP VIEW view_to_alter; CREATE VIEW view_to_alter AS SELECT id FROM table_1 AS a";
	}

	@Override
	protected String getAlterView_fromSelect(boolean create) {
		return "DROP VIEW view_to_alter; CREATE VIEW view_to_alter AS SELECT A FROM (SELECT 1 AS A) AS a";
	}

	@Override
	protected String getAlterView_fromMultiSelect(boolean create) {
		return "DROP VIEW view_to_alter; CREATE VIEW view_to_alter AS"
			+ " SELECT A"
			+ " FROM ("
				+ "SELECT 1 AS A"
				+ " UNION"
				+ " SELECT 2 AS A"
			+ ") AS a";
	}

	@Override
	protected String getAlterView(boolean create) {
		return "DROP VIEW view_to_alter; CREATE VIEW view_to_alter AS"
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
			+ " LIMIT 10 OFFSET 15";
	}

	@Override
	protected String getDeleteView() {
		return "DROP VIEW view_to_delete";
	}

	@Override
	protected String getCreateIndex() {
		return "CREATE INDEX index_name ON table_for_index(id, name)";
	}

	@Override
	protected String getDeleteIndex() {
		return "DROP INDEX index_to_delete";
	}

	@Override
	protected String getQueryInsert() {
		return "INSERT INTO table_1 (id, name, typ) VALUES (123, 'Item 123', 'X')";
	}

	@Override
	protected String getQueryInsertFromSelect(boolean create) {
		return "WITH cte AS (SELECT id, name FROM table_2 WHERE (id = 2))"
			+ "INSERT INTO table_1 (id, name, typ)"
			+ " SELECT id, name, " + (create ? "'X'" : ":type") + " FROM cte";
	}

	@Override
	protected String getQueryUpdateBasic(boolean create) {
		String id = create ? "1" : ":id";
		return "UPDATE table_1"
			+ " SET name = " + (create ? "123" : ":value") + ", typ = UPPER('x')"
			+ " WHERE (id = " + id + ") OR (id = " + id + ") AND (id = " + id + ") OR (id = " + id + ")";
	}

	@Override
	protected String getQueryUpdateJoins(boolean create) {
		return "UPDATE table_1 AS t1"
			+ " SET name = " + (create ? "123" : ":value") + ", typ = UPPER('x')"
			+ " FROM table_2"
			+ " LEFT JOIN table_3 AS t3 ON table_2.id = t3.id"
			+ " RIGHT JOIN (SELECT * FROM table_4) AS st4 ON t3.id = st4.id"
			+ " JOIN table_5 ON table_2.id = table_5.id"
			+ " LEFT JOIN table_6 AS t6 ON table_2.id = t6.id"
			+ " RIGHT JOIN (SELECT * FROM table_7) AS st7 ON table_2.id = st7.id"
			+ " WHERE (table_2.id = t1.id) AND (t1.id = 1)";
	}

	@Override
	protected String getQueryUpdateWith(boolean create) {
		return "WITH cte AS (SELECT 1 as id)"
			+ "UPDATE table_1 AS t1"
			+ " SET name = " + (create ? "123" : ":value") + ", typ = UPPER('x')"
			+ " FROM cte"
			+ " WHERE (cte.id = t1.id)";
	}

	@Override
	protected String getQueryDeleteBasic(boolean create) {
		String id = create ? "1" : ":id";
		return "DELETE FROM table_1"
			+ " WHERE (id = " + id + ") OR (id = " + id + ") AND (id = " + id + ") OR (id = " + id + ")";
	}

	@Override
	protected String getQueryDeleteJoins(boolean create) {
		return "DELETE FROM table_1 AS t1"
			+ " USING table_2, table_3 AS t3, (SELECT * FROM table_4) AS st4, table_5, table_6 AS t6, (SELECT * FROM table_7) AS st7"
			+ " WHERE (t1.id = table_2.id) AND (t1.id = t3.id) AND (t3.id = st4.id)"
			+ " AND (t1.id = table_5.id) AND (t1.id = t6.id) AND (t1.id = st7.id)"
			+ " AND (st7.id = 1)";
	}

	@Override
	protected String getQueryDeleteWith(boolean create) {
		return "WITH cte AS (SELECT 1 as id)"
			+ "DELETE FROM table_1 AS t1 USING cte WHERE (cte.id = t1.id)";
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
		return "WITH cte AS (SELECT 42 as a)SELECT a FROM cte";
	}
	
	@Override
	protected String getQuerySelect_withRecursive(boolean create) {
		return "WITH recursive cte AS (SELECT 1 AS A UNION SELECT 2 AS A FROM cte)SELECT A FROM cte";
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
			+ " LIMIT 10 OFFSET 15";
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
		return "SELECT " + (create ? "123" : ":id") + ", ':x' as a;"
				+ " SELECT " + (create ? "123" : ":id") + ", '" + (create ? "1" : ":x") + "' as b;";
	}

	/***********************/
	
	@Override
	protected String getFunctions_concat() {
		return "SELECT CONCAT(', name, ') FROM table_for_functions";
	}

	@Override
	protected String getFunctions_groupConcat() {
		return "SELECT STRING_AGG(name, ',') FROM table_for_functions GROUP BY name";
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
	protected Connection getConnection() throws SQLException {
		Properties props = new Properties();
		props.setProperty("user", "postgres");
		props.setProperty("password", "SomeP@ssw0rd");
		props.setProperty("serverTimezone", "Europe/Prague");
		props.setProperty("allowMultiQueries", "true");
		return DriverManager.getConnection("jdbc:postgresql://localhost:5433/query_builder", props);
	}
	
}
