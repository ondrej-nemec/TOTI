package ji.database.support;

import java.sql.SQLException;

import ji.querybuilder.QueryBuilder;
import toti.common.structures.ThrowingFunction;

public interface QueryBuilderFunction<T> extends ThrowingFunction<QueryBuilder, T, SQLException> {

	T apply(QueryBuilder queryBuilder) throws SQLException;
}
