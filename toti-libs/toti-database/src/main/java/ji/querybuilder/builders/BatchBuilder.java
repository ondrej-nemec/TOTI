package ji.querybuilder.builders;

import java.sql.SQLException;

import ji.querybuilder.Builder;
import ji.querybuilder.builders.parents.Parametrized;

public interface BatchBuilder extends Builder, Parametrized<BatchBuilder> {

	BatchBuilder addBatch(Builder batch);
	
	void execute() throws SQLException;

}
