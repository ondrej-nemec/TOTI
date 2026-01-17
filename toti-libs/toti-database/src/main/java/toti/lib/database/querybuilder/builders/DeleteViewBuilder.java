package toti.lib.database.querybuilder.builders;

import java.sql.SQLException;

import toti.lib.database.querybuilder.Builder;

public interface DeleteViewBuilder extends Builder {

	int execute() throws SQLException;
	
}
