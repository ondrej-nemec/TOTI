package mvc.authentication;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class Language {
	
	private final static String COOKIE_NAME = "Language";
	
	private final String defLang;

	public Language(String defLang) {
		this.defLang = defLang;
	}
	
	public Locale getLocale(Properties header) {
		String cookieLang = Cookie.getCookieValue(header, COOKIE_NAME);
		if (cookieLang != null) {
			return new Locale(cookieLang);
		}
		String lang = header.getProperty("Accept-Language");
		if (lang == null) {
			return new Locale(defLang);
		} else {
			String locale = lang.split(" ", 2)[0].split(";")[0].split(",")[0].trim().replace("-", "_");
			return new Locale(locale);
		}
	}
	
	public List<String> getHeaders(Locale locale) {
		return Arrays.asList(
				"Set-Cookie: "
				+ COOKIE_NAME + "=" + locale
				+ "; Path=/"
				+ "; SameSite=Strict"
			);
	}
	
}
