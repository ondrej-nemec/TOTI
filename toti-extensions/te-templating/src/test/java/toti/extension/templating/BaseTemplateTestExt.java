package toti.extension.templating;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import toti.core.answers.request.Identity;
import toti.core.answers.response.ResponseContainer;
import toti.core.answers.router.Link;
import toti.core.application.register.MappedAction;
import toti.core.extensions.Translator;

public class BaseTemplateTestExt {

	public void testTemplate(Map<String, String> modules, String module, String templatePath, Map<String, Object> variables, String expected) throws Exception {
		TemplateExtension ext = new TemplateExtension(
			"temp", false, false, mock(Logger.class)
		);
		ext.setAuthorize((user, params)->{
			return false;
		});
		modules.forEach((name, path)->{
			ext.registerModule(module, path);
		});

		Identity identity = mock(Identity.class);
		when(identity.getScope(Translator.class)).thenReturn(new Translator() {
			@Override public String translate(String key, Map<String, Object> params) {
				return key + " " + params;
			}
			@Override public String translate(String key) {
				return translate(key, new HashMap<>());
			}
		});
		variables.put("totiIdentity", identity);

		Link link = mock(Link.class);
		when(link.create(anyString())).thenReturn("/params/contr/func");
		// TODO maybe someting more sofisticated for better testing
		when(link.create(anyString(), anyString(), any(), any())).thenReturn("/params/contr/func");

		String actual = ext.getTemplate(module, templatePath, variables, new ResponseContainer(
			"", null, identity, link, MappedAction.test("aa", "bb", "cc"), ext)
		);
		assertEquals(expected, actual);
	}

	@BeforeAll
	public static void beforeAll() throws IOException {
		FileUtils.deleteDirectory(new File("temp/cache"));
	}

}
