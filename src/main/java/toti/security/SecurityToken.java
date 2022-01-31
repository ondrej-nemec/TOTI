package toti.security;

import java.util.HashMap;
import java.util.Map;

import ji.json.Jsonable;

public class SecurityToken implements Jsonable {

	private final String accessToken;
	private final long expriredIn;
	
	public SecurityToken(String accessToken, long expriredIn) {
		this.accessToken = accessToken;
		this.expriredIn = expriredIn;
	}
	
	public String getAccessToken() {
		return accessToken;
	}

	public long getExpriredIn() {
		return expriredIn;
	}

	@Override
	public Object toJson() {
		Map<String, Object> json = new HashMap<>();
		json.put("access_token", accessToken);
		json.put("expires_in", expriredIn);
		return json;
	}
	
}
