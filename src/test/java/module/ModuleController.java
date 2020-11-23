package module;

import java.util.HashMap;
import java.util.Map;

import toti.annotations.inject.Translate;
import toti.annotations.url.Action;
import toti.annotations.url.Controller;
import toti.response.Response;
import translator.Translator;

@Controller("module")
public class ModuleController {

	@Translate
	private Translator trans;
	
	public void setTrans(Translator trans) {
		this.trans = trans;
	}
	
	@Action("mod")
	public Response index() {
		Map<String, Object> params = new HashMap<>();
		params.put("text", trans.translate("module.some-name"));
		params.put("text2", trans.translate("some-name"));
		return Response.getTemplate("/b/a/index.jsp", params);
	}
	
}
