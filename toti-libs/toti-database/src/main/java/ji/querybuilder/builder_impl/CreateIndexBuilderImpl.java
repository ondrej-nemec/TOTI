package ji.querybuilder.builder_impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import ji.querybuilder.DbInstance;
import ji.querybuilder.builder_impl.share.SingleExecute;
import ji.querybuilder.builders.CreateIndexBuilder;

public class CreateIndexBuilderImpl implements CreateIndexBuilder, SingleExecute {

	private final Connection connection;
	private final DbInstance instance;
	private final String name;
	private final String table;
	private final String[] columns;
	
	public CreateIndexBuilderImpl(Connection connection, DbInstance instance,
		String name, String table, String[] columns) {
		this.connection = connection;
		this.instance = instance;
		this.name = name;
		this.table = table;
		this.columns = columns;
	}
	
	public String getIndexName() {
		return name;
	}
	
	public String getTable() {
		return table;
	}
	
	public String[] getColumns() {
		return columns;
	}

	@Override
	public String getSql() {
		return instance.createSql(this);
	}

	@Override
	public int execute() throws SQLException {
		return execute(connection, createSql(), new HashMap<>());
	}

}
