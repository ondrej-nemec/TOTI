package toti.profiler;

import java.util.LinkedList;
import java.util.List;

import common.annotations.MapperIgnored;

public class SqlLog {

	@MapperIgnored
	private final String id;

	private String sql;
	
	private List<Object> params = new LinkedList<>();
	
	private boolean isExecuted;
	
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

}
