package samples.tutorial1.web;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ji.translator.Translator;
import samples.tutorial1.web.api.SignApiController;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Param;
import toti.control.Form;
import toti.control.inputs.Option;
import toti.control.inputs.Password;
import toti.control.inputs.Select;
import toti.control.inputs.Submit;
import toti.response.Response;
import toti.url.Link;

@Controller("sign")
public class SignPageController {
	
	private final Translator translator;
	
	public SignPageController(Translator translator) {
		this.translator = translator;
	}

	@Action("in")
	public Response loginPage(@Param("backlink") String backlink) {
		Form form = new Form(Link.get().create(SignApiController.class, c->c.login(null, null)), true);
		form.setFormMethod("post");
		
		List<Option> userOptions = new LinkedList<>();
		SignApiController.USERS.forEach((login, user)->{
			userOptions.add(Option.create(login, user.getProperty("name").getString()));
		});
		form.addInput(
			Select.input("username", true, userOptions)
			.setTitle(translator.translate("mesasges.sign.username"))
		);
		form.addInput(
			Password.input("password", true)
			.setTitle(translator.translate("mesasges.sign.password"))
		);
		form.addInput(
			Submit.create(translator.translate("mesasges.sign.login"), "login")
			.setOnSuccess("setLogin")
			.setRedirect(
				backlink == null ? Link.get().create(WelcomePageController.class, c->c.welcomePage()) : backlink
			)
		);
		Map<String, Object> params = new HashMap<>();
		params.put("loginForm", form);
		params.put("refreshLink", Link.get().create(SignApiController.class, c->c.refresh()));
		return Response.getTemplate("login.jsp", params);
	}
	
}
