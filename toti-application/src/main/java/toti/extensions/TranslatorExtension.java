package toti.extensions;

import toti.answers.request.Identity;

public interface TranslatorExtension {

	Translator getTranslator(Identity identity);
	
}
