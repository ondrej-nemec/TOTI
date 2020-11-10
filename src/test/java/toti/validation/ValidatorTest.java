package toti.validation;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;

import common.MapInit;
import common.structures.Tuple2;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.validation.ItemRules;
import toti.validation.Validator;

@RunWith(JUnitParamsRunner.class)
public class ValidatorTest {

	@Test
	@Parameters(method = "dataValidateAllowedMapWorks")
	public void testValidateAllowedMapWorks(List<ItemRules> rules, Properties prop, boolean expected) {
		Validator val = new Validator(true);
		rules.forEach((rule)->{
			val.addRule(rule);
		});
		assertEquals(expected, val.validate(prop).isEmpty());
	}
	
	public Object[] dataValidateAllowedMapWorks() {
		Map<String, Object> correct = new HashMap<>();
		correct.put("val1", "text");
		correct.put("val2", 123);
		correct.put("val3", true);
		
		return new Object[] {
			new Object[] {
				Arrays.asList(ItemRules.forName("item1", true).setMapSpecification(
					new Validator(true)
						.addRule(ItemRules.forName("val1", true).setType(String.class))
						.addRule(ItemRules.forName("val2", true).setType(Integer.class))
						.addRule(ItemRules.forName("val3", true).setType(Boolean.class))
				)),
				MapInit.properties(new Tuple2<>("item1", correct)),
				true
			},
			new Object[] {
					Arrays.asList(ItemRules.forName("item1", true).setMapSpecification(
						new Validator(true)
							.addRule(ItemRules.forName("val1", true).setType(String.class))
							.addRule(ItemRules.forName("val2", true).setType(Integer.class))
							.addRule(ItemRules.forName("val3", true).setType(Boolean.class))
							.addRule(ItemRules.forName("val4", false))
					)),
					MapInit.properties(new Tuple2<>("item1", correct)),
					true
				},
			new Object[] {
					Arrays.asList(ItemRules.forName("item1", true).setMapSpecification(
						new Validator(false)
							.addRule(ItemRules.forName("val1", true).setType(String.class))
							.addRule(ItemRules.forName("val2", true).setType(Integer.class))
					)),
					MapInit.properties(new Tuple2<>("item1", correct)),
					true
				},
			new Object[] {
					Arrays.asList(ItemRules.forName("item1", true).setMapSpecification(
						new Validator(true)
							.addRule(ItemRules.forName("val1", true).setType(String.class))
							.addRule(ItemRules.forName("val2", true).setType(Integer.class))
							.addRule(ItemRules.forName("val3", true).setType(Boolean.class))
							.addRule(ItemRules.forName("val4", true).setType(Integer.class))
					)),
					MapInit.properties(new Tuple2<>("item1", correct)),
					false
				},
			new Object[] {
					Arrays.asList(ItemRules.forName("item1", true).setMapSpecification(
						new Validator(true)
							.addRule(ItemRules.forName("val1", true).setType(Integer.class))
							.addRule(ItemRules.forName("val2", true).setType(Integer.class))
							.addRule(ItemRules.forName("val3", true).setType(Boolean.class))
					)),
					MapInit.properties(new Tuple2<>("item1", correct)),
					false
				},
		};
	}
	
	@Test
	@Parameters(method = "dataValidateAllowedListWorks")
	public void testValidateAllowedListWorks(List<ItemRules> rules, Properties prop, boolean expected) {
		Validator val = new Validator(true);
		rules.forEach((rule)->{
			val.addRule(rule);
		});
		assertEquals(expected, val.validate(prop).isEmpty());
	}
	
	public Object[] dataValidateAllowedListWorks() {
		return new Object[] {
			new Object[] {
				Arrays.asList(ItemRules.forName("item1", true).setAllowedValues(Arrays.asList("value1", "value2"))),
				MapInit.properties(MapInit.t("item1", "value1")),
				true
			},
			new Object[] {
					Arrays.asList(ItemRules.forName("item1", true).setAllowedValues(Arrays.asList("value1", "value2"))),
					MapInit.properties(MapInit.t("item1", "value")),
					false
				},
		};
	}
	
	@Test
	@Parameters(method = "dataValidateTypeWorks")
	public void testValidateTypeWorks(List<ItemRules> rules, Properties prop, boolean expected) {
		Validator val = new Validator(true);
		rules.forEach((rule)->{
			val.addRule(rule);
		});
		assertEquals(expected, val.validate(prop).isEmpty());
	}
	
	public Object[] dataValidateTypeWorks() {
		return new Object[] {
			new Object[] {
				Arrays.asList(ItemRules.forName("item1", true).setType(String.class)),
				MapInit.properties(MapInit.t("item1", "value")),
				true
			},
			new Object[] {
					Arrays.asList(ItemRules.forName("item1", true).setType(Integer.class)),
					MapInit.properties(MapInit.t("item1", 12)),
					true
				},
			/*new Object[] {
					Arrays.asList(Rules.forName("item1", true).setType(Number.class)),
					MapInit.properties(MapInit.t("item1", 12)),
					true
				},
			new Object[] {
					Arrays.asList(Rules.forName("item1", true).setType(Number.class)),
					MapInit.properties(MapInit.t("item1", 12.4)),
					true
				},*/
			new Object[] {
					Arrays.asList(ItemRules.forName("item1", true).setType(Integer.class)),
					MapInit.properties(MapInit.t("item1", 12.4)),
					false
				},
			new Object[] {
					Arrays.asList(ItemRules.forName("item1", true).setType(Double.class)),
					MapInit.properties(MapInit.t("item1", 12.4)),
					true
				},
			new Object[] {
					Arrays.asList(ItemRules.forName("item1", true).setType(String.class)),
					MapInit.properties(MapInit.t("item1", 12.4)),
					true
				},
			new Object[] {
					Arrays.asList(ItemRules.forName("item1", true).setType(Boolean.class)),
					MapInit.properties(MapInit.t("item1", "value")),
					true
				},
			new Object[] {
					Arrays.asList(ItemRules.forName("item1", true).setType(Boolean.class)),
					MapInit.properties(MapInit.t("item1", "true")),
					true
				},
		};
	}
	
	@Test
	@Parameters(method = "dataValidateNumberWorks")
	public void testValidateNumberWorks(List<ItemRules> rules, Properties prop, boolean expected) {
		Validator val = new Validator(true);
		rules.forEach((rule)->{
			val.addRule(rule);
		});
		assertEquals(expected, val.validate(prop).isEmpty());
	}
	
	public Object[] dataValidateNumberWorks() {
		return new Object[] {
			new Object[] {
				Arrays.asList(ItemRules.forName("item1", true).setMinValue(0).setMaxValue(10)),
				MapInit.properties(MapInit.t("item1", 0)),
				true	
			},
			new Object[] {
					Arrays.asList(ItemRules.forName("item1", true).setMinValue(0).setMaxValue(10)),
					MapInit.properties(MapInit.t("item1", -1)),
					false	
				},
			new Object[] {
					Arrays.asList(ItemRules.forName("item1", true).setMinValue(0).setMaxValue(10)),
					MapInit.properties(MapInit.t("item1", 10)),
					true	
				},
			new Object[] {
					Arrays.asList(ItemRules.forName("item1", true).setMinValue(0).setMaxValue(10)),
					MapInit.properties(MapInit.t("item1", 5)),
					true	
				},
			new Object[] {
					Arrays.asList(ItemRules.forName("item1", true).setMinValue(0).setMaxValue(10)),
					MapInit.properties(MapInit.t("item1", 12)),
					false	
				}
		};
	}
	
	@Test
	@Parameters(method = "dataValidateTextWorks")
	public void testValidateTextWorks(List<ItemRules> rules, Properties prop, boolean expected) {
		Validator val = new Validator(true);
		rules.forEach((rule)->{
			val.addRule(rule);
		});
		assertEquals(expected, val.validate(prop).isEmpty());
	}
	
	public Object[] dataValidateTextWorks() {
		return new Object[] {
			new Object[] {
				Arrays.asList(ItemRules.forName("item1", true).setMaxLength(10).setMinLength(2).setRegex(".*")),
				MapInit.properties(MapInit.t("item1", "correct")),
				true
			},
			new Object[] {
					Arrays.asList(ItemRules.forName("item1", true).setMaxLength(10).setMinLength(2).setRegex(".*")),
					MapInit.properties(MapInit.t("item1", "a")),
					false
				},
			new Object[] {
					Arrays.asList(ItemRules.forName("item1", true).setMaxLength(10).setMinLength(2).setRegex(".*")),
					MapInit.properties(MapInit.t("item1", "soooooo long")),
					false
				},
			new Object[] {
					Arrays.asList(ItemRules.forName("item1", true).setRegex("^[0-9]$")),
					MapInit.properties(MapInit.t("item1", "not a number")),
					false
				}
		};
	}

	@Test
	@Parameters(method = "dataValidateWithStrickWorks")
	public void testValidateWithStrickWorks(boolean strict, List<ItemRules> rules, Properties prop, boolean expected) {
		Validator val = new Validator(strict);
		rules.forEach((rule)->{
			val.addRule(rule);
		});
		assertEquals(expected, val.validate(prop).isEmpty());
	}
	
	public Object[] dataValidateWithStrickWorks() {
		return new Object[] {
			new Object[] {
				true, Arrays.asList(
						ItemRules.forName("item1", true),
						ItemRules.forName("item2", true)
				), MapInit.properties(
					MapInit.t("item1", ""),
					MapInit.t("item2", "")
				), true
			},
			new Object[] {
					true, Arrays.asList(
							ItemRules.forName("item1", true),
							ItemRules.forName("item2", true),
							ItemRules.forName("item3", false)
					), MapInit.properties(
						MapInit.t("item1", ""),
						MapInit.t("item2", "")
					), true
				},
			new Object[] {
					true, Arrays.asList(
							ItemRules.forName("item1", true),
							ItemRules.forName("item2", true),
							ItemRules.forName("item3", true)
					), MapInit.properties(
						MapInit.t("item1", ""),
						MapInit.t("item2", "")
					), false
				},
			new Object[] {
					true, Arrays.asList(
							ItemRules.forName("item1", true)
					), MapInit.properties(
						MapInit.t("item1", ""),
						MapInit.t("item2", "")
					), false
				},
			new Object[] {
					true, Arrays.asList(
							ItemRules.forName("item1", true),
							ItemRules.forName("item2", true),
							ItemRules.forName("item3", false)
					), MapInit.properties(
						MapInit.t("item1", ""),
						MapInit.t("item3", "")
					), false
				},
			new Object[] {
					true, Arrays.asList(
							ItemRules.forName("item1", true),
							ItemRules.forName("item2", true),
							ItemRules.forName("item3", false)
					), MapInit.properties(
						MapInit.t("item1", ""),
						MapInit.t("item2", ""),
						MapInit.t("item3", "")
					), true
				},
			new Object[] {
					true, Arrays.asList(
							ItemRules.forName("item1", true),
							ItemRules.forName("item2", true)
					), MapInit.properties(
						MapInit.t("item1", ""),
						MapInit.t("item2", ""),
						MapInit.t("item3", "")
					), false
				},
			new Object[] {
					false, Arrays.asList(
							ItemRules.forName("item1", true),
							ItemRules.forName("item2", true)
					), MapInit.properties(
						MapInit.t("item1", ""),
						MapInit.t("item2", ""),
						MapInit.t("item3", "")
					), true
				},
			new Object[] {
					false, Arrays.asList(
							ItemRules.forName("item1", true),
							ItemRules.forName("item2", true),
							ItemRules.forName("item3", false)
					), MapInit.properties(
						MapInit.t("item1", ""),
						MapInit.t("item3", "")
					), false
				}
		};
	}
	
}
