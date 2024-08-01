package ji.querybuilder.builder_impl.share;

import java.util.Map;

public interface ParametrizedSql {

	default String parse(String sql, Map<String, String> parameters) {
		String query = sql;
		for (String name : parameters.keySet()) {
			query = query.replace(name, parameters.get(name));
		}
		return query;
	}

}
