package toti.answers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import ji.common.structures.DictionaryValue;

public class Headers {
	
	private final Map<String, List<Object>> headers;
	
	public Headers() {
		this(new HashMap<>());
	}
	
	public Headers(Map<String, List<Object>> headers) {
		this.headers = headers.entrySet().stream()
			.collect(Collectors.toMap(e->e.getKey(), e->new ArrayList<>(e.getValue())));
	}
	
	public Headers addHeader(String name, Object value) {
		String headerName = name.toLowerCase();
		if (!headers.containsKey(headerName)) {
			headers.put(headerName, new LinkedList<>());
		}
		headers.get(headerName).add(value);
		return this;
	}
	
	public Map<String, List<Object>> getHeaders() {
		return headers;
	}
	
	public void setHeaders(Map<String, List<Object>> headers) {
		this.headers.putAll(headers);
	}
	
	public List<Object> getHeaders(String name) {
		return headers.get(name.toLowerCase());
	}
	
	public Object getHeader(String name)  {
		String headerName = name.toLowerCase();
		if (headers.containsKey(headerName) && headers.get(headerName).size() > 0) {
			return headers.get(headerName).get(0);
		}
		return null;
	}
	
	public boolean containsHeader(String name) {
		String headerName = name.toLowerCase();
		return headers.containsKey(headerName) && headers.get(headerName).size() > 0;
	}
	
	public <T> T getHeader(String name, Class<T> clazz) {
		return new DictionaryValue(getHeader(name)).getValue(clazz);
	}
	
	/**
	 * Returns cookie value by name
	 * @param cookieName
	 * @return cookie value or null
	 */
	public Optional<String> getCookieValue(String cookieName) {
		if (containsHeader("Cookie")) {
			String[] cookiesArray = getHeader("Cookie").toString().split(";");
			for (String cookies : cookiesArray) {
				String[] cookie = cookies.split("=", 2);
				if (cookie.length == 2 && cookie[0].trim().equals(cookieName)) {
					String value = cookie[1].trim();
					if (value.trim().equalsIgnoreCase("null")) {
						return Optional.empty();
					}
					return Optional.of(value);
				}
			}
		}
		return  Optional.empty();
	}

	
	/**
	 * Determite if request is brower link or not
	 * <strong>Result is not 100% sure.</strong>
	 * @return true if request is asychronious
	 */
	public boolean isAsyncRequest() {
		Object destination = getHeader("Sec-Fetch-Dest");
		return destination != null && destination.toString().equals("empty");
	}
/*
	public Headers clone() {
		Headers clone = new Headers(new HashMap<>());
		headers.forEach((name, list)->{
			list.forEach(value->clone.addHeader(name, value));
		});
		return clone;
	}
	*/

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Headers other = (Headers) obj;
		if (headers == null) {
			if (other.headers != null) {
				return false;
			}
		} else if (!headers.equals(other.headers)) {
			return false;
		}
		return true;
	}
}
