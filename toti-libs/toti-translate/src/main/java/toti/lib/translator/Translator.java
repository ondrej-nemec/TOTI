package toti.lib.translator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Translator {
/*
upravit klic na: 		namespace:klic.klic....
mozna nacitat on demand
pridat default namespace: messages/konfigurovatelny -- lepsi pro moduly
*/

	private final String defNamespace;
	private final String selectedLang;
	private final Map<String, Map<Object, Object>> cache;
	private final Set<String> folders;

    public Translator(Set<String> folders) {
        this("messages", "", folders);
    }

    public Translator(String defNamespace, String selectedlang, Set<String> folders) {
		this(defNamespace, selectedlang, folders, new HashMap<>());
	}

    private Translator(String defNamespace, String selectedlang, Set<String> folders, Map<String, Map<Object, Object>> cache) {
        this.defNamespace = defNamespace;
		this.selectedLang = selectedlang;
		this.folders = folders;
		this.cache = cache;
    }

	public Translator withNamespace(String defNamespace) {
		return new Translator(defNamespace, selectedLang, folders, cache);
	}

	public Translator withLang(String selectedLang) {
		return new Translator(defNamespace, selectedLang, folders, cache);
	}

	public String translate(String key) {
		return translate(selectedLang, key, new HashMap<>());
	}

	public String translate(String key, Map<String, Object> variables) {
		return translate(selectedLang, key, variables);
	}

	protected String translate(String lang, String key, Map<String, Object> variables) {
		String namespace = defNamespace;
		if (key.contains(":")) {
			String[] split = key.split(":", 2);
			namespace = split[0];
			key = split[1];
		}
		return translate(namespace, lang, key, variables);
	}

	public String translate(String namespace, String lang, String key, Map<String, Object> variables) {
		String id = namespace + "_" + key;
		Map<Object, Object> dataset = cache.get(id);
		if (dataset == null) {
			dataset = getDataset(namespace, lang);
			cache.put(id, dataset);
		}
		return translate(namespace, lang, key, variables, dataset);
	}

	protected  String translate(String namespace, String lang, String key, Map<String, Object> variables, Map<Object, Object> dataset) {
		Object translated = dataset.get(key);
		if (translated == null) {
			translated = namespace + ":" + key;
		}
		return replaceVariables(translated.toString(), variables);
	}

	protected String replaceVariables(String value, Map<String, Object> variables) {
		for (String varName : variables.keySet()) {
			Object variable = variables.get(varName);
			value = value.replaceAll("\\%" + varName + "\\%", variable == null ? "" : variable.toString());
		}
		return value;
	}

	protected Map<Object, Object> getDataset(String namespace, String lang) {
		Map<Object, Object> result = new HashMap<>();
		for (MessagesFile loader : Arrays.asList(new PropertiesMessagesFile(), new XmlMessagesFile())) {
			String fileFormat = getFileFormat(namespace, lang, loader.getExtension());
			String dirFormat = getDirFormat(namespace, lang, loader.getExtension());
			for (String folder : folders) {
				result.putAll(load(loader, folder, fileFormat));
				result.putAll(load(loader, folder, dirFormat));
			}
		}
		return result;
	}

	private Map<Object, Object> load(MessagesFile loader, String folder, String format) {
		try {
			return loader.getMessages(folder + "/" + format);
		} catch (Exception ex) {
			// TODO need logging
			return new HashMap<>();
		}
	}
	
	private String getFileFormat(String namespace, String lang, String ext) {
		if (lang.isEmpty()) {
			return String.format("%s.%s", namespace, ext);
		}
		return String.format("%s.%s.%s", namespace, lang, ext);
	}
	
	private String getDirFormat(String namespace, String lang, String ext) {
		if (lang.isEmpty()) {
			return String.format("%s.%s", namespace, ext);
		}
		return String.format("%s/%s.%s", lang, namespace, ext);
	}

	/*public String translate(String key, Map<String, Object> variables) {
		// TODO
		//
		// split - pokud jde
		// pokud nepujde, default
		// taky vybrat podle jazyku - opet def
		///
		MessagesFile file = null; // TODO
		if (file == null) {
			return key;
		}
		String message = translate(file, key, variables);
		if (message == null) {
			return key;
		}
		return message;
	}

	// TODO test it
	protected String translate(MessagesFile file, String key, Map<String, Object> variables) {
		String message = file.getMessage(key);
		if (message == null) {
			return null;
		}
		return replaceVariables(message, variables);
	}*/

}
