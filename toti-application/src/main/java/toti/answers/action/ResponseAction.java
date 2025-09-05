package toti.answers.action;

import toti.answers.request.Identity;
import toti.answers.request.Request;
import toti.answers.response.Response;
import toti.extensions.Translator;

public interface ResponseAction {

	Response create(Request request, Translator translator, Identity identity);
	
}
