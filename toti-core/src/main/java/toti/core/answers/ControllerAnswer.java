package toti.core.answers;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.logging.log4j.Logger;

import toti.core.ServerException;
import toti.core.answers.action.BodyType;
import toti.core.answers.action.ResponseAction;
import toti.core.answers.request.Identity;
import toti.core.answers.request.Request;
import toti.core.answers.response.FinalResponse;
import toti.core.answers.response.Response;
import toti.core.answers.response.ResponseContainer;
import toti.core.answers.response.ResponseException;
import toti.core.answers.router.Link;
import toti.core.answers.router.Router;
import toti.core.application.register.MappedAction;
import toti.core.application.register.Param;
import toti.core.extensions.TemplateFactory;
import toti.lib.common.structures.DictionaryValue;
import toti.lib.files.json.JsonReader;
import toti.lib.files.xml.XmlObject;
import toti.lib.files.xml.XmlReader;
import toti.lib.tcpip.enums.HttpMethod;
import toti.lib.tcpip.enums.StatusCode;

public class ControllerAnswer {
	
	private final Param root;
	private final Link link;
	private final TemplateFactory templateExtension;
	private final Router router;
	private final Logger logger;
	
	public ControllerAnswer(Router router, Param root, TemplateFactory templateExtension, Link link, Logger logger) {
		this.root = root;
		this.router = router;
		this.templateExtension = templateExtension;
		this.link = link;
		this.logger = logger;
	}
	
	public FinalResponse answer(Request request, Identity identity, Headers responseHeaders, String charset) throws Exception {
		String uri = request.getUri();
		String routered = router.getUrlMapping(uri);
		if (routered != null) {
			uri = routered;
		}
		MappedAction mapped = getMappedAction(root, getUrlParts(uri), request.getMethod(), request);
		if (mapped == null) {
			request.getPathParams().clear();
			return null;
		}
		try {
			return run(request.getUri(), mapped, request, identity)
				.prepare(new ResponseContainer(charset, responseHeaders, identity, link, mapped, templateExtension));
		} catch (ServerException e){
			throw e;
		} catch (InvocationTargetException e) { // if exception throwed in method
			throw new ServerException(StatusCode.INTERNAL_SERVER_ERROR, mapped, (e.getCause() == null ? e : e.getCause()));
		} catch (ResponseException e){
			throw new ServerException(StatusCode.INTERNAL_SERVER_ERROR, mapped, e.getCause());
		} catch (Throwable e) {
			throw new ServerException(StatusCode.INTERNAL_SERVER_ERROR, mapped, e);
		}
	}

	protected LinkedList<String> getUrlParts(String url) {
		if (url.length() == 0 || "/".equals(url)) {
			return new LinkedList<>();
		}
		return new LinkedList<>(Arrays.asList(url.substring(1).split("/")));
	}
	
	protected MappedAction getMappedAction(Param root, LinkedList<String> urls, HttpMethod method, Request request) {
		if (urls.isEmpty()) {
			return root.getAction(method);
		}
		String part = urls.getFirst();
		Param child = root.getChild(part);
		if (child == null) {
			child = root.getChild(null); // param child
		}
		if (child == null) {
			urls.forEach(p->request.getPathParams().add(p));
			return root.getAction(method);
		}
		if (child.isParam()) {
			request.getPathParams().add(part);
		}
		urls.removeFirst();
		return getMappedAction(child, urls, method, request);
	}
	
	protected Response run(String uri, MappedAction mapped, Request request, Identity identity) throws Throwable {
		if (mapped.getAction().getParameterCount() != request.getPathParams().size()) {
			// probably never happends
			logger.info("Request " + uri + " contains wrong parameters count");
			return Response.create(StatusCode.BAD_REQUEST).getEmpty();
		}
		Object[] params = new Object[mapped.getAction().getParameterCount()];
		try {
			request.getPathParams().forEach((index, dicValue)->{
				params[index] = dicValue.getValue(mapped.getAction().getParameters()[index].getType());
			});
		} catch (ClassCastException | NumberFormatException e) {
			logger.info("Request " + uri + " contains parameter with wrong type");
			return Response.create(StatusCode.BAD_REQUEST).getEmpty();
		}
		Object controller = mapped.getClassFactory().create();
		ResponseAction action = (ResponseAction)mapped.getAction().invoke(controller, params);
		// TODO jeste bude potreba zavolat parse body
		// typ body mozna pridat do @action
		return action.create(request, identity);
	}

	protected void parseBody(Request request, List<BodyType> allowedTypes, MappedAction mapped) throws ServerException {
		if (request.getBodyParams().size() > 0) {
			if (!allowedTypes.contains(BodyType.FORM_DATA) || !allowedTypes.contains(BodyType.URL_PARAMS)) {
				// TODO IMPROVEMENT maybe convert from structured to plaintext - need change JI
				throw new ServerException(StatusCode.NOT_ACCEPTABLE, mapped, "URL not allow body in FORM DATA or URL encoded");
			}
		}
		Object contentType = request.getHeaders().getHeader("content-type");
		if (request.getBody() != null && contentType != null) {
			if (contentType.toString().startsWith("application/json") && allowedTypes.contains(BodyType.JSON)) {
				DictionaryValue json = new DictionaryValue(new JsonReader().read(new String(request.getBody())));
				if (json.is(Map.class)) {
					request.getBodyParams().putAll(json.getMap());
				}
			} else if (contentType.toString().startsWith("application/xml") && allowedTypes.contains(BodyType.XML)) {
				try {
					XmlObject xml = new XmlReader().read(new String(request.getBody()));
					request.getBodyParams().putAll(toMap(xml));
				} catch (XMLStreamException e) {
					throw new ServerException(StatusCode.NOT_ACCEPTABLE, mapped, "XML is broken");
				}
			}
		}
	}
	
	protected Map<String, Object> toMap(XmlObject root) {
		Map<String, Object> result = new HashMap<>();
		root.getReferences().forEach((o)->{
			result.put(o.getName(), parse(o));
		});
		return result;
	}
	
	private Object parse(XmlObject xml) {
		// TODO IMPROVE
		if (!xml.getReferences().isEmpty() || !xml.getAttributes().isEmpty()) {
			List<Object> list = new LinkedList<>();
			xml.getReferences().forEach((o)->{
				list.add(parse(o));
			});
			return list;
		} else {
			return xml.getValue().getValue();
		}
	}
	
}
