package toti;

import java.util.HashMap;
import java.util.Map;

public class Router {

	private Map<String, String> map = new HashMap<>();
	
	private String redirectOnNotLogedUser = null;
	
	public void addUrl(String origin, String destination) {
		map.put(origin, destination);
	}
	
	protected String getUrlMapping(String url) {
		return map.get(url);
	}
	
	@Override
	public String toString() {
		return "Router:" + map.toString();
	}

	public String getRedirectOnNotLogedUser() {
		return redirectOnNotLogedUser;
	}

	public void setRedirectOnNotLogedUser(String redirectOnNotLogedUser) {
		this.redirectOnNotLogedUser = redirectOnNotLogedUser;
	}
	
}
