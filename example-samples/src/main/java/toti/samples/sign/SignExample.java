package toti.samples.sign;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import ji.common.functions.Env;
import ji.database.Database;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import ji.translator.Translator;
import toti.Router;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Method;
import toti.annotations.Param;
import toti.annotations.Secured;
import toti.answers.request.AuthMode;
import toti.answers.request.Identity;
import toti.answers.response.Response;
import toti.application.Module;
import toti.application.Task;
import toti.application.register.Register;
import toti.authentication.AuthentizationException;
import toti.control.Form;
import toti.control.inputs.Hidden;
import toti.control.inputs.Password;
import toti.control.inputs.Submit;
import toti.control.inputs.Text;
import toti.security.Authenticator;
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
	private Link link;
	
	// module constructor
	public SignExample() {}
	
	// controller constructor
	public SignExample(Identity identity, Authenticator authenticator, Link link) {
		this.identity = identity;
		this.authenticator = authenticator;
		this.link = link;
	}
	
	@Override
	public void addRoutes(Router router) {
		//*
		// redirect to async login
		router.setRedirectOnNotLoggedInUser(router.getLink().create(SignExample.class, c->c.asyncLoginPage(null)));
		/*/
		// redirect to sync login
		router.setRedirectOnNotLoggedInUser(router.getLink().create(SignExample.class, c->c.syncLoginPage(null)));
		//*/
	}
	
	/**
	 * Async login page with TOTI form
	 * @return http://localhost:8080/examples-sign/sign/async-page
	 */
	@Action("async-page")
	public Response asyncLoginPage(@Param("backlink") String backlink) {
		Form form = new Form(link.create(SignExample.class, c->c.asyncLogin(null, null)), true);
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
	 * @return http://localhost:8080/examples-sign/sign/async-login
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
	 * @return http://localhost:8080/examples-sign/sign/async-logout
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
	 * @return http://localhost:8080/examples-sign/sign/sync-page
	 */
	@Action("sync-page")
	public Response syncLoginPage(@Param("backlink") String backlink) {
		return getSyncPageResponse(backlink, null);
	}
	
	/**
	 * Sync login with TOTI form
	 * @return http://localhost:8080/examples-sign/sign/sync-login
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
			return Response.getRedirect(link.create(SignExample.class, c->c.index2()));
		} catch (AuthentizationException e) {
			e.printStackTrace();
			return getSyncPageResponse(backlink, "Sync login failed");
		}
	}
	
	private Response getSyncPageResponse(String backlink, String errorMessage) {
		Form form = new Form(link.create(SignExample.class, c->c.syncLogin(null, null, null)), true);
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
	 * @return http://localhost:8080/examples-sign/sign/sync-logout
	 */
	@Action("sync-logout")
	@Secured(mode = AuthMode.COOKIE)
	public Response syncLogout() {
		authenticator.logout(identity);
		return Response.getRedirect(link.create(SignExample.class, c->c.syncLoginPage(null)));
	}
	
	/*********************/
	
	/**
	 * Secured method for login verify
	 * @return http://localhost:8080/examples-sign/sign/index
	 */
	@Action("index")
	@Secured(mode = AuthMode.COOKIE)
	public Response index() {
		return Response.getTemplate("/index.jsp", new HashMap<>());
	}
	
	/**
	 * Second secured method for login verify
	 * @return http://localhost:8080/examples-sign/sign/index2
	 */
	@Action("index2")
	@Secured(mode = AuthMode.COOKIE)
	public Response index2() {
		identity.getUser().setProperty("index2", true);
		return Response.getTemplate("/index2.jsp", new HashMap<>());
	}
	
	/***************/
	
	/**
	 * Example of using user data
	 * @return http://localhost:8080/examples-sign/sign/user-data
	 */
	@Action("user-data")
	public Response userData() throws AuthentizationException {
		if (identity.isAnonymous()) {
			// automatic login before page load
			// demonstation requires logged user
			authenticator.login(new User("someuser", null), identity);
		}
		User user = identity.getUser();
		if (user.getProperty("counter").isPresent()) {
			user.updateProperty("counter", (old)->old + "*");
		} else {
			user.setProperty("counter", "*");
		}
		return Response.getText("Counter: " + user.getProperty("counter"));
	}
	
	/***************/
	
	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Link link, Database database, Logger logger)
			throws Exception {
		register.addFactory(
			SignExample.class, 
			(trans, identity, authorizator, authenticator)->new SignExample(identity, authenticator, link)
		);
		return Arrays.asList();
	}

	@Override
	public String getName() {
		return "examples-sign";
	}
	
	@Override
	public String getTemplatesPath() {
		return "examples/samples/templates/sign";
	}

	@Override
	public String getControllersPath() {
		return "toti/samples/sign";
	}

}
