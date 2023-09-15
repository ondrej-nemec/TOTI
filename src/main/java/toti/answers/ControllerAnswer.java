package toti.answers;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.Logger;

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
import toti.application.register.Param;
import toti.security.AuthMode;
import toti.security.Authenticator;
import toti.security.Authorizator;
import toti.security.Identity;
import toti.security.exceptions.AccessDeniedException;
import toti.security.exceptions.NotAllowedActionException;
import toti.templating.TemplateException;
import toti.templating.TemplateFactory;
import toti.url.Link;
import toti.url.MappedAction;

public class ControllerAnswer {
	
	private final Param root;
	private final Translator translator;
	private final Authenticator authenticator;
	private final Authorizator authorizator;
	private final Link link;
	private final Map<String, TemplateFactory> modules;
	private final Router router;
	private final Logger logger;
	
	public ControllerAnswer(
			Router router, Param root, Map<String, TemplateFactory> modules,
			Authenticator authenticator, Authorizator authorizator,
			Link link, Translator translator, Logger logger) {
		this.root = root;
		this.router = router;
		this.modules = modules;
		this.authenticator = authenticator;
		this.authorizator = authorizator;
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
		try {
			Response response = run(request.getUri(), mapped, totiRequest, identity);
			
			TemplateFactory templateFactory = modules.get(mapped.getModuleName());
			
			// TODO
			// identityFactory.setResponseHeaders(identity, responseHeaders); // for cookies and custom headers
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
		String[] urls = url.length() == 0 ? new String[] {} : url.substring(1).split("/");
		Param param = this.root;
		int index = 0;
		while(param != null && !param.isAction()) {
			String part = urls[index++];
			param = param.getChild(part);
			if (param.isParam()) {
				request.getPathParams().add(part);
			}
		}
		MappedAction action = null;
		if (param != null) {
			action = param.getAction(method);
		}
		if (action != null) {
			for (int i = index; i < urls.length; i++) {
				request.getPathParams().add(urls[i]);
			}
		}
		return action;
	}
	
	private Response run(String uri, MappedAction mapped, Request request, Identity identity) throws Throwable {
		Object controller = mapped.getClassFactory().create();
		ResponseAction action = (ResponseAction)mapped.getAction()
				.invoke(controller, request.getPathParams().toArray());
		Translator trans = translator.withLocale(identity.getLocale());
		parseBody(request, action.getAllowedBody());
		// TODO check path params with expected?
		try {
			// prevalidate can be interrupted by exception
			action.getPrevalidate().prevalidate(request, trans, identity);
			try {
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
				}
				// authorize can be interrupted by exception
				action.getAuthorize().authrorize(request, trans, identity);
			} catch (ServerException e) {
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
	
	private String getBackLink(String fullUrl) {
        try {
             return URLEncoder.encode(fullUrl, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
             return fullUrl;
        }
    }
	
	private void parseBody(Request request, List<BodyType> allowedTypes) {
		if (request.getBodyParams().size() > 0) {
			if (!allowedTypes.contains(BodyType.FORM_DATA) || !allowedTypes.contains(BodyType.URL_PARAMS)) {
				// TODO correct reaction
			}
		}
		Object contentType = request.getHeaders().getHeader("content-type");
		if (request.getBody() == null && contentType != null) {
			if (contentType.toString().startsWith("application/json") && allowedTypes.contains(BodyType.JSON)) {
				// TODO can be list
				// new JsonReader().read(new String(request.getBody()));
			} else if (contentType.toString().startsWith("application/xml") && allowedTypes.contains(BodyType.XML)) {
				// TODO
			}
		}
	}
	
}
