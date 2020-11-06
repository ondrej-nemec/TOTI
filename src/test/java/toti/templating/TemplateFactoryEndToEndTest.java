package toti.templating;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import core.text.Text;
import core.text.basic.WriteText;
import toti.templating.Template;
import toti.templating.TemplateFactory;
import translator.Translator;

public class TemplateFactoryEndToEndTest {

	public static void main(String[] args) throws Exception {
		Map<String, Object> variables = new HashMap<>();
		
		variables.put("article", "My first variable in template");
		variables.put("xss", "<script>alert(\"Successfully XSS\");</script>");
		variables.put("limit", 10);
		
		//*
		TemplateFactory factory = new TemplateFactory("temp/cache", "toti/templating", new HashMap<>(), false);
		Template template = factory.getTemplate("dir/dir2/index.jsp");
	//	Logger logger = LoggerFactory.getLogger("test");
		Translator translator = new Translator() {

			@Override
			public String translate(String key) {
				// TODO Auto-generated method stub
				return key;
			}

			@Override
			public String translate(String key, Locale locale) {
				// TODO Auto-generated method stub
				return key;
			}

			@Override
			public String translate(String key, Map<String, String> variables) {
				// TODO Auto-generated method stub
				return key;
			}

			@Override
			public String translate(String key, Map<String, String> variables, Locale locale) {
				// TODO Auto-generated method stub
				return key;
			}
		};
		String html = template.create(factory, variables, translator);
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
