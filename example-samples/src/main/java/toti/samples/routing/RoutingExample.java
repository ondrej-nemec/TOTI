package toti.samples.routing;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import ji.common.functions.Env;
import ji.common.structures.MapInit;
import ji.database.Database;
import ji.translator.Translator;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Param;
import toti.annotations.ParamUrl;
import toti.annotations.Secured;
import toti.answers.request.AuthMode;
import toti.answers.response.Response;
import toti.answers.router.Link;
import toti.answers.router.Router;
import toti.application.Module;
import toti.application.Task;
import toti.application.register.Register;

/**
 * This example shows routing and link options
 * @author Ondřej Němec
 *
 */
@Controller("routing")
public class RoutingExample implements Module {
	
	private Link link;
	
	public RoutingExample(Link link) {
		this.link = link;
	}

	public RoutingExample() {}
	
	/**
	 * Secured route
	 * Never show, always redirect
	 * @return
	 *  http://localhost:8080/examples-routing/routing/secured
	 *  OR with another pattern
	 *  http://localhost:8080/api/routing/secured
	 */
	@Action("secured")
	@Secured(mode = AuthMode.COOKIE)
	public Response secured() {
		return Response.getText("Secured");
	}
	
	/**
	 * Unsecured route for redirect
	 * @return
	 *  http://localhost:8080/examples-routing/routing/unsecured
	 *  OR with another pattern
	 *  http://localhost:8080/api/routing/unsecured
	 */
	@Action("unsecured")
	public Response unsecured(@Param("backlink") String backLink) {
		return Response.getText("Unsecured " + backLink);
	}
	
	/**
	 * This route is be overrrided in Router
	 * @return 
	 *  http://localhost:8080/examples-routing/routing/notAccessible
	 *  OR with another pattern
	 *  http://localhost:8080/api/routing/notAccessible
	 */
	@Action("notAccessible")
	public Response notAccessibleMethod() {
		return Response.getText("This never appear");
	}
	
	/**
	 * This route is accessible
	 * @return
	 *  http://localhost:8080/examples-routing/routing/accessible
	 *  http://localhost:8080/examples-routing/routing/notAccessible
	 *  http://localhost:8080
	 *  OR with another pattern
	 *  http://localhost:8080/api/routing/accessible
	 *  http://localhost:8080/api/routing/notAccessible
	 *  http://localhost:8080
	 */
	@Action("accessible")
	public Response accessibleMethod() {
		return Response.getText("This always appear");
	}
	
	/**
	 * Display result of varios generated links
	 * @return 
	 *  http://localhost:8080/examples-routing/routing/links
	 *  OR with another pattern
	 *  http://localhost:8080/api/routing/links
	 */
	@Action("links")
	public Response links() {
		Map<String, Object> params = new HashMap<>();
		
		// method calling
		params.put(
			"calling",
			link
				.create(RoutingExample.class, c->c.accessibleMethod())
		);
		// method calling parameters
		params.put(
			"callingParameter",
			link.create(
				RoutingExample.class, c->c.unsecured(null),
				MapInit.create().append("backlink", "/back/link").toMap()
			)
		);
		// method calling url parameters
		params.put(
			"callingUrlParameter",
			link.create(
				RoutingExample.class, c->c.linksDestination(null, null),
				MapInit.create().append("get", "getParam").toMap(),
				42, "john-smith"
			)
		);
		
		return Response.getTemplate(
			"/links.jsp",
			params
		);
	}

	/**
	 * Used for Link::create testing
	 * @return
	 */
	@Action("links-destination")
	public Response linksDestination(@ParamUrl("id") Integer id, @ParamUrl("name") String name) {
		return Response.getText("This never appear");
	}
	
	@Override
	public void addRoutes(Router router) {
		// set URL for redirect if not logged in user try access secured route
		router.setRedirectOnNotLoggedInUser(router.getLink().create(RoutingExample.class, c->c.unsecured(null)));
		
		// replace empty route with 'accessible'
		router.addUrl("", router.getLink().create(RoutingExample.class, c->c.accessibleMethod()));

		// replace 'notAccessible' route with 'accessible'
		router.addUrl(
			router.getLink().create(RoutingExample.class, c->c.notAccessibleMethod()), 
			router.getLink().create(RoutingExample.class, c->c.accessibleMethod())
		);
	}

	@Override
	public String getName() {
		return "examples-routing";
	}

	@Override
	public String getControllersPath() {
		return "toti/samples/routing";
	}
	
	@Override
	public String getTemplatesPath() {
		return "examples/samples/templates/routing";
	}

	@Override
	public List<Task> initInstances(Env env, Translator translator, Register registr, Link link, Database database, Logger logger)
			throws Exception {
		registr.addFactory(RoutingExample.class, ()->new RoutingExample(link));
		return Arrays.asList();
	}
}
