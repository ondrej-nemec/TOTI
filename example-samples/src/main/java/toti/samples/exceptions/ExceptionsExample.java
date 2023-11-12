package toti.samples.exceptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;
import ji.common.exceptions.LogicException;
import ji.common.functions.Env;
import ji.database.Database;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import ji.translator.Translator;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Secured;
import toti.answers.action.ResponseAction;
import toti.answers.action.ResponseBuilder;
import toti.answers.request.Identity;
import toti.answers.response.Response;
import toti.answers.router.Link;
import toti.answers.router.Router;
import toti.application.Module;
import toti.application.Task;
import toti.application.register.Register;
import toti.extensions.CustomExceptionResponse;

/**
 * This example shows TOTI reaction on varios exceptions
 * @author Ondřej Němec
 *
 */
@Controller("exceptions")
public class ExceptionsExample implements Module {
	
	/**
	 * Exception throwed inside method before Response is returned
	 * @throws LogicException
	 * @return http://localhost:8080/exception-example/exceptions/method
	 */
	@Action(path="method")
	public ResponseAction inMethod() {
		throw new LogicException("Example of logic exception");
	}
	
	/**
	 * Exception throwed inside method before Response is returned
	 * @throws LogicException
	 * @return http://localhost:8080/exception-example/exceptions/cause
	 */
	@Action(path="cause")
	public ResponseAction inMethodCause() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			throw new RuntimeException(new LogicException("Example of logic exception"));
		});
	}

	/**
	 * Exception is throwed by TOTI before method is called
	 * Caused: user is not logged
	 * @throws ServerException 401 Unauthorized
	 * @return http://localhost:8080/exception-example/exceptions/secured
	 */
	@Action(path="secured")
	@Secured()
	public ResponseAction secured() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getText("This text should not be displayed");
		});
	}

	/**
	 * Exception is throwed by TOTI before method is called
	 * Caused by wrong method
	 * @throws ServerException 404 Not found
	 * @return http://localhost:8080/exception-example/exceptions/post
	 */
	@Action(path="post", methods = HttpMethod.POST)
	public ResponseAction wrongHttpMethod() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getText("This text should not be displayed");
		});
	}

	/**
	 * Exception throwed by TOTI after method is called
	 * Caused by missing template
	 * @throws FileNotFoundException
	 * @return http://localhost:8080/exception-example/exceptions/notemplate
	 */
	@Action(path="notemplate")
	public ResponseAction noTemplate() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getTemplate("/missing-template.jsp", new HashMap<>());
		});
	}
	
	/**
	 * Exception throwed by TOTI after method is called during template rendering
	 * Caused: template parameters are NULL, calling parameters in template
	 * @throws NullPoinerException
	 * @return http://localhost:8080/exception-example/exceptions/intemplate
	 */
	@Action(path="intemplate")
	public ResponseAction inTemplate() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getTemplate("/inTemplate.jsp", null);
		});
	}
	
	/**
	 * Exception throwed by TOTI after method is called during template parsing
	 * Caused: wrong template syntax
	 * @throws TemplateException Unknown syntax error
	 * @return http://localhost:8080/exception-example/exceptions/syntax
	 */
	@Action(path="syntax")
	public ResponseAction templateSyntax() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getTemplate("/syntax.jsp", new HashMap<>());
		});
	}

	/**
	 * No exception
	 * All previous method are requested async
	 * @return http://localhost:8080/exception-example/exceptions/async
	 */
	@Action(path="async")
	public ResponseAction async() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getTemplate("/async.jsp", new HashMap<>());
		});
	}
	
	@Override
	public void addRoutes(Router router, Link link) {
		// uncoment for trying custm exception handler
		// router.setCustomExceptionResponse(getCustomExceptionHandler());
	}
	
	protected CustomExceptionResponse getCustomExceptionHandler() {
		return new CustomExceptionResponse() {

			@Override
			public Response catchException(toti.answers.request.Request request, StatusCode status, Identity identity,
					Translator translator, Throwable t, boolean isDevelopResponseAllowed, boolean isAsyncRequest) {
				return Response.OK().getText("Oops, something happends.");
			}
			
		};
	}

	@Override
	public String getName() {
		return "exception-example";
	}
	
	@Override
	public String getTemplatesPath() {
		return "templates/exceptions";
	}

	@Override
	public List<Task> initInstances(Env env, Translator translator, Register registr, Link link, Database database, Logger logger)
			throws Exception {
		registr.addController(ExceptionsExample.class, ()->new ExceptionsExample());
		return Arrays.asList();
	}
	
}
