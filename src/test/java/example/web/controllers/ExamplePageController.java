package example.web.controllers;

import java.util.HashMap;
import java.util.Map;

import socketCommunication.http.HttpMethod;
import toti.annotations.inject.Translate;
import toti.annotations.url.Action;
import toti.annotations.url.Controller;
import toti.annotations.url.Domain;
import toti.annotations.url.Method;
import toti.annotations.url.ParamUrl;
import toti.annotations.url.Secured;
import toti.control.Form;
import toti.control.Grid;
import toti.response.Response;
import translator.Translator;

@Controller("example")
public class ExamplePageController {

	private final static String SECURITY_DOMAIN = "example";
	private final static String JSP_PAGE = "Example.jsp";

	@Translate
	private Translator translator;
	
	public void setTranslator(Translator translator) {
		this.translator = translator;
	}
	
	@Action("list")
	@Secured(isApi = false, value={@Domain(name=SECURITY_DOMAIN, action=acl.Action.READ)})
	public Response grid() {
		Map<String, Object> params = new HashMap<>();
		Grid grid = new Grid("/example-module/api/example/all", "get");
		// HERE
		
		// END
		params.put("control", grid);
		params.put("title", translator.translate("messages.example-list"));
		return Response.getTemplate(JSP_PAGE, params);
	}
	
	@Action("add")
	@Secured(isApi = false, value={@Domain(name=SECURITY_DOMAIN, action=acl.Action.CREATE)})
	public Response add() {
		return getOne(null, true);
	}

	@Action("edit")
	@Secured(isApi = false, value={@Domain(name=SECURITY_DOMAIN, action=acl.Action.UPDATE)})
	public Response edit(@ParamUrl("id") Integer id) {
		return getOne(id, true);
	}

	@Action("detail")
	@Method({HttpMethod.GET})
	@Secured(isApi = false, value={@Domain(name=SECURITY_DOMAIN, action=acl.Action.READ)})
	public Response detail(@ParamUrl("id") Integer id) {
		return getOne(id, false);
	}
	
	private Response getOne(Integer id, boolean editable) {
		Map<String, Object> params = new HashMap<>();
		String url = "/example-module/api/example/" +  (id == null ? "insert" : "update/" + id);
		Form form = new Form(url, editable);
		// HERE
		
		// END
		if (id != null) {
			form.setBindMethod("get");
			form.setBindUrl("/example-module/api/example/get/" + id);
		}
		params.put("control", form);
		params.put("title", translator.translate("example-" + (id == null ? "add" : "edit")));
		return Response.getTemplate(JSP_PAGE, params);
	}
}
