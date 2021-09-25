package toti.url;

import static org.junit.Assert.fail;

import toti.annotations.Action;
import toti.annotations.Controller;
import toti.response.Response;

@Controller("users")
public class TestingController {
	
	@Action("insert")
	public Response doInsert() {
		fail("Method doInsert cannot be called");
		return null;
	}
}
