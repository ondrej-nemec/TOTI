package toti.templating.parameters;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.Logger;
import ji.common.structures.MapInit;
import ji.translator.Locale;
import ji.translator.Translator;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.register.Register;
import toti.security.Authorizator;
import toti.security.Identity;
import toti.security.User;
import toti.templating.Template;
import toti.templating.TemplateFactory;
import toti.url.Link;
import toti.url.MappedUrl;

@RunWith(JUnitParamsRunner.class)
public class ParametersEndToEndTest {

	private final static String PATH = "toti/templating/parameters";
	
	@Test
	@Parameters(method="dataTags")
	public void testTags(String module, String template, String expected) throws Exception {
		User user = mock(User.class);
		Identity identity = mock(Identity.class);
		when(identity.getUser()).thenReturn(user);
		
		Register register = new Register();
		new Controller().initInstances(null, null, register, null, null);
		Link.init("/[module]/[controller]/[method]</[param]>", register);
		
		Translator trans = new Translator() {
			@Override public Translator withLocale(Locale locale) { return this; }
			@Override public String translate(String key, Map<String, Object> variables, String locale) {
				return key + " " + variables; 
			}
			@Override public void setLocale(Locale locale) {}
			@Override public Locale getLocale(String locale) { return mock(Locale.class); }
			@Override public Locale getLocale() { return mock(Locale.class); }
		};
		Map<String, Object> variables = new MapInit<String, Object>()
				.append("totiIdentity", identity)
				.append("toTranslate", "some.key")
				.append("fullLink", "toti.templating.parameters.Controller:index")
				.append("method", "index")
				.toMap();
		String t = create(
			module, template, variables, trans, mock(Authorizator.class), mock(MappedUrl.class), mock(Logger.class)
		);
		assertEquals(expected, t);
	}
	
	public Object[] dataTags() {
		return new Object[] {
			new Object[] {
				"", "href.jsp", "<a href=\"/params/contr/func\" />"
			},
			new Object[] {
				"", "hrefReturning.jsp", "<a href=\"/params/contr/func\" />"
			},
			new Object[] {
				"", "hrefVariable.jsp", "<a href=\"/params/contr/func\" />"
			},
			new Object[] {
				"", "hrefVariableAndString.jsp", "<a href=\"/params/contr/func\" />"
			},
			new Object[] {
				"", "title.jsp", "<div title=\"some.key {}\">"
			},
			new Object[] {
				"", "titleReturning.jsp", "<div title=\"some.key {}\">"
			},
			new Object[] {
				"", "titleVariable.jsp", "<div title=\"some.key {}\">"
			},
			new Object[] {
				"", "titleVariableAndString.jsp", "<div title=\"domain.some.key {}\">"
			}
		};
	}
	
	private String create(
			String submodule, String file, Map<String, Object> variables,
			Translator translator, Authorizator authorizator, MappedUrl mappedUrl,
			Logger logger) throws Exception {
		FileUtils.deleteDirectory(new File("temp/cache"));
		Map<String, TemplateFactory> modules = new HashMap<>();
		
		TemplateFactory templateFactory = new TemplateFactory(
			"temp", PATH + "/" + submodule, "", "", modules,
			false, false, logger
		);
		modules.put("", templateFactory);
		modules.put("module", new TemplateFactory(
			"temp", PATH + "/module", "module", "", modules,
			true, true, logger
		));
		
		Template template = templateFactory.getTemplate(file);
		return template.create(templateFactory, variables, translator, authorizator, mappedUrl);
	}
}
