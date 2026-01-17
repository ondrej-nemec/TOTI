package toti.application.answers.action;

import toti.application.answers.request.Identity;
import toti.application.answers.request.Request;
import toti.application.answers.response.Response;
import toti.application.extensions.Translator;

public interface ResponseAction {

	Response create(Request request, Translator translator, Identity identity);
	
}
