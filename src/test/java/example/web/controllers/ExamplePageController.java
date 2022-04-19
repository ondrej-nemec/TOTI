package example.web.controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ji.common.structures.MapInit;
import example.web.controllers.api.ExampleApiController;
import ji.socketCommunication.http.HttpMethod;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Domain;
import toti.annotations.Method;
import toti.annotations.Param;
import toti.annotations.ParamUrl;
import toti.annotations.Secured;
import toti.control.Form;
import toti.control.Grid;
import toti.control.columns.ButtonsColumn;
import toti.control.columns.ValueColumn;
import toti.control.inputs.Button;
import toti.control.inputs.ButtonType;
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
import toti.control.inputs.Reset;
import toti.control.inputs.Select;
import toti.control.inputs.Submit;
import toti.control.inputs.Text;
import toti.control.inputs.TextArea;
import toti.control.inputs.Time;
import toti.control.inputs.Week;
import toti.response.Response;
import toti.security.AuthMode;
import toti.url.Link;
import ji.translator.Translator;

@Controller("example")
public class ExamplePageController {

	private final static String SECURITY_DOMAIN = "example";
	private final static String JSP_PAGE = "Example.jsp";

	private Translator translator;
		
	public ExamplePageController(Translator translator) {
		this.translator = translator;
	}
	
	@Action("list")
	@Secured(mode = AuthMode.COOKIE, value={@Domain(name=SECURITY_DOMAIN, action=toti.security.Action.READ)})
	public Response grid() {
		Map<String, Object> params = new HashMap<>();
	//	Grid grid = new Grid("/example-module/api/example/all", "get");
		Grid grid = new Grid(Link.get().create(ExampleApiController.class, c->c.getAll(null)), "get");
		// HERE
		grid.addColumn(new ValueColumn("id").setTitle(translator.translate("grid.id", new MapInit<String, Object>("a", "aa").toMap())));
		grid.addColumn(new ValueColumn("name").setTitle("Name").setFilter(Text.filter()).setUseSorting(true));
		grid.addColumn(
			new ValueColumn("age").setTitle("Age").setFilter(Number.filter()).setUseSorting(true)
		);
		grid.addColumn(
			new ValueColumn("active").setTitle("Active").setFilter(Select.filter( Arrays.asList(
				Option.create("", "---"),
				Option.create("true", "YES"),
				Option.create("false", "NO")
			))).setUseSorting(true)
		);
		grid.addColumn(
			new ValueColumn("parent").setTitle("Parent").setFilter(Select.filter( Arrays.asList(
					Option.create("", "---")
			)).setLoadData(Link.get().create(ExampleApiController.class, c->c.getInArray(null)), "get")).setUseSorting(true)
			// )).setLoadData("/example-module/api/example/help", "get")).setUseSorting(true)
		);
		
		grid.addColumn(
			new ValueColumn("simple_date").setTitle("Simple date").setFilter(Date.filter()).setUseSorting(true)
		);
		grid.addColumn(
			new ValueColumn("dt_local").setTitle("Datetime local").setFilter(Datetime.filter()).setUseSorting(true)
		);
		grid.addColumn(
				new ValueColumn("dt_local").setTitle("Datetime local").setFilter(Datetime.filter().setStrict(true)).setUseSorting(true)
			);
		grid.addColumn(
			new ValueColumn("month").setTitle("Month").setFilter(Month.filter()).setUseSorting(true)
		);
		grid.addColumn(
			new ValueColumn("week").setTitle("Week").setFilter(Week.filter()).setUseSorting(true)
		);
		grid.addColumn(
			new ValueColumn("time").setTitle("Time").setFilter(Time.filter()).setUseSorting(true)
		);
		
		// COMMON
		grid.addColumn(new ButtonsColumn("Actions")
			.addButton(Button.create(Link.get().addUrlParam("{id}").create(ExamplePageController.class, c->c.detail(null, null)), "edit").setTitle("Edit").setType(ButtonType.WARNING))
			//.addButton(Button.create("/example-module/example/edit/{id}", "edit").setTitle("Edit").setType(ButtonType.WARNING))
			.addButton(Button.create("/example-module/example/detail/{id}", "detail").setTitle("Detail").setType(ButtonType.INFO))
			
			.addButton(Button.create("/example-module/example/edit/{id}?template=2", "edit").setTitle("Edit").setType(ButtonType.WARNING))
			.addButton(Button.create("/example-module/example/detail/{id}?template=2", "detail").setTitle("Detail").setType(ButtonType.INFO))
			
			.addButton(
				Button.create("/example-module/example/delete/{id}", "delete")
					.setTitle("Delete").setType(ButtonType.DANGER)
					.setMethod("delete").setAsync(true).setConfirmation("Really delete {name}?")
					.setCondition("colCondition")
			)
			
			.addButton(
					Button.create("/example-module/api/example/test", "test").setMethod("post")
					.setTitle("Last button")
					.setCondition("!{active}", true)
					.addRequestParam("a", "a").addRequestParam("b", "bb").setAsync(true)
			)
		);
		// END
		params.put("exampleControl", grid);
		params.put("title", translator.translate("messages.example-list"));
		return Response.getTemplate(JSP_PAGE, params);
	}
	
	@Action("add")
	@Secured(mode = AuthMode.COOKIE, value={@Domain(name=SECURITY_DOMAIN, action=toti.security.Action.CREATE)})
	public Response add() {
		return getOne(null, true, null);
	}

	@Action("edit")
	@Secured(mode = AuthMode.COOKIE, value={@Domain(name=SECURITY_DOMAIN, action=toti.security.Action.UPDATE)})
	public Response edit(@ParamUrl("id") Integer id, @Param("template") String template) {
		return getOne(id, true, template);
	}

	@Action("detail")
	@Method({HttpMethod.GET})
	@Secured(mode = AuthMode.COOKIE, value={@Domain(name=SECURITY_DOMAIN, action=toti.security.Action.READ)})
	public Response detail(@ParamUrl("id") Integer id, @Param("template") String template) {
		return getOne(id, false, template);
	}
	
	private Response getOne(Integer id, boolean editable, String template) {
		Map<String, Object> params = new HashMap<>();
		String url = "/example-module/api/example/" +  (id == null ? "insert" : "update/" + id);
		Form form = new Form(url, editable);
		form.setFormMethod("put");
		// HERE
		form.addInput(Hidden.input("id"));
		form.addInput(Text.input("name", false).setTitle("Name"));
		form.addInput(Email.input("email", false).setTitle("Email"));
		form.addInput(Number.input("age", false).setTitle("Age").setMax(100).setMin(0).setDefaultValue(18));
		form.addInput(Password.input("pasw", false).setTitle("Password"));
		form.addInput(Range.input("range", false).setTitle("Range").setMax(100).setMin(0).setDefaultValue(20));
		
		form.addInput(Checkbox.input("active", false).setTitle("Active"));
		form.addInput(Checkbox.input("defvalue", false).setTitle("DefValue").setDefaultValue(true));
		form.addInput(RadioList.input("sex", false, new MapInit<String, String>()
			.append("male", "Male")
			.append("female", "Feale")
			.toMap()
		).setTitle("Sex").setDefaultValue("female"));
		form.addInput(Select.input("parent", false, Arrays.asList(
			Option.create("", "---")
		)).setTitle("Parent").setLoadData("/example-module/api/example/help", "get"));
		form.addInput(Select.input("select1", false, Arrays.asList(
			Option.create("1", "A1"),
			Option.create("2", "A2"),
			Option.create("3", "A3")
		)).setTitle("Parent2"));
		form.addInput(
			Date.input("simple_date", false).setTitle("Date")
				.setDefaultValue("2021-02-19")
		);
		form.addInput(
			Datetime.input("dt_local", false)
				.setTitle("DateTime Local").setDefaultValue("2021-02-19T22:55")
		);
		form.addInput(
			Month.input("month", false).setTitle("Month").setDefaultValue("2021-02")
		);
		form.addInput(
			Time.input("time", false).setTitle("Time").setDefaultValue("22:55")
		);
		form.addInput(
			Week.input("week", false).setTitle("Week").setDefaultValue("2021-W07")
		);
		
		form.addInput(Color.input("favorite_color", false).setTitle("Favorite color"));
		form.addInput(File.input("file", false).setTitle("File"));
		form.addInput(TextArea.input("comment", false).setTitle("Comment").setRows(10).setCols(50));
		
		form.addInput(
			Select.input("dependson", false, Arrays.asList(
				Option.create("", "---"),
				Option.create("first", "First Group"),
				Option.create("second", "Second Groupt"),
				Option.create("First Group", "Wrong First Group"),
				Option.create("Second Group", "Wrong Second Group")
			))
			.setTitle("DependsOn")
			.setExclude(true)
		);
		form.addInput(
			Select.input("depending", false, Arrays.asList(
				Option.create("", "---"),
				Option.create("a1", "A1"),
				Option.create("a2", "A2"),
				Option.create("b1", "B1").setOptGroup("First Group"),
				Option.create("b2", "B2").setOptGroup("First Group"),
				Option.create("c3", "C3").setOptGroup("Second Group"),
				Option.create("c4", "C4").setOptGroup("Second Group")
			))
			.setTitle("Depending")
			.setDepends("dependson")
			.setExclude(true)
		);
		
		form.addInput(
			InputList.input("map")
			.addInput(Text.input("subText1", false).setTitle("Map Sub Text 1"))
			.addInput(Text.input("subText2", false).setTitle("Map Sub Text 2"))
		);
		
		form.addInput(
			InputList.input("list")
			.addInput(
				Select.input("", false, Arrays.asList(Option.create("", "---")))
					.setLoadData("/example-module/api/example/help", "get")
					.setTitle("List Sub Text 1")
					.setShowedOptionGroup("Opt Group #0")
			)
			.addInput(
				Select.input("", false, Arrays.asList(Option.create("", "---")))
					.setLoadData("/example-module/api/example/help", "get")
					.setTitle("List Sub Text 2")
					.setShowedOptionGroup("Opt Group #1")
			)
			.addInput(
				Select.input("", false, Arrays.asList(Option.create("", "---")))
					.setTitle("List Sub Text 3")
					.setLoadData("/example-module/api/example/help", "get")
					.setShowedOptionGroup("Opt Group #2")
			)
		);
		
		form.addInput(
			DynamicList.input("pairs")
				.setTitle("Pairs")
				.addInput(Text.input("first-in-pair", false).setTitle("First In Pair ({i})"))
				.addInput(Text.input("second-in-pair", false).setTitle("Second In Pair ({i})"))
		);
		
		form.addInput(
				Text.input("disabled-not-send", false)
				.setDisabled(true)
				.setTitle("Disabled not send")
				.setDefaultValue("A")
		);
		form.addInput(
				Text.input("disabled-but-send", false)
				.setDisabled(true)
				.setExclude(false)
				.setTitle("Disabled but send")
				.setDefaultValue("B")
		);
		form.addInput(
				Text.input("editable-not-send", false)
				.setExclude(true)
				.setTitle("Editable not send")
				.setDefaultValue("C")
		);
		form.addInput(
				Text.input("not-editable", false)
				.setExclude(false)
				.setEditable(false)
				.setTitle("Not editable")
				.setDefaultValue("D")
		);
		
		form.addInput(
			DynamicList.input("main")
				.addInput(Text.input("text1", false).setTitle("Text {i}-1"))
				.addInput(Text.input("text2", false).setTitle("Text {i}-2"))
		);
		
		form.addInput(Reset.create("reset").setTitle("Reset button"));
		form.addInput(Button.create("/example-module/example/list", "back").setTitle("Back"));
		form.addInput(
			Submit.create("Save", "save").setConfirmation("You will save it")
			   .setRedirect("/example-module/example/edit/{id}").setAsync(true)
		);
		form.addInput(
			Submit.create("Save And Return", "save-back").setAsync(true)
			   .setRedirect("/example-module/example/list")
		);
		
		form.addInput(
				Button.create("/example-module/api/example/test", "test").setMethod("post")
				.addRequestParam("a", "a").addRequestParam("b", "bb").setAsync(true)
		);
		
		// END
		if (id != null) {
			form.setBindMethod("get");
			form.setBindUrl("/example-module/api/example/get/" + id);
		}
		form.setAfterBind("afterBind");
		form.setBeforeBind("beforeBind");
		params.put("exampleControl", form);
		params.put("title", translator.translate("example-" + (id == null ? "add" : "edit")));
		return Response.getTemplate((template == null ? JSP_PAGE : "Example2.jsp"), params);
	}

	@Action("test")
	public Response test() {
		Map<String, Object> params = new HashMap<>();
		Form form = new Form("/example-module/api/example/test", true);
		form.addInput(InputList.input("list")
			.addInput(Text.input("", false).setTitle("List 1").setDefaultValue("A1"))
			.addInput(Text.input("", false).setTitle("List 2").setDefaultValue("A2"))
			.addInput(Text.input("", false).setTitle("List 3").setDefaultValue("A3"))
		);
		form.addInput(InputList.input("map")
			.addInput(Text.input("a", false).setTitle("Map 1").setDefaultValue("B1"))
			.addInput(Text.input("b", false).setTitle("Map 2").setDefaultValue("B2"))
			.addInput(Text.input("c", false).setTitle("Map 3").setDefaultValue("B3"))
		);
		form.addInput(InputList.input("listmap")
			.addInput(InputList.input("")
				.addInput(Text.input("a1", false).setTitle("A1").setDefaultValue("Ca1"))
				.addInput(Text.input("a2", false).setTitle("A2").setDefaultValue("Ca2"))
			)
			.addInput(InputList.input("")
				.addInput(Text.input("a1", false).setTitle("A1").setDefaultValue("Cb1"))
				.addInput(Text.input("a2", false).setTitle("A2").setDefaultValue("Cb2"))
			)
			.addInput(InputList.input("")
				.addInput(Text.input("a1", false).setTitle("A1").setDefaultValue("Cc1"))
				.addInput(Text.input("a2", false).setTitle("A2").setDefaultValue("Cc2"))
			)
		);
		form.addInput(InputList.input("maplist")
				.addInput(InputList.input("a")
					.addInput(Text.input("", false).setTitle("B1").setDefaultValue("Da1"))
					.addInput(Text.input("", false).setTitle("B2").setDefaultValue("Da2"))
				)
				.addInput(InputList.input("b")
					.addInput(Text.input("", false).setTitle("B1").setDefaultValue("Db1"))
					.addInput(Text.input("", false).setTitle("B2").setDefaultValue("Db2"))
				)
				.addInput(InputList.input("c")
					.addInput(Text.input("", false).setTitle("B1").setDefaultValue("Dc1"))
					.addInput(Text.input("", false).setTitle("B2").setDefaultValue("Dc2"))
				)
			);
		form.addInput(DynamicList.input("dynamic")
			.addInput(Text.input("", false).setTitle("Item {i}"))
		);
		form.setFormMethod("get");
		form.addInput(Submit.create("Submit", "submit").setAsync(false));
		params.put("exampleControl", form);
		return Response.getTemplate(JSP_PAGE, params);
	}
}
