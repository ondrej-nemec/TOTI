package translator;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import common.Logger;
import common.functions.PropertiesLoader;

public class PropertiesTranslator implements Translator {
		
	private final Logger logger;
	
	private final Map<String, Properties> resources;
	
	private final Map<String, String> files;
	
	private String locale;
	
	public static PropertiesTranslator create(Logger logger, String... files) {
		Map<String, String> filesMap = new HashMap<>();
		for (String file : files) {
			filesMap.put(new File(file).getName(), file);
		}
		return new PropertiesTranslator(logger, java.util.Locale.getDefault().toString(), new HashMap<>(), filesMap);
	}
	
	private PropertiesTranslator(
			Logger logger,
			String locale,
			Map<String, Properties> resources,
			Map<String, String> files) {
		this.logger = logger;
		this.resources = resources;
		this.locale = locale;
		this.files = files;
	}
	
	@Override
	public Translator withLocale(Locale locale) {
		return new PropertiesTranslator(logger, locale.getLang(), resources, files);
	}
	
	@Override
	public void setLocale(Locale locale) {
		this.locale = locale.getLang();
	}

	@Override
	public String translate(String key, Map<String, Object> variables, String locale) {
		return transWithVars(key, variables, locale);
	}
	
	private String transWithVars(String key, Map<String, Object> variables, String locale) {
		String value = trans(key, locale, variables);
		for (String varName : variables.keySet()) {
		    Object variable = variables.get(varName);
			value = value.replaceAll("\\%" + varName + "\\%", variable == null ? "" : variable.toString());
		}
		return value;
	}

	private String trans(String key, String locale, Map<String, Object> variables) {
		String[] split = key.split("\\.", 2);
		
		Properties prop = null;
		String module = "";
		if (split.length > 1) {
			prop = load(locale, split[0]);
			if (prop.isEmpty()) {
				module = "messages";
				prop = load(locale, "messages");
			} else {
				module = split[0];
				key = split[1];
			}
		}
		if (prop == null) {
			module = "messages";
			prop = load(locale, "messages");
		}
		
		String value= prop.getProperty(key);
		if (value == null) {
			logger.warn(String.format(
				"Missing translation [%s] in %s: %s (%s)",
				locale,
				module,
				key,
				variables
			));
			return key;
		}
		return value;
	}
	
	private Properties load(String locale, String module) {
		String localeKey = locale + "--" + module;
		if (resources.get(localeKey) != null) {
			return resources.get(localeKey);
		}
		logger.debug("No properties for: Locale=" + locale + ", module=" + module + ". Trying load.");
		
		Properties messages = new Properties();
		String path = files.get(module);
		if (path == null) {
			logger.warn("No path for module " + module);
			return messages;
		}
		messages.putAll(loadFile(locale, path));
		resources.put(localeKey, messages);
		return messages;
	}
	
	private Properties loadFile(String locale, String path) {
		String[] names = new String [] {
			String.format("%s.%s.properties", path, locale),
			String.format("%s.properties", path)
		};
		for(String name : names) {
			try {
				return PropertiesLoader.loadProperties(name);
			} catch (IOException e) {
				logger.warn("Cannot load properies file: " + name);
			}
		}
		return new Properties();
	}

	@Override
	public Locale getLocale() {
		return new Locale(locale, true, Arrays.asList());
	}

	@Override
	public Locale getLocale(String locale) {
		return new Locale(locale, true, Arrays.asList());
	}

}
