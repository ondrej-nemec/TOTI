package toti.samples.application.controllers;

import toti.application.annotations.Action;
import toti.application.annotations.Controller;
import toti.application.annotations.Secured;
import toti.application.answers.action.ResponseAction;
import toti.application.answers.response.Response;
import toti.lib.common.exceptions.LogicException;
import toti.lib.tcpip.enums.HttpMethod;

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
		return (req, translator, identity)->{
			throw new RuntimeException(new LogicException("Example of logic exception"));
		};
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
		return (req, translator, identity)->{
			return Response.OK().getText("This text should not be displayed");
		};
	}

	/**
	 * Exception is throwed by TOTI before method is called
	 * Caused by wrong method
	 * @throws ServerException 404 Not found
	 * @return http://localhost:8080/application/exceptions/post
	 */
	@Action(path="post", methods = HttpMethod.POST)
	public ResponseAction wrongHttpMethod() {
		return (req, translator, identity)->{
			return Response.OK().getText("This text should not be displayed");
		};
	}

	/**
	 * Exception throwed inside method before Response is returned, exception catched by CustomExceptionExtension
	 * @throws LogicException
	 * @return http://localhost:8080/application/exceptions/catched
	 */
	@Action(path="catched")
	public ResponseAction catched() {
		return (req, translator, identity)->{
			throw new LogicException("Example of logic exception");
		};
	}
	
}
