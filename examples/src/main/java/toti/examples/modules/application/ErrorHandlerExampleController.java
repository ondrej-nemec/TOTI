package toti.examples.modules.application;

import toti.application.annotations.Controller;
import toti.application.answers.action.ResponseAction;
import toti.application.answers.response.Response;
import toti.application.extensions.CustomErrorHandler;
import toti.lib.tcpip.enums.StatusCode;

@Controller("error")
public class ErrorHandlerExampleController implements CustomErrorHandler {

	@Override
	public ResponseAction onError(StatusCode status, Throwable t) {
		return (request, transator, identity)->{
			return Response.create(status).getText("Ups, something happend: " + t.getMessage());
		};
	}

}
