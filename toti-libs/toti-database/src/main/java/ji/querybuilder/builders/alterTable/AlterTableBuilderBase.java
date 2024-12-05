package ji.querybuilder.builders.alterTable;

import java.sql.SQLException;

import ji.querybuilder.Builder;
import ji.querybuilder.enums.ColumnSetting;
import ji.querybuilder.enums.ColumnType;
import ji.querybuilder.enums.OnAction;

public interface AlterTableBuilderBase extends Builder {
	
	// TODO set settings https://stackoverflow.com/a/4146313/8240462
	
	AlterTableBuilderBase addColumn(String column, ColumnType type, ColumnSetting... settings);

	AlterTableBuilderBase addColumn(String column, ColumnType type, Object defaultValue, ColumnSetting... settings);
	
	default AlterTableBuilderBase addForeignKey(String column, String referedTable, String referedColumn) {
		return addForeignKey(column, referedTable, referedColumn, null, null);
	}
	
	AlterTableBuilderBase addForeignKey(String column, String referedTable, String referedColumn, OnAction onDelete, OnAction onUpdate);
		
	AlterTableBuilderBase deleteColumn(String name);
	
	AlterTableBuilderBase deleteForeingKey(String name);

	AlterTableBuilderBase modifyColumnType(String column, ColumnType type);
	
	AlterTableBuilderBase modifyColumnDefault(String column, Object value);
	
	AlterTableBuilderBase removeColumnDefault(String column);
	
	AlterTableBuilderBase setColumnNullable(String column);
	
	AlterTableBuilderBase setColumnNotNull(String column);
	
	AlterTableBuilderBase setColumnUnique(String column);
	
	AlterTableBuilderBase removeColumnUnique(String column);
	
	void execute() throws SQLException;

}
