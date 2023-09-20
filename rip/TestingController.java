package toti.url;

import static org.junit.Assert.fail;

import toti.annotations.Action;
import toti.annotations.Controller;
import toti.answers.action.ResponseAction;
import toti.answers.response.Response;

@Controller("test")
public class TestingController {
	
	@Action(path="no-param")
	public ResponseAction methodNoParams() {
		fail("Method doInsert cannot be called");
		return null;
	}
	
	/*@Action(path="path-param")
	public ResponseAction methodPathParam(String path) {
		fail("Method doInsert cannot be called");
		return null;
	}
	
	@Action(path="path-query-param")
	public ResponseAction methodPathAndQueryParam(String path, @Param("query") String query) {
		fail("Method doInsert cannot be called");
		return null;
	}
	
	@Action(path="query-param")
	public ResponseAction methodQueryParam(@Param("query") String query) {
		fail("Method doInsert cannot be called");
		return null;
	}*/
	
	@Action()
	public Response methodNoName(String path) {
		fail("Method doInsert cannot be called");
		return null;
	}
	
}
