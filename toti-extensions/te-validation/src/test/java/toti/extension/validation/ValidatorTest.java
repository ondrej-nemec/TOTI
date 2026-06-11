package toti.extension.validation;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import toti.extension.validation.collections.RulesCollection;
import toti.extension.validation.results.CheckResult;
import toti.extension.validation.results.ValidationCollection;
import toti.extension.validation.results.ValidationItem;
import toti.extension.validation.rules.CustomValidationRule;
import toti.lib.common.structures.ListInit;
import toti.lib.common.structures.MapInit;
import toti.lib.tcpip.structures.RequestParameters;

public class ValidatorTest {

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
					new ListInit<>().add("toti.validation.not-expected-parameters").toSet(),
					new RequestParameters().put("key1", "value1")
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
					new ListInit<>().add("toti.validation.not-expected-parameters").toSet(),
					new RequestParameters()
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
