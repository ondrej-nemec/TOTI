package toti.samples.validation;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import ji.common.functions.Env;
import ji.database.Database;
import ji.translator.Translator;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.answers.action.ResponseAction;
import toti.answers.action.ResponseBuilder;
import toti.answers.response.Response;
import toti.answers.router.Link;
import toti.application.Module;
import toti.application.Task;
import toti.application.register.Register;
import toti.ui.validation.ItemRules;
import toti.ui.validation.Validator;

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
	@Action(path="noValidator")
	public ResponseAction noValidator() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			String name = req.getBodyParam("username").getString();
			int age = req.getBodyParam("age").getInteger();
			return Response.OK().getText("Validated " + name + " " + age);
		});
	}
	
	/**
	 * Validator come from validatioMethodValidator() method
	 * @return http://localhost:8080/examples-validation/validation/validationMethod?username=smith&age=42
	 */
	@Action(path = "validationMethod")
	public ResponseAction validationMethod() {
		return ResponseBuilder.get()
		.validate(
			new Validator(true)
				.addRule(ItemRules.objectRules("username", true).setMinLength(3).setMaxLength(15))
				.addRule(ItemRules.numberRules("age", true, Integer.class).setMinValue(0))
		)
		.createResponse((req, translator, identity)->{
			return Response.OK().getText("Validated " + req.getBodyParams());
		});
	}

	/**
	 * Validator fill validator parameters
	 * @return http://localhost:8080/examples-validation/validation/validatorParams?id=42&year=2022
	 */
	@Action(path = "validatorParams")
	public ResponseAction validatorParams() {
		return ResponseBuilder.get()
		.validate(
			new Validator(false)
			.setGlobalFunction((request, body, result, translator)->{
				body.forEach((key, value)->{
					if ("id".equals(key)) {
						request.getData().put(key, "--" + value + "--");
					} else {
						request.getData().put("v__" + key, "--" + value + "--");
					}
				});
			})
		)
		.createResponse((req, translator, identity)->{
			return Response.OK().getText("Validated " + req.getBodyParams() + " " + req.getData());
		});
	}

	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Link link, Database database, Logger logger)
			throws Exception {
		register.addController(ValidationExample.class, ()->new ValidationExample());
		return Arrays.asList();
	}

	@Override
	public String getName() {
		return "examples-validation";
	}
	
	@Override
	public String getTemplatesPath() {
		return "templates/validation";
	}

}
