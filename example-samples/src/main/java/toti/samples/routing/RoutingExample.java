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
import toti.annotations.Secured;
import toti.answers.action.ResponseAction;
import toti.answers.action.ResponseBuilder;
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
	@Action(path="secured")
	@Secured(AuthMode.COOKIE)
	public ResponseAction secured() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getText("Secured");
		});
	}
	
	/**
	 * Unsecured route for redirect
	 * @return
	 *  http://localhost:8080/examples-routing/routing/unsecured
	 *  OR with another pattern
	 *  http://localhost:8080/api/routing/unsecured
	 */
	@Action(path="unsecured")
	public ResponseAction unsecured() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getText("Unsecured " + req.getQueryParam("backlink"));
		});
	}
	
	/**
	 * This route is be overrrided in Router
	 * @return 
	 *  http://localhost:8080/examples-routing/routing/notAccessible
	 *  OR with another pattern
	 *  http://localhost:8080/api/routing/notAccessible
	 */
	@Action(path="notAccessible")
	public ResponseAction notAccessibleMethod() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getText("This never appear");
		});
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
	@Action(path="accessible")
	public ResponseAction accessibleMethod() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getText("This always appear");
		});
	}
	
	/**
	 * Display result of varios generated links
	 * @return 
	 *  http://localhost:8080/examples-routing/routing/links
	 *  OR with another pattern
	 *  http://localhost:8080/api/routing/links
	 */
	@Action(path="links")
	public ResponseAction links() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
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
					RoutingExample.class, c->c.unsecured(),
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
			
			return Response.OK().getTemplate(
				"/links.jsp",
				params
			);
		});
	}

	/**
	 * Used for Link::create testing
	 * @return
	 */
	@Action(path="links-destination")
	public ResponseAction linksDestination(Integer id, String name) {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getText("This never appear");
		});
	}
	
	@Override
	public void addRoutes(Router router) {
		// TODO
		/*
		// set URL for redirect if not logged in user try access secured route
		router.setRedirectOnNotLoggedInUser(RoutingExample.class, c->c.unsecured());
		
		// replace empty route with 'accessible'
		router.addUrl("", router.getLink().create(RoutingExample.class, c->c.accessibleMethod()));

		// replace 'notAccessible' route with 'accessible'
		router.addUrl(
			router.getLink().create(RoutingExample.class, c->c.notAccessibleMethod()), 
			router.getLink().create(RoutingExample.class, c->c.accessibleMethod())
		);
		*/
	}

	@Override
	public String getName() {
		return "examples-routing";
	}
	
	@Override
	public String getTemplatesPath() {
		return "templates/routing";
	}

	@Override
	public List<Task> initInstances(Env env, Translator translator, Register registr, Link link, Database database, Logger logger)
			throws Exception {
		registr.addController(RoutingExample.class, ()->new RoutingExample(link));
		return Arrays.asList();
	}
}
