package toti.dbviewer;

import java.util.List;

import ji.database.support.DatabaseRow;

public class QueryResult {

	private final String sql;
	
	private final Integer resultCount;
	
	private final List<DatabaseRow> resultSet;
	
	private final String exception;

	public QueryResult(String sql, Integer resultCount, List<DatabaseRow> resultSet, String exception) {
		this.sql = sql;
		this.resultCount = resultCount;
		this.resultSet = resultSet;
		this.exception = exception;
	}

	public String getSql() {
		return sql;
	}

	public Integer getResultCount() {
		return resultCount;
	}

	public List<DatabaseRow> getResultSet() {
		return resultSet;
	}

	public String getException() {
		return exception;
	}
	
}
