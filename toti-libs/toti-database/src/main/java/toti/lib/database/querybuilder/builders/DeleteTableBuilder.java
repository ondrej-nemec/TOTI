package toti.lib.database.querybuilder.builders;

import java.sql.SQLException;

import toti.lib.database.querybuilder.Builder;

public interface DeleteTableBuilder extends Builder {

	int execute() throws SQLException;
	
}
