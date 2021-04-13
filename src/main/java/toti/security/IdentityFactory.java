package toti.security;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import common.structures.Tuple2;

public class IdentityFactory {

	private final static String LOCALE_COOKIE_NAME = "Language";
	private final static String LOCALE_HEADER_NAME = "Accept-Language";
	private final static String SESSION_COOKIE_NAME = "SessionID";
	private final static String SESSION_HEADER_NAME = "Authorization";
	
	private final String defLang;
	private final AuthenticationCache cache;
	
	public IdentityFactory(String defLang, AuthenticationCache cache) {
		this.defLang = defLang;
		this.cache = cache;
	}
	
	public List<String> getResponseHeaders(Identity identity) throws IOException {
		List<String> headers = new LinkedList<>();
		headers.add("Set-Cookie: "
				+ LOCALE_COOKIE_NAME + "=" + identity.getLocale() // .toLanguageTag()
				+ "; Path=/"
				+ "; SameSite=Strict");
		if (!identity.isAnonymous()) {
			headers.add(
				"Set-Cookie: "
				+ SESSION_COOKIE_NAME + "=" + identity.getToken()
				+ "; HttpOnly"
				+ "; Path=/"
				+ "; SameSite=Strict"
				+ "; Max-Age=" + (identity.getExpirationTime() / 1000)
			);
		} else if (identity.getExpirationTime() < 0) {
			headers.add(
					"Set-Cookie: "
					+ SESSION_COOKIE_NAME + "="
					+ "; HttpOnly"
					+ "; Path=/"
					+ "; SameSite=Strict"
					+ "; Max-Age=" + 0
				);
		}
		cache.save(identity.getId(), identity.getUser());
		return headers;
	}
	
	public Identity createIdentity(Properties headers, String IP) {
		Tuple2<String, Boolean> token = getToken(headers);
		return new Identity(IP, getLocale(headers), headers, token._1(), token._2());
	}
	
	private Tuple2<String, Boolean> getToken(Properties headers) {
		String token = null;
		boolean apiAllowed = false;
		if (headers.get(SESSION_HEADER_NAME) != null) {
			String[] vals = headers.get(SESSION_HEADER_NAME).toString().split(" ", 2);
			if (vals.length == 2) {
				token = vals[1];
				apiAllowed = true;
			}
		} else {
			token = getCookieValue(headers, SESSION_COOKIE_NAME);
		}
		return new Tuple2<>(token, apiAllowed);
	}
	
	private Locale getLocale(Properties header) {
		String cookieLang = getCookieValue(header, LOCALE_COOKIE_NAME);
		if (cookieLang != null) {
			return resolveLocale(cookieLang);
		}
		String lang = header.getProperty(LOCALE_HEADER_NAME);
		if (lang == null) {
			return resolveLocale(defLang);
		} else {
			String locale = lang.split(" ", 2)[0].split(";")[0].split(",")[0].trim();
			return resolveLocale(locale);
		}
	}
	
	private Locale resolveLocale(String locale) {
		return locale.contains("_") ? new Locale(locale) : Locale.forLanguageTag(locale);
	}

	// TODO test this method
	protected String getCookieValue(Properties header, String cookieName) {
		if (header.get("Cookie") != null) {
			String[] cookiesArray = header.get("Cookie").toString().split(";");
			for (String cookies : cookiesArray) {
				String[] cookie = cookies.split("=", 2);
				if (cookie.length == 2 && cookie[0].trim().equals(cookieName)) {
					return cookie[1].trim();
				}
			}
		}
		return null;
	}
	
}
