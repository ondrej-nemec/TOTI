package toti.samples.application.controllers;

import toti.annotations.Action;
import toti.annotations.Controller;
import toti.answers.action.ResponseAction;
import toti.answers.response.Response;
import toti.tcpip.enums.StatusCode;

@Controller("processing")
public class ProcessingController {

	@Action(path="action-chaing")
	public ResponseAction fullChain() {
		return (request, translator, identity)->{
			if (!request.getQueryParam("id").isPresent()) {
				return Response.create(StatusCode.OK).getText("Cannot process: missing id");
			}
			if (identity.isPresent()) {
				return Response.create(StatusCode.OK).getText("Cannot process: someone is logged");
			}
			Integer id = request.getQueryParam("id").getInteger();
			if (id < 5 || id > 20) {
				return Response.create(StatusCode.OK).getText("Cannot process: id is out of range");
			}
			return Response.create(StatusCode.OK).getText("Processed: " + request.getQueryParam("id"));
		};
	}

}
