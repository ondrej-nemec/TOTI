package controllers.other;

import java.util.Arrays;

import mvc.validation.ItemRules;
import mvc.validation.Validator;

public class EntityValidator {
	
	public static final String NAME_FORM = "entityFormValidator";
	public static final String NAME_GRID = "entityGridValidator";
	
	public static Validator getGridValidator() {
		return new Validator(true)
				.addRule(ItemRules.forName("pageIndex", true))
				.addRule(ItemRules.forName("pageSize", true))
				.addRule(ItemRules.forName("filters", false)) // TODO values check
				.addRule(ItemRules.forName("sorting", false)); // TODO values check
	}
	
	public static Validator getFormValidator() {
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
