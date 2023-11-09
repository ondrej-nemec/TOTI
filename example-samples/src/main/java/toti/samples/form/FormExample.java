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
import ji.socketCommunication.http.structures.UploadedFile;
import ji.translator.Translator;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Secured;
import toti.answers.action.ResponseAction;
import toti.answers.action.ResponseBuilder;
import toti.answers.request.AuthMode;
import toti.answers.response.Response;
import toti.answers.router.Link;
import toti.application.Module;
import toti.application.Task;
import toti.application.register.Register;
import toti.ui.control.Form;
import toti.ui.control.inputs.Button;
import toti.ui.control.inputs.Checkbox;
import toti.ui.control.inputs.Color;
import toti.ui.control.inputs.Date;
import toti.ui.control.inputs.Datetime;
import toti.ui.control.inputs.DynamicList;
import toti.ui.control.inputs.Email;
import toti.ui.control.inputs.File;
import toti.ui.control.inputs.Hidden;
import toti.ui.control.inputs.InputList;
import toti.ui.control.inputs.Month;
import toti.ui.control.inputs.Option;
import toti.ui.control.inputs.Password;
import toti.ui.control.inputs.RadioList;
import toti.ui.control.inputs.Range;
import toti.ui.control.inputs.Select;
import toti.ui.control.inputs.Submit;
import toti.ui.control.inputs.SubmitPolicy;
import toti.ui.control.inputs.Text;
import toti.ui.control.inputs.TextArea;
import toti.ui.control.inputs.Time;
import toti.ui.control.inputs.Week;
import toti.ui.control.inputs.Number;
import toti.ui.validation.ItemRules;
import toti.ui.validation.Validator;

/**
 * Example shows using forms
 * @author Ondřej Němec
 *
 */
@Controller("form")
public class FormExample implements Module {
	
	private FormExampleDao dao;
	private Link link;
	//private Authenticator authenticator;
	
	public FormExample() {}
	
	public FormExample(FormExampleDao dao, Link link/*, Authenticator authenticator*/) {
		this.dao = dao;
		this.link = link;
	//	this.authenticator = authenticator;
	}
	
	/**
	 * Display inputs
	 * @return http://localhost:8080/examples-form/form/inputs/{0-100}
	 */
	@Action(path="inputs")
	public ResponseAction inputsForm(int i) {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return getInputs(i, true, "All inputs - Bind");
		});
	}
	
	/**
	 * Display form as detail - not editable
	 * @return http://localhost:8080/examples-form/form/detail/{0-100}
	 */
	@Action(path="detail")
	public ResponseAction inputsNotEditable(int i) {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return getInputs(i, false, "All inputs - Not editable");
		});
	}
	
	/**
	 * Display inputs
	 * @return http://localhost:8080/examples-form/form/insert
	 */
	@Action(path="insert")
	public ResponseAction allInputsSave() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return getInputs(null, true, "All inputs - No bind");
		});
	}
	
	/**
	 * Get data for form
	 * @return http://localhost:8080/examples-form/form/get/{0-100}
	 */
	@Action(path="get")
	public ResponseAction allInputsGet(int i) {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getJson(dao.get(i));
		});
	}
	
	/**
	 * Save data from form
	 * @return http://localhost:8080/examples-form/form/save-inputs/{0-100}
	 */
	@Action(path="save-inputs")
	public ResponseAction allInputs(Integer index) {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			FormExampleEntity entity = req.getBodyParams().parse(FormExampleEntity.class);
			int id = dao.update(index, entity);
			return Response.OK().getJson(new MapInit<>().append("id", id).append("message", "Saved").toMap());
		});
	}
	
	private Response getInputs(Integer i, boolean editable, String title) {
		Form form = new Form(link.create(getClass(), c->c.allInputs(0), i), editable);
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
			.setRedirect(link.create(getClass(), c->c.inputsForm(0), i))
		);
		if (i != null) {
			form.setBindUrl(link.create(getClass(), c->c.allInputsGet(0), i));
			form.setBindMethod("get");
		}
		Map<String, Object> params = new HashMap<>();
		params.put("form", form);
		params.put("title", title);
		return Response.OK().getTemplate("inputs.jsp", params);
	}
	
	/**
	 * Display inputs with placeholders
	 * @return http://localhost:8080/examples-form/form/placeholders/{0-100}
	 */
	@Action(path="placeholders")
	public ResponseAction placeholders(int i) {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{

			Form form = new Form(link.create(getClass(), c->c.allInputs(0), i), true);
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
				.setRedirect(link.create(getClass(), c->c.inputsForm(0), i))
			);
			
			form.setPlaceholderUrl(link.create(getClass(), c->c.allInputsGet(0), i));
			form.setPlaceholderMethod("get");
			
			Map<String, Object> params = new HashMap<>();
			params.put("form", form);
			params.put("title", "Form - load placeholders");
			return Response.OK().getTemplate("inputs.jsp", params);
		});
	}
	
	/*******/
	
	/**
	 * Submit returns 400 with errors
	 * @return http://localhost:8080/examples-form/form/validated
	 */
	@Action(path="validated")
	public ResponseAction validated() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Form form = new Form(link.create(getClass(), c->c.validateSave()), true);
			form.addInput(
				Text.input("text", true).setTitle("Text input")
				.setDefaultValue("Some text to submit")
			);

			form.addInput(
				Text.input("not-expected", true).setTitle("Not expected Text input")
				.setDefaultValue("Not expected")
			);
			form.addInput(
				Submit.create("Save", "save")
				.setRedirect(link.create(getClass(), c->c.validated()))
			);
			Map<String, Object> params = new HashMap<>();
			params.put("form", form);
			params.put("title", "With validator");
			return Response.OK().getTemplate("inputs.jsp", params);
		});
	}
	
	/**
	 * Save data from validated form
	 * @return http://localhost:8080/examples-form/form/validate-save/{0-100}
	 */
	@Action(path = "validate-save")
	public ResponseAction validateSave() {
		return ResponseBuilder.get()
		.validate(
			new Validator(true)
			.addRule(ItemRules.objectRules("text", true).setMaxLength(10).setMinLength(5))
			.addRule(ItemRules.objectRules("missing-text", true))
		)
		.createRequest((req, translator, identity)->{
			return Response.OK().getText("NOK - this could not happends");
		});
	}
	
	/******/
	
	/**
	 * Form with sync and async submit
	 * @return http://localhost:8080/examples-form/form/async
	 */
	@Action(path="async", methods=HttpMethod.GET)
	public ResponseAction asyncForm() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Form form = new Form(link.create(getClass(), c->c.save()), true);
			form.setFormMethod("post");
			
			form.addInput(Text.input("textInput", true).setTitle("Text"));
			
			form.addInput(Submit.create("Submit sync", "sync").setAsync(false));
			form.addInput(Submit.create("Submit async", "async").setAsync(true));
			
			Map<String, Object> params = new HashMap<>();
			params.put("form", form);
			params.put("title", "Sync and async send");
			return Response.OK().getTemplate("inputs.jsp", params);
		});
	}
	
	/**
	 * Form with sync and async submit
	 * @return http://localhost:8080/examples-form/form/submit-modes
	 */
	@Action(path="submit-modes", methods=HttpMethod.GET)
	public ResponseAction submitModes() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Form form = new Form(link.create(getClass(), c->c.save()), true);
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
			return Response.OK().getTemplate("inputs.jsp", params);
		});
	}
	
	/******/

	/**
	 * Example of buttons
	 * @return http://localhost:8080/examples-form/form/buttons
	 */
	@Action(path="buttons")
	public ResponseAction buttons() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Form form = new Form(link.create(getClass(), c->c.save()), true);
			form.setFormMethod("post");
			// setCondition is not supported for button in form
			form.addInput(
				Button.create(
					link.create(
						getClass(),
						c->c.notSave(),
						MapInit.create().append("urlParam", "URL param").toMap()
					), 
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
					link.create(
						getClass(),
						c->c.notSave(),
						MapInit.create().append("urlParam", "URL param").toMap()
					),
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
				.setRedirect(link.create(
					getClass(), c->c.notSave(),
					MapInit.create().append("redirected", true).toMap()
				))
				.setAsync(true)
			);
			
			Map<String, Object> params = new HashMap<>();
			params.put("form", form);
			params.put("title", "Buttons");
			return Response.OK().getTemplate("inputs.jsp", params);
		});
	}
	
	/**
	 * Link for not save
	 * @return http://localhost:8080/examples-form/form/notsave
	 */
	@Action(path="notsave")
	public ResponseAction notSave() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getText("OK: " + req.getBodyParams());
		});
	}
	
	/**
	 * Save for form testing
	 * @return (POST)http://localhost:8080/examples/form/save
	 */
	@Action(path="save", methods=HttpMethod.POST)
	public ResponseAction save() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getText("OK: " + req.getBodyParams());
		});
	}
	
	/**
	 * Form printed with template
	 * @return http://localhost:8080/examples-form/form/template
	 */
	@Action(path="template")
	public ResponseAction template() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return templateResponse("Editable", true);
		});
	}
	
	/**
	 * Form printed with template - detail mode
	 * @return http://localhost:8080/examples-form/form/template-detail
	 */
	@Action(path="template-detail")
	public ResponseAction templateDetail() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return templateResponse("Detail", false);
		});
	}
	
	private Response templateResponse(String title, boolean editable) {
		Form form = new Form(link.create(getClass(), c->c.templateSave()), editable);
		form.setFormMethod("post");
		
		form.setBindUrl(link.create(getClass(), c->c.templateData()));
		
		form.addInput(Text.input("textInput", true).setTitle("Text"));
		form.addInput(Number.input("numberInput", false).setTitle("Number"));
		form.addInput(Select.input("selectInput", true, Arrays.asList(
			Option.create("a", "A"),
			Option.create("b", "B"),
			Option.create("c", "C")
		)).setTitle("Selectbox"));
		form.addInput(Checkbox.input("checkboxInput", false).setTitle("Checbox"));
		form.addInput(Text.input("notExpected", false).setTitle("Validator not expect this parameter"));
		
		form.addInput(Submit.create("Submit", "submit1"));
		form.addInput(Submit.create("Another submit", "submit2"));
		
		Map<String, Object> params = new HashMap<>();
		params.put("form", form);
		params.put("title", "Form template " + title);
		return Response.OK().getTemplate("template.jsp", params);
	}

	/**
	 * Data for template
	 * @return http://localhost:8080/examples-form/form/template-data
	 */
	@Action(path="template-data")
	public ResponseAction templateData() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getJson(
				new MapInit<String, Object>()
				.append("textInput", "Some text")
				.append("numberInput", 42)
				.append("selectInput", "b")
				.append("checkboxInput", true)
				.append("notExpected", "Not expected")
				.toMap()
			);
		});
	}
	
	/**
	 * Save for template form testing
	 * @return (POST)http://localhost:8080/examples/form/template-save
	 */
	@Action(path = "template-save", methods=HttpMethod.POST)
	public ResponseAction templateSave() {
		return ResponseBuilder.get()
		.validate(new Validator(true)
			.addRule(ItemRules.objectRules("textInput", true).setMaxLength(10))
			.addRule(ItemRules.objectRules("numberInput", true).setMinLength(2).setMaxLength(4))
		)
		.createRequest((req, translator, identity)->{
			return Response.OK().getText("OK: " + req.getBodyParams());
		});
	}
	
	/**
	 * Show select options: load, depends
	 * @return http://localhost:8080/examples-form/form/select
	 */
	@Action(path="select", methods=HttpMethod.GET)
	public ResponseAction select() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Form form = new Form(link.create(getClass(), c->c.save()), true);
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
				.setLoadData(link.create(getClass(), c->c.loadToSelect()), "get")
			);
			form.addInput(
				Select.input("load-with-def", false, defOptions)
				.setTitle("Load with default options")
				.setLoadData(link.create(getClass(), c->c.loadToSelect()), "get")
				.setDefaultValue("3")
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
				.setLoadData(link.create(getClass(), c->c.loadToSelect()), "get")
				.setTitle("Depends on previous input")
				.setDepends("[name=groups]")
			);
			// TODO depends on text, radio

			form.addInput(
				Select.input("selected-group", false, defOptions)
				.setLoadData(link.create(getClass(), c->c.loadToSelect()), "get")
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
				.setLoadData(link.create(getClass(), c->c.loadToSelect()), "get")
				.setTitle("Depends on previous input - selected value")
				.setDepends("[name=groupsSelected]")
			);

			form.addInput(
				Select.input("load-with-def-value", false, defOptions)
				.setTitle("Load with default options and default value")
				.setLoadData(link.create(getClass(), c->c.loadToSelect()), "get")
				.setDefaultValue(5)
			);
			
			
			form.addInput(
				Select.input("tree-structure", false, Arrays.asList(
					Option.create("0", "Option 0"),
					Option.create("-1", "Option 0x1").setOptGroup("0"),
					Option.create("-2", "Option 0x2").setOptGroup("0")
				))
				.setTitle("Options in tree structure")
				.setLoadData(link.create(getClass(), c->c.loadTreeOptions()), "get")
				.setSelfReference(true)
			);
			
			form.addInput(Submit.create("Submit", "submit"));
			
			Map<String, Object> params = new HashMap<>();
			params.put("form", form);
			params.put("title", "Usage of select option loading");
			return Response.OK().getTemplate("inputs.jsp", params);
		});
	}

	/**
	 * Options for select
	 * @return http://localhost:8080/examples-form/form/load-select
	 */
	@Action(path="load-select")
	public ResponseAction loadToSelect() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getJson(Arrays.asList(
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
		});
	}

	/**
	 * Tree options for select
	 * @return http://localhost:8080/examples-form/form/load-tree-options
	 */
	@Action(path="load-tree-options")
	public ResponseAction loadTreeOptions() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getJson(Arrays.asList(
					new MapInit<String, Object>()
						.append("value", "1")
						.append("title", "Option 1")
						.toMap(),
					new MapInit<String, Object>()
						.append("value", "11")
						.append("title", "Option 1x1")
						.append("optgroup", "1")
						.toMap(),
					new MapInit<String, Object>()
						.append("value", "12")
						.append("title", "Option 1x2")
						.append("optgroup", "1")
						.toMap(),
					new MapInit<String, Object>()
						.append("value", "121")
						.append("title", "Option 1x2x1")
						.append("optgroup", "12")
						.toMap(),
					new MapInit<String, Object>()
						.append("value", "122")
						.append("title", "Option 1x3")
						.append("optgroup", "12")
						.toMap(),
					new MapInit<String, Object>()
						.append("value", "2")
						.append("title", "Option 2")
						.toMap(),
					new MapInit<String, Object>()
						.append("value", "3")
						.append("title", "Option 3")
						.toMap(),
					new MapInit<String, Object>()
						.append("value", "31")
						.append("title", "Option 3x1")
						.append("optgroup", "3")
						.toMap(),
					new MapInit<String, Object>()
						.append("value", "32")
						.append("title", "Option 3x2")
						.append("optgroup", "3")
						.toMap()
				));
		});
	}

	/**
	 * Options for select
	 * @return http://localhost:8080/examples-form/form/load-optgroup
	 */
	@Action(path="load-optgroup")
	public ResponseAction loadGroup() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getJson(Arrays.asList(
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
		});
	}
	
	/********/
	
	/**
	 * Shows options of datetime
	 * @return http://localhost:8080/examples-form/form/datetime
	 */
	@Action(path="datetime")
	public ResponseAction dateTimeStrict() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Form form = new Form(link.create(getClass(), c->c.save()), true);
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
			return Response.OK().getTemplate("inputs.jsp", params);
		});
	}
	
	/********/
	
	/**
	 * Shows extended inputs
	 * @return http://localhost:8080/examples-form/form/optional
	 */
	@Action(path="optional")
	public ResponseAction optionalInputs() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Form form = new Form(link.create(getClass(), c->c.save()), true);
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
			return Response.OK().getTemplate("inputs.jsp", params);
		});
	}
	
	/********/
	
	/**
	 * Shows checkbox options
	 * @return http://localhost:8080/examples-form/form/checkbox
	 */
	@Action(path="checkbox")
	public ResponseAction checkbox() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			// checkbox renderer works only if form or input is not editable
			Form form = new Form(link.create(getClass(), c->c.save()), false);
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
			return Response.OK().getTemplate("inputs.jsp", params);
		});
	}
	
	/********/
	
	/**
	 * Usage and disabled
	 * @return http://localhost:8080/examples-form/form/exclude
	 */
	@Action(path="exclude")
	public ResponseAction exclude() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Form form = new Form(link.create(getClass(), c->c.save()), true);
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
			return Response.OK().getTemplate("inputs.jsp", params);
		});
	}
	
	/********/
	
	/**
	 * Not editable form with editable input
	 * @return http://localhost:8080/examples-form/form/not-editable
	 */
	@Action(path="not-editable")
	public ResponseAction notEditable() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Form form = new Form(link.create(getClass(), c->c.save()), false);
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
			return Response.OK().getTemplate("inputs.jsp", params);
		});
	}
	
	/**
	 * Not editable form with editable input
	 * @return http://localhost:8080/examples-form/form/editable
	 */
	@Action(path="editable")
	public ResponseAction editable() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Form form = new Form(link.create(getClass(), c->c.save()), true);
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
			return Response.OK().getTemplate("inputs.jsp", params);
		});
	}
	
	/*****/
	
	/**
	 * Redirect after async form submit
	 * @return http://localhost:8080/examples-form/form/redirect
	 */
	@Action(path="redirect")
	public ResponseAction submitWithRedirect() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Form form = new Form(link.create(getClass(), c->c.formRedirectSave()), true);
			form.setFormMethod("post");

			form.addInput(
				Text.input("some-input", false)
				.setTitle("Some input")
			);
			form.addInput(
				Submit.create("Submit", "submit")
				.setRedirect(
					link.create(
						getClass(), c->c.afterRedirect(),
						MapInit.create().append("someParam", "{id}").toMap()
					)
				)
			);
			
			Map<String, Object> params = new HashMap<>();
			params.put("form", form);
			params.put("title", "Submit redirect");
			return Response.OK().getTemplate("inputs.jsp", params);
		});
	}
	
	/**
	 * For after form submit
	 * @return http://localhost:8080/examples-form/form/after-redirect-save
	 */
	@Action(path="after-redirect-save")
	public ResponseAction formRedirectSave() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Map<String, Object> json = new HashMap<>();
			json.put("id", req.getBodyParams().getString("some-input"));
			json.put("message", "Saved");
			return Response.OK().getJson(json);
		});
	}
	
	/**
	 * For after form submit
	 * @return http://localhost:8080/examples-form/form/after-redirect
	 */
	@Action(path="after-redirect")
	public ResponseAction afterRedirect() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getText("OK: " + req.getBodyParams());
		});
	}

	/*****/
	
	/**
	 * Redirect after async form submit
	 * @return http://localhost:8080/examples-form/form/file
	 */
	@Action(path="file")
	public ResponseAction file() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Form form = new Form(link.create(getClass(), c->c.fileSave()), true);
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
			return Response.OK().getTemplate("inputs.jsp", params);
		});
	}
	
	/**
	 * Redirect after async form submit
	 * @return http://localhost:8080/examples-form/form/file-save
	 */
	@Action(path="file-save")
	public ResponseAction fileSave() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			UploadedFile file = req.getBodyParams().getUploadedFile("fileInput");
			return Response.OK().getText("OK: " + file.getFileName() + " " + file.getContent().length + "b");
		});
	}
	
	/*****/
	
	/**
	 * Using form callbaks
	 * @return http://localhost:8080/examples-form/form/callbacks
	 */
	@Action(path="callbacks")
	public ResponseAction callbacks() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Form form = new Form(link.create(getClass(), c->c.allInputs(0), 10), true);
			form.setBeforeRender("beforeRenderCallback");
			form.setAfterRender("afterRenderCallback");
			
			form.setBeforeBind("beforeBindCallback");
			form.setAfterBind("afterBindCallback");
			
			form.addInput(Text.input("text", true).setTitle("Text input"));
			form.setBindUrl(link.create(getClass(), c->c.allInputsGet(0), 10));
			form.setBindMethod("get");
			
			Map<String, Object> params = new HashMap<>();
			params.put("form", form);
			params.put("title", "Callbacks");
			return Response.OK().getTemplate("inputs.jsp", params);
		});
	}

	/*****/
	
	/**
	 * Using input list
	 * @return http://localhost:8080/examples-form/form/inputlist
	 */
	@Action(path="inputlist")
	public ResponseAction inputList() {
		return inputListResponse("Input list - default print", "inputs.jsp");
	}

	/**
	 * Using input list with custom template
	 * @return http://localhost:8080/examples-form/form/inputlist-template
	 */
	@Action(path="inputlist-template")
	public ResponseAction inputListTemplate() {
		return inputListResponse("Input list - template", "inputListTemplate.jsp");
	}
	
	private ResponseAction inputListResponse(String title, String template) {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Form form = new Form(link.create(getClass(), c->c.save()), true);
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
			
			form.setBindUrl(link.create(getClass(), c->c.inputListData()));
			
			Map<String, Object> params = new HashMap<>();
			params.put("form", form);
			params.put("title", title);
			return Response.OK().getTemplate(template, params);
		});
	}

	/**
	 * Data for input list
	 * @return http://localhost:8080/examples-form/form/inputlist-data
	 */
	@Action(path="inputlist-data")
	public ResponseAction inputListData() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getJson(
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
		});
	}

	/*****/
	
	/**
	 * Using dynamic input list
	 * @return http://localhost:8080/examples-form/form/dynamiclist
	 */
	@Action(path="dynamiclist")
	public ResponseAction dynamicInputList() {
		return dynamicListResponse("Dynamic list - default print", "inputs.jsp", true);
	}

	/**
	 * Using dynamic input list with custom template
	 * @return http://localhost:8080/examples-form/form/dynamiclist-template
	 */
	@Action(path="dynamiclist-template")
	public ResponseAction dynamicListTemplate() {
		return dynamicListResponse("Dynamic list - template", "dynamicListTemplate.jsp", true);
	}

	/**
	 * Using dynamic input list with custom template
	 * @return http://localhost:8080/examples-form/form/dynamiclist-detail
	 */
	@Action(path="dynamiclist-detail")
	public ResponseAction dynamicListDetail() {
		return dynamicListResponse("Dynamic list - detail", "inputs.jsp", false);
	}

	private ResponseAction dynamicListResponse(String title, String template, boolean editable) {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Form form = new Form(link.create(getClass(), c->c.save()), editable);
			form.setFormMethod("post");

			form.addInput(
				DynamicList.input("simple-list")
				.addInput(
					Text.input("", true).setTitle("Simple list {i}")
				)
				.setTitle("Simple list")
			);
			
			form.addInput(
				DynamicList.input("map-of-map")
				.addInput(
					InputList.input("{i}")
					.addInput(
						Text.input("a", true).setTitle(" {i} - A")
					)
					.addInput(
						Text.input("b", true).setTitle(" {i} - B")
					)
					.addInput(
						Text.input("c", true).setTitle(" {i} - C")
					)
				)
			);
			
			form.addInput(
				DynamicList.input("map-of-list")
				.addInput(
					InputList.input("{i}")
					.addInput(
						Text.input("", true).setTitle(" {i} - 1")
					)
					.addInput(
						Text.input("", true).setTitle(" {i} - 2")
					)
					.addInput(
						Text.input("", true).setTitle(" {i} - 3")
					)
				)
			);
			
			form.addInput(
				DynamicList.input("with-select")
				.addInput(
					Select.input("{i}", false, Arrays.asList())
					.setTitle("{i}")
					.setLoadData(link.create(getClass(), c->c.dynamicListLoadGroupsSelectOptions()), "get")
				)
			);
			
			form.addInput(Submit.create("Submit", "submit"));
			
			form.setBindUrl(link.create(getClass(), c->c.dynamicListData()));
			
			Map<String, Object> params = new HashMap<>();
			params.put("form", form);
			params.put("title", title);
			return Response.OK().getTemplate(template, params);
		});
	}

	/**
	 * Data for input list
	 * @return http://localhost:8080/examples-form/form/dynamiclist-data
	 */
	@Action(path="dynamiclist-data")
	public ResponseAction dynamicListData() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getJson(
				new MapInit<String, Object>()
				.append("simple-list", Arrays.asList("value-01", "value-02", "value-03"))
				.append(
					"map-of-map", 
					new MapInit<String, Object>()
					.append(
						"1",
						new MapInit<String, Object>()
							.append("a", "A 1")
							.append("b", "B 1")
							.append("c", "C 1")
							.toMap()
					)
					.append(
						"2",
						new MapInit<String, Object>()
							.append("a", "A 2")
							.append("b", "B 2")
							.append("c", "C 2")
							.toMap()
					)
					.toMap()
				)
				.append(
					"map-of-list", 
					new MapInit<String, Object>()
					.append("1", Arrays.asList("Item 1 A", "Item 1 B", "Item 1 C"))
					.append("2", Arrays.asList("Item 2 A", "Item 2 B", "Item 2 C"))
					.toMap()
				)
				.toMap()
			);
		});
	}
	
	/**
	 * Dynamic list with custom buttons
	 * @return http://localhost:8080/examples-form/form/dynamiclist-buttons
	 */
	@Action(path="dynamiclist-buttons")
	public ResponseAction dynamicInputListButtons() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Form form = new Form(link.create(getClass(), c->c.save()), true);
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
			
			form.setBindUrl(link.create(getClass(), c->c.dynamicListData()));
			
			Map<String, Object> params = new HashMap<>();
			params.put("form", form);
			params.put("title", "DynamicList with custom buttons");
			return Response.OK().getTemplate("inputs.jsp", params);
		});
	}

	/**
	 * Using dynamic input list with custom template
	 * @return http://localhost:8080/examples-form/form/dynamiclist-load
	 */
	@Action(path="dynamiclist-load")
	public ResponseAction dynamicLoaded() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			Form form = new Form(link.create(getClass(), c->c.save()), true);
			form.setFormMethod("post");

			form.addInput(
				DynamicList.input("dynamic-load")
				.setLoadData(link.create(getClass(), c->c.dynamicListLoadGroups()), "get")
				.addInput(
					Select.input("{i}", false, Arrays.asList())
					.setTitle("{i}")
					.setLoadData(link.create(getClass(), c->c.dynamicListLoadGroupsSelectOptions()), "get")
				)
			);
			// TODO improve - add text inputs
			
			form.addInput(Submit.create("Submit", "submit"));
			
			form.setBindUrl(link.create(getClass(), c->c.dynamicListLoadData()));
			
			Map<String, Object> params = new HashMap<>();
			params.put("form", form);
			params.put("title", "Dynamic list loaded");
			return Response.OK().getTemplate("inputs.jsp", params);
		});
	}

	/**
	 * Data for loaded dynamic list
	 * @return http://localhost:8080/examples-form/form/dynamiclist-load-groups
	 */
	@Action(path="dynamiclist-load-groups")
	public ResponseAction dynamicListLoadGroups() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getJson(
				Arrays.asList(
					new MapInit<String, Object>()
						.append("value", "group1")
						.append("title", "Group 1")
						.toMap(),
					new MapInit<String, Object>()
						.append("value", "group2")
						.append("title", "Group 2")
						.toMap(),
					new MapInit<String, Object>()
						.append("value", "group3")
						.append("title", "Group 3")
						.toMap()
				)
			);
		});
	}

	/**
	 * Data for loaded dynamic input list
	 * @return http://localhost:8080/examples-form/form/dynamiclist-load-bind
	 */
	@Action(path="dynamiclist-load-bind")
	public ResponseAction dynamicListLoadData() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getJson(
				new MapInit<String, Object>()
				.append(
					"dynamic-load",
					new MapInit<String, Object>()
					.append("group1", "a1")
					.append("group2", "b2")
					.append("group3", "c2")
					.toMap()
				)
				.toMap()
			);
		});
	}

	/**
	 * Data for select in loaded dynamic list
	 * @return http://localhost:8080/examples-form/form/dynamiclist-load-select
	 */
	@Action(path="dynamiclist-load-select")
	public ResponseAction dynamicListLoadGroupsSelectOptions() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getJson(
				Arrays.asList(
					new MapInit<String, Object>()
						.append("value", "a1")
						.append("title", "A1")
						.append("optgroup", "group1")
						.toMap(),
					new MapInit<String, Object>()
						.append("value", "a2")
						.append("title", "A2")
						.append("optgroup", "group1")
						.toMap(),
					new MapInit<String, Object>()
						.append("value", "b1")
						.append("title", "B1")
						.append("optgroup", "group2")
						.toMap(),
					new MapInit<String, Object>()
						.append("value", "b2")
						.append("title", "B2")
						.append("optgroup", "group2")
						.toMap(),
					new MapInit<String, Object>()
						.append("value", "c1")
						.append("title", "C1")
						.append("optgroup", "group3")
						.toMap(),
					new MapInit<String, Object>()
						.append("value", "c2")
						.append("title", "C2")
						.append("optgroup", "group3")
						.toMap()
				)
			);
		});
	}

	/**
	 * Data for input list
	 * @return http://localhost:8080/examples-form/form/dynamiclist-data
	 */
	/*@Action(path="dynamiclist-data")
	public ResponseAction dynamicListData() {
		return Response.OK().getJson(
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
	}*/
	
	/************************/
	
	/**
	 * Sync form with CSRF token
	 * @return http://localhost:8080/examples-form/form/csrf
	 */
	@Action(path="csrf")
	public ResponseAction csrf() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			if (identity.isAnonymous()) {
				// automatic login before page load
				// demonstation requires logged user
				// TODO authenticator.login(new User("someuser", new FormPermissions()), identity);
			}
			
			Form form = new Form(link.create(getClass(), c->c.saveCsrf()), true);
			form.setCsrfSecured(identity);
			form.setFormMethod("post");
			
			form.addInput(Text.input("textInput", true).setTitle("Text"));
			
			form.addInput(Submit.create("Submit sync", "sync").setAsync(false));
			
			Map<String, Object> params = new HashMap<>();
			params.put("form", form);
			params.put("title", "Sync and async send");
			return Response.OK().getTemplate("inputs.jsp", params);
		});
	}
	
	/**
	 * Save for form testing
	 * @return (POST)http://localhost:8080/examples-form/form/save
	 */
	@Action(path="save-csrf", methods=HttpMethod.POST)
	@Secured(AuthMode.COOKIE_AND_CSRF)
	public ResponseAction saveCsrf() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			// TODO authenticator.logout(identity);
			return Response.OK().getText("Secured with CSRF OK: " + req.getBodyParams());
		});
	}
	
	/**************/

	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Link link, Database database, Logger logger)
			throws Exception {
		FormExampleDao dao = new FormExampleDao();
		register.addFactory(
			FormExample.class,
			()->new FormExample(dao, link)
		);
		return Arrays.asList();
	}
	
	@Override
	public String getName() {
		return "examples-form";
	}
	
	@Override
	public String getTemplatesPath() {
		return "examples/samples/templates/form";
	}

}
