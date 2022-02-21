package toti.templating.tags;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;
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
import toti.security.Authorizator;
import toti.security.Identity;
import toti.security.User;
import toti.templating.Template;
import toti.templating.TemplateFactory;
import toti.url.MappedUrl;

@RunWith(JUnitParamsRunner.class)
public class TagsEndToEndTest {
	
	private final static String PATH = "toti/templating/tags";
	
	@Test
	@Parameters(method="dataTags")
	public void testTags(String module, String template, String expected) throws Exception {
		User user = mock(User.class);
		Identity identity = mock(Identity.class);
		when(identity.getUser()).thenReturn(user);
		
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
				.append("layout", "layout.jsp")
				.append("included", "includedFile.jsp")
				.append("blockName", "myBlock")
				.append("parameter", "value")
				.append("caseValue", 132)
				.append("switchValue", 123)
				.append("exceptionClass", Exception.class)
				.append("currentMethod", "myMethod")
				.append("securityDoamin", "my-domain")
				.append("toTranslate", "some.key")
				.append("transParam", "some.value")
				.append("condition", true)
				.append("max", 3)
				.append(
					"map",
					new MapInit<String, String>()
					.append("a", "aa")
					.append("a", "bb")
					.append("a", "cc")
					.toMap()
				)
				.append("list", Arrays.asList("a", "b", "c"))
				.toMap();
		String t = create(
			module, template, variables, trans, mock(Authorizator.class), mock(MappedUrl.class), mock(Logger.class)
		);
		assertEquals(expected, t);
	}
	
	public Object[] dataTags() {
		return new Object[] {
			// layout + block
			new Object[] {
				"layout", "thisModule.jsp", "Layout from this module Content"
			},
			new Object[] {
				"layout", "anotherModule.jsp", "Layout from another module Content"
			},
			new Object[] {
				"layout", "returning.jsp", "Layout from this module Content"
			},
			new Object[] {
				"layout", "variable.jsp", "Layout from this module Content"
			},
			// include: file
			new Object[] {
				"include", "file.jsp", "Some content File for include this module"
			},
			new Object[] {
				"include", "fileModule.jsp", "Some content File for include another module"
			},
			new Object[] {
				"include", "fileReturning.jsp", "Some content File for include this module"
			},
			new Object[] {
				"include", "fileVariable.jsp", "Some content File for include this module"
			},
			// include: block + block
			new Object[] {
					"include", "block.jsp", "Block: Block content NULL"
				},
			new Object[] {
					"include", "blockOptional.jsp", "Block: "
				},
			new Object[] {
					"include", "blockReturning.jsp", "Block: Block content NULL"
				},
			new Object[] {
					"include", "blockVariable.jsp", "Block: Block content NULL"
				},
			new Object[] {
					"include", "blockParameter.jsp", "Block: Block content value"
				},
			new Object[] {
					"include", "blockParameterReturning.jsp", "Block: Block content value"
				},
			new Object[] {
					"include", "blockParameterVariable.jsp", "Block: Block content value"
				},
			// switch + case + default
			new Object[] {
					"switchCase", "switchAlone.jsp", "Switch: "
				},
			new Object[] {
					"switchCase", "switchCase.jsp", "Switch: Case"
				},
			new Object[] {
					"switchCase", "switchCaseDefault.jsp", "Switch: Default"
				},
			new Object[] {
					"switchCase", "switchDefault.jsp", "Switch: Default"
				},
			new Object[] {
					"switchCase", "switchVariable.jsp", "Switch: Case"
				},
			new Object[] {
					"switchCase", "switchReturning.jsp", "Switch: Case"
				},
			// try + catch + finally
			new Object[] {
					"tryCatch", "tryCatchE.jsp", "Try: tried"
				},
			new Object[] {
					"tryCatch", "tryCatchN.jsp", "Try: tried"
				},
			new Object[] {
					"tryCatch", "tryCatchFinally.jsp", "Try: tried finally"
				},
			new Object[] {
					"tryCatch", "tryFinally.jsp", "Try: tried finally"
				},
			// variables
			new Object[] {
				"variables", "define.jsp", "Variable: NULL"
			},
			new Object[] {
				"variables", "defineType.jsp", "Variable: NULL"
			},
			new Object[] {
				"variables", "defineValue.jsp", "Variable: 123"
			},
			new Object[] {
				"variables", "defineValueFinal.jsp", "Variable: 123"
			},
			new Object[] {
				"variables", "defineValueChar.jsp", "Variable: a"
			},
			new Object[] {
				"variables", "defineValueString.jsp", "Variable: text"
			},
			new Object[] {
				"variables", "defineValueReturned.jsp", "Variable: value"
			},
			new Object[] {
				"variables", "defineValueVariable.jsp", "Variable: some.key"
			},
			new Object[] {
				"variables", "set.jsp", "Set: 123"
			},
			new Object[] {
				"variables", "setChar.jsp", "Set: a"
			},
			new Object[] {
				"variables", "setString.jsp", "Set: 123"
			},
			new Object[] {
				"variables", "setReturning.jsp", "Set: text"
			},
			new Object[] {
				"variables", "setVariable.jsp", "Set: some.key"
			},
			// if + else if + else
			new Object[] {
				"condition", "ifTag.jsp", "Cond: Yes"
			},
			new Object[] {
				"condition", "ifElse.jsp", "Cond: Yes"
			},
			new Object[] {
				"condition", "ifElseIf.jsp", "Cond: Another"
			},
			new Object[] {
				"condition", "ifElseIfElse.jsp", "Cond: No"
			},
			new Object[] {
				"condition", "ifVariable.jsp", "Cond: Yes"
			},
			new Object[] {
				"condition", "ifReturning.jsp", "Cond: Yes"
			},
			// for + break + continue
			new Object[] {
				"cycles", "forTag.jsp", "For: 0 1 2"
			},
			new Object[] {
				"cycles", "forBreak.jsp", "For: 0 1"
			},
			new Object[] {
				"cycles", "forContinue.jsp", "For: 0 2"
			},
			new Object[] {
				"cycles", "forReturning.jsp", "For: 0 1 2"
			},
			new Object[] {
				"cycles", "forVariable.jsp", "For: 0 1 2"
			},
			// foreach + break + continue
			new Object[] {
				"cycles", "foreachBreak.jsp", "Foreach: a b"
			},
			new Object[] {
				"cycles", "foreachContinue.jsp", "Foreach: a c"
			},
			new Object[] {
				"cycles", "foreachCol.jsp", "Collection: a b c"
			},
			new Object[] {
				"cycles", "foreachColReturning.jsp", "Collection: a b c"
			},
			new Object[] {
				"cycles", "foreachColVariable.jsp", "Collection: a b c"
			},
			new Object[] {
				"cycles", "foreachMap.jsp", "Map: a:aa b:bb c:cc"
			},
			new Object[] {
				"cycles", "foreachMapVariable.jsp", "Map: a:aa b:bb c:cc"
			},
			new Object[] {
				"cycles", "foreachMapReturning.jsp", "Map: a:aa b:bb c:cc"
			},
			// while + break + continue
			new Object[] {
				"cycles", "whileTag.jsp", "Dowhile: 0 1 2"
			},
			new Object[] {
				"cycles", "whileBreak.jsp", "Dowhile: 0 1"
			},
			new Object[] {
				"cycles", "whileContinue.jsp", "Dowhile: 0 2"
			},
			new Object[] {
				"cycles", "whileReturning.jsp", "Dowhile: 0 1 2"
			},
			new Object[] {
				"cycles", "whileVariable.jsp", "Dowhile: 0 1 2"
			},
			// do while + break + continue
			new Object[] {
				"cycles", "dowhile.jsp", "Dowhile: 0 1 2 3"
			},
			new Object[] {
				"cycles", "dowhileBreak.jsp", "Dowhile: 0 1"
			},
			new Object[] {
				"cycles", "dowhileContinue.jsp", "Dowhile: 0 1 3"
			},
			new Object[] {
				"cycles", "dowhileReturning.jsp", "Dowhile: 0 1 2 3"
			},
			new Object[] {
				"cycles", "dowhileVariable.jsp", "Dowhile: 0 1 2 3"
			},
			// if current + else
			new Object[] {
					"other", "ifCurrent.jsp", "If current: "
				},
			new Object[] {
					"other", "ifCurrentElse.jsp", "If current: Not current"
				},
			new Object[] {
					"other", "ifCurrentReturning.jsp", "If current: "
				},
			new Object[] {
					"other", "ifCurrentVariable.jsp", "If current: "
				},
			// permissions + else
			new Object[] {
					"other", "permissions.jsp", "Allowed: "
				},
			new Object[] {
					"other", "permissionsElse.jsp", "Allowed: No"
				},
			new Object[] {
					"other", "permissionsReturning.jsp", "Allowed: "
				},
			new Object[] {
					"other", "permissionsVariable.jsp", "Allowed: "
				},
			// transate
			new Object[] {
					"other", "translate.jsp", "Trans: some.key {}"
				},
			new Object[] {
					"other", "translateParam.jsp", "Trans: some.key {param=some.value}"
				},
			new Object[] {
					"other", "translateVariable.jsp", "Trans: some.key {}"
				},
			new Object[] {
					"other", "translateReturning.jsp", "Trans: some.key {}"
				},
			new Object[] {
					"other", "translateParamReturning.jsp", "Trans: some.key {param=some.value}"
				},
			new Object[] {
					"other", "translateParamVariable.jsp", "Trans: some.key {param=some.value}"
				},
			//TODO link is missing - no controlelr
			// print
			new Object[] {
					"variables", "consoleText.jsp", "Console: "
				},
				new Object[] {
					"variables", "consoleVariable.jsp", "Console: "
				},
				new Object[] {
					"variables", "consoleReturning.jsp", "Console: "
				},
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
