package ji.database.support;

import java.sql.SQLException;

import ji.common.structures.ThrowingConsumer;
import ji.querybuilder.QueryBuilder;

public interface QueryBuilderConsumer extends ThrowingConsumer<QueryBuilder, SQLException> {

	void accept(QueryBuilder queryBuilder) throws SQLException;
}
