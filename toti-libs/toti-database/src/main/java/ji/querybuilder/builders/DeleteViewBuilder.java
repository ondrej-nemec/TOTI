package ji.querybuilder.builders;

import java.sql.SQLException;

import ji.querybuilder.Builder;

public interface DeleteViewBuilder extends Builder {

	int execute() throws SQLException;
	
}
