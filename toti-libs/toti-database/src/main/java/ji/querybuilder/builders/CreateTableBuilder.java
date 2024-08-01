package ji.querybuilder.builders;

import java.sql.SQLException;

import ji.querybuilder.Builder;
import ji.querybuilder.enums.ColumnSetting;
import ji.querybuilder.enums.ColumnType;
import ji.querybuilder.enums.OnAction;

public interface CreateTableBuilder extends Builder {

	default CreateTableBuilder addColumn(String name, ColumnType type, ColumnSetting... settings) {
		return addColumn(name, type, null, settings);
	}

	CreateTableBuilder addColumn(String name, ColumnType type, Object defaultValue, ColumnSetting... settings);
	
	default CreateTableBuilder addForeignKey(String column, String referedTable, String referedColumn) {
		return addForeignKey(column, referedTable, referedColumn, null, null);
	}
	
	CreateTableBuilder addForeignKey(String column, String referedTable, String referedColumn, OnAction onDelete, OnAction onUpdate);

	CreateTableBuilder setPrimaryKey(String... columns);

	void execute() throws SQLException;

}
