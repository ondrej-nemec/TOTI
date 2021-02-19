package example.web.controllers;

import java.util.HashMap;
import java.util.Map;

import common.MapInit;
import common.structures.Tuple2;
import socketCommunication.http.HttpMethod;
import toti.annotations.inject.Translate;
import toti.annotations.url.Action;
import toti.annotations.url.Controller;
import toti.annotations.url.Domain;
import toti.annotations.url.Method;
import toti.annotations.url.ParamUrl;
import toti.annotations.url.Secured;
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
import toti.control.inputs.Email;
import toti.control.inputs.File;
import toti.control.inputs.Hidden;
import toti.control.inputs.Month;
import toti.control.inputs.Number;
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
import translator.Translator;

@Controller("example")
public class ExamplePageController {

	private final static String SECURITY_DOMAIN = "example";
	private final static String JSP_PAGE = "Example.jsp";

	@Translate
	private Translator translator;
	
	public void setTranslator(Translator translator) {
		this.translator = translator;
	}
	
	@Action("list")
	@Secured(isApi = false, value={@Domain(name=SECURITY_DOMAIN, action=acl.Action.READ)})
	public Response grid() {
		Map<String, Object> params = new HashMap<>();
		Grid grid = new Grid("/example-module/api/example/all", "get");
		// HERE
		grid.addColumn(new ValueColumn("id"));
		grid.addColumn(new ValueColumn("name").setTitle("Name").setFilter(Text.filter()).setUseSorting(true));
		grid.addColumn(
			new ValueColumn("age").setTitle("Age").setFilter(Number.filter()).setUseSorting(true)
		);
		grid.addColumn(
			new ValueColumn("active").setTitle("Active").setFilter(Select.filter(MapInit.hashMap(
				new Tuple2<>("", "---"),
				new Tuple2<>("true", "YES"),
				new Tuple2<>("no", "NO")
			))).setUseSorting(true) // TODO renderer
		);
		grid.addColumn(
			new ValueColumn("parent").setTitle("Parent").setFilter(Select.filter(MapInit.hashMap(
				new Tuple2<>("", "---")
			))).setUseSorting(true) // TODO binding options AND renderer
		);
		
		grid.addColumn(
			new ValueColumn("simple_date").setTitle("Simple date").setFilter(Date.filter()).setUseSorting(true)
		); // TODO renderer
		grid.addColumn(
			new ValueColumn("dt_local").setTitle("Datetime local").setFilter(Datetime.filter()).setUseSorting(true)
		); // TODO renderer
		grid.addColumn(
			new ValueColumn("month").setTitle("Month").setFilter(Month.filter()).setUseSorting(true)
		); // TODO renderer
		grid.addColumn(
			new ValueColumn("week").setTitle("Week").setFilter(Week.filter()).setUseSorting(true)
		); // TODO renderer
		grid.addColumn(
			new ValueColumn("time").setTitle("Time").setFilter(Time.filter()).setUseSorting(true)
		); // TODO renderer
		
		// COMMON
		grid.addColumn(new ButtonsColumn("Actions")
			.addButton(Button.create("/example-module/example/edit/{id}", "edit").setTitle("Edit").setType(ButtonType.WARNING))
			.addButton(Button.create("/example-module/example/detail/{id}", "detail").setTitle("Detail").setType(ButtonType.INFO))
			.addButton(
				Button.create("/example-module/example/delete/{id}", "delete")
					.setTitle("Delete").setType(ButtonType.DANGER)
					.setMethod("delete").setAjax(true).setConfirmation("Really delete {name}?")
			)
		);
		// END
		params.put("exampleControl", grid);
		params.put("title", translator.translate("messages.example-list"));
		return Response.getTemplate(JSP_PAGE, params);
	}
	
	@Action("add")
	@Secured(isApi = false, value={@Domain(name=SECURITY_DOMAIN, action=acl.Action.CREATE)})
	public Response add() {
		return getOne(null, true);
	}

	@Action("edit")
	@Secured(isApi = false, value={@Domain(name=SECURITY_DOMAIN, action=acl.Action.UPDATE)})
	public Response edit(@ParamUrl("id") Integer id) {
		return getOne(id, true);
	}

	@Action("detail")
	@Method({HttpMethod.GET})
	@Secured(isApi = false, value={@Domain(name=SECURITY_DOMAIN, action=acl.Action.READ)})
	public Response detail(@ParamUrl("id") Integer id) {
		return getOne(id, false);
	}
	
	private Response getOne(Integer id, boolean editable) {
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
		
		form.addInput(Checkbox.input("active", false).setTitle("Active")); // TODO renderer ?
		form.addInput(Checkbox.input("defvalue", false).setTitle("DefValue").setDefaultValue(true)); // TODO renderer ?
		form.addInput(RadioList.input("sex", false, MapInit.hashMap(
			new Tuple2<>("male", "Male"),
			new Tuple2<>("female", "Feale")
		)).setTitle("Sex").setDefaultValue("female"));
		form.addInput(Select.input("parent", false, MapInit.hashMap(
			new Tuple2<>("", "---")
		)).setTitle("Parent")); // TODO binding options AND renderer
		
		form.addInput(
			Date.input("simple_date", false).setTitle("Date")
				.setDefaultValue("2021-02-19")
		); // TODO renderer
		form.addInput(
			Datetime.input("dt_local", false)
				.setTitle("DateTime Local").setDefaultValue("2021-02-19T22:55")
		); // TODO renderer
		form.addInput(
			Month.input("month", false).setTitle("Month").setDefaultValue("2021-02")
		); // TODO renderer
		form.addInput(
			Time.input("time", false).setTitle("Time").setDefaultValue("22:55")
		); // TODO renderer
		form.addInput(
			Week.input("week", false).setTitle("Week").setDefaultValue("2021-W07")
		); // TODO renderer
		
		form.addInput(Color.input("favorite_color", false).setTitle("Favorite color"));
		form.addInput(File.input("file", false).setTitle("File"));
		form.addInput(TextArea.input("comment", false).setTitle("Comment").setRows(10).setCols(50));
		
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
		// END
		if (id != null) {
			form.setBindMethod("get");
			form.setBindUrl("/example-module/api/example/get/" + id);
		}
		params.put("exampleControl", form);
		params.put("title", translator.translate("example-" + (id == null ? "add" : "edit")));
		return Response.getTemplate(JSP_PAGE, params);
	}
}
