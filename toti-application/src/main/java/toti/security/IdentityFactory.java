package toti.security;

import java.io.IOException;
import java.util.Random;

import ji.translator.Locale;
import ji.translator.Translator;
import toti.Headers;
import toti.profiler.Profiler;

import ji.common.structures.Tuple2;

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
	
	public void setResponseHeaders(Identity identity, Headers responseHeaders) throws IOException {
		/*List<String> headers = new LinkedList<>();
		headers.add("Set-Cookie: "
				+ LOCALE_COOKIE_NAME + "=" + identity.getLocale().getLang() // .toLanguageTag()
				+ "; Path=/"
				+ "; SameSite=Strict");*/
		responseHeaders.addHeader(
			"Set-Cookie", 
			LOCALE_COOKIE_NAME + "=" + identity.getLocale().getLang() // .toLanguageTag()
			+ "; Path=/"
			+ "; SameSite=Strict"
		);
		
		if (!identity.isAnonymous()) {
			/*headers.add(
				"Set-Cookie: "
				+ SESSION_COOKIE_NAME + "=" + identity.getToken()
				+ "; HttpOnly"
				+ "; Path=/"
				+ "; SameSite=Strict"
				+ "; Max-Age=" + (identity.getExpirationTime() / 1000)
			);*/
			responseHeaders.addHeader(
				"Set-Cookie",
				SESSION_COOKIE_NAME + "=" + identity.getToken()
				+ "; HttpOnly"
				+ "; Path=/"
				+ "; SameSite=Strict"
				+ "; Max-Age=" + (identity.getExpirationTime() / 1000)
			);
		} else if (identity.getExpirationTime() < 0) {
			/*headers.add(
					"Set-Cookie: "
					+ SESSION_COOKIE_NAME + "="
					+ "; HttpOnly"
					+ "; Path=/"
					+ "; SameSite=Strict"
					+ "; Max-Age=" + 0
				);*/
			responseHeaders.addHeader(
				"Set-Cookie", 
				SESSION_COOKIE_NAME + "="
					+ "; HttpOnly"
					+ "; Path=/"
					+ "; SameSite=Strict"
					+ "; Max-Age=" + 0
			);
		}
		if (identity.getPageId() != null) {
			responseHeaders.addHeader(
				"Set-Cookie", PAGE_ID_COOKIE_NAME + "=" + identity.getPageId()
				+ "; SameSite=Strict"
			);
		}
		//headers.addAll(identity.getResponseHeaders());
		////cache.save(identity.getId(), identity.getUser());
		//return headers;
	}
	
	public Identity createIdentity(Headers headers, String IP, Profiler profiler) {
		Tuple2<String, AuthMode> token = getToken(headers);
		Identity identity = new Identity(IP, getLocale(headers), token._1(), token._2());
		if (profiler.isUse()) {
			identity.setPageId(getPageId(headers)); // profiler.getCurrentPageId()
		}
		return identity;
	}

	private String getPageId(Headers headers) {
		Object pageHeader = headers.getHeader(PAGE_ID_HEADER_NAME);
		if (pageHeader == null) {
			return ("Page_" + new Random().nextDouble()).replace(".", "");
		}
		return pageHeader.toString();
	}

	private Tuple2<String, AuthMode> getToken(Headers headers) {
		String token = null;
		AuthMode mode = AuthMode.NO_TOKEN;
		if (headers.containsHeader(SESSION_HEADER_NAME)) {
			String[] vals = headers.getHeader(SESSION_HEADER_NAME).toString().split(" ", 2);
			if (vals.length == 2) {
				token = vals[1];
				mode = AuthMode.HEADER;
			}
		} else {
			token = headers.getCookieValue(SESSION_COOKIE_NAME);
			mode = AuthMode.COOKIE;
		}
		return new Tuple2<>(token, mode);
	}
	
	private Locale getLocale(Headers headers) {
		String cookieLang = headers.getCookieValue(LOCALE_COOKIE_NAME);
		if (cookieLang != null) {
			return resolveLocale(cookieLang);
		}
		Object lang = headers.getHeader(LOCALE_HEADER_NAME);
		if (lang == null) {
			return resolveLocale(defLang);
		} else {
			String locale = lang.toString().split(" ", 2)[0].split(";")[0].split(",")[0].trim();
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

}
