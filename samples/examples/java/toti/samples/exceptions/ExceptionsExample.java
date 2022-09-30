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
import ji.socketCommunication.http.structures.Request;
import ji.translator.Translator;
import toti.CustomExceptionResponse;
import toti.Module;
import toti.Router;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Method;
import toti.annotations.Secured;
import toti.application.Task;
import toti.register.Register;
import toti.response.Response;
import toti.security.Identity;
import toti.url.MappedUrl;

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
	@Action("method")
	public Response inMethod() {
		throw new LogicException("Example of logic exception");
	}
	
	/**
	 * Exception throwed inside method before Response is returned
	 * @throws LogicException
	 * @return http://localhost:8080/exception-example/exceptions/cause
	 */
	@Action("cause")
	public Response inMethodCause() {
		throw new RuntimeException(new LogicException("Example of logic exception"));
	}

	/**
	 * Exception is throwed by TOTI before method is called
	 * Caused: user is not logged
	 * @throws ServerException 401 Unauthorized
	 * @return http://localhost:8080/exception-example/exceptions/secured
	 */
	@Action("secured")
	@Secured()
	public Response secured() {
		return Response.getText("This text should not be displayed");
	}

	/**
	 * Exception is throwed by TOTI before method is called
	 * Caused by wrong method
	 * @throws ServerException 404 Not found
	 * @return http://localhost:8080/exception-example/exceptions/post
	 */
	@Action("post")
	@Method(HttpMethod.POST)
	public Response wrongHttpMethod() {
		return Response.getText("This text should not be displayed");
	}

	/**
	 * Exception throwed by TOTI after method is called
	 * Caused by missing template
	 * @throws FileNotFoundException
	 * @return http://localhost:8080/exception-example/exceptions/notemplate
	 */
	@Action("notemplate")
	public Response noTemplate() {
		return Response.getTemplate("/missing-template.jsp", new HashMap<>());
	}
	
	/**
	 * Exception throwed by TOTI after method is called during template rendering
	 * Caused: template parameters are NULL, calling parameters in template
	 * @throws NullPoinerException
	 * @return http://localhost:8080/exception-example/exceptions/intemplate
	 */
	@Action("intemplate")
	public Response inTemplate() {
		return Response.getTemplate("/inTemplate.jsp", null);
	}
	
	/**
	 * Exception throwed by TOTI after method is called during template parsing
	 * Caused: wrong template syntax
	 * @throws TemplateException Unknown syntax error
	 * @return http://localhost:8080/exception-example/exceptions/syntax
	 */
	@Action("syntax")
	public Response templateSyntax() {
		return Response.getTemplate("/syntax.jsp", new HashMap<>());
	}

	/**
	 * No exception
	 * All previous method are requested async
	 * @return http://localhost:8080/exception-example/exceptions/async
	 */
	@Action("async")
	public Response async() {
		return Response.getTemplate("/async.jsp", new HashMap<>());
	}
	
	@Override
	public void addRoutes(Router router) {
		// uncoment for trying custm exception handler
		// router.setCustomExceptionResponse(getCustomExceptionHandler());
	}
	
	protected CustomExceptionResponse getCustomExceptionHandler() {
		return new CustomExceptionResponse() {
			@Override
			public Response catchException(StatusCode status, Request request, Identity identity, MappedUrl mappedUrl,
					Throwable t, Translator translator, boolean isDevelopResponseAllowed, boolean isAsyncRequest) {
				return Response.getText("Oops, something happends.");
			}
		};
	}

	@Override
	public String getName() {
		return "exception-example";
	}

	@Override
	public String getControllersPath() {
		return "toti/samples/exceptions";
	}
	
	@Override
	public String getTemplatesPath() {
		return "samples/examples/templates/exceptions";
	}

	@Override
	public List<Task> initInstances(Env env, Translator translator, Register registr, Database database, Logger logger)
			throws Exception {
		registr.addFactory(ExceptionsExample.class, ()->new ExceptionsExample());
		return Arrays.asList();
	}
	
}
