package toti.answers;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.Logger;

import ji.common.structures.DictionaryValue;
import ji.json.JsonReader;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.WebSocket;
import ji.translator.Translator;
import toti.Headers;
import toti.Router;
import toti.ServerException;
import toti.answers.action.BodyType;
import toti.answers.action.RequestInterruptedException;
import toti.answers.action.ResponseAction;
import toti.answers.request.Request;
import toti.answers.response.Response;
import toti.answers.response.ResponseContainer;
import toti.answers.response.ResponseException;
import toti.application.register.MappedAction;
import toti.application.register.Param;
import toti.security.AuthMode;
import toti.security.Authenticator;
import toti.security.Authorizator;
import toti.security.Identity;
import toti.security.IdentityFactory;
import toti.security.exceptions.AccessDeniedException;
import toti.security.exceptions.NotAllowedActionException;
import toti.templating.TemplateException;
import toti.templating.TemplateFactory;
import toti.url.Link;

public class ControllerAnswer {
	
	private final Param root;
	private final Translator translator;
	private final Authenticator authenticator;
	private final Authorizator authorizator;
	private final IdentityFactory identityFactory;
	private final Link link;
	private final Map<String, TemplateFactory> modules;
	private final Router router;
	private final Logger logger;
	
	public ControllerAnswer(
			Router router, Param root, Map<String, TemplateFactory> modules,
			Authenticator authenticator, Authorizator authorizator, IdentityFactory identityFactory,
			Link link, Translator translator, Logger logger) {
		this.root = root;
		this.router = router;
		this.modules = modules;
		this.authenticator = authenticator;
		this.authorizator = authorizator;
		this.identityFactory = identityFactory;
		this.translator = translator;
		this.link = link;
		this.logger = logger;
	}
	
	public ji.socketCommunication.http.structures.Response answer(
			ji.socketCommunication.http.structures.Request request,
			Identity identity, Headers requestHeaders, Optional<WebSocket> websocket,
			Headers responseHeaders, String charset
		) throws ServerException {
		Request totiRequest = new Request(
			requestHeaders,
			request.getQueryParameters(),
			request.getBodyInParameters(),
			request.getBody(),
			websocket
		);
		MappedAction mapped = getMappedAction(request.getPlainUri(), request.getMethod(), totiRequest);
		if (mapped == null) {
			return null;
		}
		try {
			Response response = run(request.getUri(), mapped, totiRequest, identity);
			
			TemplateFactory templateFactory = modules.get(mapped.getModuleName());
			identityFactory.setResponseHeaders(identity, responseHeaders); // for cookies and custom headers
	    	try {
	    		authenticator.saveIdentity(identity);
	    	} catch (Exception e) {
	    		logger.warn("Identity save fail", e);
	    	}
			return response.getResponse(null, responseHeaders, identity,  new ResponseContainer(
				translator.withLocale(identity.getLocale()), authorizator, mapped, templateFactory, link
			), charset);
		} catch (ServerException e){
			throw e;
		} catch (RequestInterruptedException e) {
			return e.getResponse().getResponse(request.getProtocol(), responseHeaders, charset);
		} catch (NotAllowedActionException | AccessDeniedException e) {
			throw new ServerException(StatusCode.FORBIDDEN, mapped, e);
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
	
	private LinkedList<String> getUrlParts(String url) {
		if (url.length() == 0 || "/".equals(url)) {
			return new LinkedList<>();
		}
		return new LinkedList<>(Arrays.asList(url.substring(1).split("/")));
	}
	
	private MappedAction getMappedAction(Param root, LinkedList<String> urls, HttpMethod method, Request request) {
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
		Object controller = mapped.getClassFactory().create();
		ResponseAction action = (ResponseAction)mapped.getAction()
				.invoke(controller, request.getPathParams().toArray());
		Translator trans = translator.withLocale(identity.getLocale());
		parseBody(request, action.getAllowedBody(), mapped);
		// TODO check path params with expected?
		try {
			// prevalidate can be interrupted by exception
			action.getPrevalidate().prevalidate(request, trans, identity);
			try {
				checkSecured(mapped, identity);
				// authorize can be interrupted by exception
				action.getAuthorize().authorize(request, trans, identity);
			} catch (ServerException | RequestInterruptedException e) {
				if (mapped.getSecurityMode() == AuthMode.HEADER || router.getRedirectOnNotLoggedInUser() == null) {
					throw e;
				}
				logger.debug(uri + " Redirect to login page: " + e.getMessage());
				String backlink = "";
				if (!uri.equals("/")) {
					backlink = "?backlink=" + getBackLink(uri);
				}
				return Response.getRedirect(
					router.getRedirectOnNotLoggedInUser() + backlink
			    );
			}
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
				// TODO
			}
		}
	}
	
}