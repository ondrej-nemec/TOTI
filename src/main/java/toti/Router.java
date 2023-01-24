package toti;

import java.util.HashMap;
import java.util.Map;

import toti.url.Link;

public class Router {

	private Map<String, String> map = new HashMap<>();
	
	private String redirectOnNotLoggedInUser = null;
	
	private CustomExceptionResponse customExceptionResponse = null;
	
	private final Link link;
	
	public Router(Link link) {
		this.link = link;
	}
	
	public Link getLink() {
		return link;
	}
	
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

	public String getRedirectOnNotLoggedInUser() {
		return redirectOnNotLoggedInUser;
	}
	
	public CustomExceptionResponse getCustomExceptionResponse() {
		return customExceptionResponse;
	}

	public void setRedirectOnNotLoggedInUser(String redirectOnNotLoggedInUser) {
		this.redirectOnNotLoggedInUser = redirectOnNotLoggedInUser;
	}
	
	public void setCustomExceptionResponse(CustomExceptionResponse customExceptionResponse) {
		this.customExceptionResponse = customExceptionResponse;
	}
	
}
