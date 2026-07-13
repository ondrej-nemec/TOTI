package toti.core.answers.router;

import java.util.HashMap;
import java.util.Map;

public class Router {

	private Map<String, String> map = new HashMap<>();
	
	public <T> void addUrl(String origin, String target) {
		map.put(origin, target);
	}
	
	public String getUrlMapping(String url) {
		return map.get(url);
	}
	
	@Override
	public String toString() {
		return "Router:" + map.toString();
	}
}
