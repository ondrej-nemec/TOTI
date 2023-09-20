package toti.answers.action;

import ji.translator.Translator;
import toti.answers.request.Request;
import toti.security.Identity;

public interface Prevalidate {

	void prevalidate(Request request, Translator translator, Identity identity) throws RequestInterruptedException;
	
}
