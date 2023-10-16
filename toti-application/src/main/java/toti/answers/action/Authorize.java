package toti.answers.action;

import ji.translator.Translator;
import toti.answers.request.Identity;
import toti.answers.request.Request;

public interface Authorize {

	void authorize(Request request, Translator translator, Identity identity) throws RequestInterruptedException;
	
}
