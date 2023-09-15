package toti.answers.action;

import ji.translator.Translator;
import toti.answers.request.Request;
import toti.security.Identity;

public interface Authorize {

	void authrorize(Request request, Translator translator, Identity identity) throws RequestInterruptedException;
	
}
