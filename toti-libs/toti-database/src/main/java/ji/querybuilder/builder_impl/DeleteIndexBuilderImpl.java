package ji.querybuilder.builder_impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import ji.querybuilder.DbInstance;
import ji.querybuilder.builder_impl.share.SingleExecute;
import ji.querybuilder.builders.DeleteIndexBuilder;

public class DeleteIndexBuilderImpl implements DeleteIndexBuilder, SingleExecute {
	
	private final Connection connection;
	private final DbInstance instance;
	private final String indexName;
	private final String tableName;

	public DeleteIndexBuilderImpl(Connection connection, DbInstance instance, String indexName, String tableName) {
		this.connection = connection;
		this.instance = instance;
		this.indexName = indexName;
		this.tableName = tableName;
	}
	
	public String getIndexName() {
		return indexName;
	}
	
	public String getTable() {
		return tableName;
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
