package toti.lib.database.querybuilder.builders;

import java.sql.SQLException;

import toti.lib.database.querybuilder.Builder;
import toti.lib.database.querybuilder.enums.ColumnSetting;
import toti.lib.database.querybuilder.enums.ColumnType;
import toti.lib.database.querybuilder.enums.OnAction;

public interface CreateTableBuilder extends Builder {

	CreateTableBuilder addColumn(String name, ColumnType type, ColumnSetting... settings);

	CreateTableBuilder addColumn(String name, ColumnType type, Object defaultValue, ColumnSetting... settings);
	
	default CreateTableBuilder addForeignKey(String column, String referedTable, String referedColumn) {
		return addForeignKey(column, referedTable, referedColumn, null, null);
	}
	
	CreateTableBuilder addForeignKey(String column, String referedTable, String referedColumn, OnAction onDelete, OnAction onUpdate);

	CreateTableBuilder setPrimaryKey(String... columns);

	void execute() throws SQLException;

}
