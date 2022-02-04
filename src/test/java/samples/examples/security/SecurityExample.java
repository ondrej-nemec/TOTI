package samples.examples.security;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ji.common.Logger;
import ji.common.functions.Env;
import ji.database.Database;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import ji.translator.Translator;
import toti.HttpServer;
import toti.HttpServerFactory;
import toti.Module;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Domain;
import toti.annotations.Method;
import toti.annotations.Param;
import toti.annotations.Secured;
import toti.application.Task;
import toti.authentication.AuthentizationException;
import toti.register.Register;
import toti.response.Response;
import toti.security.Authenticator;
import toti.security.Authorizator;
import toti.security.Identity;
import toti.security.User;

/**
 * Example demonstrate authorization in TOTI
 * @author Ondřej Němec
 *
 */
@Controller("security")
public class SecurityExample implements Module {
	
	public static final String DOMAIN_1 = "domain-1";
	public static final String DOMAIN_2 = "domain-2";
	
	private Identity identity;
	private Authorizator authorizator;
	private Authenticator authenticator;
	
	// module constructor
	public SecurityExample() {}
	
	// controller constructor
	public SecurityExample(Identity identity, Authorizator authorizator, Authenticator authenticator) {
		this.identity = identity;
		this.authorizator = authorizator;
		this.authenticator = authenticator;
	}
	
	/*****************/
	
	/**
	 * Page for login with links and buttons for testing
	 * @return http://localhost:8080/examples/security/index
	 */
	@Action("index")
	public Response index() {
		return Response.getTemplate("index.jsp", new HashMap<>());
	}
	
	/**
	 * Not secured method, anybody has access
	 * @return http://localhost:8080/examples/security/unsecured
	 */
	@Action("unsecured")
	public Response notSecured() {
		return Response.getText("Unsecured");
	}

	/**
	 * Secured, require loged user. Can authenticate with header and cookie
	 * @return http://localhost:8080/examples/security/secured
	 */
	@Action("Secured")
	@Secured(isApi = false)
	public Response secured() {
		return Response.getText("Secured");
	}
	
	/**
	 * Secured, require loged user. Can authenticate only with header
	 * @return http://localhost:8080/examples/security/super-secured
	 */
	@Action("super-secured")
	@Secured
	public Response superSecured() {
		return Response.getText("Super secured");
	}
	
	/**
	 * Secured. Requred user access to domain-1 with at least read permissions
	 * @return http://localhost:8080/examples/security/domain-read
	 */
	@Action("domain-read")
	@Secured({@Domain(name=DOMAIN_1, action=toti.security.Action.READ)})
	public Response domainead() {
		return Response.getText("Domain READ");
	}
	
	/**
	 * Secured. Requred user access to domain-1 with at least delete permissions
	 * @return http://localhost:8080/examples/security/domain-delete
	 */
	@Action("domain-delete")
	@Secured({@Domain(name=DOMAIN_1, action=toti.security.Action.DELETE)})
	public Response domainDelete() {
		return Response.getText("Domain DELETE");
	}
	
	/**
	 * Secured. Requred user access to (both):
	 *  domain-1 with at least create permissions
	 *  domain-2 with at leas update permissions 
	 * @return http://localhost:8080/examples/security/domain-multiple
	 */
	@Action("domain-multiple")
	@Secured({
		@Domain(name=DOMAIN_1, action=toti.security.Action.CREATE),
		@Domain(name=DOMAIN_2, action=toti.security.Action.UPDATE)
	})
	public Response domainMultiple() {
		return Response.getText("Domain Multiple");
	}
	
	/**
	 * Secured. Requred user access to domain-1 with at least allowed permissions
	 * In response returns allowed ids of logged user for this 
	 * @return http://localhost:8080/examples/security/owner-ids
	 */
	@Action("owner-ids")
	@Secured({@Domain(name=DOMAIN_1, action=toti.security.Action.ALLOWED)})
	public Response ownerIds() {
		return Response.getText("Owner IDs " + identity.getUser().getAllowedIds()); // TODO
	}
	
	/**
	 * Secured. Requred user access to domain-1 with at least allowed permissions
	 * @return http://localhost:8080/examples/security/owner
	 */
	@Action("owner")
	@Secured({@Domain(name=DOMAIN_1, action=toti.security.Action.ALLOWED, owner="owner-name")}) // TODO
	public Response owner() {
		return Response.getText("Owner");
	}
	
	/**
	 * Method shows usage of Authorizator in method
	 * @return http://localhost:8080/examples/security/list
	 */
	@Action("list")
	@Secured
	public Response inMethod() {
		String response = "";
		if (authorizator.isAllowed(identity.getUser(), DOMAIN_1, toti.security.Action.READ)) {
			response += "Allowed: " + DOMAIN_1 + " - READ";
		}
		if (authorizator.isAllowed(identity.getUser(), DOMAIN_2, toti.security.Action.READ)) {
			response += "Allowed: " + DOMAIN_2 + " - READ";
		}
		return Response.getText(response);
	}
	
	
	/***********/
	
	@Action("login")
	@Method({HttpMethod.POST})
	public Response login(@Param("username") String username) {
		try {
			return Response.getJson(
				authenticator.login(
					new User(username, new SecurityExamplePermissions(username)),
					identity
				)
			);
		} catch (AuthentizationException e) {
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, "");
		}
	}
	
	@Action("logout")
	@Method({HttpMethod.POST})
	public Response logout() {
		authenticator.logout(identity);
		return Response.getText(StatusCode.OK, "");
	}
	
	/*****************/
	
	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Database database, Logger logger)
			throws Exception {
		register.addFactory(
				SecurityExample.class, 
			(trans, identity, authorizator, authenticator)->new SecurityExample(identity, authorizator, authenticator)
		);
		return Arrays.asList();
	}

	@Override
	public String getName() {
		return "examples";
	}
	
	@Override
	public String getTemplatesPath() {
		return "samples/examples/security";
	}

	@Override
	public String getControllersPath() {
		return "samples/examples/security";
	}
	
	public static void main(String[] args) {
		List<Module> modules = Arrays.asList(new SecurityExample());
		try {
			HttpServer server = new HttpServerFactory()
				.setPort(8080)
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
