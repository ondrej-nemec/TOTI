package example.web.validator;

import java.util.Arrays;

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
		return Validator.create(NAME_GRID, false)
			.addRule(ItemRules.forName("pageIndex", true))
			.addRule(ItemRules.forName("pageSize", true))
			.addRule(ItemRules.forName("filters", true).setMapSpecification(new Validator(true)
				.addRule(ItemRules.forName("name", false).setType(String.class))
				.addRule(ItemRules.forName("age", false).setType(Integer.class))
				.addRule(ItemRules.forName("active", false).setType(Boolean.class))
				.addRule(ItemRules.forName("parent", false).setType(Integer.class))
				.addRule(ItemRules.forName("simple_date", false).setType(String.class))
				.addRule(ItemRules.forName("dt_local", false).setType(String.class))
				.addRule(ItemRules.forName("month", false).setType(String.class))
				.addRule(ItemRules.forName("week", false).setType(String.class))
				.addRule(ItemRules.forName("time", false).setType(String.class))
			))
			.addRule(ItemRules.forName("sorting", true).setMapSpecification(new Validator(true)
					.addRule(ItemRules.forName("name", false).setAllowedValues(Arrays.asList("ASC", "DESC")))
					.addRule(ItemRules.forName("age", false).setAllowedValues(Arrays.asList("ASC", "DESC")))
					.addRule(ItemRules.forName("active", false).setAllowedValues(Arrays.asList("ASC", "DESC")))
					.addRule(ItemRules.forName("parent", false).setAllowedValues(Arrays.asList("ASC", "DESC")))
					.addRule(ItemRules.forName("simple_date", false).setAllowedValues(Arrays.asList("ASC", "DESC")))
					.addRule(ItemRules.forName("dt_local", false).setAllowedValues(Arrays.asList("ASC", "DESC")))
					.addRule(ItemRules.forName("month", false).setAllowedValues(Arrays.asList("ASC", "DESC")))
					.addRule(ItemRules.forName("week", false).setAllowedValues(Arrays.asList("ASC", "DESC")))
					.addRule(ItemRules.forName("time", false).setAllowedValues(Arrays.asList("ASC", "DESC")))
			));
	}
	
	public static Validator getFormValidator() {
		return Validator.create(NAME_FORM, true) // TODO validator
				.addRule(ItemRules.forName("id", false).setType(Integer.class))
				.addRule(ItemRules.forName("name", false).setType(String.class))
				.addRule(ItemRules.forName("email", false).setType(String.class))
				.addRule(ItemRules.forName("age", false).setType(Integer.class))
				.addRule(ItemRules.forName("pasw", false).setType(String.class))
				.addRule(ItemRules.forName("range", false).setType(Integer.class))
				.addRule(ItemRules.forName("active", false).setType(Boolean.class))
				.addRule(ItemRules.forName("defvalue", false).setType(Boolean.class))
				.addRule(ItemRules.forName("sex", false).setType(String.class))
				.addRule(ItemRules.forName("parent", false).setType(Integer.class))
				.addRule(ItemRules.forName("select1", false).setType(Integer.class))
				.addRule(ItemRules.forName("simple_date", false).setType(String.class))
				.addRule(ItemRules.forName("dt_local", false).setType(String.class))
				.addRule(ItemRules.forName("month", false).setType(String.class))
				.addRule(ItemRules.forName("time", false).setType(String.class))
				.addRule(ItemRules.forName("week", false).setType(String.class))
				.addRule(ItemRules.forName("favorite_color", false).setType(String.class))
				.addRule(ItemRules.forName("file", false))
				.addRule(ItemRules.forName("comment", false).setType(String.class))
				;
	}
}
