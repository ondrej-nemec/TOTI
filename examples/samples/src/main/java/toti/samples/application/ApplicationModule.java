package toti.samples.application;

import java.util.Arrays;
import java.util.List;

import ji.common.exceptions.LogicException;
import ji.common.functions.Env;
import ji.socketCommunication.http.StatusCode;
import toti.answers.request.Identity;
import toti.answers.request.Request;
import toti.answers.response.Response;
import toti.answers.router.Link;
import toti.answers.router.Router;
import toti.application.Module;
import toti.application.Task;
import toti.application.register.Register;
import toti.extensions.CustomExceptionExtension;
import toti.extensions.TranslatorExtension;
import toti.samples.application.controllers.ExceptionsController;
import toti.samples.application.controllers.ProcessingController;
import toti.samples.application.controllers.RequestController;
import toti.samples.application.controllers.ResponseController;

public class ApplicationModule implements Module {

	@Override
	public String getName() {
		return "application";
	}

	@Override
	public List<Task> initInstances(Env env, Register register, Link link) throws Exception {
		// TODO register.createRoutedAction(getClass(), null); + example
		// TODO register.setSessionUserProvider(null); + example
		// TODO zpracovani pozadavku - steps
		
		register.setCustomExceptionResponse(new CustomExceptionExtension() {
			@Override
			public Response catchException(Request request, StatusCode status, Identity identity,
				TranslatorExtension translator, Throwable t, boolean isDevelopResponseAllowed, boolean isAsyncRequest) {
				// handle only some expected exceptions
				if (t instanceof LogicException) {
					return Response.OK().getText("Oops, something happends: " + t.getMessage());
				}
				throw new RuntimeException(t);
			}
		});
		
		// TODO links to all methods - call sync and async
		register.addController(ExceptionsController.class, ()->new ExceptionsController());
		register.addController(RequestController.class, ()->new RequestController());
		register.addController(ResponseController.class, ()->new ResponseController());
		register.addController(ProcessingController.class, ()->new ProcessingController());
		return Arrays.asList();
	}
	
	@Override
	public void addRoutes(Router router, Link link) {
		// TODO router.addUrl(getName(), getName()); + example
		// TODO router.setRedirectOnNotLoggedInUser(getName()); + example
	}

}
