package controllers;

import java.util.Arrays;

import mvc.validation.ItemRules;
import mvc.validation.Validator;

public class EntityValidator {
	
	public static final String NAME = "entityValidator";
	
	public Validator getValidator() {
		return new Validator(true)
				.addRule(ItemRules.forName("name", true).setMaxLength(10).setMinLength(2))
				.addRule(ItemRules.forName("age", true).setMaxValue(18, "Must be adult"))
				.addRule(ItemRules.forName("maried", true))
				.addRule(ItemRules.forName("born", true))
				.addRule(ItemRules.forName("password", true))
				.addRule(ItemRules.forName("email", true).setRegex("([a-zA-Z0-9\\.])?@([a-zA-Z0-9\\.])?\\.([a-zA-Z0-9\\.]){2}"))
				.addRule(ItemRules.forName("sex", true).setAllowedValues(Arrays.asList("female")))
				.addRule(ItemRules.forName("department", true))
				.addRule(ItemRules.forName("foto", true)
						.setFileMaxSize(10*1024)
						.setFileMinSize(5*1024)
						.setAllowedFileTypes(Arrays.asList("image/png"))
				);
	}

}
