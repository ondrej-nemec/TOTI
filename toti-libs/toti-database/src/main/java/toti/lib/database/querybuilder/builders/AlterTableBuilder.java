package toti.lib.database.querybuilder.builders;

import java.sql.SQLException;
import java.util.function.Consumer;

import toti.lib.database.querybuilder.Builder;
import toti.lib.database.querybuilder.enums.ColumnSetting;
import toti.lib.database.querybuilder.enums.ColumnType;
import toti.lib.database.querybuilder.enums.OnAction;
import toti.lib.database.querybuilder.structures.ModifyColumn;

public interface AlterTableBuilder extends Builder {
	
	// TODO set settings https://stackoverflow.com/a/4146313/8240462
	
	AlterTableBuilder renameTable(String newName);
	
	AlterTableBuilder addColumn(String column, ColumnType type, ColumnSetting... settings);

	AlterTableBuilder addColumn(String column, ColumnType type, Object defaultValue, ColumnSetting... settings);

	AlterTableBuilder renameColumn(String originName, String newName, ColumnType type);

	default AlterTableBuilder modifyColumnType(String column, ColumnType type) {
		return modifyColumn(column, c->c.setColumnType(type));
	}

	default AlterTableBuilder addColumnDefault(String column, Object value) {
		return modifyColumn(column, c->c.addDefault(value));
	}

	default AlterTableBuilder modifyColumnDefault(String column, Object value) {
		return modifyColumn(column, c->c.modifyDefault(value));
	}

	default AlterTableBuilder removeColumnDefault(String column) {
		return modifyColumn(column, c->c.removeDefault());
	}

	default AlterTableBuilder setColumnNotNull(String column) {
		return modifyColumn(column, c->c.setIsNullable(false));
	}
	
	default AlterTableBuilder setColumnNullable(String column) {
		return modifyColumn(column, c->c.setIsNullable(true));
	}
	
	default AlterTableBuilder setColumnUnique(String column) {
		return modifyColumn(column, c->c.setUnique(true));
	}
	
	default AlterTableBuilder removeColumnUnique(String column) {
		return modifyColumn(column, c->c.setUnique(false));
	}

	AlterTableBuilder modifyColumn(String columnName, Consumer<ModifyColumn> modify);
	
	AlterTableBuilder deleteColumn(String name);
	
	default AlterTableBuilder addForeignKey(String column, String referedTable, String referedColumn) {
		return addForeignKey(column, referedTable, referedColumn, null, null);
	}
	
	AlterTableBuilder addForeignKey(String column, String referedTable, String referedColumn, OnAction onDelete, OnAction onUpdate);
		
	AlterTableBuilder deleteForeingKey(String name);
	
	void execute() throws SQLException;

}
