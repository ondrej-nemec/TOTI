package toti.answers;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.stream.XMLStreamException;

import org.apache.logging.log4j.Logger;

import ji.common.structures.DictionaryValue;
import ji.json.JsonReader;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.WebSocket;
import ji.translator.Translator;
import ji.xml.XmlObject;
import ji.xml.XmlReader;
import toti.ServerException;
import toti.answers.action.BodyType;
import toti.answers.action.RequestInterruptedException;
import toti.answers.action.ResponseAction;
import toti.answers.request.AuthMode;
import toti.answers.request.Identity;
import toti.answers.request.IdentityFactory;
import toti.answers.request.Request;
import toti.answers.request.SessionUserProvider;
import toti.answers.response.Response;
import toti.answers.response.ResponseContainer;
import toti.answers.response.ResponseException;
import toti.answers.router.Link;
import toti.answers.router.Router;
import toti.application.register.MappedAction;
import toti.application.register.Param;
import toti.templating.TemplateException;
import toti.templating.TemplateFactory;

public class ControllerAnswer {
	
	private final Param root;
	private final Translator translator;
	private final SessionUserProvider sessionUserProvider;
	private final IdentityFactory identityFactory;
	private final Link link;
	private final Map<String, TemplateFactory> modules;
	private final Router router;
	private final Logger logger;
	
	public ControllerAnswer(
			Router router, Param root, Map<String, TemplateFactory> modules,
			SessionUserProvider sessionUserProvider, IdentityFactory identityFactory,
			Link link, Translator translator, Logger logger) {
		this.root = root;
		this.router = router;
		this.modules = modules;
		this.sessionUserProvider = sessionUserProvider;
		this.identityFactory = identityFactory;
		this.translator = translator;
		this.link = link;
		this.logger = logger;
	}
	
	public ji.socketCommunication.http.structures.Response answer(
			ji.socketCommunication.http.structures.Request request,
			Identity identity, Headers requestHeaders, Optional<WebSocket> websocket,
			Headers responseHeaders, String charset
		) throws Exception {
		String uri = request.getPlainUri();
		String routered = router.getUrlMapping(uri);
		if (routered != null) {
			uri = routered;
		}
		
		Request totiRequest = Request.fromRequest(request, requestHeaders, websocket);
		/*
		MappedAction mapped = getMappedAction(request.getPlainUri(), request.getMethod(), totiRequest);
		if (mapped == null) {
			return null;
		}
		*/
		MappedAction mapped = getMappedAction(root, getUrlParts(uri), request.getMethod(), totiRequest);
		if (mapped == null) {
			totiRequest.getPathParams().clear();
			return null;
		}
		try {
			Response response = run(request.getUri(), mapped, totiRequest, identity);
			
			TemplateFactory templateFactory = modules.get(mapped.getModuleName());
			/***************/
			identityFactory.finalizeIdentity(identity, responseHeaders); // for cookies and custom headers
	    	/*************/
			return response.getResponse(request.getProtocol(), responseHeaders, identity,  new ResponseContainer(
				translator.withLocale(identity.getLocale()), sessionUserProvider, mapped, templateFactory, link
			), charset);
		} catch (ServerException e){
			throw e;
		} catch (RequestInterruptedException e) {
			return e.getResponse().getResponse(request.getProtocol(), responseHeaders, charset);
		/*} catch (NotAllowedActionException | AccessDeniedException e) {
			throw new ServerException(StatusCode.FORBIDDEN, mapped, e);*/
		} catch (TemplateException e) {
			throw new ServerException(StatusCode.INTERNAL_SERVER_ERROR, mapped, e);
		} catch (InvocationTargetException e) { // if exception throwed in method
			throw new ServerException(StatusCode.INTERNAL_SERVER_ERROR, mapped, (e.getCause() == null ? e : e.getCause()));
		} catch (ResponseException e){
			throw new ServerException(StatusCode.INTERNAL_SERVER_ERROR, mapped, e.getCause());
		} catch (Throwable e) {
			throw new ServerException(StatusCode.INTERNAL_SERVER_ERROR, mapped, e);
		}
	}
/*
	protected MappedAction getMappedAction(String url, HttpMethod method, Request request) {
		MappedAction routered = router.getUrlMapping(url);
		if (routered != null) {
			return routered;
		}
		MappedAction action =  getMappedAction(root, getUrlParts(url), method, request);
		if (action == null) {
			request.getPathParams().clear();
		}
		return action;
	}
	*/
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
		/*
		Object controller = mapped.getClassFactory().create();
		ResponseAction action = (ResponseAction)mapped.getAction()
				.invoke(controller, request.getPathParams().toArray());
		/*/
		if (mapped.getAction().getParameterCount() != request.getPathParams().size()) {
			// probably never happends
			return Response.create(StatusCode.BAD_REQUEST).getEmpty();
		}
		Object[] params = new Object[mapped.getAction().getParameterCount()];
		try {
			request.getPathParams().forEach((index, dicValue)->{
				params[index] = dicValue.getValue(mapped.getAction().getParameters()[index].getType());
			});
		} catch (ClassCastException | NumberFormatException e) {
			return Response.create(StatusCode.BAD_REQUEST).getEmpty();
		}
		Object controller = mapped.getClassFactory().create();
		ResponseAction action = (ResponseAction)mapped.getAction().invoke(controller, params);
		//*/
		Translator trans = translator.withLocale(identity.getLocale());
		parseBody(request, action.getAllowedBody(), mapped);
		try {
			// prevalidate can be interrupted by exception
			action.getPrevalidate().prevalidate(request, trans, identity);
			try {
				checkSecured(mapped, identity);
			} catch (ServerException e) {
				if (mapped.getSecurityMode() == AuthMode.HEADER || router.getRedirectOnNotLoggedInUser() == null) {
					throw e;
				}
				logger.debug(uri + " Redirect to login page: " + e.getMessage());
				String backlink = "";
				if (!uri.equals("/")) {
					backlink = "?backlink=" + getBackLink(uri);
				}
				return Response.create(StatusCode.TEMPORARY_REDIRECT).getRedirect(
					router.getRedirectOnNotLoggedInUser() + backlink
			    );
			}
			// authorize can be interrupted by exception
			action.getAuthorize().authorize(request, trans, identity);
			// validate can be interrupted by exception
			action.getValidate().validate(request, trans, identity);
			return action.getCreate().create(request, trans, identity);
		} catch (RequestInterruptedException e) {
			return e.getResponse();
		}
	}   
	
	protected void checkSecured(MappedAction mapped, Identity identity) throws ServerException {
		if (mapped.isSecured()) {
			if (identity.isAnonymous()) {
				throw new ServerException(StatusCode.UNAUTHORIZED, mapped, "Method require logged user");
			}
			if (mapped.getSecurityMode() == AuthMode.HEADER && identity.getLoginMode() != AuthMode.HEADER) {
				throw new ServerException(StatusCode.FORBIDDEN, mapped, "For this url you cannot use cookie token");
			}
			if (mapped.getSecurityMode() == AuthMode.COOKIE_AND_CSRF
					&& (identity.getLoginMode() == AuthMode.COOKIE || identity.getLoginMode() == AuthMode.NO_TOKEN)) {
				throw new ServerException(StatusCode.FORBIDDEN, mapped, "For this url you need CSRF token");
			}
			if (mapped.getSecurityMode() == AuthMode.COOKIE && identity.getLoginMode() == AuthMode.NO_TOKEN) {
				throw new ServerException(StatusCode.FORBIDDEN, mapped, "For this url you need CSRF token");
			}
		}
	}
	
	private String getBackLink(String fullUrl) {
        try {
             return URLEncoder.encode(fullUrl, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
             return fullUrl;
        }
    }
	
	protected void parseBody(Request request, List<BodyType> allowedTypes, MappedAction mapped) throws ServerException {
		if (request.getBodyParams().size() > 0) {
			if (!allowedTypes.contains(BodyType.FORM_DATA) || !allowedTypes.contains(BodyType.URL_PARAMS)) {
				// TODO maybe convert from structured to plaintext - need change JI
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
		root.getReferences().forEach((n, o)->{
			toMap(o, result);
		});
		return result;
	}
	
	private void toMap(XmlObject xml, Map<String, Object> result) {
		if (!xml.getReferences().isEmpty() || !xml.getAttributes().isEmpty()) {
			Map<String, Object> attributes = new HashMap<>();
			xml.getReferences().forEach((n, o)->{
				attributes.put(n, toMap(o));
			});
			attributes.putAll(xml.getAttributes().toMap());
		} else {
			result.put(xml.getName(), xml.getValue().getValue());
		}
	}
	
}
