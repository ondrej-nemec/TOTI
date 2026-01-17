package toti.application.answers.response;

import toti.application.answers.router.Link;
import toti.application.application.register.MappedAction;
import toti.application.extensions.AuthenticationExtension;
import toti.application.extensions.TemplateExtension;
import toti.application.extensions.Translator;

public class ResponseContainer {

	private final Translator translator;
	private final AuthenticationExtension authenticationExtension;
	private final MappedAction current;
	private final TemplateExtension templateExtension;
	private final Link link;
	
	public ResponseContainer(Translator translator, AuthenticationExtension authenticationExtension, MappedAction current,
			TemplateExtension templateExtension, Link link) {
		this.translator = translator;
		this.authenticationExtension = authenticationExtension;
		this.current = current;
		this.templateExtension = templateExtension;
		this.link = link;
	}
	
	public Translator getTranslator() {
		return translator;
	}

	public AuthenticationExtension getAuth() {
		return authenticationExtension;
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
