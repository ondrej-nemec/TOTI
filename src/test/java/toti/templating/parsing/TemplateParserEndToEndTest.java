package toti.templating.parsing;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ji.common.Logger;
import ji.common.structures.MapInit;
import ji.files.text.Text;
import ji.files.text.basic.WriteText;
import toti.logging.TotiLogger;
import toti.templating.Tag;
import toti.templating.Template;
import toti.templating.TemplateFactory;
import toti.templating.tags.ElseIfTag;
import toti.templating.tags.ElseTag;
import toti.templating.tags.IfTag;

public class TemplateParserEndToEndTest extends TemplateFactory {
	
	public TemplateParserEndToEndTest(String tempPath, String templatePath, String module, String modulePath,
			Map<String, TemplateFactory> modules, boolean deleteAuxJavaClass, boolean minimalize, Logger logger) {
		super(tempPath, templatePath, module, modulePath, modules, deleteAuxJavaClass, minimalize, logger);
	}


	@Override
	protected List<Tag> initTags(String namespace) {
		return Arrays.asList(
			new Tag() {
				
				@Override
				public String getPairStartCode(Map<String, String> params) {
					return "for (int j = 0; j < 5; j++) {"
							+ initNode(params, new LinkedList<>())
							+ "addVariable(\"j\", j);";
				}
				
				@Override
				public String getPairEndCode(Map<String, String> params) {
					return finishPaired()
							+ "}";
				}
				
				@Override
				public String getNotPairCode(Map<String, String> params) {
					return ""
					   + "write(\"Your message: \" +"
					   + params.get("message")
					   + ");";
				}
				
				@Override
				public String getName() {
					return "tagName";
				}
			},
			new IfTag(), new ElseTag(), new ElseIfTag()
		);
	}
	
	
	public static void main(String[] args) {
		try {
			/*
			new TemplateParser(new HashMap<>(), false).createTempCache(
				"toti/templating/parsing2", // namespace
				"GeneratedTemplate", // class name
				"toti/templating/parsing2/template.jsp", // template
				"src/test/java/", // temp
				"",
				System.currentTimeMillis()
			);
			/*/
			File f = new File("temp/cache/toti_templating_parsing/template.class");
			f.delete();
			
			TemplateParserEndToEndTest test = new TemplateParserEndToEndTest(
				"temp", "toti/templating/parsing", "", "", new HashMap<>(), false, false, TotiLogger.getLogger("parsing")
			);
			Template t = test.getTemplate("template.jsp");
			Text.get().write((bw)->{
				try {
					WriteText.get().write(bw, t.create(null, 
						new MapInit<String, Object>()
						.append("title", "Hello World!")
						.append("age", 42)
						.append("map", new MapInit<>("key1", "some value").toMap())
						.toMap(),
						null, null, null));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}, "temp/index.html", false);
			//*/
			System.out.println("DONE");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
