package toti.lib.database.querybuilder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Function;

import toti.lib.database.querybuilder.builders.AlterTableBuilder;
import toti.lib.database.querybuilder.builders.AlterViewBuilder;
import toti.lib.database.querybuilder.builders.BatchBuilder;
import toti.lib.database.querybuilder.builders.CallProcedureBuilder;
import toti.lib.database.querybuilder.builders.CreateIndexBuilder;
import toti.lib.database.querybuilder.builders.CreateTableBuilder;
import toti.lib.database.querybuilder.builders.CreateViewBuilder;
import toti.lib.database.querybuilder.builders.DeleteBuilder;
import toti.lib.database.querybuilder.builders.DeleteIndexBuilder;
import toti.lib.database.querybuilder.builders.DeleteTableBuilder;
import toti.lib.database.querybuilder.builders.DeleteViewBuilder;
import toti.lib.database.querybuilder.builders.InsertBuilder;
import toti.lib.database.querybuilder.builders.MultipleSelectBuilder;
import toti.lib.database.querybuilder.builders.SelectBuilder;
import toti.lib.database.querybuilder.builders.UpdateBuilder;
import toti.lib.database.querybuilder.builders.WithBuilder;

public interface QueryBuilderFactory {

	Connection getConnection();

	/**
	 * Begin transaction
	 * @throws SQLException
	 */
	void begin() throws SQLException;

	void commit() throws SQLException;

	void rollback() throws SQLException;

	Functions getSqlFunctions();

	BatchBuilder batch();

	MultipleSelectBuilder multiSelect(SelectBuilder builder);

	DeleteBuilder delete(String table);

	DeleteBuilder delete(String table, String alias);

	default InsertBuilder insert(String table) {
		return insert(table, Optional.empty());
	}

	default InsertBuilder insert(String table, String alias) {
		return insert(table, alias, Optional.empty());
	}

	InsertBuilder insert(String table, Optional<String> idName);

	InsertBuilder insert(String table, String alias, Optional<String> idName);

	UpdateBuilder update(String table);

	UpdateBuilder update(String table, String alias);

	SelectBuilder select(Function<Functions, String> select);

	SelectBuilder select(String... select);

	DeleteTableBuilder deleteTable(String table);

	CreateTableBuilder createTable(String name);

	AlterTableBuilder alterTable(String name);

	DeleteViewBuilder deleteView(String table);

	CreateViewBuilder createView(String name);

	AlterViewBuilder alterView(String name);

	CreateIndexBuilder createIndex(String name, String table, String... columns);

	DeleteIndexBuilder deleteIndex(String name, String tableName);
	
	WithBuilder with(String alias, SelectBuilder builder);
	
	WithBuilder with(String alias, MultipleSelectBuilder builder);
	
	CallProcedureBuilder call(String procedure);

}