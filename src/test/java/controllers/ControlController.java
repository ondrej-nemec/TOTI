package controllers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import common.MapInit;
import mvc.annotations.url.Action;
import mvc.annotations.url.Controller;
import mvc.annotations.url.Method;
import mvc.annotations.url.Param;
import mvc.annotations.url.ParamUrl;
import mvc.annotations.url.Params;
import mvc.annotations.url.Secured;
import mvc.control.Form;
import mvc.control.Grid;
import mvc.control.columns.ActionsColumn;
import mvc.control.columns.ButtonsColumn;
import mvc.control.columns.GroupAction;
import mvc.control.columns.ValueColumn;
import mvc.control.inputs.Button;
import mvc.control.inputs.Checkbox;
import mvc.control.inputs.Datetime;
import mvc.control.inputs.Hidden;
import mvc.control.inputs.Number;
import mvc.control.inputs.RadioList;
import mvc.control.inputs.Select;
import mvc.control.inputs.Submit;
import mvc.control.inputs.Text;
import mvc.response.Response;
import socketCommunication.http.HttpMethod;

@Controller("control")
public class ControlController {
	
	private final static Map<Integer, Map<String, Object>> DATA = new HashMap<>();
	{
		if (DATA.isEmpty()) {
			for (int i = 0; i < 50; i++) {
				Map<String, Object> row = new HashMap<>();
				row.put("id", i);
				row.put("name", "Name #" + i);
				row.put("age", (i%10)*20);
				row.put("maried", i%3);
				row.put("born", "2020-02-12 12:21");
				DATA.put(i, row);
			}
		}
	}
	
	/*************************/
	
	@Action("list")
	public Response grid() {
		Map<String, Object> params = new HashMap<>();
		Grid grid = new Grid("/control/all", "gEt");
		grid.addColumn(new ActionsColumn("Actions"));
		grid.addColumn(new ValueColumn("id"));
		grid.addColumn(new ValueColumn("name").setTitle("Name").setFilter(Text.filter()).setUseSorting(true));
		grid.addColumn(new ValueColumn("age").setTitle("Age").setFilter(Number.filter()));
		grid.addColumn(new ValueColumn("maried").setTitle("Is Maried").setFilter(Select.filter(
			MapInit.hashMap(MapInit.t("", "---"), MapInit.t("no", "No"), MapInit.t("yes", "Yes"))
		)));
		grid.addColumn(new ValueColumn("born").setTitle("Born date").setFilter(Datetime.filter()));
		grid.addColumn(new ButtonsColumn("Buttons")
			.addButton(Button.create("/control/delete/{id}").setAjax(true).setTitle("Delete")
					.setConfirmation("Delete {name}?").setMethod("put"))
			.addButton(Button.create("/control/pdf/{id}").setAjax(false).setTitle("PDF"))
			.addButton(Button.create("/control/detail/{id}").setAjax(false).setTitle("Detail"))
			.addButton(Button.create("/control/edit/{id}").setAjax(false).setTitle("Edit"))
		);
		grid.addAction(new GroupAction("Action with no link", "url"));
		params.put("great", grid);
		return Response.getTemplate("/control.jsp", params);
	}
	
	@Action("add")
	public Response add() {
		return getOne(null, true);
	}

	@Action("edit")
	public Response edit(@ParamUrl("id") Integer id) {
		return getOne(id, true);
	}

	@Action("detail")
	@Method({HttpMethod.GET})
	public Response detail(@ParamUrl("id") Integer id) {
		return getOne(id, false);
	}

	@Action("pdf")
	@Method({HttpMethod.GET})
	public Response pdf(@ParamUrl("id") Integer id) {
		return Response.getPdf(detail(id));
	}
	
	private Response getOne(Integer id, boolean editable) {
		Map<String, Object> params = new HashMap<>();
		String url = id == null ? "/control/insert" : "/control/update/" + id;
		Form form = new Form("someNewForm", url, editable);
		form.setFormMethod("put");
		form.addInput(Text.input("name", true).setTitle("Name").addParam("class", "testing-class").setDisabled(true));
		form.addInput(Number.input("age", true).setTitle("Age"));
		form.addInput(Checkbox.input("maried", false).setTitle("Is Maried"));
		form.addInput(Datetime.input("born", true).setTitle("Born date"));
		form.addInput(Hidden.input("id"));
		form.addInput(Text.input("other", false).setTitle("Other").setDefaultValue("Fill"));
		
		Map<String, String> radios = new HashMap<>();
		radios.put("male", "Male");
		radios.put("female", "Female");
		radios.put("empty", null);
		form.addInput(RadioList.input("sex", true, radios).setTitle("Sex").setDefaultValue("empty"));
		
		form.setBindMethod("get");
		form.setBindUrl("/control/get/" + id);
		
		form.addInput(Submit.create("Save", "save").setConfirmation("Send {name}?"));
		form.addInput(Submit.create("Save and return", "save-and-return").setRedirect("/control/all"));
		form.addInput(Button.create("/control/all").setTitle("Cancel").setAjax(false));
		params.put("great", form);
		params.put("itemId", id);
		return Response.getTemplate("/detail.jsp", params);
	}
	
	/*************************/
	
	@Action("all")
	@Method({HttpMethod.GET})
	public Response getAll(
			@Param("pageIndex") Integer pageIndex,
			@Param("pageSize") Integer pageSize,
			@Param("filters") Map<String, Object> filters,
			@Param("sorting") Map<String, Object> sorting,
			@Params Properties prop
		) {
		// TODO validator
		System.out.println(prop);
		Map<String, Object> json = new HashMap<>();
		List<Map<String, Object>> data = new LinkedList<>(DATA.values()).subList(
				Math.min(DATA.size() - 1, (pageIndex-1)*pageSize+1), 
				Math.min(DATA.size(), pageIndex*pageSize+1)
		);
		json.put("data", data);
		json.put("itemsCount", DATA.size());
		json.put("pageIndex", pageIndex);
		return Response.getJson(json);
	}

	@Action("get")
	@Method({HttpMethod.GET})
	public Response get(@ParamUrl("id") Integer id) {
		return Response.getJson(DATA.get(id));
	}

	@Action("delete")
	@Method({HttpMethod.DELETE})
	public Response delete(@Params Properties prop) {
		System.out.println(prop);
		return Response.getText("Item deleted");
	}

	@Action("update")
	@Method({HttpMethod.PUT})
	public Response update(@ParamUrl("id") Integer id, @Params Properties prop) {
		// TODO validator
		System.out.println(prop);
		return Response.getText("Item updated");
	}

	@Action("insert")
	@Method({HttpMethod.PUT})
	public Response insert(@Params Properties prop) {
		// TODO validator
		System.out.println(prop);
		return Response.getText("Item inserted");
	}
	
}
