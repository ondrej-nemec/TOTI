package toti.answers.action;

import java.util.List;

public interface ResponseAction {

	Prevalidate getPrevalidate();

	Authorize getAuthorize();

	Validate getValidate();

	Create getCreate();
	
	List<BodyType> getAllowedBody();
	
}
