package ji.querybuilder.builder_impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import ji.common.structures.SortedMap;
import ji.querybuilder.DbInstance;
import ji.querybuilder.builder_impl.share.MultipleExecute;
import ji.querybuilder.builders.AlterTableBuilder;
import ji.querybuilder.enums.ColumnSetting;
import ji.querybuilder.enums.ColumnType;
import ji.querybuilder.enums.OnAction;
import ji.querybuilder.structures.Column;
import ji.querybuilder.structures.ForeignKey;
import ji.querybuilder.structures.ModifyColumn;

public class AlterTableBuilderImpl implements AlterTableBuilder, MultipleExecute {

	private final Connection connection;
	private final DbInstance instance;
	private final String table;
	
	private final List<Column> addColumns;
	private final List<Column> deleteColumns;
	private final List<Column> renameColumns;
	private final SortedMap<String, ModifyColumn> modifyColumns;
	private final List<ForeignKey> addForeignKeys;
	private final List<ForeignKey> deleteForeignKeys;
	private String newName;
	
	public AlterTableBuilderImpl(Connection connection, DbInstance instance, String table) {
		this.instance = instance;
		this.connection = connection;
		this.table = table;
		
		this.addColumns = new LinkedList<>();
		this.deleteColumns = new LinkedList<>();
		this.renameColumns = new LinkedList<>();
		this.modifyColumns = new SortedMap<>();
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
	
	public List<ModifyColumn> getModifyColumns() {
		return modifyColumns.toList();
	}
	
	public List<ForeignKey> getAddForeignKeys() {
		return addForeignKeys;
	}
	
	public List<ForeignKey> getDeleteForeignKeys() {
		return deleteForeignKeys;
	}
	
	public String getNewName() {
		return newName;
	}

	@Override
	public String getSql() {
		return _toString(getSqls());
	}
	
	@Override
	public List<String> getSqls() {
		return instance.createSql(this);
	}

	@Override
	public AlterTableBuilder addColumn(String name, ColumnType type, ColumnSetting... settings) {
		this.addColumns.add(Column.create(name, type, settings));
		return this;
	}

	@Override
	public AlterTableBuilder addColumn(String name, ColumnType type, Object defaultValue, ColumnSetting... settings) {
		this.addColumns.add(Column.create(name, type, defaultValue, settings));
		return this;
	}

	@Override
	public AlterTableBuilder deleteColumn(String name) {
		this.deleteColumns.add(Column.delete(name));
		return this;
	}
	
	@Override
	public AlterTableBuilder modifyColumn(String columnName, Consumer<ModifyColumn> modify) {
		ModifyColumn column = modifyColumns.getValue(columnName);
		if (column == null) {
			column = new ModifyColumn(columnName);
			modifyColumns.put(columnName, column);
		}
		modify.accept(column);
		return this;
	}

	@Override
	public AlterTableBuilder renameColumn(String originName, String newName, ColumnType type) {
		this.renameColumns.add(Column.rename(originName, newName, type));
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
	public AlterTableBuilder renameTable(String newName) {
		this.newName = newName;
		return this;
	}

	@Override
	public void execute() throws SQLException {
		execute(connection, getSqls(), new HashMap<>());
	}

}
