package ji.querybuilder.builders;

import java.sql.SQLException;

import ji.querybuilder.Builder;
import ji.querybuilder.builders.parents.Joins;
import ji.querybuilder.builders.parents.Wheres;

public interface DeleteBuilder extends Builder, Joins<DeleteBuilder>, Wheres<DeleteBuilder> {

	DeleteBuilder addParameter(String name, Object value);
	
	int execute() throws SQLException;
	
}
