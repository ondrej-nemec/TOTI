package mvc.authentication;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class TokenType {
	
	public static TokenType NULL() {
		return new TokenType("", (token)->{
			return null;
		}, (identity)->{
			return new LinkedList<>();
		});
	}
	
	public static TokenType COOKIE() {
		return new TokenType("Cookie", (cookieString)->{
			if (cookieString == null) { return null; }
			String[] cookiesArray = cookieString.toString().split(";");
			for (String cookies : cookiesArray) {
				String[] cookie = cookies.split("=", 2);
				if (cookie.length == 2 && cookie[0].trim().equals("SessionID")) {
					return cookie[1].trim();
				}
			}
			return null;
		}, (identity)->{
			if (!identity.isPresent()) {
				return new LinkedList<>();
			}
			return Arrays.asList(
					"Set-Cookie: SessionID="
					+ identity.get().getToken()
					+ "; HttpOnly; SameSite=Strict;"
					+ " Max-Age="
					+ (identity.get().getExpired() / 1000)
			);
		});
	}
	
	public static TokenType AUTHENTIZATION() {
		return new TokenType("Authentization", (value)->{
			if (value == null) { return null; }
			String[] vals = value.split(" ", 2);
			if (vals.length == 2) {
				return vals[1];
			}
			return null;
		},(identity)->{
			return new LinkedList<>();
		});
	}
	
	private final String headerName;
	private final Function<String, String> fromValueToToken;
	private final Function<Optional, List<String>> createHeader;
	
	private TokenType(
			String headerName, 
			Function<String, String> fromValueToToken,
			Function<Optional, List<String>> createHeader) {
		this.headerName = headerName;
		this.createHeader = createHeader;
		this.fromValueToToken = fromValueToToken;
	}

	public Function<Optional, List<String>> getCreateHeader() {
		return createHeader;
	}

	public String getHeaderName() {
		return headerName;
	}

	public Function<String, String> getFromValueToToken() {
		return fromValueToToken;
	}
}
