package toti.core.answers.action;

import toti.core.answers.request.Identity;
import toti.core.answers.request.Request;
import toti.core.answers.response.Response;

public interface ResponseAction {

	Response create(Request request, Identity identity);
	
}
