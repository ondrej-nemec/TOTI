package ji.database.support;

import java.sql.SQLException;

import ji.common.structures.ThrowingFunction;
import ji.querybuilder.QueryBuilder;

public interface QueryBuilderFunction<T> extends ThrowingFunction<QueryBuilder, T, SQLException> {

	T apply(QueryBuilder queryBuilder) throws SQLException;
}
