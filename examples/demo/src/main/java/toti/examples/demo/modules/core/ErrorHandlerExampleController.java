package toti.examples.demo.modules.core;

import toti.core.annotations.Controller;
import toti.core.answers.action.ResponseAction;
import toti.core.answers.response.Response;
import toti.core.extensions.CustomErrorHandler;
import toti.lib.tcpip.enums.StatusCode;

@Controller("error")
public class ErrorHandlerExampleController implements CustomErrorHandler {

	@Override
	public ResponseAction onError(StatusCode status, Throwable t) {
		return (request, identity)->{
			return Response.create(status).getText("Ups, something happend: " + t.getMessage());
		};
	}

}
