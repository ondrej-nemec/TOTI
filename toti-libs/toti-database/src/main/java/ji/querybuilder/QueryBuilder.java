package ji.querybuilder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

import ji.querybuilder.builder_impl.AlterTableBuilderImpl;
import ji.querybuilder.builder_impl.AlterViewBuilderImpl;
import ji.querybuilder.builder_impl.BatchBuilderImpl;
import ji.querybuilder.builder_impl.CreateIndexBuilderImpl;
import ji.querybuilder.builder_impl.CreateTableBuilderImpl;
import ji.querybuilder.builder_impl.CreateViewBuilderImpl;
import ji.querybuilder.builder_impl.DeleteBuilderImpl;
import ji.querybuilder.builder_impl.DeleteIndexBuilderImpl;
import ji.querybuilder.builder_impl.DeleteTableBuilderImpl;
import ji.querybuilder.builder_impl.DeleteViewBuilderImpl;
import ji.querybuilder.builder_impl.InsertBuilderImpl;
import ji.querybuilder.builder_impl.MultipleSelectBuilderImpl;
import ji.querybuilder.builder_impl.SelectBuilderImpl;
import ji.querybuilder.builder_impl.UpdateBuilderImpl;
import ji.querybuilder.builders.AlterTableBuilder;
import ji.querybuilder.builders.AlterViewBuilder;
import ji.querybuilder.builders.BatchBuilder;
import ji.querybuilder.builders.CreateIndexBuilder;
import ji.querybuilder.builders.CreateTableBuilder;
import ji.querybuilder.builders.CreateViewBuilder;
import ji.querybuilder.builders.DeleteBuilder;
import ji.querybuilder.builders.DeleteIndexBuilder;
import ji.querybuilder.builders.DeleteViewBuilder;
import ji.querybuilder.builders.DeleteTableBuilder;
import ji.querybuilder.builders.InsertBuilder;
import ji.querybuilder.builders.MultipleSelectBuilder;
import ji.querybuilder.builders.SelectBuilder;
import ji.querybuilder.builders.UpdateBuilder;
import ji.querybuilder.builders.WithBuilder;

public class QueryBuilder implements QueryBuilderFactory, PreparedQueries {

	// TODO transaction
	
	private final DbInstance instance;
	private final Connection connection;
	
	public QueryBuilder(DbInstance instance, Connection connection) {
		this.instance = instance;
		this.connection = connection;
	}

	@Override
	public Connection getConnection() {
		return connection;
	}
	
	/**
	 * Begin transaction
	 * @throws SQLException
	 */
	@Override
	public void begin() throws SQLException {
		connection.setAutoCommit(false);
	}

	/**
	 * Commit transaction
	 * @throws SQLException
	 */
	@Override
	public void commit() throws SQLException {
		connection.commit();
	}
	
	/**
	 * Rollback transaction
	 * @throws SQLException
	 */
	@Override
	public void rollback() throws SQLException {
		connection.rollback();
	}

	@Override
	public Functions getSqlFunctions() {
		return instance;
	}
	
	@Override
	public BatchBuilder batch() {
		return new BatchBuilderImpl(connection, instance);
	}
	
	@Override
	public MultipleSelectBuilder multiSelect(SelectBuilder builder) {
		return new MultipleSelectBuilderImpl(connection, instance, builder);
	}
	
	@Override
	public DeleteBuilder delete(String table) {
		return delete(table, null);
	}

	@Override
	public DeleteBuilder delete(String table, String alias) {
		return new DeleteBuilderImpl(connection, instance, table, alias);
	}

	@Override
	public InsertBuilder insert(String table) {
		// return insert(table, null); // cannot select between preparedQuery.insert and this.insert
		return new InsertBuilderImpl(connection, instance, table, null);
	}
	
	@Override
	public InsertBuilder insert(String table, String alias) {
		return new InsertBuilderImpl(connection, instance, table, alias);
	}

	@Override
	public UpdateBuilder update(String table) {
		return update(table, null);
	}

	@Override
	public UpdateBuilder update(String table, String alias) {
		return new UpdateBuilderImpl(connection, instance, table, alias);
	}

	@Override
	public SelectBuilder select(Function<Functions, String> select) {
		return select(select.apply(instance));
	}

	@Override
	public SelectBuilder select(String... select) {
		return new SelectBuilderImpl(connection, instance, select);
	}

	@Override
	public DeleteTableBuilder deleteTable(String table) {
		return new DeleteTableBuilderImpl(connection, instance, table);
	}

	@Override
	public CreateTableBuilder createTable(String name) {
		return new CreateTableBuilderImpl(connection, instance, name);
	}

	@Override
	public AlterTableBuilder alterTable(String name) {
		return new AlterTableBuilderImpl(connection, instance, name);
	}

	@Override
	public DeleteViewBuilder deleteView(String table) {
		return new DeleteViewBuilderImpl(connection, instance, table);
	}

	@Override
	public CreateViewBuilder createView(String name) {
		return new CreateViewBuilderImpl(connection, instance, name);
	}

	@Override
	public AlterViewBuilder alterView(String name) {
		return new AlterViewBuilderImpl(connection, instance, name);
	}

	@Override
	public CreateIndexBuilder createIndex(String name, String table, String... columns) {
		return new CreateIndexBuilderImpl(connection, instance, name, table, columns);
	}

	@Override
	public DeleteIndexBuilder deleteIndex(String indexName, String tableName) {
		return new DeleteIndexBuilderImpl(connection, instance, indexName, tableName);
	}

	@Override
	public WithBuilder with(String alias, SelectBuilder builder) {
		return new WithBuilder(instance, connection, alias, builder);
	}

	@Override
	public WithBuilder with(String alias, MultipleSelectBuilder builder) {
		return new WithBuilder(instance, connection, alias, builder);
	}

}
