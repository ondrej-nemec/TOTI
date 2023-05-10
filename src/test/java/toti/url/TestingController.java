package toti.url;

import static org.junit.Assert.fail;

import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Param;
import toti.annotations.ParamUrl;
import toti.response.Response;

@Controller("test")
public class TestingController {
	
	@Action("no-param")
	public Response methodNoParams() {
		fail("Method doInsert cannot be called");
		return null;
	}
	
	@Action("path-param")
	public Response methodPathParam(@ParamUrl("path") String path) {
		fail("Method doInsert cannot be called");
		return null;
	}
	
	@Action("path-query-param")
	public Response methodPathAndQueryParam(@ParamUrl("path") String path, @Param("query") String query) {
		fail("Method doInsert cannot be called");
		return null;
	}
	
	@Action("query-param")
	public Response methodQueryParam(@Param("query") String query) {
		fail("Method doInsert cannot be called");
		return null;
	}
	
	@Action("")
	public Response methodNoName(@ParamUrl("path") String path) {
		fail("Method doInsert cannot be called");
		return null;
	}
	
}
