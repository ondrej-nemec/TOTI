package example.web.controllers.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import common.Logger;
import common.structures.MapInit;
import example.AuditTrail;
import example.dao.Example;
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
import toti.control.inputs.Option;
import toti.response.Response;
import toti.security.Identity;
import translator.Translator;

@Controller("example")
public class ExampleApiController {

	private final static String SECURITY_DOMAIN = "example";
	//private final static String UNIQUE = "id";
	
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

	@Action(value = "test", validator = ExampleValidator.TEST)
	@Method({HttpMethod.GET, HttpMethod.POST})
	public Response test(@Params RequestParameters params, @Params Example entity) {
		System.err.println(params);
		params.forEach((key, name)->{
			System.err.println(key + ": " + name);
		});
		System.err.println(params.getList("tags"));
		// System.err.println(entity);
		return Response.getJson(StatusCode.ACCEPTED, new HashMap<>());
	}
	
	@Action("help")
	@Method({HttpMethod.GET})
	public Response getInArray(@Param("view") Boolean viewOnly) {
		try {
			Map<Object, Object> values = dao.getHelp(identity.getUser().getAllowedIds());
			for (int i = 0; i < 10; i++) {
				values.put("#" + i, Option.create("#" + i, "Option #" + i).setOptGroup("Opt Group #" + i%3));
			}
			return Response.getJson(values);
		} catch (Exception e) {
			logger.error("Example List", e);
			return Response.getJson(StatusCode.INTERNAL_SERVER_ERROR, new HashMap<>());
		}
	}
	
	@Action(value = "all", validator = ExampleValidator.NAME_GRID)
	@Method({HttpMethod.GET})
	@Secured({@Domain(name=SECURITY_DOMAIN, action=toti.security.Action.READ)})
	public Response getAll(
			@Param("pageIndex") Integer pageIndex,
			@Param("pageSize") Integer pageSize,
			@Param("filters") Map<String, Object> filters,
			@Param("sorting") Map<String, Object> sorting,
			@Params RequestParameters prop
		) {
		try {
			return Response.getJson(dao.getAll(pageIndex, pageSize, filters, sorting, identity.getUser().getAllowedIds()));
		} catch (Exception e) {
			logger.error("Example GetAll", e);
			return Response.getJson(StatusCode.INTERNAL_SERVER_ERROR, new HashMap<>());
		}
	}

	@Action("get")
	@Method({HttpMethod.GET})
	@Secured({@Domain(name=SECURITY_DOMAIN, action=toti.security.Action.READ)})
	public Response get(@ParamUrl("id") Integer id) {
		try {
			Example item = dao.get(id);
			Map<String, String> map = new HashMap<>();
			map.put("subText1", "<script>alert('XSS!');</script>");
			//  element.querySelector("[value='" + value + "']");
		//	map.put("subText2", "\\\"]'); alert('Successfull XSS'); // ");
		//	map.put("sex", "\\\"]'); alert('Successfull XSS1'); // ");
			List<String> list= new LinkedList<>();
			list.add("1");
			list.add("#1");
			list.add("#6");
		//	list.add("\\\"]'); alert('Successfull XSS2'); // ");
			
			item.setPairs(Arrays.asList(
				new MapInit<String, Object>()
					.append("first-in-pair", "A1")
					.append("second-in-pair", "A2")
				.toMap(),
				new MapInit<String, Object>()
					.append("first-in-pair", "B1")
					.append("second-in-pair", "B2")
					.toMap(),
				new MapInit<String, Object>()
					.append("first-in-pair", "C1")
					.append("second-in-pair", "C2")
					.toMap()
			));
			item.setMap(map);
			item.setList(list);
			return Response.getJson(item);
		} catch (Exception e) {
			logger.error("Example Get", e);
			return Response.getJson(StatusCode.INTERNAL_SERVER_ERROR, new HashMap<>());
		}
	}

	@Action("delete")
	@Method({HttpMethod.DELETE})
	@Secured({@Domain(name=SECURITY_DOMAIN, action=toti.security.Action.DELETE)})
	public Response delete(@ParamUrl("id") Integer id) {
		try {
			Example deleted = dao.delete(id);
			auditTrail.delete(identity.getUser().getId(), deleted);
			return Response.getText(translator.translate("common.item-deleted"));
		} catch (Exception e) {
			logger.error("Example Delete", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, translator.translate("common.deleting-problem"));
		}
	}

	@Action(value = "update", validator = ExampleValidator.NAME_FORM)
	@Method({HttpMethod.PUT})
	@Secured({@Domain(name=SECURITY_DOMAIN, action=toti.security.Action.UPDATE)})
	// public Response update(@ParamUrl("id") Integer id, @Params RequestParameters updated) {
	public Response update(@ParamUrl("id") Integer id, @Params Example updated, @Params RequestParameters p) {
		try {
			Example origin = dao.get(id);
			
			editValues(updated, false);
			
			dao.update(id, updated);
			auditTrail.update(identity.getUser().getId(), origin, updated.toMap());
			
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
	@Secured({@Domain(name=SECURITY_DOMAIN, action=toti.security.Action.CREATE)})
	// public Response insert(@Params RequestParameters inserted, @Param("file") UploadedFile file) {
	public Response insert(@Params Example inserted, @Param("file") UploadedFile file) {
		try {
			// file.save("www");
			
			editValues(inserted, true);
			
			int id = dao.insert(inserted);
			inserted.setId(id);
			auditTrail.insert(identity.getUser().getId(), inserted.toMap());
			
			Map<String, Object> params = new HashMap<>();
			params.put("id", id);
			params.put("message", translator.translate("common.item-inserted"));
			return Response.getJson(params);
		} catch (Exception e) {
			logger.error("Example Insert", e);
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, translator.translate("common.saving-problem"));
		}
	}

	private void editValues(Example values, boolean insert) {
		System.err.println(values);
		System.err.println(values.getMap());
		System.err.println(values.getList());
		System.err.println(values.getPairs());
		/*
		try {
			UploadedFile file = (UploadedFile)values.get("file");
			System.err.println(file.getFileName());
			System.err.println(file.getContentType());
			System.err.println(file.getContent().size());
			System.err.println();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		*/
		values.setToDbSerialize(true);
		//values.remove(UNIQUE);
	//	values.put("edited_at", DateTime.format("yyyy-MM-dd H:m:s")); // TODO not as string
	//	values.put("edited_by", identity.getUser().getId());
		
		//values.remove("file");
		//values.remove("map");
		//values.remove("list");
		//values.remove("pairs");
	}
}
