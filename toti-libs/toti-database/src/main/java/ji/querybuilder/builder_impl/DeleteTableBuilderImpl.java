package ji.querybuilder.builder_impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import ji.querybuilder.DbInstance;
import ji.querybuilder.builder_impl.share.SingleExecute;
import ji.querybuilder.builders.DeleteTableBuilder;

public class DeleteTableBuilderImpl implements DeleteTableBuilder, SingleExecute {

	private final Connection connection;
	private final DbInstance instance;
	private final String table;

	public DeleteTableBuilderImpl(Connection connection, DbInstance instance, String table) {
		this.connection = connection;
		this.instance = instance;
		this.table = table;
	}

	public String getTable() {
		return table;
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
