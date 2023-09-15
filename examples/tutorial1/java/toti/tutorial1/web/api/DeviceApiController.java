package toti.tutorial1.web.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import ext.FilterMode;
import ext.GridColumn;
import ext.GridOptions;
import ji.common.structures.DictionaryValue;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import ji.translator.Translator;
import toti.tutorial1.entity.Device;
import toti.tutorial1.services.DevicesDao;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Method;
import toti.annotations.ParamUrl;
import toti.annotations.Params;
import toti.annotations.Secured;
import toti.answers.response.Response;
import toti.validation.ItemRules;
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
	
	@Action()
	@Method({HttpMethod.OPTIONS})
	@Secured
	public Response getHelp() {
		try {
			return Response.getJson(dao.getHelp(null));
		} catch (Exception e) {
			logger.error("Device: Get Help", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, "");
		}
	}
	
	public Validator validateList() {
		return GridOptions.getValidator(Arrays.asList(
			new GridColumn("name").setFilterMode(FilterMode.LIKE),
			new GridColumn("ip"),
			new GridColumn("is_running", Boolean.class)
		));
	}
	
	@Action(validator = "validateList")
	@Method({HttpMethod.GET})
	@Secured
	public Response getAll(@Params GridOptions options) {
		try {
			return Response.getJson(dao.getAll(options, null, translator));
		} catch (Exception e) {
			logger.error("Device: Get All", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, "");
		}
	}
	
	@Action()
	@Method({HttpMethod.GET})
	@Secured
	public Response get(@ParamUrl("id") Object id) {
		try {
			return Response.getJson(dao.get(id));
		} catch (Exception e) {
			logger.error("Device: Get Item " + id, e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, "");
		}
	}
	
	public Validator validateForm() {
		return new Validator(true)
			.addRule(ItemRules.forName("id", false).setType(Integer.class))
			.addRule(ItemRules.forName("name", true).setMaxLength(50))
			.addRule(ItemRules.forName("ip", true).setMaxLength(30))
			.addRule(ItemRules.forName("is_running", false).setType(Boolean.class))
			;
	}
	
	@Action(validator = "validateForm")
	@Method({HttpMethod.PUT})
	@Secured
	public Response insert(@Params Device device) {
		try {
			DictionaryValue id = dao.insert(device);
			Map<String, Object> result = new HashMap<>();
			result.put("id", id); // id for redirection
			return Response.getJson(result);
		} catch (Exception e) {
			logger.error("Device: Insert", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, "");
		}
	}
	
	@Action(validator = "validateForm")
	@Method({HttpMethod.PUT})
	@Secured
	public Response update(@ParamUrl("id") Object id, @Params Device device) {
		try {
			dao.update(id, device);
			Map<String, Object> result = new HashMap<>();
			result.put("id", id); // id for redirection
			return Response.getJson(result);
		} catch (Exception e) {
			logger.error("Device: Update", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, "");
		}
	}
	
	@Action()
	@Method({HttpMethod.DELETE})
	@Secured
	public Response delete(@ParamUrl("id") Object id) {
		try {
			dao.delete(id);
			return Response.getText("OK"); // response is not used, status code is nessessary
		} catch (Exception e) {
			logger.error("Device: Delete", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, "");
		}
	}
	
}
