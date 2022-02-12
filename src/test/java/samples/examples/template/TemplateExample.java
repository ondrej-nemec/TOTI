package samples.examples.template;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ji.common.Logger;
import ji.common.functions.Env;
import ji.database.Database;
import ji.translator.Translator;
import toti.HttpServer;
import toti.HttpServerFactory;
import toti.Module;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Param;
import toti.application.Task;
import toti.register.Register;
import toti.response.Response;
import toti.url.Link;

/**
 * Example shows options of TOTI templates
 * @author Ondřej Němec
 *
 */
@Controller("template")
public class TemplateExample implements Module {
	
	/**
	 * Basics of templating
	 * @return http://localhost:8080/examples/template/basics
	 */
	@Action("basics")
	public Response basics() {
		Map<String, Object> params = new HashMap<>();
		params.put("title", "Page title");
		return Response.getTemplate("basics.jsp", params);
	}
	
	/**
	 * How work with variables
	 * @return http://localhost:8080/examples/template/variable
	 */
	@Action("variable")
	public Response variableOptions() {
		Map<String, Object> params = new HashMap<>();
		params.put("title", "Some text");
		params.put(
			"url", 
			Link.get()
				.addGetParam("foo", "dump")
				.addGetParam("foo2", "dump2")
				.create(TemplateExample.class, c->c.variableOptions())
		);
		params.put("color", "#ff45ee");
		params.put("age", "42");
		
		// TODO more variables in variable/tag/parameter/inline/comment
		
		params.put("attack-html", "<scipt>alert('XSS from HTML!');</script>");
		params.put("attack-parameter", "\" onclick='alert(\"XSS from parameter!\")'");
		params.put("attack-color", "javascript:alert(1)");
		params.put("attack-src", "j&#X41vascript:alert('XSS from img!')");
		params.put("attack-js", "';alert('XSS from JS!');//");
		params.put("attack-onclick", "alert('XSS from parameter value!')");
		// params.put("attack-meta", "0;url=data:text/html;base64,PHNjcmlwdD5hbGVydCgndGVzdDMnKTwvc2NyaXB0Pg");
		
		return Response.getTemplate("variables.jsp", params);
	}
	
	/**
	 * 
	 * @return http://localhost:8080/examples/template/owasp-form
	 */
	@Action("owasp-form")
	public Response owaspForm() {
		Map<String, Object> params = new HashMap<>();
		params.put("action", Link.get().create(getClass(), c->c.owaspTest(null, null)));
		return Response.getTemplate("owaspForm.jsp", params);
	}
	
	/**
	 * 
	 * @return http://localhost:8080/examples/template/owasp-print
	 */
	@Action("owasp-print")
	public Response owaspTest(@Param("first") String first, @Param("second") String second) {
		Map<String, Object> params = new HashMap<>();
		params.put("first", first);
		params.put("second", second);
		return Response.getTemplate("owaspPrint.jsp", params);
	}
	
	/*******************/
	
	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Database database, Logger logger)
			throws Exception {
		register.addFactory(
			TemplateExample.class, 
			(trans, identity, authorizator, authenticator)->new TemplateExample()
		);
		return Arrays.asList();
	}

	@Override
	public String getName() {
		return "examples";
	}
	
	@Override
	public String getTemplatesPath() {
		return "samples/examples/template";
	}

	@Override
	public String getControllersPath() {
		return "samples/examples/template";
	}
	
	public static void main(String[] args) {
		List<Module> modules = Arrays.asList(new TemplateExample());
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
