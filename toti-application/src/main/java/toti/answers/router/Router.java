package toti.answers.router;

import java.util.HashMap;
import java.util.Map;

public class Router {

	private Map<String, String> map = new HashMap<>();
	
	private String redirectOnNotLoggedInUser = null;
	
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

	public String getRedirectOnNotLoggedInUser() {
		return redirectOnNotLoggedInUser;
	}

	public void setRedirectOnNotLoggedInUser(String redirectOnNotLoggedInUser) {
		this.redirectOnNotLoggedInUser = redirectOnNotLoggedInUser;
	}
	
}
