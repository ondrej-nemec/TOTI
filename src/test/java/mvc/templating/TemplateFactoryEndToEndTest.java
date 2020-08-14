package mvc.templating;

import java.util.HashMap;
import java.util.Map;

import common.Logger;
import logging.LoggerFactory;
import translator.Translator;

public class TemplateFactoryEndToEndTest {

	public static void main(String[] args) throws Exception {
		Map<String, Object> variables = new HashMap<>();
		variables.put("title", "My first variable in template");
		variables.put("outputValue", "Output to console");
		variables.put("limit", 10);
		//*
		TemplateFactory factory = new TemplateFactory("test/template-factory-cache");
		Template template = factory.getTemplate(
				"src/test/resources/mvc/templating/index.jsp"
		);
		Logger logger = LoggerFactory.getLogger("test");
		Translator translator = new Translator(logger, "", "");
		System.out.println(template.create(variables, translator));
		/*/
		index in = new index();
		System.out.println(in.create(variables));
		//*/
	}
	
}
