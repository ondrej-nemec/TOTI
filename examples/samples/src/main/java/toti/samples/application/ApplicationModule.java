package toti.samples.application;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;

import ji.common.functions.Env;
import ji.database.Database;
import ji.socketCommunication.http.StatusCode;
import ji.translator.Translator;
import toti.answers.request.Identity;
import toti.answers.response.Response;
import toti.answers.router.Link;
import toti.answers.router.Router;
import toti.application.Module;
import toti.application.Task;
import toti.application.register.Register;
import toti.extensions.CustomExceptionExtension;
import toti.samples.application.controllers.ExceptionsController;
import toti.samples.application.controllers.RequestController;
import toti.samples.application.controllers.ResponseController;

public class ApplicationModule implements Module {

	@Override
	public String getName() {
		return "application";
	}

	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Link link, Database database, Logger logger) throws Exception {
		// TODO register.createRoutedAction(getClass(), null); + example
		// TODO register.setSessionUserProvider(null); + example
		// TODO zpracovani pozadavku - steps
		
		// uncomment for using custom exception handler
		//*
		register.setCustomExceptionResponse(new CustomExceptionExtension() {
			@Override
			public Response catchException(toti.answers.request.Request request, StatusCode status, Identity identity,
					Translator translator, Throwable t, boolean isDevelopResponseAllowed, boolean isAsyncRequest) {
				return Response.OK().getText("Oops, something happends.");
			}
		});
		//*/
		
		// TODO links to all methods - call sync and async
		register.addController(ExceptionsController.class, ()->new ExceptionsController());
		register.addController(RequestController.class, ()->new RequestController());
		register.addController(ResponseController.class, ()->new ResponseController());
		return Arrays.asList();
	}
	
	@Override
	public void addRoutes(Router router, Link link) {
		// TODO router.addUrl(getName(), getName()); + example
		// TODO router.setRedirectOnNotLoggedInUser(getName()); + example
	}
	
	@Override
	public String getTemplatesPath() {
		return "templates/application";
	}

}
