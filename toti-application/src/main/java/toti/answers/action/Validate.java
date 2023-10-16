package toti.answers.action;

import ji.translator.Translator;
import toti.answers.request.Identity;
import toti.answers.request.Request;

public interface Validate {

	void validate(Request request, Translator translator, Identity identity) throws RequestInterruptedException;
	
}
