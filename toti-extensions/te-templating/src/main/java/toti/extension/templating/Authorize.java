package toti.extension.templating;

import java.util.Map;

import toti.core.answers.session.Identity;

public interface Authorize {

	boolean isAllowed(Identity user, Map<String, Object> params);

}
