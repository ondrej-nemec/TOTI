package toti.examples.modules;

import java.util.Arrays;
import java.util.List;

import toti.application.answers.router.Link;
import toti.application.answers.router.Router;
import toti.application.application.Module;
import toti.application.application.Task;
import toti.application.application.register.Register;
import toti.examples.modules.application.ErrorHandlerExampleController;
import toti.examples.modules.application.ExceptionsController;
import toti.lib.files.env.Env;

/**
 *
 * @author coder
 */
public class ApplicationModule implements Module {

	@Override
	public String getName() {
		return "application";
	}

	@Override
	public List<Task> initInstances(Env env, Register register, Link link) throws Exception {
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
	
	@Override
	public void addRoutes(Router router, Link link) {
		//router.addUrl("/toti-test", link.create(ResponseController.class, c->c.getText()));
		//router.addUrl("/application-response.text", link.create(ResponseController.class, c->c.getText()));
		router.addUrl("/ico", "/favicon.ico");
	}

}
