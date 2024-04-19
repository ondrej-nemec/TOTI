package toti.samples.templating;

import java.util.HashMap;

import toti.annotations.Action;
import toti.annotations.Controller;
import toti.answers.action.ResponseAction;
import toti.answers.action.ResponseBuilder;
import toti.answers.response.Response;

/**
 * This example shows TOTI reaction on Template errors
 * @author Ondřej Němec
 *
 */
@Controller("exceptions")
public class ExceptionsController {

	/**
	 * Exception throwed by TOTI after method is called
	 * Caused by missing template
	 * @throws FileNotFoundException
	 * @return http://localhost:8080/application/exceptions/notemplate
	 */
	@Action(path="notemplate")
	public ResponseAction noTemplate() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			return Response.OK().getTemplate("/exceptions/missing-template.jsp", new HashMap<>());
		});
	}
	
	/**
	 * Exception throwed by TOTI after method is called during template rendering
	 * Caused: template parameters are NULL, calling parameters in template
	 * @throws NullPoinerException
	 * @return http://localhost:8080/application/exceptions/intemplate
	 */
	@Action(path="intemplate")
	public ResponseAction inTemplate() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			return Response.OK().getTemplate("/exceptions/inTemplate.jsp", null);
		});
	}
	
	/**
	 * Exception throwed by TOTI after method is called during template parsing
	 * Caused: wrong template syntax
	 * @throws TemplateException Unknown syntax error
	 * @return http://localhost:8080/application/exceptions/syntax
	 */
	@Action(path="syntax")
	public ResponseAction templateSyntax() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			return Response.OK().getTemplate("/exceptions/syntax.jsp", new HashMap<>());
		});
	}
	
}
