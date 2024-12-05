package ji.querybuilder.builders.alterTable;

import java.sql.SQLException;

import ji.querybuilder.Builder;
import ji.querybuilder.enums.ColumnType;

public interface AlterTableBuilderRenameColumn extends Builder {

	AlterTableBuilderRenameColumn renameColumn(String originName, String newName, ColumnType type);
	
	void execute() throws SQLException;

}
