package toti.tutorial1.web.api;

import java.util.Arrays;

import org.apache.logging.log4j.Logger;

import ji.common.structures.DictionaryValue;
import ji.socketCommunication.http.StatusCode;
import ji.translator.Translator;
import toti.tutorial1.entity.Device;
import toti.tutorial1.services.DevicesDao;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.ParamUrl;
import toti.annotations.Params;
import toti.application.GridOptions;
import toti.response.Response;
import toti.validation.Validator;

@Controller("device")
public class DeviceApiController {

	private final Logger logger;
	private final Translator translator;
	private final DevicesDao dao;
	
	public DeviceApiController(DevicesDao dao, Translator translator, Logger logger) {
		this.logger = logger;
		this.translator = translator;
		this.dao = dao;
	}
	
	public Validator validateList() {
		return GridOptions.getValidator(Arrays.asList(
			
		));
	}
	
	@Action("list")
	public Response getAll(@Params GridOptions options) {
		try {
			return Response.getJson(dao.getAll(options, null, translator));
		} catch (Exception e) {
			logger.error("Device: Get All", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, "");
		}
	}
	
	@Action("get")
	public Response get(@ParamUrl("id") Object id) {
		try {
			return Response.getJson(dao.get(id));
		} catch (Exception e) {
			logger.error("Device: Get Item " + id, e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, "");
		}
	}
	
	@Action("insert")
	public Response insert(@Params Device device) {
		try {
			DictionaryValue id = dao.insert(device);
			return Response.getJson(
				""
			);
		} catch (Exception e) {
			logger.error("Device: Insert", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, "");
		}
	}
	
	@Action("update")
	public Response update(@ParamUrl("id") Object id, @Params Device device) {
		try {
			dao.update(id, device);
			return Response.getJson(
				""
			);
		} catch (Exception e) {
			logger.error("Device: Insert", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, "");
		}
	}
	
	@Action("delete")
	public Response delete(@ParamUrl("id") Object id) {
		try {
			dao.delete(id);
			return Response.getJson(
				""
			);
		} catch (Exception e) {
			logger.error("Device: Insert", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, "");
		}
	}
	
}
