package toti.lib.database.querybuilder.builders;

import java.sql.SQLException;

import toti.lib.database.querybuilder.Builder;
import toti.lib.database.querybuilder.builders.parents.Parametrized;

public interface BatchBuilder extends Builder, Parametrized<BatchBuilder> {

	BatchBuilder addBatch(Builder batch);
	
	void execute() throws SQLException;

}
