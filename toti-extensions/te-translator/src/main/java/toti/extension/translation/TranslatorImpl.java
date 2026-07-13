package toti.extension.translation;

import java.util.Map;

import toti.lib.translator.Translator;

public class TranslatorImpl implements toti.core.extensions.Translator {

	private final Translator parent;
	
	public TranslatorImpl(Translator parent) {
		this.parent = parent;
	}
	
	@Override
	public String translate(String key) {
		return parent.translate(key);
	}

	@Override
	public String translate(String key, Map<String, Object> params) {
		return parent.translate(key, params);
	}

}
