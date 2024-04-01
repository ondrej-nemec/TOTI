package toti.answers.response;

import toti.answers.request.SessionUserProvider;
import toti.answers.router.Link;
import toti.application.register.MappedAction;
import toti.extensions.TemplateExtension;
import toti.extensions.Translator;

public class ResponseContainer {

	private final Translator translator;
	private final SessionUserProvider sup;
	private final MappedAction current;
	private final TemplateExtension templateExtension;
	private final Link link;
	
	public ResponseContainer(Translator translator, SessionUserProvider sup, MappedAction current,
			TemplateExtension templateExtension, Link link) {
		this.translator = translator;
		this.sup = sup;
		this.current = current;
		this.templateExtension = templateExtension;
		this.link = link;
	}
	
	public Translator getTranslator() {
		return translator;
	}

	public SessionUserProvider getSup() {
		return sup;
	}

	public MappedAction getCurrent() {
		return current;
	}

	public TemplateExtension getTemplateExtension() {
		return templateExtension;
	}

	public Link getLink() {
		return link;
	}

}
