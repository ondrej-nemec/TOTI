package toti.tutorial1.web;

import java.util.HashMap;

import toti.application.annotations.Action;
import toti.application.annotations.Controller;
import toti.application.answers.response.Response;

@Controller("dashboard")
public class WelcomePageController {

	@Action("welcome")
	public Response welcomePage() {
		return Response.getTemplate("welcome.jsp", new HashMap<>());
	}
	
}
