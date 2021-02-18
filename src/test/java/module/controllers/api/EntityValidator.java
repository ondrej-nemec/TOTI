package module.controllers.api;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import toti.validation.ItemRules;
import toti.validation.Validator;

public class EntityValidator {
	
	public static final String NAME_FORM = "entityFormValidator";
	public static final String NAME_GRID = "entityGridValidator";

	public static void init() {
		getGridValidator();
		getFormValidator();
		test();
	}
	
	public static Validator test() {
		return Validator.create("test", false)
				.addRule(ItemRules.forName("html-list", true).setListSpecification(new Validator(
					ItemRules.defaultRule().setType(Integer.class)
				)))
				.addRule(ItemRules.forName("html-map", true).setMapSpecification(new Validator(true)
					.addRule(ItemRules.forName("a", true).setType(Integer.class))
					.addRule(ItemRules.forName("b", true).setType(Integer.class))
					.addRule(ItemRules.forName("c", true).setType(Integer.class))
				))
				.addRule(ItemRules.forName("html-map2", true).setMapSpecification(new Validator(true)
					.addRule(ItemRules.forName("a", true).setMapSpecification(new Validator(true)
						.addRule(ItemRules.forName("aa", true).setType(Integer.class))
						.addRule(ItemRules.forName("ab", true).setType(Integer.class))
					))
				))
				.addRule(ItemRules.forName("html-map-list", true).setMapSpecification(new Validator(true)
					.addRule(ItemRules.forName("a", true).setListSpecification(new Validator(
						ItemRules.defaultRule().setType(Integer.class)
					)))
				))
			/*	
			.addRule(ItemRules.forName("number-as-text", true).setType(Integer.class, false))
			.addRule(ItemRules.forName("number-as-number", true).setType(Integer.class))
			
			.addRule(ItemRules.forName("text-value", true).setType(String.class).setChangeValue((val)->{
				return val.toString().length() > 5;
			}))
			//*/
			/*
			// working
			.addRule(ItemRules.forName("json-map", true).setType(Map.class))
			.addRule(ItemRules.forName("json-map-2", true).setMapSpecification(new Validator(true)
				.addRule(ItemRules.forName("key1", true).setType(String.class))
				.addRule(ItemRules.forName("key2", true))
			))
			.addRule(ItemRules.forName("json-list", true).setType(List.class))
			//*/
			
			/*
			// working
			.addRule(ItemRules.forName("html-map", true).setMapSpecification(new Validator(true)
				.addRule(ItemRules.forName("a", true).setType(Integer.class))
				.addRule(ItemRules.forName("b", true).setChangeValue((v)->{
					return v.toString().charAt(0);
				}))
				.addRule(ItemRules.forName("c", true))
			))
			//*/
			/*
			.addRule(ItemRules.forName("html-list", true).setListSpecification(new Validator(
					ItemRules.forName(null, false).setType(Integer.class)
				)
				.addRule(ItemRules.forName("2", true).setChangeValue((v)->{
					return v + " " + v;
				}))
			))
			//*/
			// html-list - simple list
			// html-map - simple map
			// html-map2 - complicated map
			// html-map-list - list in map
			
			/*
			.addRule(ItemRules.forName("intAsString", false).setType(Integer.class))
			.addRule(ItemRules.forName("intAsInt", false).setType(Integer.class, true))
			.addRule(ItemRules.forName("aa", true).setType(List.class))
			.addRule(ItemRules.forName("b", true).setType(Map.class).setMapSpecification(
					new Validator(false)
					 .addRule(ItemRules.forName("a", true))
					 .addRule(ItemRules.forName("b", true))
					 .addRule(ItemRules.forName("c", true))
			))
			.addRule(ItemRules.forName("c", true).setType(Map.class))*/;
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
