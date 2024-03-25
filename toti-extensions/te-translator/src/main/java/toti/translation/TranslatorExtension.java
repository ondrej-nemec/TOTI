package toti.translation;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import ji.common.functions.Env;
import ji.common.structures.MapDictionary;
import ji.socketCommunication.http.structures.RequestParameters;
import ji.translator.LanguageSettings;
import ji.translator.Locale;
import ji.translator.Translator;
import toti.answers.Headers;
import toti.answers.request.Identity;
import toti.application.register.Register;
import toti.extensions.OnSessionExtension;

public class TranslatorExtension implements toti.extensions.TranslatorExtension, OnSessionExtension {
	
	private final static String LOCALE_COOKIE_NAME = "Language";
	private final static String LOCALE_HEADER_NAME = "Accept-Language";
	
//	private final String defLang;
//	private final Translator translator;
	
	
	public TranslatorExtension() {
		/*
		if (translator == null) {
			LanguageSettings langSettings = getLangSettings(env);
			langSettings.setProfiler(profiler);
			this.translator = Translator.create(langSettings, trans, loggerFactory.apply(appIdentifier, "translator"));
		}
		*/
		/*
		
	
	private LanguageSettings getLangSettings(Env conf) {
		if (langSettings != null) {
			return langSettings;
		}
		Env env = conf.getModule("lang");
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
		*/
	}
	
	@Override
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(Env appEnv, Register register) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRequestStart(Identity identity, MapDictionary<String> sessionSpace, Headers requestHeaders,
		MapDictionary<String> queryParams, RequestParameters requestBody) {
		Locale locale = getLocale(requestHeaders);
		// TODO set to identity
	}
	
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

	@Override
	public void onRequestEnd(Identity identity, MapDictionary<String> sessionSpace, Headers responseHeaders) {
		responseHeaders.addHeader(
			"Set-Cookie", 
			LOCALE_COOKIE_NAME + "=" + identity.getLocale().getLang() // .toLanguageTag()
			+ "; Path=/"
			+ "; SameSite=Strict"
		);
	}

}
