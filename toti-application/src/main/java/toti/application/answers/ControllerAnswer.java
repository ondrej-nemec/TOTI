package toti.application.answers;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.logging.log4j.Logger;

import toti.application.ServerException;
import toti.application.answers.action.BodyType;
import toti.application.answers.action.ResponseAction;
import toti.application.answers.request.Identity;
import toti.application.answers.request.IdentityFactory;
import toti.application.answers.request.Request;
import toti.application.answers.response.FinalResponse;
import toti.application.answers.response.Response;
import toti.application.answers.response.ResponseContainer;
import toti.application.answers.response.ResponseException;
import toti.application.answers.router.Link;
import toti.application.answers.router.Router;
import toti.application.application.register.MappedAction;
import toti.application.application.register.Param;
import toti.application.extensions.TemplateExtension;
import toti.application.extensions.Translator;
import toti.application.extensions.TranslatorExtension;
import toti.lib.common.structures.DictionaryValue;
import toti.lib.files.json.JsonReader;
import toti.lib.files.xml.XmlObject;
import toti.lib.files.xml.XmlReader;
import toti.lib.tcpip.enums.HttpMethod;
import toti.lib.tcpip.enums.StatusCode;

public class ControllerAnswer {
	
	private final Param root;
	private final TranslatorExtension translatorExtension;
	private final IdentityFactory identityFactory;
	private final Link link;
	private final TemplateExtension templateExtension;
	private final Router router;
	private final Logger logger;
	
	public ControllerAnswer(
			Router router, Param root, TemplateExtension templateExtension,
			IdentityFactory identityFactory,
			Link link, TranslatorExtension translatorExtension, Logger logger) {
		this.root = root;
		this.router = router;
		this.templateExtension = templateExtension;
		this.identityFactory = identityFactory;
		this.translatorExtension = translatorExtension;
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
			Response response = run(request.getUri(), mapped, request, identity);
			return response.prepare(responseHeaders, identity, new ResponseContainer(
				translatorExtension.getTranslator(identity), mapped, templateExtension, link
			), charset);
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
		Translator trans = translatorExtension.getTranslator(identity);
		// TODO jeste bude potreba zavolat parse body
		// typ body mozna pridat do @action
		return action.create(request, trans, identity);
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
