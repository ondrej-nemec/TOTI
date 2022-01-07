package example.web.validator;

import java.util.Arrays;

import toti.validation.ItemRules;
import toti.validation.Validator;

public class ExampleValidator {

	public static final String NAME_FORM = "exampleFormValidator";
	public static final String NAME_GRID = "exampleGridValidator";
	public static final String TEST = "exampleTes";
	
	public static void init() {
		getGridValidator();
		getFormValidator();
		getTestValidator();
	}

	public static Validator getGridValidator() {
		return new Validator(true)
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
		return new Validator(false) // TODO validator
				/*.addRule(ItemRules.forName("id", false).setType(Integer.class))
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
				.addRule(ItemRules.forName("favorite_color", false).setType(String.class))*/
				.addRule(ItemRules.forName("file", false))
				//.addRule(ItemRules.forName("comment", false).setType(String.class))
				.addRule(ItemRules.forName("map", false).setMapSpecification(new Validator(false)
					.addRule(ItemRules.forName("subText1", false).setType(String.class).setMaxLength(10))
					.addRule(ItemRules.forName("subText2", false).setType(String.class).setMaxLength(10))
				))
				.addRule(ItemRules.forName("list", false).setListSpecification(
					new Validator(ItemRules.defaultRule().setType(String.class).setMaxLength(10))
				))
				.addRule(ItemRules.forName("pairs", false).setMapSpecification(
					new Validator(ItemRules.defaultRule().setMapSpecification(new Validator(false)
						.addRule(ItemRules.forName("first-in-pair", true).setMaxLength(5))
						.addRule(ItemRules.forName("second-in-pair", true).setMaxLength(5))
					))
				))
				//.addRule(ItemRules.forName("aaaaaaaa", true))
				/*.setGlobalFunction((prop, t)->{
					System.err.println("Working!!!");
					Set<String> set = new HashSet<>();
					set.add("Working :-)");
					return set;
				})*/
				;
	}
	
	private static void getTestValidator() {
		new Validator(false)
			.addRule(ItemRules.forName("list", false)
				.setListSpecification(new Validator(ItemRules.defaultRule().setType(String.class)
					.setAllowedValues(Arrays.asList("A1", "A2", "A3"))
				))
			)
			.addRule(ItemRules.forName("map", false)
				.setMapSpecification(new Validator(ItemRules.defaultRule().setAllowedValues(Arrays.asList("B1", "B2", "B3")))
					/*.addRule(ItemRules.forName("a", false).setAllowedValues(Arrays.asList("B1")))
					.addRule(ItemRules.forName("b", false).setAllowedValues(Arrays.asList("B2")))
					.addRule(ItemRules.forName("c", false).setAllowedValues(Arrays.asList("B3")))*/
				)
			)
			.addRule(ItemRules.forName("maplist", false)
				.setMapSpecification(
					new Validator(false)
						.addRule(ItemRules.forName("a", false).setListSpecification(
							new Validator(ItemRules.defaultRule().setMaxLength(3))
						))
						.addRule(ItemRules.forName("b", false).setListSpecification(
							new Validator(ItemRules.defaultRule().setMaxLength(3))
						))
						.addRule(ItemRules.forName("c", false).setListSpecification(
							new Validator(ItemRules.defaultRule().setMaxLength(3))
						))
				)
			)
			/*.addRule(ItemRules.forName("listmap", false).setChangeValue((value)->{
				System.err.println(value + " " + value.getClass());
				return value;
			}))*/
			.addRule(ItemRules.forName("dynamic", false).setChangeValue((value)->{
				System.err.println(value + " " + (value == null ? "" : value.getClass()));
				return value;
			}))
			;
		
	}
}
