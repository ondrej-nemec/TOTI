package toti.tutorial1.web;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ji.translator.Translator;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Secured;
import toti.answers.response.Response;
import toti.control.Grid;
import toti.control.columns.ValueColumn;
import toti.control.inputs.Option;
import toti.control.inputs.Select;
import toti.control.inputs.Text;
import toti.security.AuthMode;
import toti.tutorial1.web.api.DeviceApiController;
import toti.tutorial1.web.api.StateApiController;
import toti.url.Link;

@Controller("state")
public class StatePageController {

	private final Translator translator;
	private final Link link;
	
	public StatePageController(Translator translator, Link link) {
		this.translator = translator;
		this.link = link;
	}
	
	@Action()
	@Secured(mode = AuthMode.COOKIE)
	public Response index() {
		Grid grid = new Grid(link.create(StateApiController.class, c->c.getAll(null)), "get");
		
		grid.setRefreshInterval(15000);
		
		grid.addColumn(
			new ValueColumn("device_id")
			.setFilter(
				Select.filter(Arrays.asList(Option.create("", "---")))
				.setLoadData(link.create(DeviceApiController.class, c->c.getHelp()), "options")
			)
			.setTitle(translator.translate("messages.devices.name"))
		);
		grid.addColumn(
			new ValueColumn("is_connected")
			.setFilter(Select.filter(Arrays.asList(
				Option.create("", "---"),
				Option.create(true, translator.translate("messages.no")),
				Option.create(false, translator.translate("messages.no"))
			)))
			.setTitle(translator.translate("messages.state.is-connected"))
		);
		grid.addColumn(
			new ValueColumn("detail")
			.setFilter(Text.filter())
			.setTitle(translator.translate("messages.state.detail"))
		);
		
		Map<String, Object> params = new HashMap<>();
		params.put("control", grid);
		return Response.getTemplate("/state.jsp", params);
	}

}
