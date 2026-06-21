package toti.extension.validation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import toti.application.extensions.Translator;
import toti.extension.validation.collections.RulesCollection;
import toti.extension.validation.results.CheckResult;
import toti.extension.validation.results.ValidationCollection;
import toti.extension.validation.results.ValidationItem;
import toti.extension.validation.rules.CustomValidationRule;
import toti.lib.common.structures.ListInit;
import toti.lib.common.structures.MapInit;
import toti.lib.common.tests.TestCase;
import toti.lib.tcpip.structures.RequestParameters;
import toti.lib.tcpip.structures.UploadedFile;

public class ValidatorTest {

	@Test
	public void fullTest() {
		RequestParameters dataToValidate = new RequestParameters();
		RequestParameters validatedData = new RequestParameters();
		Map<String, Object> expectedErrors = new HashMap<>();

		Validator validator = Validator.create(true, new Translator() {
			@Override public String translate(String key) {
				return "E: " + key;
			}
			@Override public String translate(String key, Map<String, Object> params) {
				return "E: " + key + " " + params;
			}
		});

		for (int i = 0; i < 3; i++) {
			var index = i;
			validator.addRule(r->r.booleanRules("bool_" + index, true));
		}
		{
			dataToValidate.put("bool_0", null);
			expectedErrors.put("bool_0", new ListInit<>()
				.add("E: toti.validation.item-required {parameter=bool_0}")
			.toSet());
			validatedData.put("bool_0", null);
		}
		{
			dataToValidate.put("bool_1", "");
			validatedData.put("bool_1", false);
		}
		{
			dataToValidate.put("bool_2", "0");
			validatedData.put("bool_2", false);
		}

		for (int i = 0; i < 3; i++) {
			int index = i;
			validator.addRule(
				r->r.numberRules("number_" + index, true, Integer.class)
				.setAllowedValues(Arrays.asList(111, 222))
				.setMaxLength(2).setMinLength(8)
				.setMaxValue(5).setMinValue(999)
				.setRegex("[a-c]")
			);
		}
		validator.addRule(r->r.numberRules("number_3", false, Double.class));
		validator.addRule(r->r.numberRules("validNumber", true, Integer.class));
		{
			dataToValidate.put("number_0", "abcd");
			expectedErrors.put("number_0", new ListInit<>()
				.add("E: toti.validation.value-type-must-be {class=java.lang.Integer}")
			.toSet());
			validatedData.put("number_0", "abcd");
		}
		{
			dataToValidate.put("number_1", "12");
			expectedErrors.put("number_1", new ListInit<>()
				.add("E: toti.validation.value-must-be-one-of {values=[111, 222]}")
			.toSet());
			validatedData.put("number_1", 12);
		}
		{
			dataToValidate.put("number_2", "111");
			expectedErrors.put("number_2", new ListInit<>()
				.add("E: toti.validation.value-must-be-less-or-equals {maxValue=5}")
				.add("E: toti.validation.length-must-be-at-least {minLength=8}")
				.add("E: toti.validation.text-not-match-pattern {regex=[a-c]}")
				.add("E: toti.validation.value-must-be-equals-or-higher {minValue=999}")
				.add("E: toti.validation.length-must-be-max {maxLength=2}")
			.toSet());
			validatedData.put("number_2", 111);
		}
		{
			dataToValidate.put("number_3", "abc");
			expectedErrors.put("number_3", new ListInit<>()
				.add("E: toti.validation.value-type-must-be {class=java.lang.Double}")
			.toSet());
			validatedData.put("number_3", "abc");
		}
		{
			dataToValidate.put("validNumber", "12.3");
			validatedData.put("validNumber", 12);
		}

		for (int i = 0; i < 3; i++) {
			int index = i;
			validator.addRule(
				r->r.objectRules("string_" + index, true)
				.setAllowedValues(Arrays.asList("aa", "bb", "dddd"))
				.setType(String.class)
				.setMaxLength(2).setMinLength(8)
				.setRegex("[a-c]")
			);
		}
		validator.addRule(r->r.objectRules("validString", true));
		{
			dataToValidate.put("string_0", "ccccc");
			expectedErrors.put("string_0", new ListInit<>()
				.add("E: toti.validation.value-must-be-one-of {values=[aa, bb, dddd]}")
			.toSet());
			validatedData.put("string_0", "ccccc");
		}
		{
			dataToValidate.put("string_1", "dddd");
			expectedErrors.put("string_1", new ListInit<>()
				.add("E: toti.validation.length-must-be-at-least {minLength=8}")
				.add("E: toti.validation.text-not-match-pattern {regex=[a-c]}")
				.add("E: toti.validation.length-must-be-max {maxLength=2}")
			.toSet());
			validatedData.put("string_1", "dddd");
		}
		{
			expectedErrors.put("string_2", new ListInit<>()
				.add("E: toti.validation.item-required {parameter=string_2}")
			.toSet());
			validatedData.put("string_2", null);
		}
		{
			dataToValidate.put("validString", "something");
			validatedData.put("validString", "something");
		}

		validator.addRule(
			r->r.fileRules("file", true)
			.setAllowedFileTypes(Arrays.asList("txt", "md"))
			.setFileMaxSize(10).setFileMinSize(20)
		);
		validator.addRule(r->r.fileRules("validFile", true));

		{
			dataToValidate.put("file", new UploadedFile("fileName", "type", "bom", new byte[15]));
			expectedErrors.put("file", new ListInit<>()
				.add("E: toti.validation.file-size-can-be-max {fileMaxSize=10}")
				.add("E: toti.validation.file-type-is-not-allowed {allowedFileTypes=[txt, md]}")
				.add("E: toti.validation.file-size-must-be-at-least {fileMinSize=20}")
			.toSet());
			validatedData.put("file", new UploadedFile("fileName", "type", "bom", new byte[15]));
		}
		{
			dataToValidate.put("validFile", new UploadedFile("fileName", "type", "bom", new byte[15]));
			validatedData.put("validFile", new UploadedFile("fileName", "type", "bom", new byte[15]));
		}

		for (int i = 0; i < 2; i++) {
			int index = i;
			validator.addRule(
				r->r.listRules("list_" + index, true, v->v.create(
					s->s.numberRules(Double.class)
				)).setMaxLength(2).setMinLength(5)
			);
		}
		validator.addRule(r->r.listRules("validList", true, v->v.create(s->s.numberRules(Double.class))));

		{
			dataToValidate.put("list_0", "aaaaa");
			expectedErrors.put("list_0", new ListInit<>()
				.add("E: toti.validation.parameter-cannot-be-converted")
			.toSet());
			validatedData.put("list_0", "aaaaa");
		}
		{
			dataToValidate.put("list_1", Arrays.asList("a", "1", 2, "d"));
			expectedErrors.put("list_1", new ListInit<>()
				.add("E: toti.validation.length-must-be-at-least {minLength=5}")
				.add(
					MapInit.create()
					.append("0", new ListInit<>()
						.add("E: toti.validation.value-type-must-be {class=java.lang.Double}")
					.toSet())
					.append("3", new ListInit<>()
						.add("E: toti.validation.value-type-must-be {class=java.lang.Double}")
					.toSet())
					.toMap()
				)
				.add("E: toti.validation.length-must-be-max {maxLength=2}")
			.toSet());
			validatedData.put("list_1", Arrays.asList("a", 1.0, 2.0, "d"));
		}
		{
			dataToValidate.put("validList", Arrays.asList("12.3", "45"));
			validatedData.put("validList", Arrays.asList(12.3, 45.0));
		}
		
		for (int i = 0; i < 2; i++) {
			int index = i;
			validator.addRule(
				r->r.mapRules("map_" + index, true, v->v.create(true)
					.addRule(s->s.numberRules("a", true, Integer.class))
					.addRule(s->s.numberRules("b", false, Integer.class).setMaxValue(10))
				)
				.setMaxLength(2)
				.setMinLength(10)
			);
		}
		validator.addRule(r->r.mapRules("validMap", true, v->v.create(false)
			.addRule(s->s.numberRules("a", true, Integer.class))
			.addRule(s->s.numberRules("b", true, Double.class))
		));
		{
			dataToValidate.put("map_0", "aaaaa");
			expectedErrors.put("map_0", new ListInit<>()
				.add("E: toti.validation.parameter-cannot-be-converted")
			.toSet());
			validatedData.put("map_0", "aaaaa");
		}
		{
			dataToValidate.put("map_1", new RequestParameters()
				.put("a", 12.4)
				.put("b", 11)
				.put("c", 123)
			);
			expectedErrors.put("map_1", new ListInit<>()
				.add(
					MapInit.create()
					.append("", new ListInit<>()
						.add("E: toti.validation.not-expected-parameters {parameters=[map_1[c]]}")
					.toSet())
					.append("b", new ListInit<>()
						.add("E: toti.validation.value-must-be-less-or-equals {maxValue=10}")
					.toSet())
					.toMap()
				)
				.add("E: toti.validation.length-must-be-max {maxLength=2}")
				.add("E: toti.validation.length-must-be-at-least {minLength=10}")
			.toSet());
			validatedData.put("map_1", new RequestParameters().put("a", 12).put("b", 11).put("c", 123));
		}
		{
			dataToValidate.put("validMap", new RequestParameters().put("a", "11.1").put("b", "22"));
			validatedData.put("validMap", new RequestParameters().put("a", 11).put("b", 22.0));
		}

		validator.addRule(
			r->r.numberRules("custom_0", true, Integer.class)
			.setCustomValidation(item->{
				item.addError("Error single");
				item.addError("Raw " + item.getRawValue());
				item.addError("Parsed " + item.getParsedValue());
				item.setValue("Something");
			})
		);
		{
			dataToValidate.put("custom_0", 12.3);
			expectedErrors.put("custom_0", new ListInit<>()
				.add("Parsed 12")
				.add("Raw 12.3")
				.add("Error single")
			.toSet());
			validatedData.put("custom_0", "Something");
		}

		validator.addRule(
			r->r.mapRules("custom_1", true, v->v.create(false)
				.addRule(s->s.numberRules("number", true, Integer.class))
				.setCustomValidation(item->{
					item.addError("Error map");
					item.addError("Names " + item.getItemsNames());
					item.addError("Parsed " + item.getValue("number"));
					item.setItem("boolean", true);
					item.removeItem("a");
				})
			)
		);
		{
			dataToValidate.put("custom_1", MapInit.create().append("number", 12.3).append("a", "A").append("x", "X").toMap());
			expectedErrors.put("custom_1", new ListInit<>()
				.add(
					MapInit.create()
					.append("", new ListInit<>()
						.add("Parsed 12")
						.add("Names [number, a, x]")
						.add("Error map")
					.toSet())
					.toMap()
				)
			.toSet());
			
			validatedData.put(
				"custom_1",
				new RequestParameters().put("number", 12).put("x", "X").put("boolean", true)
			);
		}

		validator.addRule(
			r->r.mapRules("upper", true, v->v.create(true)
				.addRule(
					s->s.listRules("subList1", true, v1->v1.create(t->t.numberRules(Integer.class)))
				)
				.addRule(
					s->s.listRules("subList2", true, v1->v1.create(
						t->t.numberRules(Integer.class)
						.setMaxValue(5).setMinValue(20)
					))
					.setMaxLength(2).setMinLength(10)
				)
				.addRule(
					s->s.mapRules("subMap", true, v1->v1.create(true)
						.addRule(t->t.numberRules("a", false, Double.class))
						.addRule(t->t.numberRules("b", true, Double.class))
					)
				)
			)
		);
		dataToValidate.put(
			"upper",
			new RequestParameters()
			.put("subList1", Arrays.asList())
			.put("subList2", Arrays.asList(12, "12.3", "false", "x"))
			.put("subMap", new RequestParameters().put("a", "x").put("c", "C"))
		);
		expectedErrors.put("upper", new ListInit<>()
			.add(MapInit.create()
			.append("subList2", new ListInit<>()
				.add(MapInit.create()
				.append("0", new ListInit<>()
					.add("E: toti.validation.value-must-be-less-or-equals {maxValue=5}")
					.add("E: toti.validation.value-must-be-equals-or-higher {minValue=20}")
				.toSet())
				.append("1", new ListInit<>()
					.add("E: toti.validation.value-must-be-less-or-equals {maxValue=5}")
					.add("E: toti.validation.value-must-be-equals-or-higher {minValue=20}")
				.toSet())
				.append("2", new ListInit<>()
					.add("E: toti.validation.value-type-must-be {class=java.lang.Integer}")
				.toSet())
				.append("3", new ListInit<>()
					.add("E: toti.validation.value-type-must-be {class=java.lang.Integer}")
				.toSet())
				.toMap())
				.add("E: toti.validation.length-must-be-max {maxLength=2}")
				.add("E: toti.validation.length-must-be-at-least {minLength=10}")
			.toSet())
			.append("subMap", new ListInit<>()
				.add(MapInit.create()
				.append("a", new ListInit<>()
					.add("E: toti.validation.value-type-must-be {class=java.lang.Double}")
				.toSet())
				.append("b", new ListInit<>()
					.add("E: toti.validation.item-required {parameter=b}")
				.toSet())
				.append("", new ListInit<>()
					.add("E: toti.validation.not-expected-parameters {parameters=[upper[subMap][c]]}")
				.toSet())
				.toMap())
			.toSet())
			.toMap())
		.toSet());
		validatedData.put("upper", new RequestParameters()
			.put("subList1", Arrays.asList())
			.put("subList2", Arrays.asList(12, 12, "false", "x"))
			.put("subMap", new RequestParameters().put("a", "x").put("b", null).put("c", "C"))
		);

		var result = validator.validate(dataToValidate);
		assertFalse(result.isValid());
		TestCase.assertEquals(expectedErrors, result.getErrors());
		TestCase.assertEquals(validatedData, result.getValues());
	}

	@ParameterizedTest
	@MethodSource
	public void testValidate(String message, Validator validator, ValidationResult expected) {
		RequestParameters prop = new RequestParameters();
		prop.put("key1", "value1");
		prop.put("key2", "value2");
		ValidationResult actual = validator.validate(prop);
		assertEquals(expected, actual);
	}

	public static Object[] testValidate() {
		return new Object[] {
			new Object[] {
				"strict = false | missing rule",
				Validator.create(false)
				.addRule(r->r.objectRules("key1", true)),
				new ValidationCollection(
					MapInit.create().toMap(),
					new ListInit<>().toSet(),
					new RequestParameters().put("key1", "value1").put("key2", "value2")
				)
			},
			new Object[] {
				"strict = false | existing rules",
				Validator.create(false)
				.addRule(r->r.objectRules("key1", true))
				.addRule(r->r.objectRules("key2", true)),
				new ValidationCollection(
					MapInit.create().toMap(),
					new ListInit<>().toSet(),
					new RequestParameters().put("key1", "value1").put("key2", "value2")
				)
			},
			new Object[] {
				"strict = true | missing rule",
				Validator.create(true)
				.addRule(r->r.objectRules("key1", true)),
				new ValidationCollection(
					MapInit.create().toMap(),
					new ListInit<>().add("toti.validation.not-expected-parameters {parameters=[key2]}").toSet(),
					new RequestParameters().put("key1", "value1").put("key2", "value2")
				)
			},
			new Object[] {
				"strict = true | existing rules",
				Validator.create(true)
				.addRule(r->r.objectRules("key1", true))
				.addRule(r->r.objectRules("key2", true)),
				new ValidationCollection(
					MapInit.create().toMap(),
					new ListInit<>().toSet(),
					new RequestParameters().put("key1", "value1").put("key2", "value2")
				)
			},
			new Object[] {
				"default rule rules",
				Validator.create(r->r.objectRules()),
				new ValidationCollection(
					MapInit.create().toMap(),
					new ListInit<>().toSet(),
					new RequestParameters().put("key1", "value1").put("key2", "value2")
				)
			},
			new Object[] {
				"Custom validation: invalid",
				Validator.create(true)
				.setCustomValidation((item)->{
					item.addError("Custom " + item.getItemsNames());
				}),
				new ValidationCollection(
					MapInit.create().toMap(),
					new ListInit<>().add("toti.validation.not-expected-parameters {parameters=[key1, key2]}").toSet(),
					new RequestParameters().put("key1", "value1").put("key2", "value2")
				)
			},
			new Object[] {
				"Custom validation: valid",
				Validator.create(false)
				.setCustomValidation((item)->{
					item.addError("Custom " + item.getItemsNames());
				}),
				new ValidationCollection(
					MapInit.create().toMap(),
					new ListInit<>().add("Custom [key1, key2]").toSet(),
					new RequestParameters().put("key1", "value1").put("key2", "value2")
				)
			},
		};
	}

	@ParameterizedTest
	@MethodSource
	public void testIterateRules(
		String message,
		boolean isMoreValidationPossible, CustomValidationRule custom,
		Set<String> expectedErrors
	) {
		RulesCollection collection = mock(RulesCollection.class);
		when(collection.getRules()).thenReturn(Arrays.asList(
			item->{
				item.addError("Error1");
				return new CheckResult(true);
			},
			item->{
				item.addError("Error2");
				return new CheckResult(isMoreValidationPossible);
			},
			item->{
				item.addError("Error3");
				return new CheckResult(true);
			}
		));
		when(collection.getCustomValidation()).thenReturn(Optional.ofNullable(custom));

		ValidationItem expected = new ValidationItem(
			"originPropertyName", "format(originPropertyName)", "something"
		);
		expected.getErrors().addAll(expectedErrors);

		ValidationItem actual = Validator.create(false)
			.iterateRules("format(%s)", "originPropertyName", collection, "something");
		assertEquals(expected, actual);
	}

	public static Object[] testIterateRules() {
		return new Object[] {
			new Object[] {
				"Posible continue | no custom",
				true, null, new ListInit<>("Error1").add("Error2").add("Error3").toSet()
			},
			new Object[] {
				"Not posible continue | no custom",
				false, null, new ListInit<>("Error1").add("Error2").toSet()
			},
			new Object[] {
				"Posible continue | with custom",
				true, new CustomValidationRule(item->{ item.addError("Error4"); }),
				new ListInit<>("Error1").add("Error2").add("Error3").add("Error4").toSet()
			},
			new Object[] {
				"Not posible continue | no custom",
				false, new CustomValidationRule(item->{ item.addError("Error4"); }),
				new ListInit<>("Error1").add("Error2").toSet()
			}
		};
	}

}
