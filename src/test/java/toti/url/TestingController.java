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
	
	@Action("url-param")
	public Response methodUrlParam(@ParamUrl("url") String url) {
		fail("Method doInsert cannot be called");
		return null;
	}
	
	@Action("url-get-param")
	public Response methodUrlAndGetParam(@ParamUrl("url") String url, @Param("param") String param2) {
		fail("Method doInsert cannot be called");
		return null;
	}
	
	@Action("get-param")
	public Response methodGetParam(@Param("param") String param2) {
		fail("Method doInsert cannot be called");
		return null;
	}
	
}
