package toti.samples.application.controllers;

import java.util.HashMap;

import ji.common.exceptions.LogicException;
import ji.socketCommunication.http.HttpMethod;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Secured;
import toti.answers.action.RequestInterruptedException;
import toti.answers.action.ResponseAction;
import toti.answers.action.ResponseBuilder;
import toti.answers.response.Response;

/**
 * This example shows TOTI reaction on varios exceptions
 * @author Ondřej Němec
 *
 */
@Controller("exceptions")
public class ExceptionsController {

	/**
	 * Exception throwed inside method before Response is returned
	 * @throws LogicException
	 * @return http://localhost:8080/application/exceptions/method
	 */
	@Action(path="method")
	public ResponseAction inMethod() {
		throw new LogicException("Example of logic exception");
	}
	
	/**
	 * Exception throwed inside method before Response is returned
	 * @throws LogicException
	 * @return http://localhost:8080/application/exceptions/cause
	 */
	@Action(path="cause")
	public ResponseAction inMethodCause() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			throw new RuntimeException(new LogicException("Example of logic exception"));
		});
	}

	/**
	 * Exception is throwed by TOTI before method is called
	 * Caused: user is not logged
	 * @throws ServerException 401 Unauthorized
	 * @return http://localhost:8080/application/exceptions/secured
	 */
	@Action(path="secured")
	@Secured()
	public ResponseAction secured() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			return Response.OK().getText("This text should not be displayed");
		});
	}

	/**
	 * RequestInterruptedException throwed
	 * @throws RequestInterruptedException
	 * @return http://localhost:8080/application/exceptions/interrupt
	 */
	@Action(path="interrupt")
	@Secured()
	public ResponseAction requestInterrupt() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			throw new RequestInterruptedException(Response.OK().getText("Request interrupted"));
		});
	}

	/**
	 * Exception is throwed by TOTI before method is called
	 * Caused by wrong method
	 * @throws ServerException 404 Not found
	 * @return http://localhost:8080/application/exceptions/post
	 */
	@Action(path="post", methods = HttpMethod.POST)
	public ResponseAction wrongHttpMethod() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			return Response.OK().getText("This text should not be displayed");
		});
	}

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
