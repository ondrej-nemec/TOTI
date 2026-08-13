package toti.examples.demo.modules.core;

import toti.core.annotations.Action;
import toti.core.annotations.Controller;
import toti.core.answers.action.ResponseAction;
import toti.core.answers.response.Response;
import toti.lib.common.exceptions.LogicException;
import toti.lib.tcpip.enums.HttpMethod;

/**
 * This example shows TOTI reactions on varios exceptions
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
		return (req, identity)->{
			throw new RuntimeException(new LogicException("Example of logic exception"));
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
		return (req, identity)->{
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
		return (req, identity)->{
			throw new LogicException("Example of logic exception");
		};
	}
	
}
