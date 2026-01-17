package toti.lib.database.base.support;

import java.sql.SQLException;

import toti.lib.database.querybuilder.QueryBuilder;
import toti.lib.common.structures.ThrowingConsumer;

public interface QueryBuilderConsumer extends ThrowingConsumer<QueryBuilder, SQLException> {

	void accept(QueryBuilder queryBuilder) throws SQLException;
}
