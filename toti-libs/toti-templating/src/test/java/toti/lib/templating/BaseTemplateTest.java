package toti.lib.templating;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class BaseTemplateTest {
	
	private final List<Tag> tags;
	private final List<Parameter> parameters;
	
	public BaseTemplateTest() {
		this(new LinkedList<>(), new LinkedList<>());
	}
	
	public BaseTemplateTest(List<Tag> tags, List<Parameter> parameters) {
		this.tags = tags;
		this.parameters = parameters;
	}

	public void testTemplate(Map<String, String> modules, String module, String templatePath, Map<String, Object> variables, String expected) throws Exception {
		FileUtils.deleteDirectory(new File("temp/cache"));
	
		TemplateFactory templateFactory = new TemplateFactory(
			"temp", modules, false, false, tags, parameters, mock(Logger.class)
		);
		Template template = templateFactory.getTemplate(module, templatePath);
		String actual = template.create(variables);
		assertEquals(expected, actual);
	}

}
