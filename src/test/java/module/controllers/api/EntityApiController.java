package module.controllers.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ji.common.Logger;
import ji.common.structures.DictionaryValue;
import ji.common.structures.MapDictionary;
import module.AuditTrail;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.server.RequestParameters;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Method;
import toti.annotations.Param;
import toti.annotations.ParamUrl;
import toti.annotations.Params;
import toti.response.Response;
import ji.translator.Translator;

@Controller("entity")
public class EntityApiController {
	
	//private final static String SECURITY_DOMAIN = "entity";
	private final static String UNIQUE = "id";
	
	static {
		EntityValidator.init();
	}
	
	private Translator translator;
	
	private final EntityDao dao;
	private final Logger logger;
	//private final AuditTrail auditTrail;
	
	public EntityApiController(EntityDao dao, Logger logger, AuditTrail auditTrail, Translator translator) {
		this.dao = dao;
		this.logger = logger;
		this.translator = translator;
	}
	
	@Action(value = "unique", validator="uniq")
	public Response empty(@Params RequestParameters params) {
		System.err.println(params);
		return Response.getText("OK");
	}
	
	@Action(value = "reporting")
	public Response params(@Params RequestParameters params) {
		System.err.println("Report:");
		System.err.println(params);
		return Response.getText("");
	}
	
	@Action(value = "params", validator = "test")
	public Response params(
			@Params RequestParameters prop,
			@Param("json-map") Map<String, Object> json,
			@Param("json-map-2") Map<String, Object> json2
		) {
		/*prop.forEach((i, item)->{
			System.err.println(i + ": " + item + " " + item.getClass());
		});
		System.err.println();*/
		System.err.println(" -- Params: --");
		print("", prop);
		System.err.println();
		/*
		System.err.println(" -- Json: --");
		print("", json);
		System.err.println();
		
		System.err.println(" -- Json 2: --");
		print("", json2);
		System.err.println();*/
		return Response.getJson(StatusCode.OK, new HashMap<>());
	}
	
	private void print(String prefix, Object o) {
		if (o == null) {
			System.err.println(prefix + o);
		} else if (o instanceof Map) {
			new DictionaryValue(o).getMap().forEach((key, value)->{
				if (!(value instanceof Map) /*&& !(value instanceof List)*/) {
					print(prefix + key + ": ", value);
				} else {
					System.err.println(prefix + key + " {");
					print(prefix + "  ", value);
					System.err.println(prefix + "}");
				}
			});
		} else if (o instanceof List) {
			System.err.println(prefix + "[");
			new DictionaryValue(o).getList().forEach((value)->{
				print(prefix + "  ", value);
			});
			System.err.println(prefix + "]");
		} else {
			System.err.println(prefix + o + " -- " + o.getClass());
		}
	}

	@Action(value = "all", validator = EntityValidator.NAME_GRID)
	@Method({HttpMethod.GET})
	//@Secured({@Domain(name=SECURITY_DOMAIN, action=helper.Action.READ)})
	public Response getAll(
			@Param("pageIndex") Integer pageIndex,
			@Param("pageSize") Integer pageSize,
			@Param("filters") Map<String, Object> filters,
			@Param("sorting") Map<String, Object> sorting,
			@Params RequestParameters prop
		) {
		try {
			List<Map<String, Object>> items = dao.getAll(pageIndex, pageSize, filters, sorting);
			
			Map<String, Object> json = new HashMap<>();
			json.put("data", items);
			json.put("itemsCount", dao.getTotalCount());
			json.put("pageIndex", pageIndex);
			return Response.getJson(json);
		} catch (Exception e) {
			logger.error("Entity GetAll", e);
			return Response.getJson(StatusCode.INTERNAL_SERVER_ERROR, new HashMap<>());
		}
	}

	@Action("get")
	@Method({HttpMethod.GET})
	//@Secured({@Domain(name=SECURITY_DOMAIN, action=helper.Action.READ)})
	public Response get(@ParamUrl("id") Integer id) {
		try {
			Map<String, Object> item = dao.get(id);
			/***/
			
			/***/
			return Response.getJson(item);
		} catch (Exception e) {
			logger.error("Entity Get", e);
			return Response.getJson(StatusCode.INTERNAL_SERVER_ERROR, new HashMap<>());
		}
	}

	@Action("delete")
	@Method({HttpMethod.DELETE})
	//@Secured({@Domain(name=SECURITY_DOMAIN, action=helper.Action.DELETE)})
	public Response delete(@ParamUrl("id") Integer id) {
		try {
			/*Map<String, Object> deleted = */dao.delete(id);
		//	auditTrail.delete(identity.getUser().getId(), deleted);
			return Response.getText(translator.translate("common.item-deleted"));
		} catch (Exception e) {
			logger.error("Entity Delete", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, translator.translate("common.deleting-problem"));
		}
	}

	@Action(value = "update", validator = EntityValidator.NAME_FORM)
	@Method({HttpMethod.PUT})
	//@Secured({@Domain(name=SECURITY_DOMAIN, action=helper.Action.UPDATE)})
	public Response update(@ParamUrl("id") Integer id, @Params RequestParameters updated) {
		try {
			/*Map<String, Object> origin =*/ dao.get(id);
						
			editValues(updated, false);
			
			dao.update(id, updated.toMap());
		//	auditTrail.update(identity.getUser().getId(), origin, updated);
			
			Map<String, Object> params = new HashMap<>();
			params.put("id", id);
			params.put("message", translator.translate("common.item-updated"));
			return Response.getJson(params);
		} catch (Exception e) {
			logger.error("Entity Update", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, translator.translate("common.saving-problem"));
		}
	}

	@Action(value = "insert", validator = EntityValidator.NAME_FORM)
	@Method({HttpMethod.PUT})
	//@Secured({@Domain(name=SECURITY_DOMAIN, action=helper.Action.CREATE)})
	public Response insert(@Params RequestParameters inserted) {
		try {
			//inserted.remove(UNIQUE);
			editValues(inserted, true);
			
			int id = dao.insert(inserted.toMap());
			inserted.put(UNIQUE, id);
		//	auditTrail.insert(identity.getUser().getId(), inserted);
			
			Map<String, Object> params = new HashMap<>();
			params.put("id", id);
			params.put("message", translator.translate("common.item-inserted"));
			return Response.getJson(params);
		} catch (Exception e) {
			logger.error("Entity Insert", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, translator.translate("common.saving-problem"));
		}
	}

	private void editValues(MapDictionary<String, Object> values, boolean insert) {
		values.remove(UNIQUE);
		if (values.get("is_main") == null) {
			values.put("is_main", false);
		}
	}
	
}
