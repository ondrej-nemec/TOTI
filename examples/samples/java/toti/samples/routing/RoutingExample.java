package toti.samples.routing;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import ji.common.functions.Env;
import ji.database.Database;
import ji.translator.Translator;
import toti.Module;
import toti.Router;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Param;
import toti.annotations.ParamUrl;
import toti.annotations.Secured;
import toti.application.Task;
import toti.register.Register;
import toti.response.Response;
import toti.security.AuthMode;
import toti.url.Link;

/**
 * This example shows routing and link options
 * @author Ondřej Němec
 *
 */
@Controller("routing")
public class RoutingExample implements Module {

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
		
		// method
		params.put(
			"method",
			Link.get()
				.setMethod("accessibleMethod")
				.create()
		);
		// method, parameter
		params.put(
			"methodParam",
			Link.get()
				.setMethod("unsecured")
				.addGetParam("backlink", "/back/link")
				.create()
		);
		// method, url parameter
		params.put(
			"methodUrlParam",
			Link.get()
				.setMethod("linksDestination")
				.addUrlParam(42)
				.addUrlParam("john-smith")
				.create()
		);
		// method and controller
		params.put(
			"methodController",
			Link.get()
				.setController(RoutingExample.class)
				.setMethod("accessibleMethod")
				.create()
		);
		// method, controller, module
		params.put(
			"methodControllerModule",
			Link.get()
				.setModule(RoutingExample.class)
				.setController(RoutingExample.class)
				.setMethod("accessibleMethod")
				.create()
		);
		// method calling
		params.put(
			"calling",
			Link.get()
				.create(RoutingExample.class, c->c.accessibleMethod())
		);
		// method calling parameters
		params.put(
			"callingParameter",
			Link.get()
				.addGetParam("backlink", "/back/link")
				.create(RoutingExample.class, c->c.unsecured(null))
		);
		// method calling url parameters
		params.put(
			"callingUrlParameter",
			Link.get()
				.addGetParam("get", "getParam")
				.addUrlParam(42)
				.addUrlParam("john-smith")
				.create(RoutingExample.class, c->c.linksDestination(null, null))
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
		router.setRedirectOnNotLoggedInUser(Link.get().create(RoutingExample.class, c->c.unsecured(null)));
		
		// replace empty route with 'accessible'
		router.addUrl("", Link.get().create(RoutingExample.class, c->c.accessibleMethod()));

		// replace 'notAccessible' route with 'accessible'
		router.addUrl(
			Link.get().create(RoutingExample.class, c->c.notAccessibleMethod()), 
			Link.get().create(RoutingExample.class, c->c.accessibleMethod())
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
		registr.addFactory(RoutingExample.class, ()->new RoutingExample());
		return Arrays.asList();
	}
}
