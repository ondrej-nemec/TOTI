package toti.profiler;

import java.util.Map;

import ji.json.Jsonable;

public class TransLog implements Jsonable {

	private final String locale;
	private final String module;
	private final String key;
	private final Map<String, Object> params;
	
	public TransLog(String locale, String module, String key, Map<String, Object> params) {
		this.locale = locale;
		this.module = module;
		this.key = key;
		this.params = params;
	}

	public String getLocale() {
		return locale;
	}

	public String getModule() {
		return module;
	}

	public String getKey() {
		return key;
	}

	public Map<String, Object> getParams() {
		return params;
	}
		
}
