package toti.examples.getStarted.core.controllers;

import toti.core.annotations.Action;
import toti.core.annotations.Controller;
import toti.core.answers.action.ResponseAction;
import toti.core.answers.response.Response;
import toti.examples.getStarted.core.services.Counter;

@Controller("counter")
public class CounterController {

	private final Counter counter;

	public CounterController(Counter counter) {
		this.counter = counter;
	}

	@Action
	public ResponseAction index() {
		return (request, identity)->{
			return Response.OK()
			.addHeader("Content-type", "text/html")
			.getText("<h1>" + counter.getCount() + "</h1>");
		};
	}

}
