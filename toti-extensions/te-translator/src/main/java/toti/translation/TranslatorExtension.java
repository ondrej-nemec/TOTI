package toti.translation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import ji.common.functions.Env;
import ji.common.structures.MapDictionary;
import ji.translator.LanguageSettings;
import ji.translator.Locale;
import ji.translator.Translator;
import toti.answers.Headers;
import toti.answers.request.Identity;
import toti.application.register.Register;
import toti.extensions.Extension;
import toti.http.http.structures.RequestParameters;

public class TranslatorExtension implements toti.extensions.TranslatorExtension, Extension {
	
	private final static String LOCALE_COOKIE_NAME = "Language";
	private final static String LOCALE_HEADER_NAME = "Accept-Language";
	private final static String NAME = "selected-language";
	
	private final LanguageSettings langSettings;
	private final Logger logger;
	private final Set<String> paths;
	
	private Translator translator;

	public TranslatorExtension(Env env, Logger logger) {
		this(parseLangSettings(env.getModule("lang")), logger);
	}
	
	private static LanguageSettings parseLangSettings(Env env) {
		if (env.getString("locales") != null) {  
			List<Locale> locales = new LinkedList<>();
			for (String l : env.getString("locales").split(",")) {
				String locale = l.trim();
				Env langConf = env.getModule("locale").getModule(locale);
				Boolean ltr = langConf.getBoolean("ltr");
				String substitutions = langConf.getString("substitutions");
				locales.add(new Locale(
					locale,
					ltr == null ? true : ltr,
					substitutions == null ? Arrays.asList() : Arrays.asList(substitutions.split(",")) 
				));
			}
			return new LanguageSettings(env.getString("default"), locales);
		}
		return new LanguageSettings(java.util.Locale.getDefault().toString(), Arrays.asList());
	}

	public TranslatorExtension(LanguageSettings langSettings, Logger logger) {
		this.langSettings = langSettings;
		this.logger = logger;
		this.paths = new HashSet<>();
	}
	
	@Override
	public String getIdentifier() {
		return getClass().getName();
	}

	@Override
	public void init(Env appEnv, Register register) {
		this.translator = Translator.create(langSettings, paths, logger);
	}
	
	public void addTranslationPath(String path) {
		this.paths.add(path);
	}

	@Override
	public void onRequestStart(Identity identity, MapDictionary<String> sessionSpace, Headers requestHeaders,
		MapDictionary<String> queryParams, RequestParameters requestBody) {
		Locale locale = getLocale(requestHeaders);
		identity.getSessionSpace(this).put(NAME, locale.getLang());
	}
	
	private Locale getLocale(Headers headers) {
		Optional<String> cookieLang = headers.getCookieValue(LOCALE_COOKIE_NAME);
		if (cookieLang.isPresent()) {
			return resolveLocale(cookieLang.get());
		}
		Object lang = headers.getHeader(LOCALE_HEADER_NAME);
		if (lang == null) {
			return resolveLocale(langSettings.getDefaultLang().getLang());
		} else {
			String locale = lang.toString().split(" ", 2)[0].split(";")[0].split(",")[0].trim();
			return resolveLocale(locale);
		}
	}
	
	private Locale resolveLocale(String locale) {
		Locale loc = translator.getLocale(locale);
		if (loc == null) {
			return translator.getLocale(langSettings.getDefaultLang().getLang());
		}
		return loc;
	}

	@Override
	public void onRequestEnd(Identity identity, MapDictionary<String> sessionSpace, Headers responseHeaders) {
		responseHeaders.addHeader(
			"Set-Cookie", 
			LOCALE_COOKIE_NAME + "=" + identity.getSessionSpace(this).getString(NAME)
			+ "; Path=/"
			+ "; SameSite=Strict"
		);
	}

	@Override
	public toti.extensions.Translator getTranslator(Identity identity) {
		return new TranslatorImpl(translator.withLocale(identity.getSessionSpace(this).getString(NAME)));
	}

	@Override
	public void onApplicationStart() throws Exception {}

	@Override
	public void onApplicationStop() throws Exception {}

}
