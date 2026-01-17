package toti.lib.database.querybuilder.builders;

import java.sql.SQLException;

import toti.lib.database.querybuilder.Builder;
import toti.lib.database.querybuilder.builders.parents.Joins;
import toti.lib.database.querybuilder.builders.parents.Wheres;

public interface DeleteBuilder extends Builder, Joins<DeleteBuilder>, Wheres<DeleteBuilder> {

	DeleteBuilder addParameter(String name, Object value);
	
	int execute() throws SQLException;
	
}
