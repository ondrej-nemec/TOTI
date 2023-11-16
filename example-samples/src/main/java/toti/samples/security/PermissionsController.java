package toti.samples.security;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ji.common.structures.Tuple2;
import ji.socketCommunication.http.StatusCode;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Secured;
import toti.answers.action.RequestInterruptedException;
import toti.answers.action.ResponseAction;
import toti.answers.action.ResponseBuilder;
import toti.answers.request.AuthMode;
import toti.answers.response.Response;
import toti.answers.router.Link;

/**
 * Example demonstrate security in TOTI: Permissions
 * @author Ondřej Němec
 *
 */
@Controller("permissions")
public class PermissionsController {

	private final Link link;
	private final SessionManager session;
	
	public PermissionsController(Link link, SessionManager session) {
		this.link = link;
		this.session = session;
	}

	
	/**
	 * Page for login with links and buttons for testing
	 * @return http://localhost:8080/examples-security/security/index
	 */
	@Action(path="index")
	public ResponseAction index() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			List<Tuple2<String, String>> links = new LinkedList<>();
			
			links.add(new Tuple2<>("Not secured", link.create(PermissionsController.class, c->c.notSecured())));
			links.add(new Tuple2<>("Secured", link.create(PermissionsController.class, c->c.secured())));
			links.add(new Tuple2<>("Secured with CSRF", link.create(PermissionsController.class, c->c.securedCsrf())));
			links.add(new Tuple2<>("Secured header", link.create(PermissionsController.class, c->c.superSecured())));
			links.add(new Tuple2<>("Permissions", link.create(PermissionsController.class, c->c.permissions())));
			
			Map<String, Object> params = new HashMap<>();
			params.put("links", links);
			return Response.OK().getTemplate("index.jsp", params);
		});
	}
	
	/**
	 * Not secured method, anybody has access
	 * @return http://localhost:8080/examples-security/security/unsecured
	 */
	@Action(path="unsecured")
	public ResponseAction notSecured() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			return Response.OK().getText("Unsecured");
		});
	}

	/**
	 * Secured, require loged user. Can authenticate with header and cookie (with or without CSRF token)
	 * @return http://localhost:8080/examples-security/security/secured
	 */
	@Action(path="secured")
	@Secured(AuthMode.COOKIE)
	public ResponseAction secured() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			return Response.OK().getText("Secured");
		});
	}

	/**
	 * Secured, require loged user. Can authenticate with header and cookie(only with CSRF token)
	 * @return http://localhost:8080/examples-security/security/secured
	 */
	@Action(path="secured-csrf")
	@Secured(AuthMode.COOKIE_AND_CSRF)
	public ResponseAction securedCsrf() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			return Response.OK().getText("Secured CSRF");
		});
	}
	
	/**
	 * Secured, require loged user. Can authenticate only with header
	 * @return http://localhost:8080/examples-security/security/secured-header
	 */
	@Action(path="secured-header")
	@Secured
	public ResponseAction superSecured() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			return Response.OK().getText("Secured header");
		});
	}
	
	/**
	 * Secured, require loged user. Can authenticate only with header
	 * @return http://localhost:8080/examples-security/security/super-secured
	 */
	@Action(path="permissions")
	@Secured
	public ResponseAction permissions() {
		return ResponseBuilder.get()
		.authorize((req, translator, identity)->{
			// here can be some custom authorization
			if (!identity.getUser().getId().equals("user1")) {
				throw new RequestInterruptedException(
					Response.create(StatusCode.FORBIDDEN).getText("Only user 1 is allowed")
				);
			}
		})
		.createResponse((req, translator, identity)->{
			return Response.OK().getText("Super secured");
		});
	}
	
}
