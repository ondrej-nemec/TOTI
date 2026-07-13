package toti.core.answers.response;

import toti.core.answers.Headers;
import toti.core.answers.request.Identity;
import toti.core.answers.router.Link;
import toti.core.application.register.MappedAction;
import toti.core.extensions.TemplateFactory;

public record ResponseContainer(
	String charset, Headers headers, Identity identity, Link link, MappedAction mapped, TemplateFactory templateFactory
) {

}
