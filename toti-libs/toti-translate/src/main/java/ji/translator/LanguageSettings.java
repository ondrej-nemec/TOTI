package ji.translator;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class LanguageSettings {
	
	private TransProfiler profiler = null;

	private final Locale defaultLang;
	
	private final List<Locale> locales;

	public LanguageSettings(List<Locale> locales) {
		this(null, locales);
	}

	public LanguageSettings(String defaultLang, List<Locale> locales) {
		List<Locale> auxLocales = new LinkedList<>(locales);
		Locale locale = null;
		if (defaultLang == null) {
			if (locales.size() == 0) {
				locale = new Locale(
					java.util.Locale.getDefault().toString(),
					true,
					Arrays.asList(java.util.Locale.getDefault().getLanguage())
				);
				auxLocales.add(locale);
			} else {
				locale = locales.get(0);
			}
		} else {
			for (Locale loc : locales) {
				if (defaultLang.equals(loc.getLang())) {
					locale = loc;
				}
			}
			if (locale == null) {
				locale = new Locale(defaultLang, true, Arrays.asList());
				auxLocales.add(locale);
			}
		}
		this.defaultLang = locale;
		this.locales = auxLocales;
	}

	public Locale getDefaultLang() {
		return defaultLang;
	}

	public List<Locale> getLocales() {
		return locales;
	}

	@Override
	public String toString() {
		return "LanguageSettings [defaultLang=" + defaultLang + ", locales=" + locales + "]";
	}
	
	public void setProfiler(TransProfiler profiler) {
		this.profiler = profiler;
	}
	
	public TransProfiler getProfiler() {
		return profiler;
	}
	
}
