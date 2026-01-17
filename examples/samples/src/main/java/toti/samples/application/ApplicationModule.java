package toti.samples.application;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import toti.answers.request.Identity;
import toti.answers.request.Request;
import toti.answers.response.Response;
import toti.answers.router.Link;
import toti.answers.router.Router;
import toti.application.Module;
import toti.application.Task;
import toti.application.register.Register;
import toti.extension.templating.TemplateExtension;
import toti.extensions.auth.AuthenticationExtension;
import toti.lib.common.exceptions.LogicException;
import toti.lib.files.env.Env;
import toti.lib.tcpip.enums.StatusCode;
import toti.extensions.CustomExceptionExtension;
import toti.extensions.TranslatorExtension;
import toti.samples.application.controllers.ExceptionsController;
import toti.samples.application.controllers.ProcessingController;
import toti.samples.application.controllers.RequestController;
import toti.samples.application.controllers.ResponseController;
import toti.samples.application.controllers.UserController;

public class ApplicationModule implements Module {

	@Override
	public String getName() {
		return "application";
	}

	@Override
	public List<Task> initInstances(Env env, Register register, Link link) throws Exception {
		register.getExtension(TemplateExtension.class).registerModule(getName(), "", "templates");
		
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
		
		TaskExample task = new TaskExample(LogManager.getLogger("task"));
		
		SessionUserProviderExample sipe = new SessionUserProviderExample(link);
		
		register.getExtension(AuthenticationExtension.class).setSessionUserProvider(sipe);
		
		register.addController(ExceptionsController.class, ()->new ExceptionsController());
		register.addController(RequestController.class, ()->new RequestController());
		register.addController(ResponseController.class, ()->new ResponseController(task, link));
		register.addController(ProcessingController.class, ()->new ProcessingController());
		register.addController(UserController.class, ()->new UserController(sipe));
		return Arrays.asList(task);
	}
	
	@Override
	public void addRoutes(Router router, Link link) {
		router.addUrl("/toti-test", link.create(ResponseController.class, c->c.getText()));
		router.addUrl("/application-response.text", link.create(ResponseController.class, c->c.getText()));
		router.addUrl("/ico", "/favicon.ico");
	}

}
