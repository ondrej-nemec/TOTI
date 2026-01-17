package toti.lib.database.querybuilder.builders;

import java.sql.SQLException;
import java.util.function.Function;

import toti.lib.database.querybuilder.Builder;
import toti.lib.database.querybuilder.Functions;
import toti.lib.database.querybuilder.builders.parents.Joins;
import toti.lib.database.querybuilder.builders.parents.Parametrized;
import toti.lib.database.querybuilder.builders.parents.Wheres;

public interface UpdateBuilder extends Builder, Joins<UpdateBuilder>, Wheres<UpdateBuilder>, Parametrized<UpdateBuilder> {
	
	default UpdateBuilder set(String update) {
		return set(f->update);
	}
	
	UpdateBuilder set(Function<Functions, String> update);
	
	int execute() throws SQLException;
	
}
