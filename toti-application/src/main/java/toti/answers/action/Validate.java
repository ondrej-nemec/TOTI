package toti.answers.action;

import toti.answers.request.Identity;
import toti.answers.request.Request;
import toti.extensions.Translator;

public interface Validate {

	void validate(Request request, Translator translator, Identity identity) throws RequestInterruptedException;
	
}
