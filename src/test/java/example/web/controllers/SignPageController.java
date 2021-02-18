package example.web.controllers;

import java.util.HashMap;
import java.util.Map;

import toti.annotations.inject.Translate;
import toti.annotations.url.Action;
import toti.annotations.url.Controller;
import toti.response.Response;
import translator.Translator;

@Controller("sign")
public class SignPageController {
	
	private final static String JSP_PAGE = "Sign.jsp";

	@Translate
	private Translator translator;
	
	public void setTranslator(Translator translator) {
		this.translator = translator;
	}
	
	@Action("in")
	public Response grid() {
		Map<String, Object> params = new HashMap<>();
		return Response.getTemplate(JSP_PAGE, params);
	}
}
