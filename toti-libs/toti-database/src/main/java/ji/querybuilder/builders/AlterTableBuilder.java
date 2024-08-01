package ji.querybuilder.builders;

import java.sql.SQLException;

import ji.querybuilder.Builder;
import ji.querybuilder.enums.ColumnSetting;
import ji.querybuilder.enums.ColumnType;
import ji.querybuilder.enums.OnAction;

public interface AlterTableBuilder extends Builder {

	// TODO rename table
	// TODO set settings https://stackoverflow.com/a/4146313/8240462
	
	default AlterTableBuilder addColumn(String name, ColumnType type, ColumnSetting... settings) {
		return addColumn(name, type, null, settings);
	}

	AlterTableBuilder addColumn(String name, ColumnType type, Object defaultValue, ColumnSetting... settings);
	
	default AlterTableBuilder addForeignKey(String column, String referedTable, String referedColumn) {
		return addForeignKey(column, referedTable, referedColumn, null, null);
	}
	
	AlterTableBuilder addForeignKey(String column, String referedTable, String referedColumn, OnAction onDelete, OnAction onUpdate);
		
	AlterTableBuilder deleteColumn(String name);
	
	AlterTableBuilder deleteForeingKey(String name);
	
	AlterTableBuilder modifyColumnType(String name, ColumnType type);
	
	AlterTableBuilder renameColumn(String originName, String newName, ColumnType type);
	
	void execute() throws SQLException;

}
