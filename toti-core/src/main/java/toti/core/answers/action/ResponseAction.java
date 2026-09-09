package toti.core.answers.action;

import toti.core.answers.request.Request;
import toti.core.answers.response.Response;
import toti.core.answers.session.Identity;

public interface ResponseAction {

	Response create(Request request, Identity identity);
	
}
