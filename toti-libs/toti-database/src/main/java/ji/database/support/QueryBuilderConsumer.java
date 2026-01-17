package ji.database.support;

import java.sql.SQLException;

import ji.querybuilder.QueryBuilder;
import toti.common.structures.ThrowingConsumer;

public interface QueryBuilderConsumer extends ThrowingConsumer<QueryBuilder, SQLException> {

	void accept(QueryBuilder queryBuilder) throws SQLException;
}
