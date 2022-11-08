package toti.tutorial1.web;

import java.util.Map;

import ji.common.structures.MapInit;
import toti.annotations.Controller;
import toti.control.Form;
import toti.response.Response;

@Controller("device")
public class DevicePageController {

	
	
	
	
	private Response getOne() {
		Form form = new Form(null, false);
		
		return Response.getTemplate(
			"/device.jsp",
			MapInit.create()
			.append("control", form)
			.append("title", "")
			.toMap()
		);
	}
	
}
