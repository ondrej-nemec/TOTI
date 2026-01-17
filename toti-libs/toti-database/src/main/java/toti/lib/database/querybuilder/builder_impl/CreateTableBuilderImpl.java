package toti.lib.database.querybuilder.builder_impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import toti.lib.database.querybuilder.DbInstance;
import toti.lib.database.querybuilder.builder_impl.share.SingleExecute;
import toti.lib.database.querybuilder.builders.CreateTableBuilder;
import toti.lib.database.querybuilder.enums.ColumnSetting;
import toti.lib.database.querybuilder.enums.ColumnType;
import toti.lib.database.querybuilder.enums.OnAction;
import toti.lib.database.querybuilder.structures.Column;
import toti.lib.database.querybuilder.structures.ForeignKey;

public class CreateTableBuilderImpl implements CreateTableBuilder, SingleExecute {

	private final Connection connection;
	private final DbInstance instance;
	private final String table;
	private final List<Column> columns;
	private final List<ForeignKey> foreignKeys;
	private String[] primaryKey;
	
	public CreateTableBuilderImpl(Connection connection, DbInstance instance, String table) {
		this.instance = instance;
		this.connection = connection;
		this.table = table;
		this.columns = new LinkedList<>();
		this.foreignKeys = new LinkedList<>();
	}
	
	public String getTable() {
		return table;
	}
	
	public List<Column> getColumns() {
		return columns;
	}
	
	public List<ForeignKey> getForeignKeys() {
		return foreignKeys;
	}
	
	public String[] getPrimaryKey() {
		return primaryKey;
	}

	@Override
	public String getSql() {
		return instance.createSql(this);
	}

	@Override
	public CreateTableBuilder addColumn(String name, ColumnType type, ColumnSetting... settings) {
		this.columns.add(Column.create(name, type, settings));
		return this;
	}

	@Override
	public CreateTableBuilder addColumn(String name, ColumnType type, Object defaultValue, ColumnSetting... settings) {
		this.columns.add(Column.create(name, type, defaultValue, settings));
		return this;
	}

	@Override
	public CreateTableBuilder addForeignKey(String column, String referedTable, String referedColumn, OnAction onDelete, OnAction onUpdate) {
		this.foreignKeys.add(new ForeignKey(column, referedTable, referedColumn, onDelete, onUpdate));
		return this;
	}

	@Override
	public CreateTableBuilder setPrimaryKey(String... columns) {
		this.primaryKey = columns;
		return this;
	}

	@Override
	public void execute() throws SQLException {
		execute(connection, createSql(), new HashMap<>());
	}

}
