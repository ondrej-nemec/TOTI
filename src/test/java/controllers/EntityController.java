package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import common.MapInit;
import controllers.other.EntityValidator;
import controllers.other.Person;
import controllers.other.PersonDao;
import socketCommunication.http.HttpMethod;
import socketCommunication.http.server.UploadedFile;
import toti.annotations.url.Action;
import toti.annotations.url.Controller;
import toti.annotations.url.Method;
import toti.annotations.url.Param;
import toti.annotations.url.ParamUrl;
import toti.annotations.url.Params;
import toti.annotations.url.Secured;
import toti.control.Form;
import toti.control.Grid;
import toti.control.columns.ActionsColumn;
import toti.control.columns.ButtonsColumn;
import toti.control.columns.GroupAction;
import toti.control.columns.ValueColumn;
import toti.control.inputs.Button;
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
import toti.response.Response;

@Controller("control")
public class EntityController {
	
	private final PersonDao dao;
	
	public EntityController(PersonDao dao) {
		this.dao = dao;
	}
	
	@Action("list")
	@Secured(isApi = false)
	public Response grid() {
		Map<String, Object> params = new HashMap<>();
		Grid grid = new Grid("/control/all", "get");
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
					.setConfirmation("Delete {name}?").setMethod("delete"))
			//.addButton(Button.create("/control/pdf/{id}").setAjax(false).setTitle("PDF"))
			.addButton(Button.create("/control/detail/{id}").setAjax(false).setTitle("Detail"))
			.addButton(Button.create("/control/edit/{id}").setAjax(false).setTitle("Edit"))
		);
		grid.addAction(new GroupAction("Action with no link", "url")); // TODO some tests
		params.put("personControl", grid);
		return Response.getTemplate("/control.jsp", params);
	}
	
	@Action("add")
	@Secured(isApi = false)
	public Response add() {
		return getOne(null, true);
	}

	@Action("edit")
	@Secured(isApi = false)
	public Response edit(@ParamUrl("id") Integer id) {
		return getOne(id, true);
	}

	@Action("detail")
	@Method({HttpMethod.GET})
	@Secured(isApi = false)
	public Response detail(@ParamUrl("id") Integer id) {
		return getOne(id, false);
	}

	@Action("pdf")
	@Method({HttpMethod.GET})
	@Secured(isApi = false)
	public Response pdf(@ParamUrl("id") Integer id) {
		Response res = detail(id);
		res.addParam("pdf", "pdf");
		return res; // Response.getPdf(res);
	}
	
	private Response getOne(Integer id, boolean editable) {
		Map<String, Object> params = new HashMap<>();
		String url = id == null ? "/control/insert" : "/control/update/" + id;
		Form form = new Form("personForm", url, editable);
		form.setFormMethod("put");
		form.addInput(Hidden.input("id"));
		form.addInput(Text.input("name", true).setTitle("Name"));
		form.addInput(Number.input("age", true).setTitle("Age"));
		form.addInput(Checkbox.input("maried", false).setTitle("Is Maried"));
		form.addInput(Datetime.input("born", true).setTitle("Born date"));
		form.addInput(Password.input("password", false).setTitle("Password"));
		form.addInput(Email.input("email", true).setTitle("Email").setDefaultValue("example@example.com"));
		form.addInput(RadioList.input(
				"sex",
				true,
				MapInit.hashMap(MapInit.t("male", "Male"), MapInit.t("female", "Female"))
		).setTitle("Sex"));
		Map<String, String> departments = new HashMap<>();
		for (int i = 1; i < 11; i++) {
			departments.put(i + "", "Department #" + i);
		}
		form.addInput(Select.input("department", true, departments).setTitle("Department"));
		form.addInput(File.input("foto", false).setTitle("Foto"));
				
		form.setBindMethod("get");
		form.setBindUrl("/control/get/" + id);
		
		form.addInput(Submit.create("Save", "save").setConfirmation("Send {name}?"));
		form.addInput(Submit.create("Save and return", "save-and-return").setRedirect("/control/all"));
		form.addInput(Button.create("/control/list").setTitle("Cancel").setAjax(false));
		params.put("personControl", form);
		params.put("itemId", id);
		return Response.getTemplate("/detail.jsp", params);
	}
	
	/*************************/
	
	@Action("all")
	@Method({HttpMethod.GET})
	@Secured
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
		json.put("data", dao.getData(pageIndex, pageSize, filters, sorting));
		json.put("itemsCount", dao.getAll().size());
		json.put("pageIndex", pageIndex);
		return Response.getJson(json);
	}

	@Action("get")
	@Method({HttpMethod.GET})
	@Secured
	public Response get(@ParamUrl("id") Integer id) {
		return Response.getJson(dao.get(id).toParams());
	}

	@Action("delete")
	@Method({HttpMethod.DELETE})
	@Secured
	public Response delete(@ParamUrl("id") Integer id) {
		// System.out.println(prop);
		dao.delete(id);
		return Response.getText("Item deleted");
	}

	@Action(value = "update", validator = EntityValidator.NAME_FORM)
	@Method({HttpMethod.PUT})
	@Secured
	public Response update(@ParamUrl("id") Integer id, @Param("foto") UploadedFile file, @Params Properties prop) {
		/*try {
			file.save("temp/uploads/");
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		dao.update(id, new Person(prop));
		System.out.println(prop);
		//System.out.println(dao.get(id));
		return Response.getText("Item updated");
	}

	@Action(value = "insert", validator = EntityValidator.NAME_FORM)
	@Method({HttpMethod.PUT})
	@Secured
	public Response insert(@Params Properties prop) {
		System.out.println(prop);
		int id = dao.insert(new Person(prop));
		return Response.getText("Item inserted " + id);
	}
	
}
