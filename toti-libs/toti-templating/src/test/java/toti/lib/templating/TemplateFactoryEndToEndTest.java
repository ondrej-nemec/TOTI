package toti.lib.templating;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import toti.lib.common.tests.Log4j2LoggerTestImpl;
import toti.lib.files.text.Text;
import toti.lib.templating.Template;
import toti.lib.templating.TemplateFactory;

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
			false, false, new LinkedList<>(), new LinkedList<>(), new Log4j2LoggerTestImpl("")
		);
		Template template = factory.getTemplate("dir/dir2/index.jsp");
	//	Logger logger = LoggerFactory.getLogger("test");
		
		// Authorizator authorizator = new Authorizator(TotiLoggerFactory.get().apply("temlateTest"));
		String html = template.create(factory, variables, null);
		System.out.println(html);
		Text.get().write((bw)->{
			bw.write(html);
		}, "test/index.html", false);
		/*/
		index in = new index();
		System.out.println(in.create(variables));
		//*/
	}
	
}
