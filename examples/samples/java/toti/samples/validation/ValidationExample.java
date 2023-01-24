package toti.samples.validation;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import ji.common.functions.Env;
import ji.common.structures.ListInit;
import ji.database.Database;
import ji.socketCommunication.http.structures.RequestParameters;
import ji.translator.Translator;
import toti.Module;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Param;
import toti.annotations.ParamValidator;
import toti.annotations.Params;
import toti.annotations.ParamsValidator;
import toti.application.Task;
import toti.register.Register;
import toti.response.Response;
import toti.url.Link;
import toti.validation.ItemRules;
import toti.validation.Validator;

/**
 * This example shows various validations.
 * @author Ondřej Němec
 *
 */
@Controller("validation")
public class ValidationExample implements Module {

	/**
	 * No validator
	 * name will always be string or null
	 * age will always be int and never null
	 * @return http://localhost:8080/examples-validation/validation/noValidator?username=smith&age=42
	 */
	@Action("noValidator")
	public Response noValidator(@Param("username") String name, @Param("age") int age) {
		return Response.getText("Validated " + name + " " + age);
	}
	
	/**
	 * Validator come from validatioMethodValidator() method
	 * @return http://localhost:8080/examples-validation/validation/validationMethod?username=smith&age=42
	 */
	@Action(value = "validationMethod", validator = "validationMethodValidator")
	public Response validationMethod(@Params RequestParameters params) {
		return Response.getText("Validated " + params);
	}
	
	public Validator validationMethodValidator() {
		return new Validator(true)
			.addRule(ItemRules.forName("username", true).setMinLength(3).setMaxLength(15))
			.addRule(ItemRules.forName("age", true).setType(Integer.class).setMinValue(0));
	}
	
	/**
	 * Validator come from VALIDATOR_SERVICE_KEY (validationServiceValidator) service from register
	 * @return http://localhost:8080/examples-validation/validation/validationService?login=smith&width=180
	 */
	@Action(value = "validationService", validator = VALIDATOR_SERVICE_KEY)
	public Response validationService(@Params RequestParameters params) {
		return Response.getText("Validated " + params);
	}

	/**
	 * Validator fill validator parameters
	 * @return http://localhost:8080/examples-validation/validation/validatorParams?id=42&year=2022
	 */
	@Action(value = "validatorParams", validator = "validatorParamsValidator")
	public Response validatorParams(
			@Param("id") String id,
			@Params RequestParameters request,
			@ParamValidator("id") String validatorId,
			@ParamsValidator RequestParameters validator) {
		return Response.getText("Validated " + request + " " + validator);
	}
	
	public Validator validatorParamsValidator() {
		return new Validator(false)
			.setGlobalFunction((request, validator, trans)->{
				request.forEach((key, value)->{
					if ("id".equals(key)) {
						validator.put(key, "--" + value + "--");
					} else {
						validator.put("v__" + key, "--" + value + "--");
					}
				});
				return new ListInit<String>().toSet();
			});
	}
	
	private static final String VALIDATOR_SERVICE_KEY = "validationServiceValidator";
	
	private static Validator getServiceValidator() {
		return new Validator(true)
			.addRule(ItemRules.forName("login", true).setMinLength(3).setMaxLength(15))
			.addRule(ItemRules.forName("width", true).setType(Integer.class).setMinValue(0));
	}

	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Link link, Database database, Logger logger)
			throws Exception {
		register.addService(VALIDATOR_SERVICE_KEY, getServiceValidator());
		register.addFactory(ValidationExample.class, ()->new ValidationExample());
		return Arrays.asList();
	}

	@Override
	public String getName() {
		return "examples-validation";
	}

	@Override
	public String getControllersPath() {
		return "toti/samples/validation";
	}
	
	@Override
	public String getTemplatesPath() {
		return "examples/samples/templates/validation";
	}

}
