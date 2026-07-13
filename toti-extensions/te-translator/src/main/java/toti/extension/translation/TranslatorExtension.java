package toti.extension.translation;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import toti.core.answers.Headers;
import toti.core.answers.request.Identity;
import toti.core.application.register.Register;
import toti.core.extensions.Extension;
import toti.lib.common.structures.MapDictionary;
import toti.lib.files.env.Env;
import toti.lib.tcpip.structures.RequestParameters;
import toti.lib.translator.Translator;

public class TranslatorExtension implements Extension {
	
	private final static String LOCALE_COOKIE_NAME = "Language";
	private final static String LOCALE_HEADER_NAME = "Accept-Language";
	private final static String NAME = "selected-language";
	
	private final String defaultLanguage;
//	private final Logger logger;
	private final Set<String> paths;
	
	private final Translator translator;

	public TranslatorExtension(Env env, Logger logger) {
		this(env.getSection("lang").getString("namespace"), env.getSection("lang").getString("mainLang"), logger);
	}

	public TranslatorExtension(String defNamespace, String defaultLanguage, Logger logger) {
	//	this.logger = logger;
		this.paths = new HashSet<>();
		this.defaultLanguage = defaultLanguage == null ? "" : defaultLanguage;
		this.translator = new Translator(defNamespace == null ? "messages" : defNamespace, this.defaultLanguage, paths);
	}
	
	@Override
	public String getIdentifier() {
		return getClass().getName();
	}
	
	public void addTranslationPath(String path) {
		this.paths.add(path);
	}

	@Override
	public void onRequestStart(Identity identity, MapDictionary<String> sessionSpace, Headers requestHeaders,
		MapDictionary<String> queryParams, RequestParameters requestBody) {
		String selectedLang = getLocale(requestHeaders);
		sessionSpace.put(NAME, selectedLang);
		identity.setScope(
			toti.core.extensions.Translator.class,
			new TranslatorImpl(translator.withLang(selectedLang))
		);
	}
	
	private String getLocale(Headers headers) {
		Optional<String> cookieLang = headers.getCookieValue(LOCALE_COOKIE_NAME);
		if (cookieLang.isPresent()) {
			return cookieLang.get();
		}
		Object lang = headers.getHeader(LOCALE_HEADER_NAME);
		if (lang == null) {
			return defaultLanguage;
		} else {
			return lang.toString().split(" ", 2)[0].split(";")[0].split(",")[0].trim();
		}
	}

	@Override
	public void onRequestEnd(Identity identity, MapDictionary<String> sessionSpace, Headers responseHeaders) {
		responseHeaders.addHeader(
			"Set-Cookie", 
			LOCALE_COOKIE_NAME + "=" + sessionSpace.getString(NAME)
			+ "; Path=/"
			+ "; SameSite=Strict"
		);
	}

	public Translator getTranslator() {
		return translator;
	}

	@Override
	public void init(Env appEnv, Register register) {}

	@Override
	public void onApplicationStart() throws Exception {}

	@Override
	public void onApplicationStop() throws Exception {}

}
