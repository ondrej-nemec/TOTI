package toti.application.answers.response;

import toti.application.answers.Headers;
import toti.application.answers.request.Identity;
import toti.application.answers.router.Link;
import toti.application.application.register.MappedAction;
import toti.application.extensions.TemplateFactory;

public record ResponseContainer(
	String charset, Headers headers, Identity identity, Link link, MappedAction mapped, TemplateFactory templateFactory
) {

}
