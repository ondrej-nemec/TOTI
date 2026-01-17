package toti.lib.database.base.support;

import java.sql.SQLException;

import toti.lib.database.querybuilder.QueryBuilder;
import toti.lib.common.structures.ThrowingFunction;

public interface QueryBuilderFunction<T> extends ThrowingFunction<QueryBuilder, T, SQLException> {

	T apply(QueryBuilder queryBuilder) throws SQLException;
}
