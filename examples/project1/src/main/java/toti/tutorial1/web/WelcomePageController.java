package toti.tutorial1.web;

import java.util.HashMap;

import toti.annotations.Action;
import toti.annotations.Controller;
import toti.answers.response.Response;

@Controller("dashboard")
public class WelcomePageController {

	@Action("welcome")
	public Response welcomePage() {
		return Response.getTemplate("welcome.jsp", new HashMap<>());
	}
	
}
