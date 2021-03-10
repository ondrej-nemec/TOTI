package example.web.controllers.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import common.Logger;
import common.MapInit;
import common.structures.Tuple2;
import example.AuditTrail;
import example.dao.ExampleDao;
import example.web.validator.ExampleValidator;
import socketCommunication.http.HttpMethod;
import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RequestParameters;
import socketCommunication.http.server.UploadedFile;
import toti.annotations.inject.ClientIdentity;
import toti.annotations.inject.Translate;
import toti.annotations.url.Action;
import toti.annotations.url.Controller;
import toti.annotations.url.Domain;
import toti.annotations.url.Method;
import toti.annotations.url.Param;
import toti.annotations.url.ParamUrl;
import toti.annotations.url.Params;
import toti.annotations.url.Secured;
import toti.authentication.Identity;
import toti.control.inputs.Option;
import toti.response.Response;
import translator.Translator;

@Controller("example")
public class ExampleApiController {

	private final static String SECURITY_DOMAIN = "example";
	private final static String UNIQUE = "id";
	
	static {
		ExampleValidator.init();
	}
	
	@Translate
	private Translator translator;
	
	@ClientIdentity
	private Identity identity;
	
	public void setTranslator(Translator translator) {
		this.translator = translator;
	}
	
	public void setIdentity(Identity identity) {
		this.identity = identity;
	}
	
	private final ExampleDao dao;
	private final Logger logger;
	private final AuditTrail auditTrail;
	
	public ExampleApiController(ExampleDao dao, Logger logger, AuditTrail auditTrail) {
		this.dao = dao;
		this.logger = logger;
		this.auditTrail = auditTrail;
	}

	@Action("help")
	@Method({HttpMethod.GET})
	public Response getInArray(@Param("view") Boolean viewOnly) {
		try {
			Map<String, Object> values = dao.getHelp(identity.getAllowedIds());
			for (int i = 0; i < 10; i++) {
				values.put("#" + i, Option.input("#" + i, "Option #" + i).setOptGroup("Opt Group #" + i%3));
			}
			return Response.getJson(values);
		} catch (Exception e) {
			logger.error("Example List", e);
			return Response.getJson(StatusCode.INTERNAL_SERVER_ERROR, new HashMap<>());
		}
	}
	
	@Action(value = "all", validator = ExampleValidator.NAME_GRID)
	@Method({HttpMethod.GET})
	@Secured({@Domain(name=SECURITY_DOMAIN, action=acl.Action.READ)})
	public Response getAll(
			@Param("pageIndex") Integer pageIndex,
			@Param("pageSize") Integer pageSize,
			@Param("filters") Map<String, Object> filters,
			@Param("sorting") Map<String, Object> sorting,
			@Params RequestParameters prop
		) {
		try {
			return Response.getJson(dao.getAll(pageIndex, pageSize, filters, sorting, identity.getAllowedIds()));
		} catch (Exception e) {
			logger.error("Example GetAll", e);
			return Response.getJson(StatusCode.INTERNAL_SERVER_ERROR, new HashMap<>());
		}
	}

	@Action("get")
	@Method({HttpMethod.GET})
	@Secured({@Domain(name=SECURITY_DOMAIN, action=acl.Action.READ)})
	public Response get(@ParamUrl("id") Integer id) {
		try {
			Map<String, Object> item = dao.get(id);
			Map<String, String> map = new HashMap<>();
			map.put("subText1", "aaa");
			map.put("subText2", "bbb");
			List<String> list= new LinkedList<>();
			list.add("1");
			list.add("#1");
			list.add("#6");
			list.add("#8");
			
			item.put("pairs", Arrays.asList(
				MapInit.hashMap(
					new Tuple2<>("first-in-pair", "A1"),
					new Tuple2<>("second-in-pair", "A2")
				),
				MapInit.hashMap(
						new Tuple2<>("first-in-pair", "B1"),
						new Tuple2<>("second-in-pair", "B2")
				),
				MapInit.hashMap(
						new Tuple2<>("first-in-pair", "C1"),
						new Tuple2<>("second-in-pair", "C2")
				)
			));
			
			item.put("map", map);
			item.put("list", list);
			return Response.getJson(item);
		} catch (Exception e) {
			logger.error("Example Get", e);
			return Response.getJson(StatusCode.INTERNAL_SERVER_ERROR, new HashMap<>());
		}
	}

	@Action("delete")
	@Method({HttpMethod.DELETE})
	@Secured({@Domain(name=SECURITY_DOMAIN, action=acl.Action.DELETE)})
	public Response delete(@ParamUrl("id") Integer id) {
		try {
			Map<String, Object> deleted = dao.delete(id);
			auditTrail.delete(identity.getUser().getId(), deleted);
			return Response.getText(translator.translate("common.item-deleted"));
		} catch (Exception e) {
			logger.error("Example Delete", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, translator.translate("common.deleting-problem"));
		}
	}

	@Action(value = "update", validator = ExampleValidator.NAME_FORM)
	@Method({HttpMethod.PUT})
	@Secured({@Domain(name=SECURITY_DOMAIN, action=acl.Action.UPDATE)})
	public Response update(@ParamUrl("id") Integer id, @Params RequestParameters updated) {
		try {
			Map<String, Object> origin = dao.get(id);
			
			editValues(updated, false);
			
			dao.update(id, updated);
			auditTrail.update(identity.getUser().getId(), origin, updated);
			
			Map<String, Object> params = new HashMap<>();
			params.put("id", id);
			params.put("message", translator.translate("common.item-updated"));
			return Response.getJson(params);
		} catch (Exception e) {
			logger.error("Example Update", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, translator.translate("common.saving-problem"));
		}
	}

	@Action(value = "insert", validator = ExampleValidator.NAME_FORM)
	@Method({HttpMethod.PUT})
	@Secured({@Domain(name=SECURITY_DOMAIN, action=acl.Action.CREATE)})
	public Response insert(@Params RequestParameters inserted, @Param("file") UploadedFile file) {
		try {
			// file.save("www");
			
			editValues(inserted, true);
			
			int id = dao.insert(inserted);
			inserted.put(UNIQUE, id);
			auditTrail.insert(identity.getUser().getId(), inserted);
			
			Map<String, Object> params = new HashMap<>();
			params.put("id", id);
			params.put("message", translator.translate("common.item-inserted"));
			return Response.getJson(params);
		} catch (Exception e) {
			logger.error("Example Insert", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, translator.translate("common.saving-problem"));
		}
	}

	private void editValues(Map<String, Object> values, boolean insert) {
		System.err.println(values);
		System.err.println(values.get("map"));
		System.err.println(values.get("list"));
		System.err.println(values.get("pairs"));
		values.remove(UNIQUE);
	//	values.put("edited_at", DateTime.format("yyyy-MM-dd H:m:s")); // TODO not as string
	//	values.put("edited_by", identity.getUser().getId());
		
		values.remove("file");
		values.remove("map");
		values.remove("list");
		values.remove("pairs");
	}
}
