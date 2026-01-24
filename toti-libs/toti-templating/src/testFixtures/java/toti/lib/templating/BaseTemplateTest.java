package toti.lib.templating;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class BaseTemplateTest {

	private final String path;
	private final TemplateContainer templateContainer;
	
	private final List<Tag> tags;
	private final List<Parameter> parameters;
	
	public BaseTemplateTest(String path) {
		this(path, new TemplateContainer() {}, new LinkedList<>(), new LinkedList<>());
	}
	
	public BaseTemplateTest(String path, TemplateContainer templateContainer, List<Tag> tags, List<Parameter> parameters) {
		this.path = path;
		this.templateContainer = templateContainer;
		this.tags = tags;
		this.parameters = parameters;
	}

	public void testTemplate(Map<String, Object> variables, String module, String template, String expected) throws Exception {
		//	User user = mock(User.class);
		//	Identity identity = mock(Identity.class);
		//	when(identity.getUser()).thenReturn(user);
			
		//	Register register = new Register();
		//	Link link = new Link("/[module]/[controller]/[method]</[param]>", register);
		//	new Controller().initInstances(null, null, register, link, null, null);
			String t = create(
				module, template, variables, /* mock(Authorizator.class), mock(MappedUrl.class),*/ mock(Logger.class)
			);
			assertEquals(expected, t);
		}

	private String create(String submodule, String file, Map<String, Object> variables, Logger logger) throws Exception {
		FileUtils.deleteDirectory(new File("temp/cache"));
		Map<String, TemplateFactory> modules = new HashMap<>();
		
		TemplateFactory templateFactory = new TemplateFactory(
			"temp", path + (submodule.equals("") ? "" : "/" + submodule), "", "", modules,
			false, false, tags, parameters, logger
		);
		modules.put("", templateFactory);
		modules.put("module", new TemplateFactory(
			"temp", path + "/module", "module", "", modules,
			true, true, tags, parameters, logger
		));
		
		Template template = templateFactory.getTemplate(file);
		// translator, authorizator, mappedUrl
		return template.create(templateFactory, variables, templateContainer);
	}

}
