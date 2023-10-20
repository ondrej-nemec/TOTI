package toti.answers.request;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import ji.translator.Locale;
import ji.translator.Translator;
import toti.answers.Headers;
import toti.extensions.OnSession;
import ji.common.structures.MapDictionary;
import ji.socketCommunication.http.structures.RequestParameters;

public class IdentityFactory {

	private final static String LOCALE_COOKIE_NAME = "Language";
	private final static String LOCALE_HEADER_NAME = "Accept-Language";
	private final static String SESSION_COOKIE_NAME = "SessionID";
	private final static String SESSION_HEADER_NAME = "Authorization";
//	private final static String PAGE_ID_HEADER_NAME = "PageId";
//	private final static String PAGE_ID_COOKIE_NAME = "PageId";
	public static final String CSRF_TOKEN_PARAMETER = "_csrf_token";
	
	private final String defLang;
	private final Translator translator;
	private final SessionUserProvider sup;
	private final List<OnSession> sessions;
	
	public IdentityFactory(Translator translator, String defLang, List<OnSession> sessions, SessionUserProvider sup) {
		this.translator = translator;
		this.defLang = defLang;
		this.sup = sup;
		this.sessions = sessions;
	}
	
	public MapDictionary<String> getSpace(String name, Identity identity) {
		return identity.getSessionSpace(name);
	}

	public Identity createIdentity(Headers requestHeaders, MapDictionary<String> queryParameters, RequestParameters bodyParameters, String ip) {
		Locale locale = getLocale(requestHeaders);
		Identity identity = new Identity(ip, locale);
		if (sup != null) {
			Optional<String> cookieToken = getCookieToken(requestHeaders);
			Optional<String> csrfToken = getCsrfToken(bodyParameters);
			Optional<String> headerToken = getHeaderToken(requestHeaders);
			
			Optional<LoggedUser> user = sup.getUser(headerToken, cookieToken, csrfToken);
			if (user.isPresent()) {
				identity.setUser(user.get());
			}
		}
		sessions.forEach((session)->{
			session.onRequestStart(
				identity, identity.getSessionSpace(session),
				requestHeaders, queryParameters, bodyParameters
			);
		});
		return identity;
	}
	
	public void finalizeIdentity(Identity identity, Headers responseHeaders) throws IOException {
		if (sup != null) {
			responseHeaders.addHeader(
				"Set-Cookie", 
				LOCALE_COOKIE_NAME + "=" + identity.getLocale().getLang() // .toLanguageTag()
				+ "; Path=/"
				+ "; SameSite=Strict"
			);
			
			if (identity.isAnonymous() || identity.getUser().getExpirationTime()  < 0) {
				responseHeaders.addHeader(
					"Set-Cookie", 
					SESSION_COOKIE_NAME + "="
						+ "; HttpOnly"
						+ "; Path=/"
						+ "; SameSite=Strict"
						+ "; Max-Age=" + 0
				);
			} else if (identity.getUser().getCookieToken().isPresent())  {
				responseHeaders.addHeader(
					"Set-Cookie",
					SESSION_COOKIE_NAME + "=" + identity.getUser().getCookieToken().get()
					+ "; HttpOnly"
					+ "; Path=/"
					+ "; SameSite=Strict"
					+ "; Max-Age=" + (identity.getUser().getExpirationTime() / 1000)
				);
			}
			Optional<LoggedUser> user = Optional.empty();
			if (identity.isPresent()) {
				user = Optional.of(identity.getUser());
			}
			sup.saveUser(user);
		}
		sessions.forEach((session)->{
			session.onRequestEnd(
				identity, identity.getSessionSpace(session), responseHeaders
			);
		});
		/*if (identity.getPageId() != null) {
			responseHeaders.addHeader(
				"Set-Cookie", PAGE_ID_COOKIE_NAME + "=" + identity.getPageId()
				+ "; SameSite=Strict"
			);
		}*/
	}
	
/*
	private String getPageId(Headers headers) {
		Object pageHeader = headers.getHeader(PAGE_ID_HEADER_NAME);
		if (pageHeader == null) {
			return ("Page_" + new Random().nextDouble()).replace(".", "");
		}
		return pageHeader.toString();
	}
	*/
	/**************************/

	private Optional<String> getHeaderToken(Headers requestHeaders) {
		if (requestHeaders.containsHeader(SESSION_HEADER_NAME)) {
			String[] vals = requestHeaders.getHeader(SESSION_HEADER_NAME).toString().split(" ", 2);
			if (vals.length == 2) {
				return Optional.of(vals[1]);
			}
		}
		return Optional.empty();
	}

	private Optional<String> getCookieToken(Headers requestHeaders) {
		return requestHeaders.getCookieValue(SESSION_COOKIE_NAME);
	}
	
	public Optional<String> getCsrfToken(RequestParameters parameters) {
		if (parameters.containsKey(CSRF_TOKEN_PARAMETER)) {
			String csrfToken = parameters.getString(CSRF_TOKEN_PARAMETER);
			parameters.remove(CSRF_TOKEN_PARAMETER);
			return Optional.of(csrfToken);
		}
		return Optional.empty();
	}
	
	/***************************/
	
	private Locale getLocale(Headers headers) {
		Optional<String> cookieLang = headers.getCookieValue(LOCALE_COOKIE_NAME);
		if (cookieLang.isPresent()) {
			return resolveLocale(cookieLang.get());
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
