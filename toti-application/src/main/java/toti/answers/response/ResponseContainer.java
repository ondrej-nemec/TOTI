package toti.answers.response;

import java.util.Map;

import ji.translator.Translator;
import toti.answers.request.Identity;
import toti.answers.router.Link;
import toti.application.register.MappedAction;
import toti.security.Action;
import toti.security.Authorizator;
import toti.templating.TemplateContainer;
import toti.templating.TemplateFactory;

public class ResponseContainer implements TemplateContainer {

	private final Translator translator;
	private final Authorizator authorizator;
	private final MappedAction current;
	private final TemplateFactory templateFactory;
	private final Link link;
	
	public ResponseContainer(Translator translator, Authorizator authorizator, MappedAction current,
			TemplateFactory templateFactory, Link link) {
		this.translator = translator;
		this.authorizator = authorizator;
		this.current = current;
		this.templateFactory = templateFactory;
		this.link = link;
	}
	
	@Override
	public String translate(String key, Map<String, Object> variables) {
		return translator.translate(key, variables);
	}
	
	@Override
	public String translate(String key) {
		return translator.translate(key);
	}
	
	@Override
	public boolean isAllowed(Object identity, String domain, String action) {
		// TODO return authorizator.isAllowed(Identity.class.cast(identity).getUser(), domain, Action.valueOf(action));
		return false;
	}
	
	@Override
	public String getModuleName() {
		return current == null ? null : current.getModuleName();
	}
	
	@Override
	public String getMethodName() {
		return current == null ? null : current.getMethodName();
	}
	
	@Override
	public String getClassName() {
		return current == null ? null : current.getClassName();
	}
	
	@Override public String createLink(String url) {
		return link.create(url);
	}
	
	@Override
	public String createLink(String controllerName, String methodName, Map<String, Object> getParams, Object... urlParams) {
		return link.create(controllerName, methodName, getParams, urlParams);
	}
	
	public TemplateFactory getTemplateFactory() {
		return templateFactory;
	}
	
}
