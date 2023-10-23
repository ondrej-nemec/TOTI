package toti;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Properties;
import java.util.function.Consumer;

import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.functions.Env;
import ji.common.structures.MapInit;
import ji.testing.TestCase;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.application.Module;
import toti.application.Task;

@RunWith(JUnitParamsRunner.class)
public class ApplicationFactoryTest implements TestCase {
	
	// TODO test without modules ?
	// TODO test without aliases ?
	// TODO full test with env, manual set, def values
	
	@Test
	@Parameters(method="data")
	@Ignore
	public void test(Properties properties, Consumer<ApplicationFactory> setFactory, boolean useDb) throws Exception {
		Env env = new Env(properties);
		Env applicationsEnv = mock(Env.class);
		when(applicationsEnv.getModule(any())).thenReturn(env);
		Env baseEnv = mock(Env.class);
		when(baseEnv.getModule(any())).thenReturn(applicationsEnv);
		
		Logger totiLogger = mock(Logger.class);
		Logger databaseLogger = mock(Logger.class);
		Logger translatorLogger = mock(Logger.class);
		Logger module1Logger = mock(Logger.class);
		Logger module2Logger = mock(Logger.class);
		
		ApplicationFactory factory = new ApplicationFactory("hostName", env, "charset", (name)->{
			switch (name) {
				case "hostName_toti": return totiLogger;
				case "hostName_database": return databaseLogger;
				case "hostName_translator": return translatorLogger;
				case "hostName_module1": return module1Logger;
				case "hostName_module2": return module2Logger;
				default:
					fail("Unexpected logger: " + name);
					return null;
			}
		}, "alias1", "alias2");
		setFactory.accept(factory);
		
		Task task11 = mock(Task.class);
		Task task12 = mock(Task.class);
		Task task21 = mock(Task.class);
		
		Module module1 = mock(Module.class);
		when(module1.getName()).thenReturn("module1");
		when(module1.getMigrationsPath()).thenReturn("migrations/module1");
		when(module1.initInstances(any(), any(), any(), any(), any(), any())).thenReturn(Arrays.asList(task11, task12));
		Module module2 = mock(Module.class);
		when(module2.getName()).thenReturn("module2");
		when(module2.initInstances(any(), any(), any(), any(), any(), any())).thenReturn(Arrays.asList(task21));
		
		Application application = factory.create(Arrays.asList(module1, module2));
		
		verify(baseEnv, times(1)).getModule("applications");
		verify(applicationsEnv, times(1)).getModule("hostName");
		verifyNoMoreInteractions(baseEnv, applicationsEnv);
		
		// muze byt: logger.info - no database specified
		// muze by: logger.warn profilel
		
		if (useDb) {
			assertNotNull(application.getDatabase());
		} else {
			assertNull(application.getDatabase());
		}
		
		assertNotNull(application.getLink());
		assertNotNull(application.getMigrations());
		assertNotNull(application.getRegister());
		assertNotNull(application.getRequestAnswer());
		assertNotNull(application.getTranslator());
		assertArrayEquals(new String[] {"alias1", "alias2"}, application.getAliases());
		
		verify(module1, times(2)).getMigrationsPath();
		verify(module2, times(1)).getMigrationsPath();
		verify(module1).getTranslationPath();
		verify(module2).getTranslationPath();
		verify(module1).getName();
		verify(module2).getName();
		verify(module1).getPath();
		verify(module2).getPath();
		verify(module1).getTemplatesPath();
		verify(module2).getTemplatesPath();
		verify(module1, times(1)).initInstances(
			env, application.getTranslator(), application.getRegister(),
			application.getLink(), application.getDatabase(), module1Logger
		);
		verify(module2, times(1)).initInstances(
			env, application.getTranslator(), application.getRegister(),
			application.getLink(), application.getDatabase(), module2Logger
		);
		// TODO verify answers
		
		// TODO verify env db: type, url, external, schema-name, password, pool-size
	}
	
	public Object[] data() {
		return new Object[] {
			// default values
			new Object[] {
				MapInit.create().toProperties(),
				consumer(ApplicationFactory.class, (f)->{}),
				false
			},
			/*// TODO
			new Object[] {
				MapInit.create().toProperties(),
				consumer(ApplicationFactory.class, (f)->{
					
				}),
				false
			},
			// TODO
			new Object[] {
				MapInit.create().toProperties(),
				consumer(ApplicationFactory.class, (f)->{
					
				}),
				false
			}*/
		};
	}
	

}
