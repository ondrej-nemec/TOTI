package toti.ui.validation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.Test;
import org.junit.runner.RunWith;

import ji.socketCommunication.http.structures.RequestParameters;
import ji.translator.Locale;
import ji.translator.Translator;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.answers.request.Request;
import toti.ui.validation.collections.RulesCollection;
import toti.ui.validation.rules.Rule;

@RunWith(JUnitParamsRunner.class)
public class ValidatorTest {
	/*
	@Test
	@Parameters({"true", "false"})
	public void testValidateWithNullParam(boolean strict) {
		Translator translator = new Translator() {
			@Override
			public String translate(String key, Map<String, Object> variables, String locale) {
				return key + ":" + variables;
			}
			@Override public Translator withLocale(Locale locale) { return null; }
			@Override public void setLocale(Locale locale) {}
			@Override public Set<String> getSupportedLocales() { return null; }
			@Override public Locale getLocale(String locale) { return new Locale("", true, Arrays.asList()); }
			@Override public Locale getLocale() { return new Locale("", true, Arrays.asList()); }
		};
		Request request = mock(Request.class);
		
		RequestParameters parameters = new RequestParameters();
		parameters.put("a", "1");
		parameters.put("b", null);
		
		Validator validator = new Validator(strict);
		validator.addRule(ItemRules.objectRules("a", true));
		validator.addRule(ItemRules.objectRules("b", strict));
		
		ValidationResult actualResult = validator.validate(request, parameters, translator);

		RequestParameters expectedParameters = new RequestParameters();
		expectedParameters.put("a", "1");
		expectedParameters.put("b", null);
		ValidationResult expectedResult = new ValidationResult();
		
		
		System.out.println(actualResult);
		System.out.println(parameters);
		assertEquals(expectedResult.toString(), actualResult.toString());
		assertEquals(expectedParameters.toString(), parameters.toString());
		
		assertEquals(expectedResult, actualResult);
		assertEquals(expectedParameters, parameters);
	}
	*/
	@Test
	@Parameters(method="dataValidate")
	public void testValidate(
			String message,
			Boolean strict,
			GlobalFunction globalFunction,
			List<RulesCollection> rules,
			ValidationResult expectedResult,
			RequestParameters expectedParameters) {
		Translator translator = new Translator() {
			@Override
			public String translate(String key, Map<String, Object> variables, String locale) {
				return key + ":" + variables;
			}
			@Override public Translator withLocale(Locale locale) { return null; }
			@Override public void setLocale(Locale locale) {}
			@Override public Set<String> getSupportedLocales() { return null; }
			@Override public Locale getLocale(String locale) { return new Locale("", true, Arrays.asList()); }
			@Override public Locale getLocale() { return new Locale("", true, Arrays.asList()); }
		};
		Request request = mock(Request.class);
		
		RequestParameters parameters = new RequestParameters();
		parameters.put("a", "1");
		parameters.put("b", "2");
		parameters.put("c", "3");
		
		rules = new LinkedList<>(rules);
		Validator validator = strict == null ? new Validator(rules.remove(0)) : new Validator(strict);
		rules.forEach(r->validator.addRule(r));
		if (globalFunction != null) {
			validator.setGlobalFunction(globalFunction);
		}
		
		ValidationResult actualResult = validator.validate(request, parameters, translator);

		assertEquals(message, expectedResult.toString(), actualResult.toString());
		assertEquals(message, expectedParameters.toString(), parameters.toString());
		
		assertEquals(message, expectedResult, actualResult);
		assertEquals(message, expectedParameters, parameters);
	}
	
	public Object[] dataValidate() {
		return new Object[] {
			new Object[] {
				"no rules - strict",
				true, null, Arrays.asList(),
				new ValidationResult()
				.addError("toti.validation.not-expected-parameters:{parameters=[a, b, c]}"),
				new RequestParameters()
				.put("a", "1")
				.put("b", "2")
				.put("c", "3")
			},
			new Object[] {
				"no rules - not strict",
				false, null, Arrays.asList(),
				new ValidationResult(),
				new RequestParameters()
				.put("a", "1")
				.put("b", "2")
				.put("c", "3")
			},
			new Object[] {
				"with rule - error",
				false, null, Arrays.asList(
					new RC("a")
					.addRule((a, propertyName, ruleName, item)->{
						item.addError(propertyName, t->"expected-error");
					})
				),
				new ValidationResult()
				.addError("a", "expected-error"),
				new RequestParameters()
				.put("a", "1")
				.put("b", "2")
				.put("c", "3")
			},
			new Object[] {
				"with rule - no error",
				false, null, Arrays.asList(
					new RC("a")
					.addRule((a, propertyName, ruleName, item)->{})
				),
				new ValidationResult(),
				new RequestParameters()
				.put("a", "1")
				.put("b", "2")
				.put("c", "3")
			}, 
			new Object[] {
				"with global function",
				false, g((req, data, result, t)->{
					result.addError("expected-error");
					data.remove("a");
					data.put("x", "y");
				}), Arrays.asList(),
				new ValidationResult()
				.addError("expected-error"),
				new RequestParameters()
				.put("b", "2")
				.put("c", "3")
				.put("x", "y")
			}, 
			new Object[] {
				"STRICT: more parameters",
				true, null, Arrays.asList(
					new RC("a").addRule((a, propertyName, ruleName, item)->{}),
					new RC("b").addRule((a, propertyName, ruleName, item)->{})
				),
				new ValidationResult()
				.addError("toti.validation.not-expected-parameters:{parameters=[c]}"),
				new RequestParameters()
				.put("a", "1")
				.put("b", "2")
				.put("c", "3")
			},
			new Object[] {
				"STRICT: more rules",
				true, null, Arrays.asList(
					new RC("a").addRule((a, propertyName, ruleName, item)->{}),
					new RC("b").addRule((a, propertyName, ruleName, item)->{}),
					new RC("c").addRule((a, propertyName, ruleName, item)->{}),
					new RC("d").addRule((a, propertyName, ruleName, item)->{})
				),
				// no error occurs because missing parameter is not validator job
				new ValidationResult(),
				new RequestParameters()
				.put("a", "1")
				.put("b", "2")
				.put("c", "3")
			},
			new Object[] {
				"STRICT: correct",
				true, null, Arrays.asList(
					new RC("a").addRule((a, propertyName, ruleName, item)->{}),
					new RC("b").addRule((a, propertyName, ruleName, item)->{}),
					new RC("c").addRule((a, propertyName, ruleName, item)->{})
				),
				new ValidationResult(),
				new RequestParameters()
				.put("a", "1")
				.put("b", "2")
				.put("c", "3")
			},
			new Object[] {
				"NOSTRICT: more parameters",
				false, null, Arrays.asList(
					new RC("a").addRule((a, propertyName, ruleName, item)->{}),
					new RC("b").addRule((a, propertyName, ruleName, item)->{})
				),
				new ValidationResult(),
				new RequestParameters()
				.put("a", "1")
				.put("b", "2")
				.put("c", "3")
			},
			new Object[] {
				"NOSTRICT: more rules",
				false, null, Arrays.asList(
					new RC("a").addRule((a, propertyName, ruleName, item)->{}),
					new RC("b").addRule((a, propertyName, ruleName, item)->{}),
					new RC("c").addRule((a, propertyName, ruleName, item)->{}),
					new RC("d").addRule((a, propertyName, ruleName, item)->{})
				),
				new ValidationResult(),
				new RequestParameters()
				.put("a", "1")
				.put("b", "2")
				.put("c", "3")
			},
			new Object[] {
				"NOSTRICT: correct",
				false, null, Arrays.asList(
					new RC("a").addRule((a, propertyName, ruleName, item)->{}),
					new RC("b").addRule((a, propertyName, ruleName, item)->{}),
					new RC("c").addRule((a, propertyName, ruleName, item)->{})
				),
				new ValidationResult(),
				new RequestParameters()
				.put("a", "1")
				.put("b", "2")
				.put("c", "3")
			},
			new Object[] {
				"RULE: break",
				false, null, Arrays.asList(
					new RC("a")
					.addRule((a, propertyName, ruleName, item)->{
						item.addError(t->"E1");
					})
					.addRule((a, propertyName, ruleName, item)->{
						item.addError(t->"E2");
					})
					.addRule((a, propertyName, ruleName, item)->{
						item.setCanValidate(false);
						item.addError(t->"E3");
					})
					.addRule((a, propertyName, ruleName, item)->{
						item.addError(t->"E4");
					})
				),
				new ValidationResult()
				.addError("a", "E1")
				.addError("a", "E2")
				.addError("a", "E3"),
				new RequestParameters()
				.put("a", "1")
				.put("b", "2")
				.put("c", "3")
			},
			new Object[] {
				"RULE: change value",
				false, null, Arrays.asList(
					new RC("a").setChangeValue(x->"11"),
					new RC("d").setChangeValue(x->"44") // not existing parameter
				),
				new ValidationResult(),
				new RequestParameters()
				.put("a", "11")
				.put("b", "2")
				.put("c", "3")
			},
			new Object[] {
				"RULE: rename",
				false, null, Arrays.asList(
					new RC("a").setRename("aa"),
					new RC("d").setRename("dd") // not existing parameter
				),
				new ValidationResult(),
				new RequestParameters()
				.put("aa", "1")
				.put("b", "2")
				.put("c", "3")
			}, 
			new Object[] {
				"DEFAULT RULE: correct",
				null, null, Arrays.asList(
					new RC("").addRule((a, propertyName, ruleName, item)->{
						item.addError(t->"def applied");
					}),
					new RC("a").addRule((a, propertyName, ruleName, item)->{}),
					new RC("b").addRule((a, propertyName, ruleName, item)->{}),
					new RC("c").addRule((a, propertyName, ruleName, item)->{})
				),
				new ValidationResult(),
				new RequestParameters()
				.put("a", "1")
				.put("b", "2")
				.put("c", "3")
			},
			new Object[] {
				"DEFAULT RULE: missing rule",
				null, null, Arrays.asList(
					new RC("").addRule((a, propertyName, ruleName, item)->{
						item.addError(t->"def applied");
					}),
					new RC("a").addRule((a, propertyName, ruleName, item)->{})
				),
				new ValidationResult()
				.addError("b", "def applied")
				.addError("c", "def applied"),
				new RequestParameters()
				.put("a", "1")
				.put("b", "2")
				.put("c", "3")
			},
		};
	}
	
	private GlobalFunction g(GlobalFunction g) {
		return g;
	}
	
	class RC implements RulesCollection {
		private final String name;
		private final List<Rule> rules = new LinkedList<>();
		private Optional<String> rename = Optional.empty();
		private Optional<Function<Object, Object>> changeValue = Optional.empty();
		private Optional<Consumer<ValidationItem>> customValidation = Optional.empty();
		
		public RC(String name) {
			this.name = name;
		}
		
		public RC addRule(Rule rule) {
			rules.add(rule);
			return this;
		}
		public RC setChangeValue(Function<Object, Object> changeValue) {
			this.changeValue = Optional.of(changeValue);
			return this;
		}
		public RC setCustomValidation(Consumer<ValidationItem> customValidation) {
			this.customValidation = Optional.of(customValidation);
			return this;
		}
		public RC setRename(String rename) {
			this.rename = Optional.of(rename);
			return this;
		}
		@Override public List<Rule> getRules() {
			return rules;
		}
		@Override public String getName() {
			return name;
		}
		@Override public Optional<String> getRename() {
			return rename;
		}
		@Override public Optional<Function<Object, Object>> getChangeValue() {
			return changeValue;
		}
		@Override public Optional<Consumer<ValidationItem>> getCustomValidation() {
			return customValidation;
		}
	}
	
}
