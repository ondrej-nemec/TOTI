package toti.templating;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import common.Logger;
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
		TemplateFactory factory = new TemplateFactory("temp/cache", "toti/templating", "", new HashMap<>(), false, false, new Logger() {
			
			@Override public void warn(Object message, Throwable t) {}
			
			@Override public void warn(Object message) {}
			
			@Override public void trace(Object message, Throwable t) {}
			
			@Override public void trace(Object message) {}
			
			@Override public void info(Object message, Throwable t) {}
			
			@Override public void info(Object message) {}
			
			@Override public void fatal(Object message, Throwable t) {}
			
			@Override public void fatal(Object message) {}
			
			@Override public void error(Object message, Throwable t) {}
			
			@Override public void error(Object message) {}
			
			@Override public void debug(Object message, Throwable t) {}
			
			@Override public void debug(Object message) {}
		});
		Template template = factory.getTemplate("dir/dir2/index.jsp");
	//	Logger logger = LoggerFactory.getLogger("test");
		Translator translator = new Translator() {

			@Override
			public String translate(String key, Map<String, String> variables) {
				return key;
			}

			@Override
			public String translate(String key, Map<String, String> variables, Locale locale) {
				return key;
			}

			@Override
			public Translator withLocale(Locale locale) {return this;}

			@Override
			public void setLocale(Locale locale) {}

			@Override
			public Locale getLocale() {
				return null;
			}
		};
		String html = template.create(factory, variables, translator);
		System.out.println(html);
		Text.get().write((bw)->{
			WriteText.get().write(bw, html);
		}, "test/index.html", false);
		/*/
		index in = new index();
		System.out.println(in.create(variables));
		//*/
	}
	
}
