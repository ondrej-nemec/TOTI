package ji.querybuilder.builder_impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import ji.querybuilder.DbInstance;
import ji.querybuilder.builder_impl.share.SingleExecute;
import ji.querybuilder.builders.AlterTableBuilder;
import ji.querybuilder.enums.ColumnSetting;
import ji.querybuilder.enums.ColumnType;
import ji.querybuilder.enums.OnAction;
import ji.querybuilder.structures.Column;
import ji.querybuilder.structures.ForeignKey;

public class AlterTableBuilderImpl implements AlterTableBuilder, SingleExecute {

	private final Connection connection;
	private final DbInstance instance;
	private final String table;
	
	private final List<Column> addColumns;
	private final List<Column> deleteColumns;
	private final List<Column> renameColumns;
	private final List<Column> modifyColumns;
	private final List<ForeignKey> addForeignKeys;
	private final List<ForeignKey> deleteForeignKeys;
	
	public AlterTableBuilderImpl(Connection connection, DbInstance instance, String table) {
		this.instance = instance;
		this.connection = connection;
		this.table = table;
		
		this.addColumns = new LinkedList<>();
		this.deleteColumns = new LinkedList<>();
		this.renameColumns = new LinkedList<>();
		this.modifyColumns = new LinkedList<>();
		this.addForeignKeys = new LinkedList<>();
		this.deleteForeignKeys = new LinkedList<>();
	}
	
	public String getTable() {
		return table;
	}
	
	public List<Column> getAddColumns() {
		return addColumns;
	}
	
	public List<Column> getDeleteColumns() {
		return deleteColumns;
	}
	
	public List<Column> getRenameColumns() {
		return renameColumns;
	}
	
	public List<Column> getModifyColumns() {
		return modifyColumns;
	}
	
	public List<ForeignKey> getAddForeignKeys() {
		return addForeignKeys;
	}
	
	public List<ForeignKey> getDeleteForeignKeys() {
		return deleteForeignKeys;
	}

	@Override
	public String getSql() {
		return instance.createSql(this);
	}

	@Override
	public AlterTableBuilder addColumn(String name, ColumnType type, Object defaultValue, ColumnSetting... settings) {
		this.addColumns.add(new Column(name, type, defaultValue, settings));
		return this;
	}

	@Override
	public AlterTableBuilder deleteColumn(String name) {
		this.deleteColumns.add(new Column(name, null, null, null));
		return this;
	}

	@Override
	public AlterTableBuilder modifyColumnType(String name, ColumnType type) {
		this.modifyColumns.add(new Column(name, type, null, null));
		return this;
	}

	@Override
	public AlterTableBuilder renameColumn(String originName, String newName, ColumnType type) {
		this.renameColumns.add(new Column(originName, type, newName, null));
		return this;
	}

	@Override
	public AlterTableBuilder addForeignKey(String column, String referedTable, String referedColumn, OnAction onDelete, OnAction onUpdate) {
		this.addForeignKeys.add(new ForeignKey(column, referedTable, referedColumn, onDelete, onUpdate));
		return this;
	}

	@Override
	public AlterTableBuilder deleteForeingKey(String name) {
		this.deleteForeignKeys.add(new ForeignKey(name, null, null, null, null));
		return this;
	}

	@Override
	public void execute() throws SQLException {
		execute(connection, createSql(), new HashMap<>());
	}

}
