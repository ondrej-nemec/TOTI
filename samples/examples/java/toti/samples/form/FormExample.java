package toti.samples.form;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import ji.common.functions.Env;
import ji.common.structures.MapInit;
import ji.database.Database;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.structures.RequestParameters;
import ji.socketCommunication.http.structures.UploadedFile;
import ji.translator.Translator;
import toti.Module;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Method;
import toti.annotations.Param;
import toti.annotations.ParamUrl;
import toti.annotations.Params;
import toti.annotations.Secured;
import toti.application.Task;
import toti.authentication.AuthentizationException;
import toti.control.Form;
import toti.control.inputs.Button;
import toti.control.inputs.Checkbox;
import toti.control.inputs.Color;
import toti.control.inputs.Date;
import toti.control.inputs.Datetime;
import toti.control.inputs.DynamicList;
import toti.control.inputs.Email;
import toti.control.inputs.File;
import toti.control.inputs.Hidden;
import toti.control.inputs.InputList;
import toti.control.inputs.Month;
import toti.control.inputs.Number;
import toti.control.inputs.Option;
import toti.control.inputs.Password;
import toti.control.inputs.RadioList;
import toti.control.inputs.Range;
import toti.control.inputs.Select;
import toti.control.inputs.Submit;
import toti.control.inputs.SubmitPolicy;
import toti.control.inputs.Text;
import toti.control.inputs.TextArea;
import toti.control.inputs.Time;
import toti.control.inputs.Week;
import toti.register.Register;
import toti.response.Response;
import toti.security.Authenticator;
import toti.security.Identity;
import toti.security.AuthMode;
import toti.security.User;
import toti.url.Link;
import toti.validation.ItemRules;
import toti.validation.Validator;

/**
 * Example shows using forms
 * @author Ondřej Němec
 *
 */
@Controller("form")
public class FormExample implements Module {
	
	private FormExampleDao dao;
	private Identity identity;
	private Authenticator authenticator;
	
	public FormExample() {}
	
	public FormExample(FormExampleDao dao, Identity identity, Authenticator authenticator) {
		this.dao = dao;
		this.identity = identity;
		this.authenticator = authenticator;
	}
	
	/**
	 * Display inputs
	 * @return http://localhost:8080/examples-form/form/inputs/{0-100}
	 */
	@Action("inputs")
	public Response inputsForm(@ParamUrl("id") int i) {
		return getInputs(i, true, "All inputs - Bind");
	}
	
	/**
	 * Display form as detail - not editable
	 * @return http://localhost:8080/examples-form/form/detail/{0-100}
	 */
	@Action("detail")
	public Response inputsNotEditable(@ParamUrl("id") int i) {
		return getInputs(i, false, "All inputs - Not editable");
	}
	
	/**
	 * Display inputs
	 * @return http://localhost:8080/examples-form/form/insert
	 */
	@Action("insert")
	public Response allInputsSave() {
		return getInputs(null, true, "All inputs - No bind");
	}
	
	/**
	 * Get data for form
	 * @return http://localhost:8080/examples-form/form/get/{0-100}
	 */
	@Action("get")
	public Response allInputsGet(@ParamUrl("id") int i) {
		return Response.getJson(dao.get(i));
	}
	
	/**
	 * Save data from form
	 * @return http://localhost:8080/examples-form/form/save-inputs/{0-100}
	 */
	@Action("save-inputs")
	public Response allInputs(@ParamUrl("hidden") Integer index, @Params FormExampleEntity entity) {
		int id = dao.update(index, entity);
		return Response.getJson(new MapInit<>().append("id", id).append("message", "Saved").toMap());
	}
	
	private Response getInputs(Integer i, boolean editable, String title) {
		Form form = new Form(Link.get().addUrlParam(i).create(getClass(), c->c.allInputs(0, null)), editable);
		form.addInput(
			Hidden.input("hidden").setDefaultValue(i)
		);
		form.addInput(
			Text.input("text", true).setTitle("Text input")
			.setMinLength(2).setMaxLength(10)
			.setPlaceholder("Some text")
		);
		form.addInput(
			TextArea.input("textarea", false).setTitle("Texarea")
			.setCols(20).setRows(3).setPlaceholder("Some text")
			.setMaxLength(255)
		);
		form.addInput(
			Email.input("email", true).setTitle("Email")
			.setPlaceholder("your.name@domain.tld")
		);
		form.addInput(
			Color.input("color", true).setTitle("Color")
		);
		form.addInput(
			Checkbox.input("checkbox", false).setTitle("Checkbox")
		);
		form.addInput(
			Number.input("number", true).setStep(0.1).setTitle("Number (double)")
			.setMin(0).setMax(10000).setPlaceholder("Number from 0 to 10000")
		);
		form.addInput(
			Password.input("password", true).setTitle("Password")
			.setPlaceholder("Password")
		);
		Map<String, String> radioListOptions = new HashMap<>();
		for (FormEnum e : FormEnum.values()) {
			radioListOptions.put(e.toString(), e.toString().replace("_", " "));
		}
		form.addInput(
			RadioList.input("radiolist", true, radioListOptions)
			.setTitle("Radio List")
		);
		form.addInput(
			Range.input("range", true).setTitle("Range")
			.setMin(0).setMax(100).setStep(1)
		);
		form.addInput(
			Select.input("select", true, Arrays.asList(
				Option.create("", "--Select--"),
				Option.create(1, "First Option"),
				Option.create(2, "Second Option"),
				Option.create(3, "Third Option")
			))
			.setTitle("Select")
		);
		form.addInput(
			Datetime.input("datetime", true).setTitle("Datetime")
		);
		form.addInput(
			Date.input("date", true).setTitle("Date")
		);
		form.addInput(
			Time.input("time", true).setTitle("Time")
		);
		form.addInput(
			Month.input("month", true).setTitle("Month")
		);
		form.addInput(
			Week.input("week", true).setTitle("Week")
		);
		form.addInput(
			Submit.create("Save", "save")
			.setRedirect(Link.get().addUrlParam(i).create(getClass(), c->c.inputsForm(0)))
		);
		if (i != null) {
			form.setBindUrl(Link.get().addUrlParam(i).create(getClass(), c->c.allInputsGet(0)));
			form.setBindMethod("get");
		}
		Map<String, Object> params = new HashMap<>();
		params.put("form", form);
		params.put("title", title);
		return Response.getTemplate("inputs.jsp", params);
	}
	
	/******/
	
	/**
	 * Form with sync and async submit
	 * @return http://localhost:8080/examples-form/form/async
	 */
	@Action("async")
	@Method(HttpMethod.GET)
	public Response asyncForm() {
		Form form = new Form(Link.get().create(getClass(), c->c.save(null)), true);
		form.setFormMethod("post");
		
		form.addInput(Text.input("textInput", true).setTitle("Text"));
		
		form.addInput(Submit.create("Submit sync", "sync").setAsync(false));
		form.addInput(Submit.create("Submit async", "async").setAsync(true));
		
		Map<String, Object> params = new HashMap<>();
		params.put("form", form);
		params.put("title", "Sync and async send");
		return Response.getTemplate("inputs.jsp", params);
	}
	
	/**
	 * Form with sync and async submit
	 * @return http://localhost:8080/examples-form/form/submit-modes
	 */
	@Action("submit-modes")
	@Method(HttpMethod.GET)
	public Response submitModes() {
		Form form = new Form(Link.get().create(getClass(), c->c.save(null)), true);
		form.setFormMethod("post");
		
		form.addInput(Text.input("textInput", true).setTitle("Text"));
		
		form.addInput(
			Submit.create("Excluded", "excluded")
			.setSubmitPolicy(SubmitPolicy.EXCLUDE) // default value
			.setValue("value1")
		);
		form.addInput(
			Submit.create("Included", "included")
			.setSubmitPolicy(SubmitPolicy.INCLUDE)
			.setValue("value2")
		);
		form.addInput(
			Submit.create("Included if clicked", "ifClicked")
			.setSubmitPolicy(SubmitPolicy.INCLUDE_ON_CLICK)
			.setValue("value3")
		);
		
		Map<String, Object> params = new HashMap<>();
		params.put("form", form);
		params.put("title", "Submit modes send");
		return Response.getTemplate("inputs.jsp", params);
	}
	
	/******/

	/**
	 * Example of buttons
	 * @return http://localhost:8080/examples-form/form/buttons
	 */
	@Action("buttons")
	public Response buttons() {
		Form form = new Form(Link.get().create(getClass(), c->c.save(null)), true);
		form.setFormMethod("post");
		// setCondition is not supported for button in form
		form.addInput(
			Button.create(
				Link.get().addGetParam("urlParam", "URL param").create(getClass(), c->c.notSave(null)), 
				"async-button"
			)
			.addRequestParam("buttonParam", "Button param") // async only
			.setTitle("Async button")
			.setAsync(true)
			.setMethod("post")
			.setConfirmation("Async button: Really?")
			.setOnFailure("buttonOnFailure").setOnFailure("buttonOnSuccess")
		);
		form.addInput(
			Button.create(
				Link.get().addGetParam("urlParam", "URL param").create(getClass(), c->c.notSave(null)),
				"sync-button"
			)
			.setAsync(false)
			.setMethod("post")
			.setTitle("Synd button")
			.setConfirmation("Sync button: Really?")
		);
		
		form.addInput(
			Submit.create("Send with redirect", "save-and-redirect")
			.setConfirmation("Send: Really?")
			.setOnFailure("submitOnFailure").setOnSuccess("submitOnSuccess")
			.setRedirect(Link.get().addGetParam("redirected", true).create(getClass(), c->c.notSave(null)))
			.setAsync(true)
		);
		
		Map<String, Object> params = new HashMap<>();
		params.put("form", form);
		params.put("title", "Buttons");
		return Response.getTemplate("inputs.jsp", params);
	}
	
	/**
	 * Link for not save
	 * @return http://localhost:8080/examples-form/form/notsave
	 */
	@Action("notsave")
	public Response notSave(@Params RequestParameters params) {
		return Response.getText("OK: " + params);
	}
	
	/**
	 * Save for form testing
	 * @return (POST)http://localhost:8080/examples/form/save
	 */
	@Action("save")
	@Method(HttpMethod.POST)
	public Response save(@Params RequestParameters params) {
		return Response.getText("OK: " + params);
	}
	
	/**
	 * Form printed with template
	 * @return http://localhost:8080/examples-form/form/template
	 */
	@Action("template")
	public Response template() {
		Form form = new Form(Link.get().create(getClass(), c->c.templateSave(null)), true);
		form.setFormMethod("post");
		
		form.addInput(Text.input("textInput", true).setTitle("Text"));
		form.addInput(Number.input("numberInput", false).setTitle("Number"));
		form.addInput(Text.input("notExpected", false).setTitle("Validator not expect this parameter"));
		
		form.addInput(Submit.create("Submit", "submit1"));
		form.addInput(Submit.create("Another submit", "submit2"));
		
		Map<String, Object> params = new HashMap<>();
		params.put("form", form);
		params.put("title", "Form template");
		return Response.getTemplate("template.jsp", params);
	}
	
	/**
	 * Validator for tempale save for testing error print
	 */
	public Validator validatorForTemplateSave() {
		return new Validator(true)
			.addRule(ItemRules.forName("textInput", true).setMaxLength(10))
			.addRule(ItemRules.forName("numberInput", true).setMinLength(2).setMaxLength(4));
	}
	
	/**
	 * Save for template form testing
	 * @return (POST)http://localhost:8080/examples/form/template-save
	 */
	@Action(value = "template-save", validator = "validatorForTemplateSave")
	@Method(HttpMethod.POST)
	public Response templateSave(@Params RequestParameters params) {
		return Response.getText("OK: " + params);
	}
	
	/**
	 * Show select options: load, depends
	 * @return http://localhost:8080/examples-form/form/select
	 */
	@Action("select")
	@Method(HttpMethod.GET)
	public Response select() {
		Form form = new Form(Link.get().create(getClass(), c->c.save(null)), true);
		form.setFormMethod("post");
		
		List<Option> defOptions = Arrays.asList(
			Option.create("no-group", "No group item"),
			Option.create("disabled", "Disabled").setDisabled(true),
			Option.create("a1", "Item A in Group 1").setOptGroup("group1"),
			Option.create("a2", "Item B in Group 1").setOptGroup("group1"),
			Option.create("b1", "Item A in Group 2").setOptGroup("group2"),
			Option.create("b2", "Item B in Group 2").setOptGroup("group2")
		);
		form.addInput(
			Select.input("options-only", false, defOptions)
			.setTitle("Options only")
		);
		form.addInput(
			Select.input("load-only", false, Arrays.asList(Option.create("", "---")))
			.setTitle("Load with empty option")
			.setLoadData(Link.get().create(getClass(), c->c.loadToSelect()), "get")
		);
		// TODO tohle nejde
		form.addInput(
			Select.input("load-with-def", false, defOptions)
			.setTitle("Load with default options")
			.setLoadData(Link.get().create(getClass(), c->c.loadToSelect()), "get")
		);
		
		form.addInput(
			Select.input("groups", false, Arrays.asList(
				Option.create("", "---"),
				Option.create("group1", "Group 1"),
				Option.create("group2", "Group 2")
			))
			.setTitle("Depends Parent")
		);
		form.addInput(
			Select.input("depends", false, defOptions)
			.setLoadData(Link.get().create(getClass(), c->c.loadToSelect()), "get")
			.setTitle("Depends on previous input")
			.setDepends("[name=groups]")
		);
		// TODO depends on text, radio

		form.addInput(
			Select.input("selected-group", false, defOptions)
			.setLoadData(Link.get().create(getClass(), c->c.loadToSelect()), "get")
			.setTitle("Selected Group 1")
			.setShowedOptionGroup("group1")
		);
				
		form.addInput(
			Select.input("groupsSelected", false, Arrays.asList(
				Option.create("", "---"),
				Option.create("group1", "Group 1"),
				Option.create("group2", "Group 2")
			))
			.setTitle("Depends Parent with selected value")
			.setDefaultValue("group1")
		);
		form.addInput(
			Select.input("depends-on-selected", false, defOptions)
			.setLoadData(Link.get().create(getClass(), c->c.loadToSelect()), "get")
			.setTitle("Depends on previous input - selected value")
			.setDepends("[name=groupsSelected]")
		);

		form.addInput(
			Select.input("load-with-def-value", false, defOptions)
			.setTitle("Load with default options and default value")
			.setLoadData(Link.get().create(getClass(), c->c.loadToSelect()), "get")
			.setDefaultValue(5)
		);
		
		
		
		
		// TODO podle select.html
		/*form.addInput(
			Select.input("load", false, Arrays.asList(Option.create("", "-- Select --").setDisabled(true)))
			.setLoadData(Link.get().create(getClass(), c->c.loadToSelect()), "get")
			.setTitle("Load data")
		);
		form.addInput(
			Select.input("load-with", false, Arrays.asList(
				Option.create("", "-- Select --").setDisabled(true),
				Option.create("some-option", "Some option").setOptGroup("Default"),
				Option.create("another", "Another").setOptGroup("Default")
			))
			.setLoadData(Link.get().create(getClass(), c->c.loadGroup()), "get")
			.setTitle("Load data with optgroup and disabled")
		);
		
		form.addInput(
			Select.input("groups", false, Arrays.asList(
				Option.create("", "-- Select --").setDisabled(true),
				Option.create("", "No group"),
				Option.create("G0", "Group 0"),
				Option.create("G1", "Group 1"),
				Option.create("G2", "Group 2"),
				Option.create("G3", "Group 3")
			))
			.setTitle("Groups")
		);
		form.addInput(
			Select.input("depends-on", false, Arrays.asList(
				Option.create("", "-- Select --").setDisabled(true),
				Option.create("v1", "No group 1"),
				Option.create("v2", "No group 2"),
				Option.create("v0.1", "G0-O1").setOptGroup("G0"),
				Option.create("v0.2", "G0-O2").setOptGroup("G0"),
				Option.create("v0.3", "G0-O3").setOptGroup("G0")
			))
			.setDepends("groups")
			.setLoadData(Link.get().create(getClass(), c->c.loadGroup()), "get")
			.setTitle("Depends on previous input")
		);*/
		
		form.addInput(Submit.create("Submit", "submit"));
		
		Map<String, Object> params = new HashMap<>();
		params.put("form", form);
		params.put("title", "Sync and async send");
		return Response.getTemplate("inputs.jsp", params);
	}

	/**
	 * Options for select
	 * @return http://localhost:8080/examples-form/form/load-select
	 */
	@Action("load-select")
	public Response loadToSelect() {
		return Response.getJson(Arrays.asList(
			new MapInit<String, Object>()
				.append("value", "0")
				.append("title", "Item 0 without group")
				.toMap(),
			new MapInit<String, Object>()
				.append("value", "1")
				.append("title", "Item 1 in Group 1")
				.append("optgroup", "group1")
				.toMap(),
			new MapInit<String, Object>()
				.append("value", "2")
				.append("title", "Item 2 in Group 1 (disabled)")
				.append("optgroup", "group1")
				.append("disabled", true)
				.toMap(),
			new MapInit<String, Object>()
				.append("value", "3")
				.append("title", "Item 3 in Group 1")
				.append("optgroup", "group1")
				.toMap(),
			new MapInit<String, Object>()
				.append("value", "4")
				.append("title", "Item 1 in Group 2")
				.append("optgroup", "group2")
				.toMap(),
			new MapInit<String, Object>()
				.append("value", "5")
				.append("title", "Item 2 in Group 2")
				.append("optgroup", "group2")
				.toMap(),
			new MapInit<String, Object>()
				.append("value", "6")
				.append("title", "Item 3 in Group 3")
				.append("optgroup", "group2")
				.toMap()
		));
	}

	/**
	 * Options for select
	 * @return http://localhost:8080/examples-form/form/load-optgroup
	 */
	@Action("load-optgroup")
	public Response loadGroup() {
		return Response.getJson(Arrays.asList(
			new MapInit<String, Object>()
				.append("value", "v1.1").append("title", "G1-O1")
				.append("optgroup", "G1").append("disabled", false)
				.toMap(),
			new MapInit<String, Object>()
				.append("value", "v1.2").append("title", "G1-O2")
				.append("optgroup", "G1").append("disabled", true)
				.toMap(),
			new MapInit<String, Object>()
				.append("value", "v1.3").append("title", "G1-O3").append("optgroup", "G1").toMap(),
				
			new MapInit<String, Object>()
				.append("value", "v2.1").append("title", "G2-O1").append("optgroup", "G2").toMap(),
			new MapInit<String, Object>()
				.append("value", "v2.1").append("title", "G2-O2").append("optgroup", "G2").toMap(),
			new MapInit<String, Object>()
				.append("value", "v2.3").append("title", "G2-O3").append("optgroup", "G2").toMap(),

			new MapInit<String, Object>()
				.append("value", "v3.1").append("title", "G3-O3").append("optgroup", "G3").toMap()
		));
	}
	
	/********/
	
	/**
	 * Shows options of datetime
	 * @return http://localhost:8080/examples-form/form/datetime
	 */
	@Action("datetime")
	public Response dateTimeStrict() {
		Form form = new Form(Link.get().create(getClass(), c->c.save(null)), true);
		form.setFormMethod("post");
		
		form.addInput(
			Datetime.input("datetime-required", true)
			.setTitle("Datetime required")
		);
		form.addInput(
			Datetime.input("datetime-optional", false)
			.setTitle("Datetime not required")
		);
		
		form.addInput(
			Datetime.input("datetime-required-not-strict", true)
			.setStrict(false)
			.setTitle("Datetime required, not strict")
		);
		form.addInput(
			Datetime.input("datetime-optional-not-strict", false)
			.setStrict(false)
			.setTitle("Datetime not required, not strict")
		);
		
		form.addInput(Submit.create("Submit", "submit"));
		
		Map<String, Object> params = new HashMap<>();
		params.put("form", form);
		params.put("title", "Datetime strict");
		return Response.getTemplate("inputs.jsp", params);
	}
	
	/********/
	
	/**
	 * Shows extended inputs
	 * @return http://localhost:8080/examples-form/form/optional
	 */
	@Action("optional")
	public Response optionalInputs() {
		Form form = new Form(Link.get().create(getClass(), c->c.save(null)), true);
		form.setFormMethod("post");
		
		form.addInput(
			Color.input("color-required", true)
			.setTitle("Color required")
		);
		form.addInput(
			Color.input("color-optional", false)
			.setTitle("Color optional")
		);
		
		form.addInput(
			Range.input("range-required", true)
			.setTitle("Range required")
		);
		form.addInput(
			Range.input("range-optional", false)
			.setTitle("Range optional")
		);
		
		form.addInput(Submit.create("Submit", "submit"));
		
		Map<String, Object> params = new HashMap<>();
		params.put("form", form);
		params.put("title", "Input extensions");
		return Response.getTemplate("inputs.jsp", params);
	}
	
	/********/
	
	/**
	 * Shows checkbox options
	 * @return http://localhost:8080/examples-form/form/checkbox
	 */
	@Action("checkbox")
	public Response checkbox() {
		// checkbox renderer works only if form or input is not editable
		Form form = new Form(Link.get().create(getClass(), c->c.save(null)), false);
		form.setFormMethod("post");
		
		form.addInput(
			Checkbox.input("no-renderer", false)
			.setTitle("No renderer")
		);
		form.addInput(
			Checkbox.input("with-renderer", false)
			.setTitle("With renderer")
			.setValuesRender("Checked", "Not Checked")
		);
		
		form.addInput(
			Checkbox.input("no-renderer-true-value", false)
			.setTitle("No renderer with true value")
			.setDefaultValue(true)
		);
		form.addInput(
			Checkbox.input("with-renderer-true-value", false)
			.setTitle("With renderer with true value")
			.setValuesRender("Checked", "Not Checked")
			.setDefaultValue(true)
		);

		form.addInput(
			Checkbox.input("no-renderer-false-value", false)
			.setTitle("No renderer with false value")
			.setDefaultValue(false)
		);
		form.addInput(
			Checkbox.input("with-renderer-false-value", false)
			.setTitle("With renderer with false value")
			.setValuesRender("Checked", "Not Checked")
			.setDefaultValue(false)
		);
		form.addInput(Submit.create("Submit", "submit"));
		
		Map<String, Object> params = new HashMap<>();
		params.put("form", form);
		params.put("title", "Input extensions");
		return Response.getTemplate("inputs.jsp", params);
	}
	
	/********/
	
	/**
	 * Usage and disabled
	 * @return http://localhost:8080/examples-form/form/exclude
	 */
	@Action("exclude")
	public Response exclude() {
		Form form = new Form(Link.get().create(getClass(), c->c.save(null)), true);
		form.setFormMethod("post");
		
		form.addInput(
			Text.input("basic", true)
			.setDefaultValue("value")
			.setTitle("Basic input")
		);
		form.addInput(
			Text.input("disabled-input", true)
			.setDefaultValue("some value")
			.setTitle("Disabled input")
			.setDisabled(true)
		);
		form.addInput(
			Text.input("disabled-not-excluded-input", true)
			.setDefaultValue("some value 2")
			.setTitle("Disabled but not excluded input")
			.setDisabled(true).setExclude(false)
		);
		form.addInput(
			Text.input("exlucded-input", true)
			.setDefaultValue("another value")
			.setTitle("Excluded input")
			.setExclude(true)
		);
		form.addInput(
			Text.input("excluded-and-disabled", true)
			.setDefaultValue("equals value")
			.setTitle("Excluded and disabeld input")
			.setExclude(true).setDisabled(true)
		);
		form.addInput(Submit.create("Submit", "submit"));
		
		Map<String, Object> params = new HashMap<>();
		params.put("form", form);
		params.put("title", "Disabled and exclude");
		return Response.getTemplate("inputs.jsp", params);
	}
	
	/********/
	
	/**
	 * Not editable form with editable input
	 * @return http://localhost:8080/examples-form/form/not-editable
	 */
	@Action("not-editable")
	public Response notEditable() {
		Form form = new Form(Link.get().create(getClass(), c->c.save(null)), false);
		form.setFormMethod("post");
		
		form.addInput(
			Text.input("not-editable", true)
			.setDefaultValue("value 1")
			.setTitle("Not editable input")
		);
		form.addInput(
			Text.input("editable", true)
			.setDefaultValue("value 2")
			.setEditable(true)
			.setTitle("Editable input")
		);
		
		form.addInput(Submit.create("Submit", "submit"));
		
		Map<String, Object> params = new HashMap<>();
		params.put("form", form);
		params.put("title", "Not editable form");
		return Response.getTemplate("inputs.jsp", params);
	}
	
	/**
	 * Not editable form with editable input
	 * @return http://localhost:8080/examples-form/form/editable
	 */
	@Action("editable")
	public Response editable() {
		Form form = new Form(Link.get().create(getClass(), c->c.save(null)), true);
		form.setFormMethod("post");
		
		form.addInput(
			Text.input("not-editable", true)
			.setDefaultValue("value 1")
			.setTitle("Not editable input")
			.setEditable(false)
		);
		form.addInput(
			Text.input("editable", true)
			.setDefaultValue("value 2")
			.setTitle("Editable input")
		);
		
		form.addInput(Submit.create("Submit", "submit"));
		
		Map<String, Object> params = new HashMap<>();
		params.put("form", form);
		params.put("title", "Editable form");
		return Response.getTemplate("inputs.jsp", params);
	}
	
	/*****/
	
	/**
	 * Redirect after async form submit
	 * @return http://localhost:8080/examples-form/form/redirect
	 */
	@Action("redirect")
	public Response submitWithRedirect() {
		Form form = new Form(Link.get().create(getClass(), c->c.formRedirectSave(null)), true);
		form.setFormMethod("post");

		form.addInput(
			Text.input("some-input", false)
			.setTitle("Some input")
		);
		form.addInput(
			Submit.create("Submit", "submit")
			.setRedirect(
				Link.get().addGetParam("someParam", "{id}")
				.create(getClass(), c->c.afterRedirect(null))
			)
		);
		
		Map<String, Object> params = new HashMap<>();
		params.put("form", form);
		params.put("title", "Submit redirect");
		return Response.getTemplate("inputs.jsp", params);
	}
	
	/**
	 * For after form submit
	 * @return http://localhost:8080/examples-form/form/after-redirect-save
	 */
	@Action("after-redirect-save")
	public Response formRedirectSave(@Param("some-input") String someInput) {
		Map<String, Object> json = new HashMap<>();
		json.put("id", someInput);
		json.put("message", "Saved");
		return Response.getJson(json);
	}
	
	/**
	 * For after form submit
	 * @return http://localhost:8080/examples-form/form/after-redirect
	 */
	@Action("after-redirect")
	public Response afterRedirect(@Params RequestParameters params) {
		return Response.getText("OK: " + params);
	}

	/*****/
	
	/**
	 * Redirect after async form submit
	 * @return http://localhost:8080/examples-form/form/file
	 */
	@Action("file")
	public Response file() {
		Form form = new Form(Link.get().create(getClass(), c->c.fileSave(null)), true);
		form.setFormMethod("post");

		form.addInput(Hidden.input("some"));
		form.addInput(
			File.input("fileInput", true)
			.setTitle("File upload")
		);
		form.addInput(Submit.create("Submit", "submit"));
		
		Map<String, Object> params = new HashMap<>();
		params.put("form", form);
		params.put("title", "File upload");
		return Response.getTemplate("inputs.jsp", params);
	}
	
	/**
	 * Redirect after async form submit
	 * @return http://localhost:8080/examples-form/form/file-save
	 */
	@Action("file-save")
	public Response fileSave(@Param("fileInput") UploadedFile file) {
		return Response.getText("OK: " + file.getFileName() + " " + file.getContent().length + "b");
	}
	
	/*****/
	
	/**
	 * Using form callbaks
	 * @return http://localhost:8080/examples-form/form/callbacks
	 */
	@Action("callbacks")
	public Response callbacks() {
		Form form = new Form(Link.get().addUrlParam(10).create(getClass(), c->c.allInputs(0, null)), true);
		form.setAfterBind("afterBindCallback");
		form.setAfterRender("afterPrintCallback");
		form.setBeforeBind("beforeBindCallback");
		
		form.addInput(Text.input("text", true).setTitle("Text input"));
		form.setBindUrl(Link.get().addUrlParam(10).create(getClass(), c->c.allInputsGet(0)));
		form.setBindMethod("get");
		
		Map<String, Object> params = new HashMap<>();
		params.put("form", form);
		params.put("title", "Callbacks");
		return Response.getTemplate("inputs.jsp", params);
	}

	/*****/
	
	/**
	 * Using input list
	 * @return http://localhost:8080/examples-form/form/inputlist
	 */
	@Action("inputlist")
	public Response inputList() {
		return inputListResponse("Input list - default print", "inputs.jsp");
	}

	/**
	 * Using input list with custom template
	 * @return http://localhost:8080/examples-form/form/inputlist-template
	 */
	@Action("inputlist-template")
	public Response inputListTemplate() {
		return inputListResponse("Input list - template", "inputListTemplate.jsp");
	}
	
	private Response inputListResponse(String title, String template) {
		Form form = new Form(Link.get().create(getClass(), c->c.save(null)), true);
		form.setFormMethod("post");
		
		form.addInput(
			InputList.input("simple-list")
			.addInput(
				Text.input("", true).setTitle("Simple list - input 1")
			)
			.addInput(
				Text.input("", true).setTitle("Simple list - input 2")
			)
		);
		
		form.addInput(
			InputList.input("simple-map")
			.addInput(
				Text.input("item1", true).setTitle("Simple map - input 1")
			)
			.addInput(
				Text.input("item2", true).setTitle("Simple map - input 2")
			)
		);

		form.addInput(
			InputList.input("list-in-map")
			.addInput(
				InputList.input("sub-list-1")
				.addInput(
					Text.input("", true).setTitle("List in map - sublist 1 - input 1")
				)
				.addInput(
					Text.input("", true).setTitle("List in map - sublist 1 - input 2")
				)
			)
			.addInput(
				InputList.input("sub-list-2")
				.addInput(
					Text.input("", true).setTitle("List in map - sublist 2 - input 1")
				)
				.addInput(
					Text.input("", true).setTitle("List in map - sublist 2 - input 2")
				)
			)
		);
		
		form.addInput(Submit.create("Submit", "submit"));
		
		form.setBindUrl(Link.get().create(getClass(), c->c.inputListData()));
		
		Map<String, Object> params = new HashMap<>();
		params.put("form", form);
		params.put("title", title);
		return Response.getTemplate(template, params);
	}

	/**
	 * Data for input list
	 * @return http://localhost:8080/examples-form/form/inputlist-data
	 */
	@Action("inputlist-data")
	public Response inputListData() {
		return Response.getJson(
			new MapInit<String, Object>()
			.append("simple-list", Arrays.asList("value01", "value02"))
			.append(
				"simple-map", 
				new MapInit<String, Object>()
				.append("item1", "value11")
				.append("item2", "value12")
				.toMap()
			)
			.append(
				"list-in-map",
				new MapInit<String, Object>()
				.append("sub-list-1", Arrays.asList("value21", "value22"))
				.append("sub-list-2", Arrays.asList("value23", "value24"))
				.toMap()
			)
			.toMap()
		);
	}

	/*****/
	
	/**
	 * Using dynamic input list
	 * @return http://localhost:8080/examples-form/form/dynamiclist
	 */
	@Action("dynamiclist")
	public Response dynamicInputList() {
		return dynamicListResponse("Dynamic list - default print", "inputs.jsp");
	}

	/**
	 * Using dynamic input list with custom template
	 * @return http://localhost:8080/examples-form/form/dynamiclist-template
	 */
	@Action("dynamiclist-template")
	public Response dynamicListTemplate() {
		return dynamicListResponse("Dynamic list - template", "dynamicListTemplate.jsp");
	}

	private Response dynamicListResponse(String title, String template) {
		Form form = new Form(Link.get().create(getClass(), c->c.save(null)), true);
		form.setFormMethod("post");

		form.addInput(
			DynamicList.input("simple-list")
			.addInput(
				Text.input("", true).setTitle("Simple list {i}")
			)
		);
		
		form.addInput(
			DynamicList.input("simple-map")
			.addInput(
				Text.input("item", true).setTitle("Simple map {i}")
			)
		);
		
		form.addInput(Submit.create("Submit", "submit"));
		
		form.setBindUrl(Link.get().create(getClass(), c->c.dynamicListData()));
		
		Map<String, Object> params = new HashMap<>();
		params.put("form", form);
		params.put("title", title);
		return Response.getTemplate(template, params);
	}

	/**
	 * Data for input list
	 * @return http://localhost:8080/examples-form/form/dynamiclist-data
	 */
	@Action("dynamiclist-data")
	public Response dynamicListData() {
		return Response.getJson(
			new MapInit<String, Object>()
			.append("simple-list", Arrays.asList("value-01", "value-02", "value-02"))
			.append(
				"simple-map", 
				new MapInit<String, Object>()
				.append("item_1", "value11")
				.append("item_2", "value12")
				.toMap()
			)
			.toMap()
		);
	}
	
	/**
	 * Dynamic list with custom buttons
	 * @return http://localhost:8080/examples-form/form/dynamiclist-buttons
	 */
	@Action("dynamiclist-buttons")
	public Response dynamicInputListButtons() {
		Form form = new Form(Link.get().create(getClass(), c->c.save(null)), true);
		form.setFormMethod("post");

		form.addInput(
			DynamicList.input("full-buttons")
			.setTitle("Full buttons")
			.addInput(
				Text.input("", true).setTitle("{i}")
			)
		);

		form.addInput(
			DynamicList.input("without-add-button")
			.setTitle("Without add button")
			.useAddButton(false)
			.addInput(
				Text.input("", true).setTitle("{i}")
			)
		);

		form.addInput(
			DynamicList.input("without-remove-buttons")
			.setTitle("Without remove buttons")
			.useRemoveButton(false)
			.addInput(
				Text.input("", true).setTitle("{i}")
			)
		);
		
		form.addInput(Submit.create("Submit", "submit"));
		
		form.setBindUrl(Link.get().create(getClass(), c->c.dynamicListData()));
		
		Map<String, Object> params = new HashMap<>();
		params.put("form", form);
		params.put("title", "DynamicList with custom buttons");
		return Response.getTemplate("inputs.jsp", params);
	}
	
	/************************/
	
	/**
	 * Sync form with CSRF token
	 * @return http://localhost:8080/examples-form/form/csrf
	 */
	@Action("csrf")
	public Response csrf() throws AuthentizationException {
		if (identity.isAnonymous()) {
			// automatic login before page load
			// demonstation requires logged user
			authenticator.login(new User("someuser", new FormPermissions()), identity);
		}
		
		Form form = new Form(Link.get().create(getClass(), c->c.saveCsrf(null)), true);
		form.setCsrfSecured(identity);
		form.setFormMethod("post");
		
		form.addInput(Text.input("textInput", true).setTitle("Text"));
		
		form.addInput(Submit.create("Submit sync", "sync").setAsync(false));
		
		Map<String, Object> params = new HashMap<>();
		params.put("form", form);
		params.put("title", "Sync and async send");
		return Response.getTemplate("inputs.jsp", params);
	}
	
	/**
	 * Save for form testing
	 * @return (POST)http://localhost:8080/examples-form/form/save
	 */
	@Action("save-csrf")
	@Method(HttpMethod.POST)
	@Secured(mode = AuthMode.COOKIE_AND_CSRF)
	public Response saveCsrf(@Params RequestParameters params) {
		authenticator.logout(identity);
		return Response.getText("Secured with CSRF OK: " + params);
	}
	
	/**************/

	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Database database, Logger logger)
			throws Exception {
		FormExampleDao dao = new FormExampleDao();
		register.addFactory(
			FormExample.class,
			(t, identity, auth, authenticator)->new FormExample(dao, identity, authenticator)
		);
		return Arrays.asList();
	}
	
	@Override
	public String getName() {
		return "examples-form";
	}

	@Override
	public String getControllersPath() {
		return "toti/samples/form";
	}
	
	@Override
	public String getTemplatesPath() {
		return "samples/examples/templates/form";
	}
	
	@Override
	public String getTranslationPath() {
		return "toti/translations"; // TODO "samples/examples/form";
	}

}
