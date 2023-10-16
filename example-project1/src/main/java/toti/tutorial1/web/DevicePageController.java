package toti.tutorial1.web;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ji.translator.Translator;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.ParamUrl;
import toti.annotations.Secured;
import toti.answers.request.AuthMode;
import toti.answers.response.Response;
import toti.control.Form;
import toti.control.Grid;
import toti.control.columns.ButtonsColumn;
import toti.control.columns.ValueColumn;
import toti.control.inputs.Button;
import toti.control.inputs.Checkbox;
import toti.control.inputs.Hidden;
import toti.control.inputs.Option;
import toti.control.inputs.Select;
import toti.control.inputs.Submit;
import toti.control.inputs.Text;
import toti.tutorial1.web.api.DeviceApiController;
import toti.url.Link;

@Controller("device")
public class DevicePageController {

	private final Translator translator;
	private final Link link;
	
	public DevicePageController(Translator translator, Link link) {
		this.translator = translator;
		this.link = link;
	}
	
	@Action("list")
	@Secured(mode = AuthMode.COOKIE)
	public Response getList() {
		Grid grid = new Grid(link.create(DeviceApiController.class, c->c.getAll(null)), "get");
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
		grid.addColumn(buttons);
		buttons.setTitle(translator.translate("messages.devices.buttons"));
		buttons.setResetFiltersButton(
			Button.reset("reset")
			.setTitle(translator.translate("messages.reset-filter"))
		);
		buttons.addGlobalButton(
			Button.create(link.create(getClass(), c->c.add()), "add")
			.setTitle(translator.translate("messages.devices.add"))
		);
		
		
		buttons.addButton(
			Button.create(link.create(getClass(), c->c.detail(null), "{id}"), "detail")
			.setTitle(translator.translate("messages.devices.detail"))
		);
		
		buttons.addButton(
			Button.create(link.create(getClass(), c->c.edit(null), "{id}"), "edit")
			.setTitle(translator.translate("messages.devices.edit"))
		);
		
		buttons.addButton(
			Button.create(
				link.create(DeviceApiController.class, c->c.delete(null), "{id}"),
				"delete"
			)
			.setTitle(translator.translate("messages.devices.delete"))
			.setConfirmation(translator.translate("messages.devices.delete-confirm"))
		);
		
		Map<String, Object> params = new HashMap<>();
		params.put("control", grid);
		params.put("title", translator.translate("messages.devices.title-list"));
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
			link.create(DeviceApiController.class, id == null ? c->c.insert(null) : c->c.update(null, null)),
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
			form.setBindUrl(link.create(DeviceApiController.class, c->c.get(null), id));
			form.setBindMethod("get");
		}
		
		form.addInput(
			Submit.create(translator.translate("mesasges.devices.save"), "submit")
			.setRedirect(link.create(getClass(), c->c.edit(null), "{id}"))
		);
		
		Map<String, Object> params = new HashMap<>();
		params.put("control", form);
		params.put("title", translator.translate(
			editable ? (id == null ? "messages.devices.add" : "messages.devices.edit") : "messages.devices.detail"
		));
		return Response.getTemplate("/device.jsp", params);
	}
	
}
