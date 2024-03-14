package toti.templating;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ji.common.Log4j2LoggerTestImpl;
import ji.files.text.Text;
import ji.files.text.basic.WriteText;
import ji.translator.Locale;
import ji.translator.Translator;

@Deprecated
public class TemplateFactoryEndToEndTest {

	public static void main(String[] args) throws Exception {
		Map<String, Object> variables = new HashMap<>();
		
		variables.put("article", "My first variable in template");
		variables.put("xss", "<script>alert(\"Successfully XSS\");</script>");
		variables.put("limit", 10);
		
		//*
		TemplateFactory factory = new TemplateFactory(
			"temp/cache", "toti/templating", "", "", new HashMap<>(), 
			false, false, new Log4j2LoggerTestImpl("")
		);
		Template template = factory.getTemplate("dir/dir2/index.jsp");
	//	Logger logger = LoggerFactory.getLogger("test");
		Translator translator = new Translator() {

			@Override
			public String translate(String key, Map<String, Object> variables) {
				return key;
			}

			@Override
			public String translate(String key, Map<String, Object> variables, String locale) {
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

			@Override
			public Locale getLocale(String locale) {
				// TODO Auto-generated method stub
				return null;
			}
			@Override public Set<String> getSupportedLocales() { return null; }
		};
		// Authorizator authorizator = new Authorizator(TotiLoggerFactory.get().apply("temlateTest"));
		String html = template.create(factory, variables, null);
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
