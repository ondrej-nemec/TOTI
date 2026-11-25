package toti.core.answers.router;

import java.util.HashMap;
import java.util.Map;

public class Router {

	private final String basePath;
	private final Map<String, String> map = new HashMap<>();

	public Router(String basePath) {
		this.basePath = basePath;
	}
	
	public <T> void addUrl(String origin, String target) {
		map.put(origin, !"".equals(basePath) && target.startsWith(basePath) ? target.replace(basePath, "") : target);
	}
	
	public String getUrlMapping(String url) {
		return map.get(url);
	}
	
	@Override
	public String toString() {
		return "Router:" + map.toString();
	}
}
