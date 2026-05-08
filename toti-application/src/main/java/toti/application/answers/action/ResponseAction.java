package toti.application.answers.action;

import toti.application.answers.request.Identity;
import toti.application.answers.request.Request;
import toti.application.answers.response.Response;

public interface ResponseAction {

	Response create(Request request, Identity identity);
	
}
