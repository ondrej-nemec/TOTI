package toti.examples.getStarted.core.controllers;

import toti.core.annotations.Action;
import toti.core.annotations.Controller;
import toti.core.answers.action.ResponseAction;
import toti.core.answers.response.Response;

@Controller("welcome")
public class WelcomeController {

	@Action(path="hello")
	public ResponseAction hello() {
		return (request, identity)->{
			String name = request.getQueryParam("name").getString();
			if (name == null) {
				name = "World";
			}
			return Response.OK()
			.addHeader("Content-type", "text/html")
			.getText("<h1>Hello " + name + "!</h1>");
		};
	}

	@Action(path="guess")
	public ResponseAction guess(Integer from, Integer to) {
		return (request, identity)->{
			return Response.OK()
			.addHeader("Content-type", "text/html")
			.getText("<div>Guess number between " + from + " to " + to + "</iv>");
		};
	}

}
