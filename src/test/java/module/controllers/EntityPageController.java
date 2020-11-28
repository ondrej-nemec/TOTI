package module.controllers;

import java.util.HashMap;
import java.util.Map;

import common.MapInit;
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
import toti.control.columns.ValueColumn;
import toti.control.inputs.Checkbox;
import toti.control.inputs.Datetime;
import toti.control.inputs.Email;
import toti.control.inputs.Hidden;
import toti.control.inputs.Number;
import toti.control.inputs.Password;
import toti.control.inputs.RadioList;
import toti.control.inputs.Select;
import toti.control.inputs.Submit;
import toti.control.inputs.Text;
import toti.response.Response;
import translator.Translator;

@Controller("entity")
public class EntityPageController {
	
	private final static String SECURITY_DOMAIN = "entity";
	
	@Translate
	private Translator translator;
	
	public void setTranslator(Translator translator) {
		this.translator = translator;
	}

	@Action("list")
	//@Secured(isApi = false, value={@Domain(name=SECURITY_DOMAIN, action=helper.Action.READ)})
	public Response grid() {
		Map<String, Object> params = new HashMap<>();
		/***/
		// TODO grid
		Grid grid = new Grid("/api/entity/all", "get");
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
		String url = "/api/entity/" +  (id == null ? "insert" : "update/" + id);
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
		)));
		form.addInput(RadioList.input("lang", true, MapInit.hashMap(
			MapInit.t("cs_CZ", "Czech"),
			MapInit.t("en_GB", "English"),
			MapInit.t("de_DE", "German"),
			MapInit.t("sk_SK", "Slovak")
		)));
		
		form.addInput(Submit.create("Save", "save"));
		
		if (id != null) {
			form.setBindMethod("get");
			form.setBindUrl("/api/entity/get/" + id);
		}
		/***/
		params.put("control", form);
		params.put("title", translator.translate("entity-" + (id == null ? "add" : "edit")));
		return Response.getTemplate("/Entity.jsp", params);
	}
	
}
