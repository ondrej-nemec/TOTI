package toti.samples.application.controllers;

import java.time.LocalDateTime;

import toti.annotations.Action;
import toti.annotations.Controller;
import toti.answers.action.RequestInterruptedException;
import toti.answers.action.ResponseAction;
import toti.answers.action.ResponseBuilder;
import toti.answers.response.Response;
import toti.http.StatusCode;

@Controller("processing")
public class ProcessingController {

	@Action(path="action-chaing")
	public ResponseAction fullChain() {
		return ResponseBuilder.get()
			// optional part
		.prevalidate((request, translator, identity)->{
			if (!request.getQueryParam("id").isPresent()) {
				throw new RequestInterruptedException(
					Response.create(StatusCode.OK).getText("Cannot process: missing id")
				);
			}
			// processing continues
		})
		// optional part
		.authorize((request, translator, identity)->{
			if (identity.isPresent()) {
				throw new RequestInterruptedException(
					Response.create(StatusCode.OK).getText("Cannot process: someone is logged")
				);
			}
			// processing continues
		})
		// optional part
		.validate((request, translator, identity)->{
			Integer id = request.getQueryParam("id").getInteger();
			if (id < 5 || id > 20) {
				throw new RequestInterruptedException(
					Response.create(StatusCode.OK).getText("Cannot process: id is out of range")
				);
			}
			// processing continues
		})
		.createResponse((request, translator, identity)->{
			return Response.create(StatusCode.OK).getText("Processed: " + request.getQueryParam("id"));
		});
	}
	
	@Action(path="data-holder")
	public ResponseAction dataHolder() {
		return ResponseBuilder.get()
		.prevalidate((request, translator, identity)->{
			request.setData("receivedTime", LocalDateTime.now());
		})
		.authorize((request, translator, identity)->{
			LocalDateTime receivedTime = request.getData("receivedTime").getDateTime();
			request.setData("receivedTime", receivedTime.withNano(0));
		})
		.validate((request, translator, identity)->{
			LocalDateTime receivedTime = request.getData("receivedTime", LocalDateTime.class);
			request.setData("receivedTime", receivedTime.toString());
		})
		.createResponse((request, translator, identity)->{
			return Response.create(StatusCode.OK).getText("Processed: " + request.getData("receivedTime"));
		});
	}
	
}
