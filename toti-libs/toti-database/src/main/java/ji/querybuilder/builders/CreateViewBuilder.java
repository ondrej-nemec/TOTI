package ji.querybuilder.builders;

import java.sql.SQLException;

import ji.querybuilder.Builder;
import ji.querybuilder.builders.parents.PlainSelect;

public interface CreateViewBuilder extends Builder, PlainSelect<CreateViewBuilder> {

	int execute() throws SQLException;

}
