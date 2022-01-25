package samples.examples.security;

import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Domain;
import toti.annotations.Secured;
import toti.response.Response;
import toti.security.Identity;

/**
 * Pages with various security
 * @author Ondřej Němec
 *
 */
@Controller("pages")
public class PagesController {
	
	public static final String DOMAIN_1 = "domain-1";
	public static final String DOMAIN_2 = "domain-2";
	
	private final Identity identity;
	
	public PagesController(Identity identity) {
		this.identity = identity;
	}
	
	/**
	 * Not secured method, anybody has access
	 * @return http://localhost:8080/examples/pages/unsecured
	 */
	@Action("unsecured")
	public Response notSecured() {
		return Response.getText("Unsecured");
	}

	/**
	 * Secured, require loged user. Can authenticate with header and cookie
	 * @return http://localhost:8080/examples/pages/secured
	 */
	@Action("Secured")
	@Secured(isApi = false)
	public Response secured() {
		return Response.getText("Secured");
	}
	
	/**
	 * Secured, require loged user. Can authenticate only with header
	 * @return http://localhost:8080/examples/pages/super-secured
	 */
	@Action("super-secured")
	@Secured
	public Response superSecured() {
		return Response.getText("Super secured");
	}
	
	/**
	 * Secured. Requred user access to domain-1 with at least read permissions
	 * @return http://localhost:8080/examples/pages/domain-read
	 */
	@Action("domain-read")
	@Secured({@Domain(name=DOMAIN_1, action=toti.security.Action.READ)})
	public Response domainead() {
		return Response.getText("Domain READ");
	}
	
	/**
	 * Secured. Requred user access to domain-1 with at least delete permissions
	 * @return http://localhost:8080/examples/pages/domain-delete
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
	 * @return http://localhost:8080/examples/pages/domain-multiple
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
	 * @return http://localhost:8080/examples/pages/owner-ids
	 */
	@Action("owner-ids")
	@Secured({@Domain(name=DOMAIN_1, action=toti.security.Action.ALLOWED)})
	public Response ownerIds() {
		return Response.getText("Owner IDs " + identity.getUser().getAllowedIds()); // TODO
	}
	
	/**
	 * Secured. Requred user access to domain-1 with at least allowed permissions
	 * @return http://localhost:8080/examples/pages/owner
	 */
	@Action("owner")
	@Secured({@Domain(name=DOMAIN_1, action=toti.security.Action.ALLOWED, owner="owner-name")}) // TODO
	public Response owner() {
		return Response.getText("Owner");
	}
}
