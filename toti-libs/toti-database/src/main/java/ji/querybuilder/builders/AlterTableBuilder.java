package ji.querybuilder.builders;

import java.sql.SQLException;

import ji.querybuilder.Builder;
import ji.querybuilder.enums.ColumnSetting;
import ji.querybuilder.enums.ColumnType;
import ji.querybuilder.enums.OnAction;

public interface AlterTableBuilder extends Builder {
	
	// TODO set settings https://stackoverflow.com/a/4146313/8240462
	
	AlterTableBuilder renameTable(String newName);
	
	AlterTableBuilder addColumn(String column, ColumnType type, ColumnSetting... settings);

	AlterTableBuilder addColumn(String column, ColumnType type, Object defaultValue, ColumnSetting... settings);

	AlterTableBuilder renameColumn(String originName, String newName, ColumnType type);

	AlterTableBuilder modifyColumnType(String column, ColumnType type);
	
	AlterTableBuilder modifyColumnDefault(String column, Object value);
	
	AlterTableBuilder removeColumnDefault(String column);
	
	AlterTableBuilder setColumnNullable(String column);
	
	AlterTableBuilder setColumnNotNull(String column);
	
	AlterTableBuilder setColumnUnique(String column);
	
	AlterTableBuilder deleteColumn(String name);
	
	AlterTableBuilder removeColumnUnique(String column);
	
	default AlterTableBuilder addForeignKey(String column, String referedTable, String referedColumn) {
		return addForeignKey(column, referedTable, referedColumn, null, null);
	}
	
	AlterTableBuilder addForeignKey(String column, String referedTable, String referedColumn, OnAction onDelete, OnAction onUpdate);
		
	AlterTableBuilder deleteForeingKey(String name);
	
	void execute() throws SQLException;

}
