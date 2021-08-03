package example.web.controllers;

import java.util.HashMap;
import java.util.Map;

import toti.annotations.url.Action;
import toti.annotations.url.Controller;
import toti.response.Response;

@Controller("sign")
public class SignPageController {
	
	private final static String JSP_PAGE = "Sign.jsp";
	
	@Action("in")
	public Response grid() {
		Map<String, Object> params = new HashMap<>();
		return Response.getTemplate(JSP_PAGE, params);
	}
}
