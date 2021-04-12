package module.controllers;

import java.util.HashMap;
import java.util.Map;

import common.structures.MapInit;
import common.structures.Tuple2;
import socketCommunication.http.HttpMethod;
import socketCommunication.http.server.RequestParameters;
import toti.annotations.inject.ClientIdentity;
import toti.annotations.inject.Translate;
import toti.annotations.url.Action;
import toti.annotations.url.Controller;
import toti.annotations.url.Method;
import toti.annotations.url.ParamUrl;
import toti.annotations.url.Params;
import toti.annotations.url.Secured;
import toti.security.Identity;
import toti.control.Form;
import toti.control.Grid;
import toti.control.columns.ActionsColumn;
import toti.control.columns.ButtonsColumn;
import toti.control.columns.GroupAction;
import toti.control.columns.ValueColumn;
import toti.control.inputs.Button;
import toti.control.inputs.ButtonType;
import toti.control.inputs.Checkbox;
import toti.control.inputs.Datetime;
import toti.control.inputs.Email;
import toti.control.inputs.File;
import toti.control.inputs.Hidden;
import toti.control.inputs.Number;
import toti.control.inputs.Password;
import toti.control.inputs.RadioList;
import toti.control.inputs.Select;
import toti.control.inputs.Submit;
import toti.control.inputs.Text;
import toti.control.inputs.TextArea;
import toti.response.Response;
import translator.Translator;

@Controller("entity")
public class EntityPageController {
	
	private final static String SECURITY_DOMAIN = "entity";
		
	@Translate
	private Translator translator;
	
	@ClientIdentity
	private Identity identity;
	
	public void setTranslator(Translator translator) {
		this.translator = translator;
	}

	public void setIdentity(Identity identity) {
		this.identity = identity;
	}
	
	@Action("form")
	public Response form() {
		Form form = new Form("/entity/entity/save", true);
		form.setFormMethod("post");
		form.setBindUrl("/entity/entity/bind");
		form.addInput(Text.input("simple", false).setTitle("Simple text"));
		form.addInput(Text.input("xss", false).setTitle("XSS"));
		form.addInput(TextArea.input("area", false).setTitle("Area"));
		form.addInput(Datetime.input("datetimeInput", false).setTitle("Datetime"));
		form.addInput(RadioList.input("radioInput", false, MapInit.hashMap(
			new Tuple2<>("a", "A"),
			new Tuple2<>("b", "B"),
			new Tuple2<>("c", "C")
		)));
		form.addInput(Checkbox.input("no-def-no-override", false).setTitle("no def no override"));
		form.addInput(Checkbox.input("no-def-override", false).setTitle("no def override"));
		form.addInput(Checkbox.input("def-no-override", false).setDefaultValue("true").setTitle("def no overrirde"));
		form.addInput(Checkbox.input("def-override", false).setDefaultValue("true").setTitle("deff overrirde"));
		form.addInput(Select.input("selectInput", false, MapInit.hashMap(
				new Tuple2<>("aa", "AA"),
				new Tuple2<>("bb", "BB"),
				new Tuple2<>("cc", "CC")
			)));
		form.addInput(File.input("pdf", false));
		
		form.addInput(Button.create("/entity/entity/list").setTitle("Back").setConfirmation("Really?"));
		form.addInput(Submit.create("Save", "save").setConfirmation("realy?"));
		form.addInput(Submit.create("Sync", "save2").setConfirmation("realy?").setAsync(false));
		form.addInput(Submit.create("Save and back", "save-back").setRedirect("/entity/entity/list"));
		
		Map<String, Object> params = new HashMap<>();
		params.put("testForm", form);
		params.put("testForm2", form);
		return Response.getTemplate("/Form.jsp", params);
	}
	
	@Action("bind")
	public Response bind() {
		Map<String, Object> params = new HashMap<>();
		params.put("simple", "Lorem ipsum");
		params.put("area", "Text area content");
		params.put("datetimeInput", "2021-02-14 23:35");
		params.put("radioInput", "b");
		params.put("selectInput", "cc");
		params.put("no-def-override", true);
		params.put("def-override", false);
		params.put("xss", "<script>Alert('XSS');</script>");
		
		return Response.getJson(params);
	}
	
	@Action("save")
	public Response save(@Params RequestParameters params/*, @Param("pdf") UploadedFile file*/) {
		params.forEach((key, value)->{
			System.err.println(key + ": " + value + " " + (value == null ? "" : value.getClass()));
		});
		/*try {
			System.err.println(file.getFileName());
			System.err.println(file.getContentType());
			System.err.println(file.getContent().size());
			file.save("www");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return Response.getJson(MapInit.hashMap(new Tuple2<>("id", 1), new Tuple2<>("message", "saved")));
	}
	
	
	@Action("action1")
	@Secured()
	public Response actionMethod1(@Params RequestParameters prop) {
		System.out.println("Action 1 properties:");
		System.out.println(prop);
		return Response.getText("Working 1");
	}
	
	@Action("action2")
	@Method({HttpMethod.POST})
	@Secured(isApi = false)
	public Response actionMethod2(@Params RequestParameters prop) {
		System.out.println("Action 2 properties:");
		System.out.println(prop);
		return Response.getText("Working 2");
	}
	
	@Action("action3")
	@Method({HttpMethod.POST})
	public Response actionMethod3(@Params RequestParameters prop) {
		System.out.println("Action 3 properties:");
		System.out.println(prop);
		Map<String, Object> a = new HashMap<>();
		a.put("text", "Working 3");
		return Response.getJson(a);
	}

	@Action("list")
	//@Secured(isApi = false, value={@Domain(name=SECURITY_DOMAIN, action=helper.Action.READ)})
	public Response grid() {
		Map<String, Object> params = new HashMap<>();
		/***/
		// TODO grid
		Grid grid = new Grid("/entity/api/entity/all", "get");
		grid.addColumn(new ActionsColumn("actions")).addAction(
			new GroupAction("Sync", "/entity/entity/action1").setAjax(false).setMethod("post")
		).addAction(
			new GroupAction("Async with confirmation", "/entity/entity/action2")
				.setAjax(true).setConfirmation("Really?").setMethod("post")
		).addAction(
			new GroupAction("Custom failure and succcess", "/entity/entity/action3").setAjax(true).setMethod("post")
			    .setOnFailure("actionFailure").setOnSuccess("actionSuccess")
		);
		grid.addColumn(new ValueColumn("id").setTitle(translator.translate("module.id")));
		grid.addColumn(
			new ValueColumn("name")
				.setTitle(translator.translate("module.name"))
				.setUseSorting(true)
				.setFilter(Text.filter())
		);
		grid.addColumn(
			new ValueColumn("edited")
				.setTitle(translator.translate("module.edited"))
				.setUseSorting(true)
				.setFilter(Datetime.filter())
		);
		grid.addColumn(
			new ValueColumn("rank")
				.setTitle(translator.translate("module.rank"))
				.setUseSorting(true)
				.setFilter(Number.filter())
		);
		grid.addColumn(
			new ValueColumn("FK_id")
				.setTitle(translator.translate("module.fk-id"))
				.setUseSorting(true)
				.setFilter(Select.filter(MapInit.hashMap(
					MapInit.t("", "---"),
					MapInit.t("1", "Department #1"),
					MapInit.t("2", "Department #2"),
					MapInit.t("3", "Department #3"),
					MapInit.t("4", "Department #4")
				)))
		);
		grid.addColumn(
			new ValueColumn("is_main")
				.setTitle(translator.translate("module.is_main"))
				.setUseSorting(true)
				.setFilter(Select.filter(MapInit.hashMap(
					MapInit.t("", "---"),
					MapInit.t("true", "Yes"),
					MapInit.t("false", "No")
				)))
				.setRenderer("isMainRenderer")
		);
		grid.addColumn(
			new ButtonsColumn("buttons").setTitle(translator.translate("module.buttons"))
				.addButton(
					Button.create("/entity/api/entity/delete/{id}").setAsync(true).setMethod("delete")
						.setTitle("Delete").setConfirmation("Really delete {name}?").setType(ButtonType.DANGER)
				)
				.addButton(
					Button.create("/entity/entity/edit/{id}").setAsync(false).setMethod("get")
						.setTitle("Edit").setType(ButtonType.INFO)
				)
				.addButton(
						Button.create("/entity/entity/detail/{id}").setAsync(false).setMethod("get")
							.setTitle("Detail").setType(ButtonType.SUCCESS)
					)
		);
		/***/
		params.put("control", grid);
		params.put("title", translator.translate("entity-list"));
		return Response.getTemplate("/Entity.jsp", params);
	}
	
	@Action("add")
	//@Secured(isApi = false, value={@Domain(name=SECURITY_DOMAIN, action=helper.Action.CREATE)})
	public Response add() {
		return getOne(null, true);
	}

	@Action("edit")
	//@Secured(isApi = false, value={@Domain(name=SECURITY_DOMAIN, action=helper.Action.UPDATE)})
	public Response edit(@ParamUrl("id") Integer id) {
		return getOne(id, true);
	}

	@Action("detail")
	@Method({HttpMethod.GET})
	//@Secured(isApi = false, value={@Domain(name=SECURITY_DOMAIN, action=helper.Action.READ)})
	public Response detail(@ParamUrl("id") Integer id) {
		return getOne(id, false);
	}
	
	private Response getOne(Integer id, boolean editable) {
		Map<String, Object> params = new HashMap<>();
		String url = "/entity/api/entity/" +  (id == null ? "insert" : "update/" + id);
		/***/
		// TODO form
		Form form = new Form(url, editable);
		form.setFormMethod("put");
		form.addInput(Hidden.input("id"));
		form.addInput(Text.input("name", true).setTitle(translator.translate("module.name")).setMaxLength(50));
		form.addInput(Password.input("secret", true).setTitle(translator.translate("module.secret")).setMaxLength(20));
		form.addInput(Email.input("email", true).setTitle(translator.translate("module.email")));
		form.addInput(Datetime.input("edited", true).setTitle(translator.translate("module.edited")));
		form.addInput(Checkbox.input("is_main", false).setTitle(translator.translate("module.is-main")));
		form.addInput(Number.input("rank", true).setTitle(translator.translate("module.rank")));
		form.addInput(Select.input("FK_id", true, MapInit.hashMap(
			MapInit.t("1", "Department #1"),
			MapInit.t("2", "Department #2"),
			MapInit.t("3", "Department #3"),
			MapInit.t("3", "Department #4")
		)).setTitle("Department"));
		form.addInput(RadioList.input("lang", true, MapInit.hashMap(
			MapInit.t("cs_CZ", "Czech"),
			MapInit.t("en_GB", "English"),
			MapInit.t("de_DE", "German"),
			MapInit.t("sk_SK", "Slovak")
		)).setTitle("Language"));
		form.addInput(TextArea.input("comment", false).setCols(20).setRows(30).setTitle("module.comment").setDefaultValue("aaa"));
		
		form.addInput(Submit.create("Save", "save").setRedirect("/entity/entity/list"));
		form.addInput(Button.create("/entity/entity/list").setTitle("Cancel").setAsync(false));
		
		form.setAfterBind("b");
		form.setBeforeBind("a");
		
		if (id != null) {
			form.setBindMethod("get");
			form.setBindUrl("/entity/api/entity/get/" + id);
		}
		/***/
		params.put("control", form);
		params.put("title", translator.translate("entity-" + (id == null ? "add" : "edit")));
		return Response.getTemplate("/Entity.jsp", params);
	}
	
}
