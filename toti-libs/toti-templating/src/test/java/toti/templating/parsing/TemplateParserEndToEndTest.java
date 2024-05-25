package toti.templating.parsing;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import ji.common.Log4j2LoggerTestImpl;
import ji.common.structures.MapInit;
import ji.files.text.Text;
import toti.templating.Parameter;
import toti.templating.Tag;
import toti.templating.TagVariableMode;
import toti.templating.Template;
import toti.templating.TemplateFactory;

public class TemplateParserEndToEndTest extends TemplateFactory {
	
	public TemplateParserEndToEndTest(String tempPath, String templatePath, String module, String modulePath,
			Map<String, TemplateFactory> modules, boolean deleteAuxJavaClass, boolean minimalize,
			List<Tag> customTags, List<Parameter> customParams, Logger logger) {
		super(
			tempPath, templatePath, module, modulePath, modules,
			deleteAuxJavaClass, minimalize, customTags, customParams, logger);
	}

	public static void main(String[] args) {
		try {
				Tag tag = new Tag() {
				
				@Override public TagVariableMode getMode(String name) {
					return TagVariableMode.NOT_SUPPORTED;
				}
				
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
					   + " + "
					   + params.get("parameterName")
					   + ");";
				}
				
				@Override
				public String getName() {
					return "tagName";
				}
			};
			Parameter parameter = new Parameter() {
				
				@Override
				public String getName() {
					return "parameterName";
				}
				
				@Override
				public String getCode(String value) {
					return "\"Value: " + value.length() + "\"";
				}
			};
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
				"temp", "toti/templating/parsing", "", "", new HashMap<>(), 
				false, false,
				Arrays.asList(tag), Arrays.asList(parameter),
				new Log4j2LoggerTestImpl("parsing")
			);
			Template t = test.getTemplate("template.jsp");
			Text.get().write((bw)->{
				try {
					bw.write(t.create(null, 
						new MapInit<String, Object>()
						.append("title", "Hello World!")
						.append("age", 42)
						.append("map", new MapInit<>("key1", "some value").toMap())
						.toMap(),
						null));
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
