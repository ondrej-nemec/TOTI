package toti.profiler;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import common.annotations.MapperIgnored;

public class SqlLog {

	@MapperIgnored
	private final String id;

	private String sql;
	
	private List<Object> params = new LinkedList<>();
	
	private boolean isExecuted;
	
	private String preparedSql;
	private String replacedSql;
	private Map<String, String> builderParams;
	
	public SqlLog(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getSql() {
		return sql;
	}

	public List<Object> getParams() {
		return params;
	}

	public String getPreparedSql() {
		return preparedSql;
	}

	public String getReplacedSql() {
		return replacedSql;
	}

	public Map<String, String> getBuilderParams() {
		return builderParams;
	}

	public boolean isExecuted() {
		return isExecuted;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public void addParams(Object param) {
		this.params.add(param);
	}

	public void setExecuted() {
		this.isExecuted = true;
	}

	public void setBuilder(String preparedSql, String replacedSql, Map<String, String> builderParams) {
		this.preparedSql = preparedSql;
		this.replacedSql = replacedSql;
		this.builderParams = builderParams;
	}
	
}
