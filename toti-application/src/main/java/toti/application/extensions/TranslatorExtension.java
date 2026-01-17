package toti.application.extensions;

import toti.application.answers.request.Identity;

public interface TranslatorExtension {

	Translator getTranslator(Identity identity);
	
}
