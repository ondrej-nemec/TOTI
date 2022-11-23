package toti.tools;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ji.common.structures.MapDictionary;
import ji.common.structures.ThrowingFunction;
import ji.files.text.basic.ReadText;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.RequestParameters;
import ji.translator.Locale;
import ji.translator.Translator;
import toti.control.Form;
import toti.control.inputs.Checkbox;
import toti.control.inputs.DynamicList;
import toti.control.inputs.InputList;
import toti.control.inputs.Number;
import toti.control.inputs.Option;
import toti.control.inputs.Select;
import toti.control.inputs.Submit;
import toti.control.inputs.Text;
import toti.response.Response;
import toti.validation.ItemRules;
import toti.validation.Validator;

public class Generate {
	
	public Response getPage() {
		Form form = new Form("/toti/generate/do", true).setAfterRender("initControl")
		.setFormMethod("post")
		.addInput(
			InputList.input("api").addInput(
				Checkbox.input("use", false).setTitle("API Controller").setDefaultValue(true)
			).addInput(
				Checkbox.input("get-all", false).setTitle("Get All").setDefaultValue(true)
			).addInput(
				Checkbox.input("get", false).setTitle("Get").setDefaultValue(true)
			).addInput(
				Checkbox.input("update", false).setTitle("Update").setDefaultValue(true)
			).addInput(
				Checkbox.input("insert", false).setTitle("Insert").setDefaultValue(true)
			).addInput(
				Checkbox.input("delete", false).setTitle("Delete").setDefaultValue(true)
			)
		).addInput(
			InputList.input("page").addInput(
				Checkbox.input("use", false).setTitle("Page Controller").setDefaultValue(true)
			).addInput(
				Checkbox.input("grid", false).setTitle("Grid").setDefaultValue(true)
			).addInput(
				Checkbox.input("detail", false).setTitle("Detail").setDefaultValue(true)
			).addInput(
				Checkbox.input("create-form", false).setTitle("Create form").setDefaultValue(true)
			).addInput(
				Checkbox.input("edit-form", false).setTitle("Edit form").setDefaultValue(true)
			)
		).addInput(
			InputList.input("others").addInput(
				Checkbox.input("jsp", false).setTitle("JSP page").setDefaultValue(true)
			).addInput(
				Checkbox.input("migration", false).setTitle("Migration").setDefaultValue(true)
			).addInput(
				Checkbox.input("dao", false).setTitle("Dao").setDefaultValue(true)
			).addInput(
				Checkbox.input("entity", false).setTitle("Entity").setDefaultValue(true)
			).addInput(
				Checkbox.input("translation", false).setTitle("Translation").setDefaultValue(true)
			)
		).addInput(
			Text.input("name", true).setTitle("Name")
		).addInput(
			Checkbox.input("secured", false).setTitle("Secured").setDefaultValue(true)
		).addInput(
			Text.input("domain", false).setTitle("Security domain")
		).addInput(
			DynamicList.input("input")
			.addInput(
				Text.input("name", true)
			).addInput(
				Select.input("type", true, Arrays.asList(
					Option.create("Object", "Object"),
					
					Option.create("boolean", "boolean").setOptGroup("Primitives"),
					Option.create("byte", "byte").setOptGroup("Primitives"),
					Option.create("short", "short").setOptGroup("Primitives"),
					Option.create("int", "int").setOptGroup("Primitives"),
					Option.create("long", "long").setOptGroup("Primitives"),
					Option.create("float", "float").setOptGroup("Primitives"),
					Option.create("double", "double").setOptGroup("Primitives"),
					
					Option.create("Boolean", "Boolean").setOptGroup("Objects"),
					Option.create("Byte", "Byte").setOptGroup("Objects"),
					Option.create("Short", "Short").setOptGroup("Objects"),
					Option.create("Integer", "Integer").setOptGroup("Objects"),
					Option.create("Long", "Long").setOptGroup("Objects"),
					Option.create("Float", "Float").setOptGroup("Objects"),
					Option.create("Double", "Double").setOptGroup("Objects"),
					
					Option.create("LocalDate", "LocalDate").setOptGroup("Date and time"),
					Option.create("LocalTime", "LocalTime").setOptGroup("Date and time"),
					Option.create("LocalDateTime", "LocalDateTime").setOptGroup("Date and time")
					// Option.create("ZonedDateTime", "ZonedDateTime").setOptGroup("Date and time")
				))
			).addInput(
				Number.input("max-length", false).setStep(1)
			).addInput(
				Number.input("min-length", false).setStep(1)
			).addInput(
				Number.input("max-value", false)
			).addInput(
				Number.input("min-value", false)
			)
		).addInput(
			Submit.create("Generate", "submit")
			.setOnSuccess("onSuccess").setOnFailure("onFailure")
		);
		Map<String, Object> params = new HashMap<>();
		params.put("control", form);
		return Response.getTemplate("/generate.jsp", params);
	}
	
	public Validator getValidator() {
		return new Validator(true)
			.addRule(
				ItemRules.forName("name", true)
			).addRule(
				ItemRules.forName("secured", false).setType(boolean.class)
			).addRule(
				ItemRules.forName("domain", false)
			).addRule(
				ItemRules.forName("api", true).setMapSpecification(
					new Validator(true)
						.addRule(ItemRules.forName("use", false).setType(boolean.class))
						.addRule(ItemRules.forName("get", false).setType(boolean.class))
						.addRule(ItemRules.forName("get-all", false).setType(boolean.class))
						.addRule(ItemRules.forName("update", false).setType(boolean.class))
						.addRule(ItemRules.forName("insert", false).setType(boolean.class))
						.addRule(ItemRules.forName("delete", false).setType(boolean.class))
				)
			).addRule(
				ItemRules.forName("page", true).setMapSpecification(
					new Validator(true)
						.addRule(ItemRules.forName("use", false).setType(boolean.class))
						.addRule(ItemRules.forName("grid", false).setType(boolean.class))
						.addRule(ItemRules.forName("detail", false).setType(boolean.class))
						.addRule(ItemRules.forName("create-form", false).setType(boolean.class))
						.addRule(ItemRules.forName("edit-form", false).setType(boolean.class))
				)
			).addRule(
				ItemRules.forName("others", true).setMapSpecification(
					new Validator(true)
						.addRule(ItemRules.forName("dao", false).setType(boolean.class))
						.addRule(ItemRules.forName("jsp", false).setType(boolean.class))
						.addRule(ItemRules.forName("migration", false).setType(boolean.class))
						.addRule(ItemRules.forName("translation", false).setType(boolean.class))
						.addRule(ItemRules.forName("entity", false).setType(boolean.class))
				)
			).addRule(
				ItemRules.forName("inputs", true).setMapSpecification(
					new Validator(ItemRules.defaultRule().setMapSpecification(
						new Validator(true)
							.addRule(ItemRules.forName("name", true))
							.addRule(ItemRules.forName("type", true)) // TODO class
							.addRule(ItemRules.forName("min-value", false).setType(java.lang.Number.class))
							.addRule(ItemRules.forName("max-value", false).setType(java.lang.Number.class))
							.addRule(ItemRules.forName("min-length", false).setType(java.lang.Number.class))
							.addRule(ItemRules.forName("max-length", false).setType(java.lang.Number.class))
					))
				)
			);
	}
	
	public Response generate(RequestParameters params) {
		Map<String, Set<String>> errors = getValidator().validate(params, params, getTranslator());
		if (errors.size() > 0) {
			return Response.getText(StatusCode.BAD_REQUEST, "");
		}
		
		System.err.println(params.getBoolean("secured") + " " + params.getString("domain"));
		System.err.println("API:");
		params.getDictionaryMap("api").forEach((name, use)->{
			System.err.println("  " + name + " " + use);
		});
		System.err.println("Page:");
		params.getDictionaryMap("page").forEach((name, use)->{
			System.err.println("  " + name + " " + use);
		});
		System.err.println("Others:");
		params.getDictionaryMap("others").forEach((name, use)->{
			System.err.println("  " + name + " " + use);
		});
		System.err.println("Inputs:");
		params.getDictionaryMap("input").forEach((index, inputs)->{
			System.err.println("  List:" + index);
			inputs.getDictionaryMap().forEach((name, value)->{
				System.err.println("    " + name + ": " + value);
			});
		});
		try {
			Map<String, Object> data = new HashMap<>();
			// ji.files.text.Text text = ji.files.text.Text.get();
			String name = params.getString("name");
			if (params.getDictionaryMap("api").getBoolean("use")) {
				data.put("api", getApiController(params, name));
			}
			if (params.getDictionaryMap("page").getBoolean("use")) {
				// TODO
			}
			
			return Response.getJson(data);
		} catch(Exception e) {
			return Response.getText(StatusCode.BAD_REQUEST, e.getMessage());
		}
	}
	
	private String getApiController(MapDictionary<String, Object> api, String name) throws IOException {
		String template = getTemplate("api-controller.txt", name);
		if (api.getBoolean("get-all")) {			
			template = fillTemplate("// list", template, "api-all.txt", name, (t)->{
				return t; // TODO fill validator
			});
		}
		if (api.getBoolean("get")) {
			template = fillTemplate("// get", template, "api-get.txt", name);
		}
		if (api.getBoolean("delete")) {
			template = fillTemplate("// delete", template, "api-delete.txt", name);
		}
		if (api.getBoolean("update")) {
			template = fillTemplate("// update", template, "api-update.txt", name);
		}
		if (api.getBoolean("insert")) {
			template = fillTemplate("// insert", template, "api-insert.txt", name);
		}
		if (api.getBoolean("insert") || api.getBoolean("update")) {
			template = fillTemplate("// insert", template, "api-validator.txt", name, (t)->{
				return t; // TODO fill validator
			});
		}
		return template;
	}
	
	private String fillTemplate(String placeholder, String template, String name, String filename) throws IOException {
		return fillTemplate(placeholder, template, name, filename, a->a);
	}
	
	private String fillTemplate(
			String placeholder, String template, String name, String filename, 
			ThrowingFunction<String, String, IOException> onTempate) throws IOException {
		return template.replace(placeholder, onTempate.apply(getTemplate(filename, name)));
	}

	private String getTemplate(String fileName, String name) throws IOException {
		String first = name.charAt(0) + "";
		String appendix = name.substring(1);
		return ji.files.text.Text.get().read(
			br->ReadText.get().asString(br),
			getClass().getResourceAsStream("/toti/generate/" + fileName)
		)
		.replace("%nameUC%", first.toUpperCase() + appendix)
		.replace("%nameLC%", first.toLowerCase() + appendix);
	}
	
	private Translator getTranslator() {
		return new Translator() {
			@Override public String translate(String key, Map<String, Object> variables, String locale) {
				return key;
			}
			@Override public Translator withLocale(Locale locale) { return null; }
			@Override public void setLocale(Locale locale) {}
			@Override public Locale getLocale(String locale) { return null; }
			@Override public Locale getLocale() { return null; }
			@Override public Set<String> getSupportedLocales() { return null; }
		};
	}
	
}
