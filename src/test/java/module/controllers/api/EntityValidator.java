package module.controllers.api;

import java.util.Arrays;

import toti.validation.ItemRules;
import toti.validation.Validator;

public class EntityValidator {
	
	public static final String NAME_FORM = "entityFormValidator";
	public static final String NAME_GRID = "entityGridValidator";

	public static void init() {
		getGridValidator();
		getFormValidator();
	}
	
	public static Validator getGridValidator() {
		return Validator.create(NAME_GRID, true)
			.addRule(ItemRules.forName("pageIndex", true))
			.addRule(ItemRules.forName("pageSize", true))
			.addRule(ItemRules.forName("filters", true).setMapSpecification(new Validator(true)
				.addRule(ItemRules.forName("name", false))
				.addRule(ItemRules.forName("rank", false).setType(Integer.class))
				.addRule(ItemRules.forName("edited", false))
				.addRule(ItemRules.forName("FK_id", false).setType(Integer.class).setAllowedValues(Arrays.asList(
					"1", "2", "3", "4"
				)))
				.addRule(ItemRules.forName("is_main", false).setType(Boolean.class).setChangeValue((value)->{
					if (value != null && value.equals("true")) {
						return "t";
					} else if (value != null && value.equals("false")) {
						return "f";
					}
					return value;
				}))
			))
			.addRule(ItemRules.forName("sorting", true).setMapSpecification(new Validator(true)
				.addRule(ItemRules.forName("name", false).setAllowedValues(Arrays.asList("DESC", "ASC")))
				.addRule(ItemRules.forName("rank", false).setAllowedValues(Arrays.asList("DESC", "ASC")))
				.addRule(ItemRules.forName("edited", false).setAllowedValues(Arrays.asList("DESC", "ASC")))
				.addRule(ItemRules.forName("FK_id", false).setAllowedValues(Arrays.asList("DESC", "ASC")))
				.addRule(ItemRules.forName("is_main", false).setAllowedValues(Arrays.asList("DESC", "ASC")))
			));
	}
	
	public static Validator getFormValidator() {
		return Validator.create(NAME_FORM, true) // TODO validator
				.addRule(ItemRules.forName("id", false).setType(Integer.class))
				.addRule(ItemRules.forName("name", true).setType(String.class).setMaxLength(50))
				.addRule(ItemRules.forName("secret", true).setType(String.class).setMaxLength(20))
				.addRule(ItemRules.forName("email", true).setType(String.class).setMaxLength(50))
				.addRule(ItemRules.forName("edited", true))
				.addRule(ItemRules.forName("is_main", false).setType(Boolean.class))
				.addRule(ItemRules.forName("rank", true).setType(Integer.class))
				.addRule(ItemRules.forName("FK_id", true).setType(Integer.class))
				.addRule(ItemRules.forName("lang", true).setType(String.class).setMaxLength(10))
				.addRule(ItemRules.forName("comment", false));
	}

}
