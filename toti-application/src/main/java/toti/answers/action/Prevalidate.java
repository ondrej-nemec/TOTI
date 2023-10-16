package toti.answers.action;

import ji.translator.Translator;
import toti.answers.request.Identity;
import toti.answers.request.Request;

public interface Prevalidate {

	void prevalidate(Request request, Translator translator, Identity identity) throws RequestInterruptedException;
	
}
