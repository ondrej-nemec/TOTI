package samples.examples.exceptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ji.common.Logger;
import ji.common.exceptions.LogicException;
import ji.common.functions.Env;
import ji.database.Database;
import ji.socketCommunication.http.HttpMethod;
import ji.translator.Translator;
import toti.HttpServer;
import toti.HttpServerFactory;
import toti.Module;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Method;
import toti.annotations.Secured;
import toti.application.Task;
import toti.registr.Register;
import toti.response.Response;

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
	 * @return http://localhost:8080/examples/exceptions/method
	 */
	@Action("method")
	public Response inMethod() {
		throw new LogicException("Example of logic exception");
	}

	/**
	 * Exception is throwed by TOTI before method is called
	 * Caused: user is not logged
	 * @throws ServerException 401 Unauthorized
	 * @return http://localhost:8080/examples/exceptions/secured
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
	 * @return http://localhost:8080/examples/exceptions/post
	 */
	@Action("post")
	@Method(HttpMethod.POST)
	public Response wrongHttpMethod() {
		return Response.getText("This text should not be displayed");
	}

	/**
	 * Exception throwed by TOTI after method is called
	 * Caused by missing template
	 * @throws RuntimeException
	 * @return http://localhost:8080/examples/exceptions/notemplate
	 */
	@Action("notemplate")
	public Response noTemplate() {
		return Response.getTemplate("/missing-template.jsp", new HashMap<>());
	}
	
	/**
	 * Exception throwed by TOTI after method is called during template rendering
	 * Caused: template parameters are NULL, calling parameters in template
	 * @throws NullPoinerException
	 * @return http://localhost:8080/examples/exceptions/intemplate
	 */
	@Action("intemplate")
	public Response inTemplate() {
		return Response.getTemplate("/inTemplate.jsp", null);
	}
	
	/**
	 * Exception throwed by TOTI after method is called during template parsing
	 * Caused: wrong template syntax
	 * @throws ???
	 * @return http://localhost:8080/examples/exceptions/syntax
	 */
	@Action("syntax")
	public Response templateSyntax() {
		return Response.getTemplate("/syntax.jsp", new HashMap<>());
	}

	/**
	 * No exception
	 * All previous method are requested async
	 * @return http://localhost:8080/examples/exceptions/async
	 */
	@Action("async")
	public Response async() {
		return Response.getTemplate("/async.jsp", new HashMap<>());
	}
	
	

	public static void main(String[] args) {
		List<Module> modules = Arrays.asList(new ExceptionsExample());
		try {
			HttpServer server = new HttpServerFactory()
				.setPort(8080)
				// .setDevelopIpAdresses(Arrays.asList()) // no develop ips
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

	@Override
	public String getName() {
		return "examples";
	}

	@Override
	public String getControllersPath() {
		return "samples/examples/exceptions";
	}
	
	@Override
	public String getTemplatesPath() {
		return "samples/examples/exceptions";
	}

	@Override
	public List<Task> initInstances(Env env, Translator translator, Register registr, Database database, Logger logger)
			throws Exception {
		registr.addFactory(ExceptionsExample.class, ()->new ExceptionsExample());
		return Arrays.asList();
	}
	
}
