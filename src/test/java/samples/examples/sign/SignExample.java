package samples.examples.sign;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ji.common.Logger;
import ji.common.functions.Env;
import ji.database.Database;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import ji.translator.Translator;
import toti.HttpServer;
import toti.HttpServerFactory;
import toti.Module;
import toti.Router;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Method;
import toti.annotations.Param;
import toti.annotations.Secured;
import toti.application.Task;
import toti.authentication.AuthentizationException;
import toti.control.Form;
import toti.control.inputs.Hidden;
import toti.control.inputs.Password;
import toti.control.inputs.Submit;
import toti.control.inputs.Text;
import toti.register.Register;
import toti.response.Response;
import toti.security.Authenticator;
import toti.security.Identity;
import toti.security.Mode;
import toti.security.User;
import toti.url.Link;

/**
 * Example demonstrate authentication in TOTI
 * @author Ondřej Němec
 *
 */
@Controller("sign")
public class SignExample implements Module {

	private Identity identity;
	private Authenticator authenticator;
	
	// module constructor
	public SignExample() {}
	
	// controller constructor
	public SignExample(Identity identity, Authenticator authenticator) {
		this.identity = identity;
		this.authenticator = authenticator;
	}
	
	@Override
	public void addRoutes(Router router) {
		//*
		// redirect to async login
		router.setRedirectOnNotLoggedInUser(Link.get().create(SignExample.class, c->c.asyncLoginPage(null)));
		/*/
		// redirect to sync login
		router.setRedirectOnNotLoggedInUser(Link.get().create(SignExample.class, c->c.syncLoginPage(null)));
		//*/
	}
	
	/**
	 * Async login page with TOTI form
	 * @return http://localhost:8080/examples/sign/async-page
	 */
	@Action("async-page")
	public Response asyncLoginPage(@Param("backlink") String backlink) {
		Form form = new Form(Link.get().create(SignExample.class, c->c.asyncLogin(null, null)), true);
		form.setFormMethod("post");
		form.addInput(Text.input("username", true).setTitle("Username"));
		form.addInput(Password.input("password", true).setTitle("Password"));
		form.addInput(
			Submit.create("Login", "login")
			.setAsync(true)
			.setRedirect(backlink)
			.setOnFailure("asyncLoginOnFailure")
			.setOnSuccess("asyncLoginOnSuccess")
		);
		Map<String, Object> params = new HashMap<>();
		params.put("loginForm", form);
		params.put("title",  "Async login with TOTI form");
		return Response.getTemplate("totiForm.jsp", params);
	}
	
	/**
	 * Async login. Can be called as Rest API
	 * @return http://localhost:8080/examples/sign/async-login
	 */
	@Action("async-login")
	@Method(HttpMethod.POST)
	public Response asyncLogin(@Param("username") String username, @Param("password") String password) {
		// here will be some kind of authentication
		try {
			return Response.getJson(authenticator.login(
				new User(username + ":" + password, new SignExamplePermissions()), 
				identity
			));
		} catch (AuthentizationException e) {
			e.printStackTrace();
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, "Async login failed");
		}
	}
	
	/**
	 * Async logout. Can be called as Rest API
	 * @return http://localhost:8080/examples/sign/async-logout
	 */
	@Action("async-logout")
	@Method(HttpMethod.POST)
	@Secured
	public Response asyncLogout() {
		authenticator.logout(identity);
		return Response.getText(""); // 202 response
	}
	
	/***************/

	/**
	 * Sync login page with TOTI form
	 * @return http://localhost:8080/examples/sign/sync-page
	 */
	@Action("sync-page")
	public Response syncLoginPage(@Param("backlink") String backlink) {
		return getSyncPageResponse(backlink, null);
	}
	
	/**
	 * Sync login with TOTI form
	 * @return http://localhost:8080/examples/sign/sync-login
	 */
	@Action("sync-login")
	@Method(HttpMethod.POST)
	public Response syncLogin(
			@Param("username") String username,
			@Param("password") String password, 
			@Param("backlink") String backlink) {
		// here will be some kind of authentication
		try {
			authenticator.login(
				new User(username + ":" + password, new SignExamplePermissions()), 
				identity
			);
			if (backlink != null) {
				return Response.getRedirect(backlink);
			}
			return Response.getRedirect(Link.get().create(SignExample.class, c->c.index2()));
		} catch (AuthentizationException e) {
			e.printStackTrace();
			return getSyncPageResponse(backlink, "Sync login failed");
		}
	}
	
	private Response getSyncPageResponse(String backlink, String errorMessage) {
		Form form = new Form(Link.get().create(SignExample.class, c->c.syncLogin(null, null, null)), true);
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
		return Response.getTemplate("totiForm.jsp", params);
	}
	
	/**
	 * Sync logout
	 * @return http://localhost:8080/examples/sign/sync-logout
	 */
	@Action("sync-logout")
	@Secured(mode = Mode.COOKIE)
	public Response syncLogout() {
		authenticator.logout(identity);
		return Response.getRedirect(Link.get().create(SignExample.class, c->c.syncLoginPage(null)));
	}
	
	/*********************/
	
	/**
	 * Secured method for login verify
	 * @return http://localhost:8080/examples/sign/index
	 */
	@Action("index")
	@Secured(mode = Mode.COOKIE)
	public Response index() {
		return Response.getTemplate("/index.jsp", new HashMap<>());
	}
	
	/**
	 * Second secured method for login verify
	 * @return http://localhost:8080/examples/sign/index2
	 */
	@Action("index2")
	@Secured(mode = Mode.COOKIE)
	public Response index2() {
		identity.getUser().setProperty("index2", true);
		return Response.getTemplate("/index2.jsp", new HashMap<>());
	}
	
	/***************/
	
	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Database database, Logger logger)
			throws Exception {
		register.addFactory(
			SignExample.class, 
			(trans, identity, authorizator, authenticator)->new SignExample(identity, authenticator)
		);
		return Arrays.asList();
	}

	@Override
	public String getName() {
		return "examples";
	}
	
	@Override
	public String getTemplatesPath() {
		return "samples/examples/sign";
	}

	@Override
	public String getControllersPath() {
		return "samples/examples/sign";
	}
	
	public static void main(String[] args) {
		List<Module> modules = Arrays.asList(new SignExample());
		try {
			HttpServer server = new HttpServerFactory()
				.setPort(8080)
				.setTokenExpirationTime(30 * 1000) // 30s
				.get(modules, null, null);
			
			/* start */
			server.start();
	
			// sleep for 2min before automatic close
			try { Thread.sleep(2 * 60 * 1000); } catch (InterruptedException e) { e.printStackTrace(); }
			
			/* stop */
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

}
