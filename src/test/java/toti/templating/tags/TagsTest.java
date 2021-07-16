package toti.templating.tags;

import java.util.HashMap;
import java.util.Map;

import common.Logger;
import toti.TotiLogger;
import toti.templating.Template;
import toti.templating.TemplateFactory;

public class TagsTest {
	
	public static void main(String[] args) {
		try {
			Logger logger = TotiLogger.getLogger("log");
			TemplateFactory templateFactory = new TemplateFactory(
				"temp", "toti/templating/tags", "", new HashMap<>(),
				false, false, logger
			);
			Template template = templateFactory.getTemplate("tags.jsp");
			Map<String, Object> params = new HashMap<>();
			params.put("ifTest", true);
			System.out.println(template.create(templateFactory, params, null, null));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
