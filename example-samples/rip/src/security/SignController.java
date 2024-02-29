package toti.samples.security;

import java.util.HashMap;
import java.util.Map;

import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Secured;
import toti.answers.action.ResponseAction;
import toti.answers.action.ResponseBuilder;
import toti.answers.request.AuthMode;
import toti.answers.response.Response;
import toti.answers.router.Link;
import toti.ui.control.Form;
import toti.ui.control.inputs.Hidden;
import toti.ui.control.inputs.Password;
import toti.ui.control.inputs.Submit;
import toti.ui.control.inputs.Text;

/**
 * Example demonstrate security in TOTI: Sign
 * @author Ondřej Němec
 *
 */
@Controller("sign")
public class SignController {

	private final Link link;
	private final SessionManager session;
	
	public SignController(Link link, SessionManager session) {
		this.link = link;
		this.session = session;
	}
	
	/**
	 * Async login page with TOTI form
	 * @return http://localhost:8080/examples-security/sign/async-page
	 */
	@Action(path="async-page")
	public ResponseAction asyncLoginPage() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			Form form = new Form(link.create(SignController.class, c->c.asyncLogin()), true);
			form.setFormMethod("post");
			form.addInput(Text.input("username", true).setTitle("Username"));
			form.addInput(Password.input("password", true).setTitle("Password"));
			form.addInput(
				Submit.create("Login", "login")
				.setAsync(true)
				.setRedirect(req.getQueryParam("backlink").getString())
				.setOnFailure("asyncLoginOnFailure")
				.setOnSuccess("asyncLoginOnSuccess")
			);
			Map<String, Object> params = new HashMap<>();
			params.put("loginForm", form);
			params.put("title",  "Async login with TOTI form");
			return Response.OK().getTemplate("/sign/totiForm.jsp", params);
		});
	}
	
	/**
	 * Async login. Can be called as Rest API
	 * @return http://localhost:8080/examples-security/sign/async-login
	 */
	@Action(path="async-login", methods=HttpMethod.POST)
	public ResponseAction asyncLogin() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			String username = req.getBodyParam("username").getString();
			String password = req.getBodyParam("password").getString();
			boolean logged = session.login(identity, username, password);
			if (!logged) {
				return Response.create(StatusCode.INTERNAL_SERVER_ERROR).getText("Async login failed");
			}
			Map<String, Object> params = new HashMap<>();
			// TODO token
			return Response.OK().getJson(params);
		});
	}
	
	/**
	 * Async logout. Can be called as Rest API
	 * @return http://localhost:8080/examples-security/sign/async-logout
	 */
	@Action(path="async-logout", methods=HttpMethod.POST)
	@Secured
	public ResponseAction asyncLogout() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			session.logout(identity);
			return Response.OK().getEmpty(); // 200 response
		});
	}
	
	/***************/

	/**
	 * Sync login page with TOTI form
	 * @return http://localhost:8080/examples-security/sign/sync-page
	 */
	@Action(path="sync-page")
	public ResponseAction syncLoginPage() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			return getSyncPageResponse(req.getBodyParam("backlink").getString(), null);
		});
	}
	
	/**
	 * Sync login with TOTI form
	 * @return http://localhost:8080/examples-security/sign/sync-login
	 */
	@Action(path="sync-login", methods=HttpMethod.POST)
	public ResponseAction syncLogin() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			String username = req.getBodyParam("username").getString();
			String password = req.getBodyParam("password").getString(); 
			String backlink = req.getBodyParam("backlink").getString();
			boolean logged = session.login(identity, username, password);
			if (!logged) {
				return getSyncPageResponse(backlink, "Sync login failed");
			}
			if (backlink != null) {
				return Response.TEMPORARY_REDIRECT().getRedirect(backlink);
			}
			return Response.TEMPORARY_REDIRECT().getRedirect(link.create(SignController.class, c->c.index2()));
		});
	}
	
	private Response getSyncPageResponse(String backlink, String errorMessage) {
		Form form = new Form(link.create(SignController.class, c->c.syncLogin()), true);
		form.setFormMethod("post");
		form.addInput(Text.input("username", true).setTitle("Username"));
		form.addInput(Password.input("password", true).setTitle("Password"));
		form.addInput(Submit.create("Login", "login").setAsync(false));
		form.addInput(Hidden.input("backlink").setDefaultValue(backlink));
		Map<String, Object> params = new HashMap<>();
		params.put("loginForm", form);
		params.put("title", "Sync login with TOTI form");
		if (errorMessage != null) {
			params.put("errorMessage", errorMessage);
		}
		return Response.OK().getTemplate("/sign/totiForm.jsp", params);
	}
	
	/**
	 * Sync logout
	 * @return http://localhost:8080/examples-security/sign/sync-logout
	 */
	@Action(path="sync-logout")
	@Secured(AuthMode.COOKIE)
	public ResponseAction syncLogout() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			session.logout(identity);
			return Response.TEMPORARY_REDIRECT().getRedirect(link.create(SignController.class, c->c.syncLoginPage()));
		});
	}
	
	/*********************/
	
	/**
	 * Secured method for login verify
	 * @return http://localhost:8080/examples-sign/sign/index
	 */
	@Action(path="index")
	@Secured(AuthMode.COOKIE)
	public ResponseAction index() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			return Response.OK().getTemplate("/sign/index.jsp", new HashMap<>());
		});
	}
	
	/**
	 * Second secured method for login verify
	 * @return http://localhost:8080/examples-sign/sign/index2
	 */
	@Action(path="index2")
	@Secured(AuthMode.COOKIE)
	public ResponseAction index2() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			return Response.OK().getTemplate("/sign/index2.jsp", new HashMap<>());
		});
	}
}
