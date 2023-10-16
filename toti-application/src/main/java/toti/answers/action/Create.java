package toti.answers.action;

import ji.translator.Translator;
import toti.answers.request.Identity;
import toti.answers.request.Request;
import toti.answers.response.Response;

public interface Create {

	Response create(Request request, Translator translator, Identity identity) throws RequestInterruptedException;
	
}
