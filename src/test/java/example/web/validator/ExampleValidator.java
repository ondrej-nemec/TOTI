package example.web.validator;

import toti.validation.ItemRules;
import toti.validation.Validator;

public class ExampleValidator {

	public static final String NAME_FORM = "exampleFormValidator";
	public static final String NAME_GRID = "exampleGridValidator";
	
	public static void init() {
		getGridValidator();
		getFormValidator();
	}
	
	public static Validator getGridValidator() {
		return Validator.create(NAME_GRID, true)
			.addRule(ItemRules.forName("pageIndex", true))
			.addRule(ItemRules.forName("pageSize", true))
			.addRule(ItemRules.forName("filters", true).setMapSpecification(new Validator(true)
				// TODO values check
			))
			.addRule(ItemRules.forName("sorting", true).setMapSpecification(new Validator(true)
				 // TODO values check
			));
	}
	
	public static Validator getFormValidator() {
		return Validator.create(NAME_FORM, true); // TODO validator
	}
}
