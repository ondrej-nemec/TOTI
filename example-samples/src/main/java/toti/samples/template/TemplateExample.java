package toti.samples.template;

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
import toti.answers.response.Response;
import toti.answers.router.Link;
import toti.application.Module;
import toti.application.Task;
import toti.application.register.Register;

/**
 * Example shows options of TOTI templates
 * @author Ondřej Němec
 *
 */
@Controller("template")
public class TemplateExample implements Module {
	
	private Link link;
	
	public TemplateExample() {}
	
	public TemplateExample(Link link) {
		this.link = link;
	}
	
	/**
	 * Basics of templating
	 * @return http://localhost:8080/examples-templates/template/basics
	 */
	@Action("basics")
	public Response basics() {
		Map<String, Object> params = new HashMap<>();
		params.put("title", "Page title");
		return Response.getTemplate("basics.jsp", params);
	}
	
	/**
	 * How work with variables
	 * @return http://localhost:8080/examples-templates/template/variable
	 */
	@Action("variable")
	public Response variableOptions() {
		Map<String, Object> params = new HashMap<>();
		params.put("title", "Some text");
		params.put(
			"url", 
			link.create(
				TemplateExample.class, c->c.variableOptions(),
				MapInit.create().append("foo", "dump").append("foo2", "dump2").toMap()
			)
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
	 * @return http://localhost:8080/examples-templates/template/owasp-form
	 */
	@Action("owasp-form")
	public Response owaspForm() {
		Map<String, Object> params = new HashMap<>();
		params.put("action", link.create(getClass(), c->c.owaspTest(null, null)));
		return Response.getTemplate("owaspForm.jsp", params);
	}
	
	/**
	 * 
	 * @return http://localhost:8080/examples-templates/template/owasp-print
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
	public List<Task> initInstances(Env env, Translator translator, Register register, Link link, Database database, Logger logger)
			throws Exception {
		register.addFactory(
			TemplateExample.class, 
			(trans, identity, authorizator, authenticator)->new TemplateExample(link)
		);
		return Arrays.asList();
	}

	@Override
	public String getName() {
		return "examples-templates";
	}
	
	@Override
	public String getTemplatesPath() {
		return "examples/samples/templates/template";
	}

	@Override
	public String getControllersPath() {
		return "toti/samples/template";
	}
	
}
