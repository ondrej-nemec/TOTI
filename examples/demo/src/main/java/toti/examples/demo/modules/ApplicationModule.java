package toti.examples.demo.modules;

import java.util.Arrays;
import java.util.List;

import toti.core.answers.router.Link;
import toti.core.answers.router.Router;
import toti.core.application.Module;
import toti.core.application.Task;
import toti.core.application.register.Register;
import toti.examples.demo.modules.application.ErrorHandlerExampleController;
import toti.examples.demo.modules.application.ExceptionsController;
import toti.lib.files.env.Env;

public class ApplicationModule implements Module {

	@Override
	public String getName() {
		return "application";
	}

	@Override
	public List<Task> init(Env env, Register register, Link link, Router router) {
		//router.addUrl("/toti-test", link.create(ResponseController.class, c->c.getText()));
		//router.addUrl("/application-response.text", link.create(ResponseController.class, c->c.getText()));
		router.addUrl("/ico", "/favicon.ico");

		// register.getExtension(TemplateExtension.class).registerModule(getName(), "", "templates");

		//TaskExample task = new TaskExample(LogManager.getLogger("task"));
		
		//SessionUserProviderExample sipe = new SessionUserProviderExample(link);
		
		//register.getExtension(AuthenticationExtension.class).setSessionUserProvider(sipe);
		
		register.addController(ErrorHandlerExampleController.class, ()->new ErrorHandlerExampleController());
		register.addController(ExceptionsController.class, ()->new ExceptionsController());
		/*register.addController(RequestController.class, ()->new RequestController());
		register.addController(ResponseController.class, ()->new ResponseController(task, link));
		register.addController(ProcessingController.class, ()->new ProcessingController());
		register.addController(UserController.class, ()->new UserController(sipe));*/
		return Arrays.asList(/*task*/);
	}

}
