package toti.tutorial1.web;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ji.translator.Translator;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.ParamUrl;
import toti.annotations.Secured;
import toti.control.Form;
import toti.control.Grid;
import toti.control.columns.ButtonsColumn;
import toti.control.columns.ValueColumn;
import toti.control.inputs.Button;
import toti.control.inputs.Checkbox;
import toti.control.inputs.Hidden;
import toti.control.inputs.Option;
import toti.control.inputs.Select;
import toti.control.inputs.Text;
import toti.response.Response;
import toti.security.AuthMode;
import toti.tutorial1.web.api.DeviceApiController;
import toti.url.Link;

@Controller("device")
public class DevicePageController {

	private final Translator translator;
	
	public DevicePageController(Translator translator) {
		this.translator = translator;
	}
	
	@Action("list")
	@Secured(mode = AuthMode.COOKIE)
	public Response getList() {
		Grid grid = new Grid(Link.get().create(DeviceApiController.class, c->c.getAll(null)), "get");
		grid.addColumn(
			new ValueColumn("name")
			.setFilter(Text.filter())
			.setTitle(translator.translate("messages.devices.name"))
		);
		grid.addColumn(
			new ValueColumn("ip")
			.setFilter(Text.filter())
			.setTitle(translator.translate("messages.devices.ip"))
		);
		grid.addColumn(
			new ValueColumn("is_running")
			.setFilter(Select.filter(Arrays.asList(
				Option.create("", "---"),
				Option.create(true, translator.translate("messages.no")),
				Option.create(false, translator.translate("messages.no"))
			)))
			.setTitle(translator.translate("messages.devices.is-running"))
		);
		
		ButtonsColumn buttons = new ButtonsColumn("buttons");
		buttons.setTitle(translator.translate("messages.devices.buttons"));
		buttons.setResetFiltersButton(
			Button.reset("reset")
			.setTitle("messages.reset-filter")
		);
		buttons.addGlobalButton(
			Button.create(Link.get().create(getClass(), c->c.add()), "add")
			.setTitle(translator.translate("messages.devices.add"))
		);
		
		
		buttons.addButton(
			Button.create(Link.get().addUrlParam("{id}").create(getClass(), c->c.detail(null)), "detail")
			.setTitle(translator.translate("messages.devices.detail"))
		);
		
		buttons.addButton(
			Button.create(Link.get().addUrlParam("{id}").create(getClass(), c->c.edit(null)), "edit")
			.setTitle(translator.translate("messages.devices.edit"))
		);
		
		buttons.addButton(
			Button.create(
				Link.get().addUrlParam("{id}").create(DeviceApiController.class, c->c.delete(null)),
				"delete"
			)
			.setTitle(translator.translate("messages.devices.delete"))
			.setConfirmation(translator.translate("messages.devices.delete-confirm"))
		);
		
		Map<String, Object> params = new HashMap<>();
		params.put("control", grid);
		params.put("title", translator.translate("messages.devices.list"));
		return Response.getTemplate("/device.jsp", params);
	}
	
	@Action("add")
	@Secured(mode = AuthMode.COOKIE)
	public Response add() {
		return getOne(null, true);
	}
	
	@Action("edit")
	@Secured(mode = AuthMode.COOKIE)
	public Response edit(@ParamUrl("id") Integer id) {
		return getOne(id, true);
	}
	
	@Action("detail")
	@Secured(mode = AuthMode.COOKIE)
	public Response detail(@ParamUrl("id") Integer id) {
		return getOne(id, false);
	}
	
	private Response getOne(Integer id, boolean editable) {
		Form form = new Form(
			Link.get().create(DeviceApiController.class, id == null ? c->c.insert(null) : c->c.update(null, null)),
			editable
		);
		form.setFormMethod("put");
		
		form.addInput(Hidden.input("id"));
		form.addInput(
			Text.input("name", true)
			.setMaxLength(50)
			.setTitle(translator.translate("messages.devices.name"))
		);
		form.addInput(
			Text.input("ip", true)
			.setMaxLength(30)
			.setTitle(translator.translate("messages.devices.ip"))
		);
		form.addInput(
			Checkbox.input("is_running", false)
			.setTitle(translator.translate("messages.devices.is-running"))
		);
		
		if (id != null) {
			form.setBindUrl(Link.get().addUrlParam(id).create(DeviceApiController.class, c->c.get(null)));
			form.setBindMethod("get");
		}
		
		Map<String, Object> params = new HashMap<>();
		params.put("control", form);
		params.put("title", translator.translate(
			editable ? (id == null ? "messages.devices.add" : "messages.devices.edit") : "messages.devices.detail"
		));
		return Response.getTemplate("/device.jsp", params);
	}
	
}
