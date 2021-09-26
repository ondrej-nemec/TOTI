package toti.templating.tags;

import java.util.HashMap;
import java.util.Map;

import common.Logger;
import toti.control.Form;
import toti.logging.TotiLogger;
import toti.templating.Template;
import toti.templating.TemplateFactory;

public class ControlTagEndToEndTest {

	public static void main(String[] args) {
		try {
			Logger logger = TotiLogger.getLogger("log");
			TemplateFactory templateFactory = new TemplateFactory(
				"temp", "toti/templating/tags", "", new HashMap<>(),
				false, false, logger
			);
			Template template = templateFactory.getTemplate("test.jsp");
			Map<String, Object> params = new HashMap<>();
			params.put("control", new Form("action", false));
			System.out.println(template.create(templateFactory, params, null, null, null));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
