package toti.annotations;

import static org.junit.Assert.fail;

import toti.annotations.url.Action;
import toti.annotations.url.Controller;
import toti.response.Response;

@Controller("users")
public class TestingController {
	
	@Action("insert")
	public Response doInsert() {
		fail("Method doInsert cannot be called");
		return null;
	}
}
