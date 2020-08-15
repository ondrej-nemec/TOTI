package mvc.templating;

import java.util.HashMap;
import java.util.Map;

import core.text.Text;
import core.text.basic.WriteText;
import translator.Translator;

public class TemplateFactoryEndToEndTest {

	public static void main(String[] args) throws Exception {
		Map<String, Object> variables = new HashMap<>();
		
		variables.put("article", "My first variable in template");
		variables.put("xss", "<script>alert(\"Successfully XSS\");</script>");
		variables.put("limit", 10);
		
		//*
		TemplateFactory factory = new TemplateFactory("test/template-factory-cache");
		Template template = factory.getTemplate(
				"src/test/resources/mvc/templating/index.jsp"
		);
	//	Logger logger = LoggerFactory.getLogger("test");
		Translator translator = new Translator() {
			
			@Override
			public String translate(String key, int count, Map<String, String> variables) {
				return key + String.format(" (%s)", count) + " " + variables;
			}
			
			@Override
			public String translate(String key, int count) {
				return key + String.format(" (%s)", count);
			}
			
			@Override
			public String translate(String key, Map<String, String> variables) {
				return key + " " + variables;
			}
			
			@Override
			public String translate(String key) {
				return key;
			}
		};
		String html = template.create(variables, translator);
		System.out.println(html);
		Text.write((bw)->{
			WriteText.write(bw, html);
		}, "test/index.html", false);
		/*/
		index in = new index();
		System.out.println(in.create(variables));
		//*/
	}
	
}
