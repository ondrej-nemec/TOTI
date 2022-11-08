package toti.samples.security;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import ji.common.functions.Env;
import ji.common.structures.Tuple2;
import ji.database.Database;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import ji.translator.Translator;
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
import toti.security.AuthMode;
import toti.security.User;
import toti.url.Link;

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
	 * @return http://localhost:8080/examples-security/security/index
	 */
	@Action("index")
	public Response index() {
		List<Tuple2<String, String>> links = new LinkedList<>();
		
		links.add(new Tuple2<>("Not secured", Link.get().create(SecurityExample.class, c->c.notSecured())));
		links.add(new Tuple2<>("Secured", Link.get().create(SecurityExample.class, c->c.secured())));
		links.add(new Tuple2<>("Secured with CSRF", Link.get().create(SecurityExample.class, c->c.securedCsrf())));
		links.add(new Tuple2<>("Super secured", Link.get().create(SecurityExample.class, c->c.superSecured())));
		links.add(new Tuple2<>("Domain READ", Link.get().create(SecurityExample.class, c->c.domaiRead())));
		links.add(new Tuple2<>("Domain DELETE", Link.get().create(SecurityExample.class, c->c.domainDelete())));
		links.add(new Tuple2<>("Multiple domains", Link.get().create(SecurityExample.class, c->c.domainMultiple())));
		links.add(new Tuple2<>("Owner", Link.get().create(SecurityExample.class, c->c.owner())));
		links.add(new Tuple2<>("Owner IDs", Link.get().create(SecurityExample.class, c->c.ownerIds())));
		links.add(new Tuple2<>("In method calling", Link.get().create(SecurityExample.class, c->c.inMethod())));
		
		Map<String, Object> params = new HashMap<>();
		params.put("links", links);
		return Response.getTemplate("index.jsp", params);
	}
	
	/**
	 * Not secured method, anybody has access
	 * @return http://localhost:8080/examples-security/security/unsecured
	 */
	@Action("unsecured")
	public Response notSecured() {
		return Response.getText("Unsecured");
	}

	/**
	 * Secured, require loged user. Can authenticate with header and cookie (with or without CSRF token)
	 * @return http://localhost:8080/examples-security/security/secured
	 */
	@Action("secured")
	@Secured(mode = AuthMode.COOKIE)
	public Response secured() {
		return Response.getText("Secured");
	}

	/**
	 * Secured, require loged user. Can authenticate with header and cookie(only with CSRF token)
	 * @return http://localhost:8080/examples-security/security/secured
	 */
	@Action("secured-csrf")
	@Secured(mode = AuthMode.COOKIE_AND_CSRF)
	public Response securedCsrf() {
		return Response.getText("Secured CSRF");
	}
	
	/**
	 * Secured, require loged user. Can authenticate only with header
	 * @return http://localhost:8080/examples-security/security/super-secured
	 */
	@Action("super-secured")
	@Secured
	public Response superSecured() {
		return Response.getText("Super secured");
	}
	
	/**
	 * Secured. Requred user access to domain-1 with at least read permissions
	 * @return http://localhost:8080/examples-security/security/domain-read
	 */
	@Action("domain-read")
	@Secured({@Domain(name=DOMAIN_1, action=toti.security.Action.READ)})
	public Response domaiRead() {
		return Response.getText("Domain READ");
	}
	
	/**
	 * Secured. Requred user access to domain-1 with at least delete permissions
	 * @return http://localhost:8080/examples-security/security/domain-delete
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
	 * @return http://localhost:8080/examples-security/security/domain-multiple
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
	 * @return http://localhost:8080/examples-security/security/owner-ids
	 */
	@Action("owner-ids")
	@Secured({@Domain(name=DOMAIN_1, action=toti.security.Action.ALLOWED)})
	public Response ownerIds() {
		return Response.getText("Owner IDs " + identity.getUser().getAllowedIds());
	}
	
	/**
	 * Secured. Requred user access to domain-1 with at least allowed permissions
	 * @return http://localhost:8080/examples-security/security/owner
	 */
	@Action("owner")
	@Secured({@Domain(name=DOMAIN_1, action=toti.security.Action.ALLOWED, owner="id")})
	public Response owner() {
		return Response.getText("Owner");
	}
	
	/**
	 * Method shows usage of Authorizator in method
	 * @return http://localhost:8080/examples-security/security/list
	 */
	@Action("list")
	@Secured
	public Response inMethod() {
		String response = "User is allowed for:";
		if (authorizator.isAllowed(identity.getUser(), DOMAIN_1, toti.security.Action.READ)) {
			response += DOMAIN_1 + " = READ";
		}
		if (authorizator.isAllowed(identity.getUser(), DOMAIN_2, toti.security.Action.READ)) {
			response += DOMAIN_2 + " = READ";
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
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, "Login fail: " + e.getMessage());
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
		return "examples-security";
	}
	
	@Override
	public String getTemplatesPath() {
		return "examples/samples/templates/security";
	}

	@Override
	public String getControllersPath() {
		return "toti/samples/security";
	}
}
