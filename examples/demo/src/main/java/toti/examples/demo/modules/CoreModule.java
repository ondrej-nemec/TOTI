package toti.examples.demo.modules;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import toti.core.answers.router.Link;
import toti.core.answers.router.Router;
import toti.core.application.Module;
import toti.core.application.Task;
import toti.core.application.register.Register;
import toti.examples.demo.modules.core.ErrorHandlerExampleController;
import toti.examples.demo.modules.core.ExceptionsController;
import toti.examples.demo.modules.core.IdentityController;
import toti.examples.demo.modules.core.RequestController;
import toti.examples.demo.modules.core.ResponseController;
import toti.examples.demo.modules.core.WebsocketTask;
import toti.lib.files.env.Env;

public class CoreModule implements Module {

	@Override
	public String getName() {
		return "core";
	}

	@Override
	public List<Task> init(Env env, Register register, Link link, Router router) {

		// register.getExtension(TemplateExtension.class).registerModule(getName(), "", "templates");

		WebsocketTask task = new WebsocketTask(LogManager.getLogger("task"));
		
		//SessionUserProviderExample sipe = new SessionUserProviderExample(link);
		
		//register.getExtension(AuthenticationExtension.class).setSessionUserProvider(sipe);
		
		register.addController(ErrorHandlerExampleController.class, ()->new ErrorHandlerExampleController());
		register.addController(ExceptionsController.class, ()->new ExceptionsController());
		register.addController(ResponseController.class, ()->new ResponseController(task, link));
		register.addController(RequestController.class, ()->new RequestController());
		register.addController(IdentityController.class, ()->new IdentityController(link));
		/*register.addController(ProcessingController.class, ()->new ProcessingController());
		register.addController(UserController.class, ()->new UserController(sipe));*/

		// redirect must be after registration
		router.addUrl("/toti-test", link.create(ResponseController.class, c->c.getText()));
		router.addUrl("/application-response.text", link.create(ResponseController.class, c->c.getText()));
		router.addUrl("/ico", "/favicon.ico");

		return Arrays.asList(task);
	}

}
