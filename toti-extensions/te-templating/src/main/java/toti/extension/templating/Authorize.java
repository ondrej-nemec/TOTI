package toti.extension.templating;

import java.util.Map;

import toti.application.answers.request.Identity;

public interface Authorize {

	boolean isAllowed(Identity user, Map<String, Object> params);

}
