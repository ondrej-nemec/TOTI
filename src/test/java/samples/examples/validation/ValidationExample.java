package samples.examples.validation;

import java.util.Arrays;
import java.util.List;

import ji.common.Logger;
import ji.common.functions.Env;
import ji.database.Database;
import ji.socketCommunication.http.server.RequestParameters;
import ji.translator.Translator;
import toti.HttpServer;
import toti.HttpServerFactory;
import toti.Module;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Params;
import toti.application.Task;
import toti.registr.Register;
import toti.response.Response;
import toti.validation.ItemRules;
import toti.validation.Validator;

/**
 * This example shows various validations.
 * @author Ondřej Němec
 *
 */
@Controller("validation")
public class ValidationExample implements Module {
	
	public Validator validationMethodValidator() {
		return new Validator(true)
			.addRule(ItemRules.forName("username", true).setMinLength(3).setMaxLength(15))
			.addRule(ItemRules.forName("age", true).setType(Integer.class).setMinValue(0));
	}
	
	/**
	 * Validator come from validatioMethodValidator() method
	 * @return http://localhost:8080/examples/validation/validationMethod?username=smith&age=42
	 */
	@Action(value = "validationMethod", validator = "validationMethodValidator")
	public Response validationMethod(@Params RequestParameters params) {
		return Response.getText("Validated " + params);
	}
	
	/**
	 * Validator come from VALIDATOR_SERVICE_KEY (validationServiceValidator) service from register
	 * @return http://localhost:8080/examples/validation/validationService?login=smith&width=180
	 */
	@Action(value = "validationService", validator = VALIDATOR_SERVICE_KEY)
	public Response validationService(@Params RequestParameters params) {
		return Response.getText("Validated " + params);
	}
	
	private static final String VALIDATOR_SERVICE_KEY = "validationServiceValidator";
	
	private static Validator getServiceValidator() {
		return new Validator(true)
			.addRule(ItemRules.forName("login", true).setMinLength(3).setMaxLength(15))
			.addRule(ItemRules.forName("width", true).setType(Integer.class).setMinValue(0));
	}

	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Database database, Logger logger)
			throws Exception {
		register.addService(VALIDATOR_SERVICE_KEY, getServiceValidator());
		register.addFactory(ValidationExample.class, ()->new ValidationExample());
		return Arrays.asList();
	}

	public static void main(String[] args) {
		List<Module> modules = Arrays.asList(new ValidationExample());
		try {
			HttpServer server = new HttpServerFactory()
				.setPort(8080)
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
		return "samples/examples/validation";
	}
	
	@Override
	public String getTemplatesPath() {
		return "samples/examples/validation";
	}

}
