package toti.security;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import translator.Locale;
import translator.Translator;

import java.util.Properties;
import java.util.Random;

import common.structures.Tuple2;

public class IdentityFactory {

	private final static String LOCALE_COOKIE_NAME = "Language";
	private final static String LOCALE_HEADER_NAME = "Accept-Language";
	private final static String SESSION_COOKIE_NAME = "SessionID";
	private final static String SESSION_HEADER_NAME = "Authorization";
	private final static String PAGE_ID_HEADER_NAME = "PageId";
	private final static String PAGE_ID_COOKIE_NAME = "PageId";
	
	private final String defLang;
	private final Translator translator;
	//private final AuthenticationCache cache;
	
	public IdentityFactory(Translator translator, String defLang/*, AuthenticationCache cache*/) {
		this.translator = translator;
		this.defLang = defLang;
	//	this.cache = cache;
	}
	
	public List<String> getResponseHeaders(Identity identity) throws IOException {
		List<String> headers = new LinkedList<>();
		headers.add("Set-Cookie: "
				+ LOCALE_COOKIE_NAME + "=" + identity.getLocale().getLang() // .toLanguageTag()
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
		if (identity.getPageId() != null) {
			headers.add(
				"Set-Cookie: "
				+ PAGE_ID_COOKIE_NAME + "=" + identity.getPageId()
			);
		}
		//cache.save(identity.getId(), identity.getUser());
		return headers;
	}
	
	public Identity createIdentity(Properties headers, String IP, boolean useProfiler) {
		Tuple2<String, Boolean> token = getToken(headers);
		Identity identity = new Identity(IP, getLocale(headers), headers, token._1(), token._2());
		if (useProfiler) {
			identity.setPageId(getPageId(headers));
		}
		return identity;
	}
	
	private String getPageId(Properties headers) {
		String pageHeader = headers.getProperty(PAGE_ID_HEADER_NAME);
		/*if (pageHeader == null) {
			pageHeader = Identity.getCookieValue(headers, PAGE_ID_COOKIE_NAME);
		}*/
		if (pageHeader == null) {
			return ("Page_" + new Random().nextDouble()).replace(".", "");
		}
		return pageHeader;
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
			token = Identity.getCookieValue(headers, SESSION_COOKIE_NAME);
		}
		return new Tuple2<>(token, apiAllowed);
	}
	
	private Locale getLocale(Properties header) {
		String cookieLang = Identity.getCookieValue(header, LOCALE_COOKIE_NAME);
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
		Locale loc = translator.getLocale(locale);
		if (loc == null) {
			return translator.getLocale(defLang);
		}
		return loc;
	}
/*
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
	*/
}
