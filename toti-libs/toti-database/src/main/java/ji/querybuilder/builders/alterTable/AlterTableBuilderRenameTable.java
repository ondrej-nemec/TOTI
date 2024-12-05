package ji.querybuilder.builders.alterTable;

import java.sql.SQLException;

import ji.querybuilder.Builder;

public interface AlterTableBuilderRenameTable extends Builder {
	
	AlterTableBuilderRenameTable renameTable(String newName);
	
	void execute() throws SQLException;

}
