package toti.answers.action;

import toti.answers.request.Identity;
import toti.answers.request.Request;
import toti.extensions.Translator;

public interface Prevalidate {

	void prevalidate(Request request, Translator translator, Identity identity) throws RequestInterruptedException;
	
}
