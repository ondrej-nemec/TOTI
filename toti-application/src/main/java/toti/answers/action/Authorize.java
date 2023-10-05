package toti.answers.action;

import ji.translator.Translator;
import toti.answers.request.Request;
import toti.security.Identity;

public interface Authorize {

	void authorize(Request request, Translator translator, Identity identity) throws RequestInterruptedException;
	
}