package toti.tutorial1.web.api;

import java.util.Arrays;

import org.apache.logging.log4j.Logger;

import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Method;
import toti.annotations.Params;
import toti.annotations.Secured;
import toti.application.FilterMode;
import toti.application.GridColumn;
import toti.application.GridOptions;
import toti.response.Response;
import toti.tutorial1.services.DeviceStateDao;
import toti.validation.Validator;

@Controller("state")
public class StateApiController {
	
	private final DeviceStateDao dao;
	private final Logger logger;
	
	public StateApiController(DeviceStateDao dao, Logger logger) {
		this.dao = dao;
		this.logger = logger;
	}
	
	public Validator validateList() {
		return GridOptions.getValidator(Arrays.asList(
			new GridColumn("device_id", Integer.class).setSortingName("device_name"),
			new GridColumn("is_connected", Boolean.class),
			new GridColumn("detail").setFilterMode(FilterMode.LIKE)
		));
	}
	
	@Action("")
	@Method({HttpMethod.GET})
	@Secured
	public Response getAll(@Params GridOptions options) {
		try {
			// TODO use translator?
			return Response.getJson(dao.getAll(options, null, null));
		} catch (Exception e) {
			logger.error("Device: Get All", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, "");
		}
	}

}
